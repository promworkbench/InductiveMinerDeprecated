package org.processmining.plugins.InductiveMiner.efficienttree.reductionrules;

import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTree.NodeType;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeReductionRule;
import org.processmining.plugins.InductiveMiner.efficienttree.EfficientTreeUtils;

public class LoopLoop implements EfficientTreeReductionRule {

	public boolean apply(EfficientTree tree, int loop) {
		if (tree.isLoop(loop)) {
			int oldBody = tree.getChild(loop, 0);
			if (tree.isLoop(oldBody)) {
				int A = tree.getChild(oldBody, 0);
				int B = tree.getChild(oldBody, 1);
				int tau = tree.getChild(oldBody, 2);

				if (tree.isTau(tau)) {

					//before:
					//loop loop A B tau C D

					//after:
					//loop A xor2 B C D

					//remove the exit tau
					EfficientTreeUtils.removeChild(tree, oldBody, tau);

					//move A one position forward (over the nested loop); leave B and further in place
					tree.copy(A, A - 1, B - A);

					//set the XOR (notice that B has not moved)
					tree.setNodeType(B - 1, NodeType.xor);
					tree.setNumberOfChildren(B - 1, 2);

					return true;
				}
			}
		}
		return false;
	}

}
