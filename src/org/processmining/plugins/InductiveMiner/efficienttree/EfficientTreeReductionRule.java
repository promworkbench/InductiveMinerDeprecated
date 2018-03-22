package org.processmining.plugins.InductiveMiner.efficienttree;

public interface EfficientTreeReductionRule {
	/**
	 * Apply the reduction rule on tree, on the node at position i.
	 * 
	 * @param tree
	 * @param i
	 * @return whether the tree was changed or not
	 * @throws UnknownTreeNodeException
	 */
	public boolean apply(EfficientTreeAb tree, int node) throws UnknownTreeNodeException;
}
