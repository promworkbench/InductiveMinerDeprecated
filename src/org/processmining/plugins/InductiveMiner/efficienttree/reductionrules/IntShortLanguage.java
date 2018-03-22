package org.processmining.plugins.InductiveMiner.efficienttree.reductionrules;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeAb;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeAb.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeMetrics;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReductionRule;
import org.processmining.plugins.InductiveMiner.efficienttree.UnknownTreeNodeException;

public class IntShortLanguage implements EfficientTreeReductionRule {

	public boolean apply(EfficientTreeAb tree, int node) throws UnknownTreeNodeException {
		if (tree.isInterleaved(node)) {
			//no child should produce a trace of length two or longer
			for (int child : tree.getChildren(node)) {
				if (!EfficientTreeMetrics.traceLengthAtMostOne(tree, child)) {
					return false;
				}
			}

			//transform the interleaved operator into a parallel operator
			tree.setNodeType(node, NodeType.concurrent);
			return true;
		}
		return false;
	}

}
