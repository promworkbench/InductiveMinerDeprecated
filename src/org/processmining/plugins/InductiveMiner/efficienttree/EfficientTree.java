package org.processmining.plugins.InductiveMiner.efficienttree;

import org.processmining.processtree.ProcessTree;

import gnu.trove.map.TObjectIntMap;

/**
 * Class to store a process tree memory efficient and perform operations cpu
 * efficient.
 * 
 * Idea: keep an array of int. An activity is a greater than 0 value. A node is
 * a negative value. Some bits encode the operator, the other bits the number of
 * children.
 * 
 * @author sleemans
 *
 */
public class EfficientTree implements Cloneable {

	//TODO: remove
	@Deprecated
	public int[] getTree() {
		throw new RuntimeException("please update your packages");
	}

	//TODO: remove
	public void setNodeType(int node, EfficientTreeInt.NodeType operator) {
		throw new RuntimeException("please update your packages");
	}

	//TODO: remove
	public EfficientTree(ProcessTree tree) {

	}

	//TODO: remove
	public EfficientTree() {

	}

	public static enum NodeType {
		tau(-1), activity(0), xor(-2), sequence(-3), interleaved(-4), concurrent(-5), or(-6), loop(-7), skip(-8);

		public final int code;

		NodeType(int code) {
			this.code = code;
		}
	}

	/**
	 * 
	 * @return A map from activity to index
	 */
	public TObjectIntMap<String> getActivity2int() {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @return A map from index (not node!) to activity.
	 */
	public String[] getInt2activity() {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return the first node after node i.
	 */
	public int traverse(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return the activity number denoted at position node. Only call if the
	 *         node is an activity.
	 */
	public int getActivity(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return the type of operator. Only call if the node is an operator.
	 */
	public NodeType getNodeType(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the node at position i is an operator
	 */
	public boolean isOperator(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 *
	 * @param node
	 * @return the number of children of the current node. Only call when the
	 *         node is an operator.
	 */
	public int getNumberOfChildren(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is a tau
	 */
	public boolean isTau(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is an activity
	 */
	public boolean isActivity(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is a sequence
	 */
	public boolean isSequence(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is a xor
	 */
	public boolean isXor(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is an and
	 */
	public boolean isConcurrent(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is an interleaved node
	 */
	public boolean isInterleaved(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is a loop
	 */
	public boolean isLoop(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is an or
	 */
	public boolean isOr(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return whether the given node is not a semantic node (doesn't exist)
	 */
	public boolean isSkip(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return an iterable over all children of the given node.
	 */
	public Iterable<Integer> getChildren(final int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param node
	 * @return the activity name denoted at position node. Only call if the node
	 *         is an activity.
	 */
	public String getActivityName(int node) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @param parent
	 * @param numberOfChild
	 *            (the first child has number 0)
	 * @return the position of the #nrOfChild child of parent.
	 */
	public int getChild(int parent, int numberOfChild) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @return The index of the root of the tree.
	 */
	public int getRoot() {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * 
	 * @return a number that is larger or equal to the number of nodes in the
	 *         tree.
	 */
	public int getMaxNumberOfNodes() {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Copies nodes in the tree, overwriting the nodes at the destination. It is
	 * the responsibility of the caller to ensure there is enough space in the
	 * tree, and to ensure the tree remains consistent.
	 * 
	 * @param srcPos
	 *            start of the block - source
	 * @param destPos
	 *            start of the block - destination
	 * @param length
	 *            number of nodes to be moved
	 */
	public void copy(int srcPos, int destPos, int length) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Sets the type of a node. It is the responsibility of the caller to ensure
	 * that no non-operator is changed into/from an operator, as this might have
	 * unexpected consequences.
	 * 
	 * @param node
	 * @param nodeType
	 */
	public void setNodeType(int node, NodeType nodeType) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Sets the number of children of a node. It is the responsibility of the
	 * caller to ensure the tree remains consistent.
	 * 
	 * @param node
	 * @param numberOfChildren
	 */
	public void setNumberOfChildren(int node, int numberOfChildren) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * @param node
	 * @param activity
	 */
	public void setNodeActivity(int node, int activity) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Set the size as given. Adds skip nodes if necessary.
	 * 
	 * @param size
	 */
	public void setSize(int size) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Swap two consecutive children in a tree.
	 * 
	 * @param startA
	 * @param startB
	 * @param lengthB
	 */
	public void swap(int startA, int startB, int lengthB) {
		throw new RuntimeException("please update your packages");
	}

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Reorders nodes
	 * 
	 * @param nodes
	 *            A consecutive but possibly shuffled list of indices, in which
	 *            to reorder nodes.
	 * @param end
	 *            The index after the end of the last node.
	 */
	public void reorderNodes(Integer[] nodes, int end) {
		throw new RuntimeException("please update your packages");
	}

	public EfficientTree clone() {
		try {
			return (EfficientTree) super.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}