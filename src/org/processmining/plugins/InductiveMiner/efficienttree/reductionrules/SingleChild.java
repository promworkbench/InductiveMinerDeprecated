package org.processmining.plugins.InductiveMiner.efficienttree.reductionrules;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeInt;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeInt.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReductionRule;

public class SingleChild implements EfficientTreeReductionRule {

	public boolean apply(EfficientTreeInt tree, int node) {
		if (tree.isOperator(node) && tree.getNumberOfChildren(node) == 1) {
			//remove this node
			tree.copy(node + 1, node, tree.getMaxNumberOfNodes() - node - 1);
			tree.setNodeType(tree.getMaxNumberOfNodes() - node - 1, NodeType.skip);

			return true;
		}
		return false;
	}
}
