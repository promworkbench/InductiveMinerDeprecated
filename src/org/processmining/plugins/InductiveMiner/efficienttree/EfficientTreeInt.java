package org.processmining.plugins.InductiveMiner.efficienttree;

public interface EfficientTreeInt {
	public static enum NodeType {
		tau(-1), activity(0), xor(-2), sequence(-3), interleaved(-4), concurrent(-5), or(-6), loop(-7), skip(-8);

		public final int code;

		NodeType(int code) {
			this.code = code;
		}
	}
}
