package org.processmining.plugins.InductiveMiner.efficienttree.reductionrules;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeAb;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeAb.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReductionRule;

public class SingleChild implements EfficientTreeReductionRule {

	public boolean apply(EfficientTreeAb tree, int node) {
		if (tree.isOperator(node) && tree.getNumberOfChildren(node) == 1) {
			//remove this node
			tree.copy(node + 1, node, tree.getMaxNumberOfNodes() - node - 1);
			tree.setNodeType(tree.getMaxNumberOfNodes() - node - 1, NodeType.skip);

			return true;
		}
		return false;
	}
}
