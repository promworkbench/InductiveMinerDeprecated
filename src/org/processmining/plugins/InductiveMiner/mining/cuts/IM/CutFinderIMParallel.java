package org.processmining.plugins.InductiveMiner.mining.cuts.IM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.Function;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.Sets;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMinerState;
import org.processmining.plugins.InductiveMiner.dfgOnly.dfgCutFinder.DfgCutFinder;
import org.processmining.plugins.InductiveMiner.graphs.ConnectedComponents;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.MinerState;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.InductiveMiner.mining.cuts.CutFinder;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog;

public class CutFinderIMParallel implements CutFinder, DfgCutFinder {

	public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		return findCut(logInfo.getStartActivities(), logInfo.getEndActivities(), logInfo.getDirectlyFollowsGraph(),
				null);
	}

	public Cut findCut(Dfg dfg, DfgMinerState minerState) {
		return findCut(dfg.getStartActivities(), dfg.getEndActivities(), dfg.getDirectlyFollowsGraph(), null);
	}

	public static Cut findCut(MultiSet<XEventClass> startActivities, MultiSet<XEventClass> endActivities,
			Graph<XEventClass> dfg, Function<XEventClass, MultiSet<XEventClass>> minimumSelfDistanceBetween) {

		//noise filtering can have removed all start and end activities.
		//if that is the case, return
		if (startActivities.toSet().size() == 0 || endActivities.toSet().size() == 0) {
			return null;
		}

		//construct the negated graph
		Graph<XEventClass> negatedGraph = new Graph<>(XEventClass.class);

		//add the vertices
		negatedGraph.addVertices(dfg.getVertices());

		//walk through all possible edges and negate them
		for (int e1 : dfg.getVertexIndices()) {
			for (int e2 : dfg.getVertexIndices()) {
				if (e1 != e2) {
					if (!dfg.containsEdge(e1, e2) || !dfg.containsEdge(e2, e1)) {
						negatedGraph.addEdge(e1, e2, 1);
					}
				}
			}
		}

		//if wanted, apply an extension to the IM algorithm to account for loops that have the same directly-follows graph as a parallel operator would have
		//make sure that activities on the minimum-self-distance-path are not separated by a parallel operator
		if (minimumSelfDistanceBetween != null) {
			for (XEventClass activity : dfg.getVertices()) {
				try {
					for (XEventClass activity2 : minimumSelfDistanceBetween.call(activity).toSet()) {
						negatedGraph.addEdge(activity, activity2, 1);
					}
				} catch (Exception e2) {
					e2.printStackTrace();
					return null;
				}
			}
		}

		//debug(dfr.debugGraph());

		//compute the connected components of the negated graph
		Set<Set<XEventClass>> connectedComponents = ConnectedComponents.compute(negatedGraph);

		//not all connected components are guaranteed to have start and end activities. Merge those that do not.
		List<Set<XEventClass>> ccsWithStartEnd = new ArrayList<Set<XEventClass>>();
		List<Set<XEventClass>> ccsWithStart = new ArrayList<Set<XEventClass>>();
		List<Set<XEventClass>> ccsWithEnd = new ArrayList<Set<XEventClass>>();
		List<Set<XEventClass>> ccsWithNothing = new ArrayList<Set<XEventClass>>();
		for (Set<XEventClass> cc : connectedComponents) {
			Boolean hasStart = true;
			if (Sets.intersection(cc, startActivities.toSet()).size() == 0) {
				hasStart = false;
			}
			Boolean hasEnd = true;
			if (Sets.intersection(cc, endActivities.toSet()).size() == 0) {
				hasEnd = false;
			}
			if (hasStart) {
				if (hasEnd) {
					ccsWithStartEnd.add(cc);
				} else {
					ccsWithStart.add(cc);
				}
			} else {
				if (hasEnd) {
					ccsWithEnd.add(cc);
				} else {
					ccsWithNothing.add(cc);
				}
			}
		}

		//if there is no set with both start and end activities, there is no parallel cut
		if (ccsWithStartEnd.size() == 0) {
			return null;
		}

		//add full sets
		List<Set<XEventClass>> connectedComponents2 = new ArrayList<Set<XEventClass>>(ccsWithStartEnd);
		//add combinations of end-only and start-only components
		Integer startCounter = 0;
		Integer endCounter = 0;
		while (startCounter < ccsWithStart.size() && endCounter < ccsWithEnd.size()) {
			Set<XEventClass> set = new HashSet<XEventClass>();
			set.addAll(ccsWithStart.get(startCounter));
			set.addAll(ccsWithEnd.get(endCounter));
			connectedComponents2.add(set);
			startCounter++;
			endCounter++;
		}
		//the start-only components can be added to any set
		while (startCounter < ccsWithStart.size()) {
			Set<XEventClass> set = connectedComponents2.get(0);
			set.addAll(ccsWithStart.get(startCounter));
			connectedComponents2.set(0, set);
			startCounter++;
		}
		//the end-only components can be added to any set
		while (endCounter < ccsWithEnd.size()) {
			Set<XEventClass> set = connectedComponents2.get(0);
			set.addAll(ccsWithEnd.get(endCounter));
			connectedComponents2.set(0, set);
			endCounter++;
		}
		//the non-start-non-end components can be added to any set
		for (Set<XEventClass> cc : ccsWithNothing) {
			Set<XEventClass> set = connectedComponents2.get(0);
			set.addAll(cc);
			connectedComponents2.set(0, set);
		}

		return new Cut(Operator.parallel, connectedComponents2);
	}

}
