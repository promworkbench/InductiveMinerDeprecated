package org.processmining.plugins.InductiveMiner.plugins;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeAb;
import org.processmining.plugins.InductiveMiner.efficienttree.ProcessTree2EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIM;

public class IMTree {

	public EfficientTreeAb mineTree(PluginContext context, XLog log) {
		return mineTree(log, new MiningParametersIM());
	}
	
	public EfficientTreeAb mineTreeParameters(PluginContext context, XLog log, MiningParameters parameters) {
		return mineTree(log, parameters);
	}
	
	public static EfficientTreeAb mineTree(XLog log, MiningParameters parameters) {
		try {
			return ProcessTree2EfficientTree.convert(IMProcessTree.mineProcessTree(log, parameters));
		} catch (UnknownTreeNodeException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
