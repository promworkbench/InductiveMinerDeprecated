package org.processmining.plugins.InductiveMiner.mining.logSplitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.mining.IMLog;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.IMTrace;
import org.processmining.plugins.InductiveMiner.mining.MinerState;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut.Operator;

public class LogSplitterIvM implements LogSplitter {

	public LogSplitResult split(IMLog log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		List<IMLog> result = new ArrayList<IMLog>();
		MultiSet<XEventClass> noise = new MultiSet<XEventClass>();

		//map activities to sigmas
		HashMap<Set<XEventClass>, IMLog> mapSigma2sublog = new HashMap<Set<XEventClass>, IMLog>();
		HashMap<XEventClass, Set<XEventClass>> mapActivity2sigma = new HashMap<XEventClass, Set<XEventClass>>();
		for (Set<XEventClass> sigma : cut.getPartition()) {
			IMLog sublog = new IMLog();
			result.add(sublog);
			mapSigma2sublog.put(sigma, sublog);
			for (XEventClass activity : sigma) {
				mapActivity2sigma.put(activity, sigma);
			}
		}

		for (IMTrace trace : log) {
			if (cut.getOperator() == Operator.loop) {
				splitLoop(result, trace, cut.getPartition(), (int) log.getCardinalityOf(trace), mapSigma2sublog,
						mapActivity2sigma, noise);
			} else if (cut.getOperator() == Operator.xor) {
				splitXor(result, trace, cut.getPartition(), (int) log.getCardinalityOf(trace), mapSigma2sublog,
						mapActivity2sigma, noise);
			} else {
				splitParallel(result, trace, cut.getPartition(), (int) log.getCardinalityOf(trace), mapSigma2sublog,
						mapActivity2sigma, noise);
			}
		}

		return new LogSplitResult(result, noise);
	}

	public static void splitXor(List<IMLog> result, IMTrace trace, Collection<Set<XEventClass>> partition,
			int cardinality, HashMap<Set<XEventClass>, IMLog> mapSigma2sublog,
			HashMap<XEventClass, Set<XEventClass>> mapActivity2sigma, MultiSet<XEventClass> noise) {

		if (trace.size() == 0) {
			//an empty trace should have been filtered as a base case, but now we have to handle it
			//we cannot know in which branch the empty trace should go, so add it to all
			for (IMLog sublog : result) {
				sublog.add(trace, cardinality);
			}
			return;
		}

		//add a new trace to every sublog
		HashMap<Set<XEventClass>, IMTrace> mapSigma2subtrace = new HashMap<Set<XEventClass>, IMTrace>();
		for (Set<XEventClass> sigma : partition) {
			IMTrace subtrace = new IMTrace();
			mapSigma2subtrace.put(sigma, subtrace);
		}

		for (XEventClass event : trace) {
			Set<XEventClass> sigma = mapActivity2sigma.get(event);
			mapSigma2subtrace.get(sigma).add(event);
		}

		for (Set<XEventClass> sigma : partition) {
			if (!mapSigma2subtrace.get(sigma).isEmpty()) {
				mapSigma2sublog.get(sigma).add(mapSigma2subtrace.get(sigma), cardinality);
			}
		}
	}

	public static void splitParallel(List<IMLog> result, IMTrace trace, Collection<Set<XEventClass>> partition,
			long cardinality, HashMap<Set<XEventClass>, IMLog> mapSigma2sublog,
			HashMap<XEventClass, Set<XEventClass>> mapActivity2sigma, MultiSet<XEventClass> noise) {

		//add a new trace to every sublog
		HashMap<Set<XEventClass>, IMTrace> mapSigma2subtrace = new HashMap<Set<XEventClass>, IMTrace>();
		for (Set<XEventClass> sigma : partition) {
			IMTrace subtrace = new IMTrace();
			mapSigma2subtrace.put(sigma, subtrace);
		}

		for (XEventClass event : trace) {
			Set<XEventClass> sigma = mapActivity2sigma.get(event);
			mapSigma2subtrace.get(sigma).add(event);
		}

		for (Set<XEventClass> sigma : partition) {
			mapSigma2sublog.get(sigma).add(mapSigma2subtrace.get(sigma), cardinality);
		}
	}

	public static void splitLoop(List<IMLog> result, IMTrace trace, Collection<Set<XEventClass>> partition,
			long cardinality, HashMap<Set<XEventClass>, IMLog> mapSigma2sublog,
			HashMap<XEventClass, Set<XEventClass>> mapActivity2sigma, MultiSet<XEventClass> noise) {
		IMTrace partialTrace = new IMTrace();

		Set<XEventClass> lastSigma = partition.iterator().next();
		for (XEventClass event : trace) {
			if (!lastSigma.contains(event)) {
				mapSigma2sublog.get(lastSigma).add(partialTrace, cardinality);
				partialTrace = new IMTrace();
				lastSigma = mapActivity2sigma.get(event);
			}
			partialTrace.add(event);
		}
		mapSigma2sublog.get(lastSigma).add(partialTrace, cardinality);

		//add an empty trace if the last event was not of sigma_1
		if (lastSigma != partition.iterator().next()) {
			mapSigma2sublog.get(lastSigma).add(new IMTrace(), cardinality);
		}
	}
}