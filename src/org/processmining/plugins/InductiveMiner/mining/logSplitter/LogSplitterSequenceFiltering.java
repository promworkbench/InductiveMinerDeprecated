package org.processmining.plugins.InductiveMiner.mining.logSplitter;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XEvent;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.MinerState;
import org.processmining.plugins.InductiveMiner.mining.cuts.Cut;
import org.processmining.plugins.InductiveMiner.mining.logs.IMLog2;
import org.processmining.plugins.InductiveMiner.mining.logs.IMTrace2;

public class LogSplitterSequenceFiltering implements LogSplitter {

	public LogSplitResult split(IMLog2 log, IMLogInfo logInfo, Cut cut, MinerState minerState) {
		return split(log, cut.getPartition());
	}

	public static LogSplitResult split(IMLog2 log, Collection<Set<XEventClass>> partition) {

		//initialise
		List<IMLog2> result = new ArrayList<>();
		Map<Set<XEventClass>, IMLog2> mapSigma2Sublog = new THashMap<>();

		Map<XEventClass, Set<XEventClass>> mapActivity2sigma = new THashMap<>();
		Map<Set<XEventClass>, Iterator<IMTrace2>> mapSigma2TraceIterator = new THashMap<>();
		for (Set<XEventClass> sigma : partition) {
			IMLog2 sublog = new IMLog2(log);
			result.add(sublog);
			mapSigma2Sublog.put(sigma, sublog);
			mapSigma2TraceIterator.put(sigma, sublog.iterator());
			for (XEventClass activity : sigma) {
				mapActivity2sigma.put(activity, sigma);
			}
		}
		MultiSet<XEventClass> noise = new MultiSet<>();

		//walk through the traces (in all sublogs and the original log)
		for (IMTrace2 trace : log) {
			Map<Set<XEventClass>, IMTrace2> subtraces = progress(mapSigma2TraceIterator);
			Set<XEventClass> ignore = new THashSet<>();

			//for each trace, fit each sigma
			int atPosition = 0; //we start before the first event
			for (Iterator<Set<XEventClass>> itSigma = partition.iterator(); itSigma.hasNext();) {
				Set<XEventClass> sigma = itSigma.next();
				IMTrace2 subtrace = subtraces.get(sigma);
				Iterator<XEvent> it = subtrace.iterator();

				//remove all events before atPosition
				int atPositionInSubtrace = 0;
				while (atPositionInSubtrace < atPosition) {
					it.next();
					it.remove();
					atPositionInSubtrace++;
				}

				//find where this sigma's subtrace will end
				if (itSigma.hasNext()) {
					atPosition = findOptimalSplit(log, trace, sigma, atPosition, ignore);
				} else {
					//if this is the last sigma, this sigma must finish the trace
					atPosition = trace.size();
				}
				ignore.addAll(sigma);

				//walk over this subtrace, remove all events not from sigma
				while (atPositionInSubtrace < atPosition) {
					XEvent event = it.next();
					XEventClass c = log.classify(event);
					if (!sigma.contains(c)) {
						it.remove();
						noise.add(c);
					}
					atPositionInSubtrace++;
				}

				//remove the remaining part of this subtrace
				while (it.hasNext()) {
					it.next();
					it.remove();
				}
			}
		}

		return new LogSplitResult(result, noise);
	}

	/**
	 * Progress all trace iterators
	 * 
	 * @param mapSigma2Trace
	 * @return
	 */
	private static Map<Set<XEventClass>, IMTrace2> progress(
			Map<Set<XEventClass>, Iterator<IMTrace2>> mapSigma2TraceIterator) {
		Map<Set<XEventClass>, IMTrace2> result = new THashMap<>();
		for (Entry<Set<XEventClass>, Iterator<IMTrace2>> e : mapSigma2TraceIterator.entrySet()) {
			result.put(e.getKey(), e.getValue().next());
		}
		return result;
	}

	private static int findOptimalSplit(IMLog2 log, IMTrace2 trace, Set<XEventClass> sigma, int startPosition,
			Set<XEventClass> ignore) {
		int positionLeastCost = 0;
		int leastCost = 0;
		int cost = 0;
		int position = 0;

		Iterator<XEvent> it = trace.iterator();

		//debug("find optimal split in " + trace.toString() + " for " + sigma.toString());

		//move to the start position
		while (position < startPosition && it.hasNext()) {
			position = position + 1;
			positionLeastCost = positionLeastCost + 1;
			it.next();
		}

		XEventClass event;
		while (it.hasNext()) {
			event = log.classify(it.next());
			if (ignore.contains(event)) {
				//skip
			} else if (sigma.contains(event)) {
				cost = cost - 1;
			} else {
				cost = cost + 1;
			}

			position = position + 1;

			if (cost < leastCost) {
				leastCost = cost;
				positionLeastCost = position;
			}
		}

		return positionLeastCost;
	}
}