package org.processmining.plugins.InductiveMiner.mining;

import java.util.Arrays;
import java.util.LinkedList;

import org.processmining.plugins.InductiveMiner.mining.baseCases.BaseCaseFinder;
import org.processmining.plugins.InductiveMiner.mining.baseCases.BaseCaseFinderIM;
import org.processmining.plugins.InductiveMiner.mining.cuts.CutFinder;
import org.processmining.plugins.InductiveMiner.mining.cuts.IM.CutFinderIM;
import org.processmining.plugins.InductiveMiner.mining.cuts.IMin.CutFinderIMin;
import org.processmining.plugins.InductiveMiner.mining.fallthrough.FallThrough;
import org.processmining.plugins.InductiveMiner.mining.fallthrough.FallThroughFlower;
import org.processmining.plugins.InductiveMiner.mining.logSplitter.LogSplitterIMi;

public class MiningParametersIMin extends MiningParameters {
	
	public MiningParametersIMin() {
		//determine algorithm
		
		setBaseCaseFinders(new LinkedList<BaseCaseFinder>(Arrays.asList(
				new BaseCaseFinderIM()
				)));
		
		setCutFinder(new LinkedList<CutFinder>(Arrays.asList(
				new CutFinderIM(),
				new CutFinderIMin()
				)));
		
		setLogSplitter(new LogSplitterIMi());
		
		setFallThroughs(new LinkedList<FallThrough>(Arrays.asList(
				new FallThroughFlower()
				)));
	}
}