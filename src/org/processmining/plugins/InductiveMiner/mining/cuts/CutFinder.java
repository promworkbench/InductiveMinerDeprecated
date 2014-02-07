package org.processmining.plugins.InductiveMiner.mining.cuts;

import org.processmining.plugins.InductiveMiner.mining.IMLog;
import org.processmining.plugins.InductiveMiner.mining.IMLogInfo;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

public interface CutFinder {

	public Cut findCut(IMLog log, IMLogInfo logInfo, MiningParameters parameters);
	
}