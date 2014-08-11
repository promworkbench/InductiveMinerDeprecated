package org.processmining.plugins.InductiveMiner.dfgOnly.plugins;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiner;
import org.processmining.plugins.InductiveMiner.dfgOnly.DfgMiningParametersSimple;
import org.processmining.processtree.ProcessTree;


public class IMd {

	@Plugin(name = "Convert directly-follows graph to process tree ", returnLabels = { "Process Tree" }, returnTypes = { ProcessTree.class }, parameterLabels = { "Direclty-follows graph" }, userAccessible = true)
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Mine a Process Tree", requiredParameterLabels = { 0 })
	public ProcessTree mineProcessTree(PluginContext context, Dfg dfg) {
		return DfgMiner.mine(dfg, new DfgMiningParametersSimple());
	}
}
