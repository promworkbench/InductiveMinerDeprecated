package org.processmining.plugins.InductiveMiner.efficienttree;

import gnu.trove.map.TObjectIntMap;

@Deprecated
public class EfficientTreeInt extends EfficientTreeImpl {
	public EfficientTreeInt(int[] tree, TObjectIntMap<String> activity2int, String[] int2activity) {
		super(tree, activity2int, int2activity);
		// TODO Auto-generated constructor stub
	}

	@Deprecated
	public static enum NodeType {
		tau(-1), activity(0), xor(-2), sequence(-3), interleaved(-4), concurrent(-5), or(-6), loop(-7), skip(-8);

		public final int code;

		NodeType(int code) {
			this.code = code;
		}
	}
}
