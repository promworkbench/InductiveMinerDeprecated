package org.processmining.plugins.inductiveminer2.framework.cutfinders;

import java.util.List;

import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;
import org.processmining.plugins.inductiveminer2.helperclasses.normalised.NormalisedIntComponents;
import org.processmining.plugins.inductiveminer2.helperclasses.normalised.NormalisedIntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.normalised.NormaliserInt;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.mining.MinerState;

import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class CutFinderIMLoop implements CutFinder {

	public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
		return findCut2(logInfo.getNormaliser(), logInfo.getDfg());
	}

	public static Cut findCut2(NormaliserInt normaliser, NormalisedIntDfg dfg) {
		//initialise the components: each activity gets its own
		NormalisedIntComponents components = new NormalisedIntComponents(dfg.getNumberOfActivities());

		if (!dfg.hasStartActivities() || !dfg.hasEndActivities()) {
			return null;
		}

		//merge all start and end activities into one component
		{
			int pivot = dfg.getStartActivityIndices()[0];
			for (int e : dfg.getStartActivityIndices()) {
				components.mergeComponentsOf(pivot, e);
			}
			for (int e : dfg.getEndActivityIndices()) {
				components.mergeComponentsOf(pivot, e);
			}
		}

		//merge the other connected components
		for (long edgeIndex : dfg.getDirectlyFollowsEdges()) {
			int source = dfg.getDirectlyFollowsEdgeSourceIndex(edgeIndex);
			int target = dfg.getDirectlyFollowsEdgeTargetIndex(edgeIndex);
			if (!dfg.isStartActivity(source)) {
				if (!dfg.isEndActivity(source)) {
					if (!dfg.isStartActivity(target)) {
						//if (!dfg.isEndActivity(target)) { //optimisation: do not perform this check
						//this is an edge inside a sub-component
						components.mergeComponentsOf(source, target);
						//} else {
						//target is an end but not a start activity
						//a redo cannot reach an end activity that is not a start activity
						//	components.mergeComponentsOf(source, target);
						//}
					}
				}
			} else {
				if (!dfg.isEndActivity(source)) {
					//source is a start but not an end activity
					//a redo cannot be reachable from a start activity that is not an end activity
					components.mergeComponentsOf(source, target);
				}
			}
		}

		/*
		 * We have merged all sub-components. We only have to find out whether
		 * each sub-component belongs to the body or the redo.
		 */

		//make a list of sub-start and sub-endactivities
		TIntSet subStartActivities = new TIntHashSet();
		TIntSet subEndActivities = new TIntHashSet();
		for (long edgeIndex : dfg.getDirectlyFollowsEdges()) {
			int source = dfg.getDirectlyFollowsEdgeSourceIndex(edgeIndex);
			int target = dfg.getDirectlyFollowsEdgeTargetIndex(edgeIndex);

			if (!components.areInSameComponent(source, target)) {
				//target is an sub-end activity and source is a sub-start activity
				subEndActivities.add(source);
				subStartActivities.add(target);
			}
		}

		//a sub-end activity of a redo should have connections to all start activities
		for (int subEndActivity : subEndActivities.toArray()) {
			for (int startActivity : dfg.getStartActivityIndices()) {
				if (components.areInSameComponent(subEndActivity, startActivity)) {
					//this subEndActivity is already in the body
					break;
				}
				if (!dfg.containsDirectlyFollowsEdge(subEndActivity, startActivity)) {
					components.mergeComponentsOf(subEndActivity, startActivity);
					break;
				}
			}
		}

		//a sub-start activity of a redo should be connections from all end activities
		for (int subStartActivity : subStartActivities.toArray()) {
			for (int endActivity : dfg.getEndActivityIndices()) {
				if (components.areInSameComponent(subStartActivity, endActivity)) {
					//this subStartActivity is already in the body
					break;
				}
				if (!dfg.containsDirectlyFollowsEdge(endActivity, subStartActivity)) {
					components.mergeComponentsOf(subStartActivity, endActivity);
					break;
				}
			}
		}

		//put the start and end activity component first
		List<TIntSet> partition = components.getComponents();
		int pivot = dfg.getStartActivityIndices()[0];
		for (int i = 0; i < partition.size(); i++) {
			if (partition.get(i).contains(pivot)) {
				TIntSet swap = partition.get(0);
				partition.set(0, partition.get(i));
				partition.set(i, swap);
				break;
			}
		}

		return new Cut(Operator.loop, normaliser.deNormalise(partition));
	}

}
