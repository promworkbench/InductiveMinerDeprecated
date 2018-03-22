package org.processmining.plugins.InductiveMiner.efficienttree;

import org.processmining.processtree.ProcessTree;

import gnu.trove.map.TObjectIntMap;

@Deprecated
public class EfficientTree extends EfficientTreeImpl {

	@Deprecated
	public EfficientTree(int[] tree, TObjectIntMap<String> activity2int, String[] int2activity) {
		super(tree, activity2int, int2activity);
	}

	public EfficientTree(ProcessTree model) {
		super(ProcessTree2EfficientTree.convert(model).getTree(),
				ProcessTree2EfficientTree.convert(model).getActivity2int(),
				ProcessTree2EfficientTree.convert(model).getInt2activity());
	}

	@Deprecated
	public void removeChild(int parent, int child) {
		EfficientTreeUtils.removeChild(this, parent, child);
	}

	@Deprecated
	public static final int skip = NodeType.skip.code;

	@Deprecated
	public static final int tau = NodeType.tau.code;

}
