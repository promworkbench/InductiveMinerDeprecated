package org.processmining.plugins.inductiveminer2.variants;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMc.probabilities.Probabilities;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinder;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyLog;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyTraces;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderEmptyTracesFiltering;
import org.processmining.plugins.inductiveminer2.framework.basecases.BaseCaseFinderSingleActivityFiltering;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.Cut;
import org.processmining.plugins.inductiveminer2.framework.cutfinders.CutFinder;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterConcurrent;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterInterleavedFiltering;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterLoop;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterOr;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterSequenceFiltering;
import org.processmining.plugins.inductiveminer2.framework.logsplitter.LogSplitterXorFiltering;
import org.processmining.plugins.inductiveminer2.helperclasses.normalised.NormalisedIntDfg;
import org.processmining.plugins.inductiveminer2.helperclasses.normalised.NormalisedIntGraph;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfo;
import org.processmining.plugins.inductiveminer2.loginfo.IMLog2IMLogInfoDefault;
import org.processmining.plugins.inductiveminer2.loginfo.IMLogInfo;
import org.processmining.plugins.inductiveminer2.logs.IMLog;
import org.processmining.plugins.inductiveminer2.logs.IMLogImpl;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.inductiveminer2.mining.MinerState;
import org.processmining.plugins.inductiveminer2.mining.MiningParametersAbstract;

import gnu.trove.set.TIntSet;

public class MiningParametersIMInfrequent extends MiningParametersAbstract implements InductiveMinerVariant {

	public static final List<BaseCaseFinder> filteringBaseCases = Arrays.asList(new BaseCaseFinder[] { //
			new BaseCaseFinderEmptyLog(), //
			new BaseCaseFinderEmptyTracesFiltering(), //
			new BaseCaseFinderEmptyTraces(), //
			new BaseCaseFinderSingleActivityFiltering() //
	});

	public static final CutFinder filteringCutFinders = new CutFinder() {
		public Cut findCut(IMLog log, IMLogInfo logInfo, MinerState minerState) {
			IMLogInfo logInfoFiltered = filterNoise(logInfo, minerState.parameters.getNoiseThreshold());

			//call IM cut detection
			return InductiveMiner.findCut(null, logInfoFiltered, MiningParametersIM.basicCutFinders, minerState);
		}
	};

	public MiningParametersIMInfrequent() {

		baseCaseFinders.addAll(filteringBaseCases);
		baseCaseFinders.addAll(MiningParametersIM.basicBaseCaseFinders);

		cutFinders.addAll(MiningParametersIM.basicCutFinders);
		cutFinders.add(filteringCutFinders);

		fallThroughs.addAll(MiningParametersIM.basicFallThroughs);

		getReduceParameters().setReduceToOr(false);
	}

	public IMLog getIMLog(XLog xLog) {
		return new IMLogImpl(xLog, getClassifier(), getLifeCycleClassifier());
	}

	public Probabilities getSatProbabilities() {
		return null;
	}

	public IMLog2IMLogInfo getLog2LogInfo() {
		return new IMLog2IMLogInfoDefault();
	}

	public boolean isRepairLifeCycle() {
		return false;
	}

	public IMLog[] splitLogConcurrent(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterConcurrent.split(log, partition, minerState);
	}

	public IMLog[] splitLogInterleaved(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterInterleavedFiltering.split(log, partition, minerState);
	}

	public IMLog[] splitLogLoop(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterLoop.split(log, partition, minerState);
	}

	public IMLog[] splitLogOr(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterOr.split(log, partition, minerState);
	}

	public IMLog[] splitLogSequence(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterSequenceFiltering.split(log, partition, minerState);
	}

	public IMLog[] splitLogXor(IMLog log, IMLogInfo logInfo, List<TIntSet> partition, MinerState minerState) {
		return LogSplitterXorFiltering.split(log, partition, minerState);
	}

	public String toString() {
		return "Inductive Miner - infrequent   (IMf)";
	}

	public boolean hasFitness() {
		return false;
	}

	public boolean hasNoise() {
		return true;
	}

	public boolean noNoiseImpliesFitness() {
		return true;
	}

	public MiningParametersAbstract getMiningParameters() {
		return this;
	}

	public int getWarningThreshold() {
		return -1;
	}

