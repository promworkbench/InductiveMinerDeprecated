package org.processmining.plugins.InductiveMiner.efficienttree;

import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;

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
public abstract class EfficientTree implements Cloneable, EfficientTreeInt {

	//TODO: remove
	public static TObjectIntMap<String> getEmptyActivity2int() {
		return new TObjectIntHashMap<String>(8, 0.5f, -1);
	}
	
	//TODO: remove
	public abstract int[] getTree();
	
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
	public abstract TObjectIntMap<String> getActivity2int();

	/**
	 * 
	 * @return A map from index (not node!) to activity.
	 */
	public abstract String[] getInt2activity();

	/**
	 * 
	 * @param node
	 * @return the first node after node i.
	 */
	public abstract int traverse(int node);

	/**
	 * 
	 * @param node
	 * @return the activity number denoted at position node. Only call if the
	 *         node is an activity.
	 */
	public abstract int getActivity(int node);

	/**
	 * 
	 * @param node
	 * @return the type of operator. Only call if the node is an operator.
	 */
	public abstract NodeType getNodeType(int node);

	/**
	 * 
	 * @param node
	 * @return whether the node at position i is an operator
	 */
	public abstract boolean isOperator(int node);

	/**
	 *
	 * @param node
	 * @return the number of children of the current node. Only call when the
	 *         node is an operator.
	 */
	public abstract int getNumberOfChildren(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is a tau
	 */
	public abstract boolean isTau(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is an activity
	 */
	public abstract boolean isActivity(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is a sequence
	 */
	public abstract boolean isSequence(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is a xor
	 */
	public abstract boolean isXor(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is an and
	 */
	public abstract boolean isConcurrent(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is an interleaved node
	 */
	public abstract boolean isInterleaved(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is a loop
	 */
	public abstract boolean isLoop(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is an or
	 */
	public abstract boolean isOr(int node);

	/**
	 * 
	 * @param node
	 * @return whether the given node is not a semantic node (doesn't exist)
	 */
	public abstract boolean isSkip(int node);

	/**
	 * 
	 * @param node
	 * @return an iterable over all children of the given node.
	 */
	public abstract Iterable<Integer> getChildren(final int node);

	/**
	 * 
	 * @param node
	 * @return the activity name denoted at position node. Only call if the node
	 *         is an activity.
	 */
	public abstract String getActivityName(int node);

	/**
	 * 
	 * @param parent
	 * @param numberOfChild
	 *            (the first child has number 0)
	 * @return the position of the #nrOfChild child of parent.
	 */
	public abstract int getChild(int parent, int numberOfChild);

	/**
	 * 
	 * @return The index of the root of the tree.
	 */
	public abstract int getRoot();

	/**
	 * 
	 * @return a number that is larger or equal to the number of nodes in the
	 *         tree.
	 */
	public abstract int getMaxNumberOfNodes();

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
	public abstract void copy(int srcPos, int destPos, int length);

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
	public abstract void setNodeType(int node, NodeType nodeType);

	public abstract void setNodeType(int node, EfficientTreeInt.NodeType nodeType);

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
	public abstract void setNumberOfChildren(int node, int numberOfChildren);

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * @param node
	 * @param activity
	 */
	public abstract void setNodeActivity(int node, int activity);

	/**
	 * Please refer to EfficientTreeUtils for higher-level editing functions.
	 * The usage of this method is not encouraged.
	 * 
	 * Set the size as given. Adds skip nodes if necessary.
	 * 
	 * @param size
	 */
	public abstract void setSize(int size);

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
	public abstract void swap(int startA, int startB, int lengthB);

	public EfficientTree clone() throws CloneNotSupportedException {
		return null;
	}
}