package org.processmining.plugins.InductiveMiner.mining.SAT.probabilities;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.mining.DirectlyFollowsRelation;

public class ProbabilitiesUnit extends Probabilities {

	public double getProbabilityXor(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		if (!D(r, a, b) && !D(r, b, a) && !E(r, a, b) && !E(r, b, a)) {
			return 1;
		} else {
			return 0;
		}
	}

	public double getProbabilitySequence(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		if (D(r, a, b) && !D(r, b, a) && !E(r, b, a)) {
			return 1;
		} else if (!D(r, a, b) && !D(r, b, a) && E(r, a, b) && !E(r, b, a)) {
			return 1;
		} else {
			return 0;
		}
	}

	public double getProbabilityParallel(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		if (D(r, a, b) && D(r, b, a)) {
			return 1;
		} else {
			return 0;
		}
	}

	public double getProbabilityLoopSingle(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		if (D(r, a, b) && !D(r, b, a) && E(r, b, a)) {
			return 1;
		}
		return 0;
	}

	public double getProbabilityLoopIndirect(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		if (!D(r, a, b) && !D(r, b, a) && E(r, a, b) && E(r, b, a)) {
			return 1;
		}
		return 0;
	}

	public double getProbabilityLoopDouble(DirectlyFollowsRelation r, XEventClass a, XEventClass b) {
		return 0;
	}
	
	public String toString() {
		return "SAT unit (without short loops)";
	}

}
