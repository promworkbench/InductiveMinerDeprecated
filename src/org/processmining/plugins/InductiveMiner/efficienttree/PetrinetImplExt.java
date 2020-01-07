package org.processmining.plugins.InductiveMiner.efficienttree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

import org.processmining.models.graphbased.LocalNodeID;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.ExpandableSubNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetImpl;

public class PetrinetImplExt extends PetrinetImpl {

	private static final AtomicLong counter = new AtomicLong();

	public PetrinetImplExt(String label) {
		super(label);
	}

	/**
	 * Add a list of arcs without any checks for duplicates. Not thread safe.
	 * 
	 * @param arcs
	 */
	public void addArcsWithoutCheck(Collection<Arc> arcs) {
		this.arcs.addAll(arcs);

		for (Arc arc : arcs) {
			graphElementAdded(arc);
		}
	}

	public void addTransitionsWithoutCheck(ArrayList<Transition> newTransitions) {
		this.transitions.addAll(newTransitions);

		for (Transition t : newTransitions) {
			graphElementAdded(t);
		}
	}

	public static Arc createArc(PetrinetNode source, PetrinetNode target, int weight, ExpandableSubNet parent) {
		//return new Arc(source, target, weight, parent);
		UUID uuid = new UUID(counter.incrementAndGet(), counter.incrementAndGet());
		LocalNodeID nodeId = new LocalNodeID(uuid);
		return new Arc(source, target, weight, parent, nodeId);
	}

	public static Arc createArc(PetrinetNode source, PetrinetNode t) {
		return createArc(source, t, 1, null);
	}
}