	public String getDoi() {
		return "http://dx.doi.org/10.1007/978-3-319-06257-0_6";
	}

	public static IMLogInfo filterNoise(IMLogInfo logInfo, float threshold) {
		return new IMLogInfo(logInfo.getNormaliser().clone(), filterNoise(logInfo.getDfg(), threshold),
				logInfo.getActivityMultiSet().clone(), logInfo.getMinimumSelfDistancesBetween(),
				logInfo.getMinimumSelfDistances(), logInfo.getNumberOfEvents(), logInfo.getNumberOfActivityInstances(),
				logInfo.getNumberOfTraces());
	}

	public static NormalisedIntDfg filterNoise(NormalisedIntDfg dfg, float threshold) {
		NormalisedIntDfg newDfg = dfg.clone();

		filterStartActivities(newDfg, threshold);
		filterEndActivities(newDfg, threshold);
		filterDirectlyFollowsGraph(newDfg, threshold);
		filterConcurrencyGraph(newDfg, threshold);
		return newDfg;
	}

	/**
	 * Filter a graph. Only keep the edges that occur often enough, compared
	 * with other outgoing edges of the source. 0 <= threshold <= 1.
	 * 
	 * @param graph
	 * @param threshold
	 * @return
	 */
	private static void filterDirectlyFollowsGraph(NormalisedIntDfg dfg, float threshold) {
		NormalisedIntGraph graph = dfg.getDirectlyFollowsGraph();

		for (int activity = 0; activity < dfg.getNumberOfActivities(); activity++) {
			//find the maximum outgoing weight of this node
			long maxWeightOut = dfg.getEndActivityCardinality(activity);
			for (long edge : graph.getOutgoingEdgesOf(activity)) {
				maxWeightOut = Math.max(maxWeightOut, (int) graph.getEdgeWeight(edge));
			}

			//remove all edges that are not strong enough
			Iterator<Long> it = graph.getOutgoingEdgesOf(activity).iterator();
			while (it.hasNext()) {
				long edge = it.next();
				if (graph.getEdgeWeight(edge) < maxWeightOut * threshold) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Filter a graph. Only keep the edges that occur often enough, compared
	 * with other outgoing edges of the source. 0 <= threshold <= 1.
	 * 
	 * @param graph
	 * @param threshold
	 * @return
	 */
	private static void filterConcurrencyGraph(NormalisedIntDfg dfg, float threshold) {
		NormalisedIntGraph graph = dfg.getConcurrencyGraph();

		for (int activity = 0; activity < dfg.getNumberOfActivities(); activity++) {
			//find the maximum outgoing weight of this node
			long maxWeightOut = dfg.getEndActivityCardinality(activity);
			for (long edge : graph.getOutgoingEdgesOf(activity)) {
				maxWeightOut = Math.max(maxWeightOut, (int) graph.getEdgeWeight(edge));
			}

			//remove all edges that are not strong enough
			Iterator<Long> it = graph.getOutgoingEdgesOf(activity).iterator();
			while (it.hasNext()) {
				long edge = it.next();
				if (graph.getEdgeWeight(edge) < maxWeightOut * threshold) {
					it.remove();
				}
			}
		}
	}

	/**
	 * Filter start activities. Only keep those occurring more times than
	 * threshold * the most occurring activity. 0 <= threshold <= 1.
	 * 
	 * @param activities
	 * @param threshold
	 * @return
	 */
	private static void filterStartActivities(NormalisedIntDfg dfg, float threshold) {
		long max = dfg.getMostOccurringStartActivityCardinality();
		for (int activity : dfg.getStartActivityIndices()) {
			if (dfg.getStartActivityCardinality(activity) < threshold * max) {
				dfg.removeStartActivity(activity);
			}
		}
	}

	/**
	 * Filter start activities. Only keep those occurring more times than
	 * threshold * the most occurring activity. 0 <= threshold <= 1.
	 * 
	 * @param activities
	 * @param threshold
	 * @return
	 */
	private static void filterEndActivities(NormalisedIntDfg dfg, float threshold) {
		long max = dfg.getMostOccurringEndActivityCardinality();
		for (int activity : dfg.getEndActivityIndices()) {
			if (dfg.getEndActivityCardinality(activity) < threshold * max) {
				dfg.removeEndActivity(activity);
			}
		}
	}
}