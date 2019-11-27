package org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet;

import java.util.Collection;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * Reduce a Petri net using Murata's Fusion of Series Transitions. However,
 * preserve the language of the net, i.e. only remove invisible transitions.
 * 
 * Adapted from Eric Verbeek's code in the Murata package of ProM.
 * 
 * @author sander
 *
 */

public class MurataFPTkeepLanguage {

	public static boolean reduce(AcceptingPetriNet anet, Canceller canceller) {
		Petrinet net = anet.getNet();

		/*
		 * Iterate over all transitions. Build inputMap and outputMap if all
		 * incident edges regular.
		 */
		for (Transition transition : net.getTransitions()) {

			if (canceller.isCancelled()) {
				return true;
			}

			if (!transition.isInvisible()) {
				continue;
			}

			/*
			 * Get input edges. Should all be regular.
			 */
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inputArcs = net
					.getInEdges(transition);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outputArcs = net
					.getOutEdges(transition);
			{
				boolean ok = true;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : inputArcs) {
					if (!(edge instanceof Arc)) {
						ok = false;
						break;
					}
				}
				if (!ok) {
					continue;
				}

				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : outputArcs) {
					if (!(edge instanceof Arc)) {
						ok = false;
						break;
					}
				}
				if (!ok) {
					continue;
				}
			}

			/*
			 * Checking for matching transitions.
			 */
			for (Transition siblingTransition : net.getTransitions()) {

				if (!siblingTransition.isInvisible()) {
					continue;
				}

				if (siblingTransition == transition) {
					continue;
				}

				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> siblingInputArcs = net
						.getInEdges(siblingTransition);
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> siblingOutputArcs = net
						.getOutEdges(siblingTransition);
				if (siblingInputArcs.size() != inputArcs.size()) {
					continue;
				}
				if (siblingOutputArcs.size() != outputArcs.size()) {
					continue;
				}
				boolean equal = true;
				boolean found;
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc : inputArcs) {
					if (equal) {
						found = false;
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> siblingArc : siblingInputArcs) {
							if ((arc.getSource() == siblingArc.getSource())
									&& (((Arc) arc).getWeight() == ((Arc) siblingArc).getWeight())) {
								found = true;
							}
						}
						if (!found) {
							equal = false;
						}
					}
				}
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> arc : outputArcs) {
					if (equal) {
						found = false;
						for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> siblingArc : siblingOutputArcs) {
							if ((arc.getTarget() == siblingArc.getTarget())
									&& (((Arc) arc).getWeight() == ((Arc) siblingArc).getWeight())) {
								found = true;
							}
						}
						if (!found) {
							equal = false;
						}
					}
				}
				if (equal) {
					/*
					 * Found a sibling with identical inputs and outputs. Remove
					 * the sibling.
					 */

					net.removeTransition(siblingTransition);
					return true;
				}
			}
		}
		return false;
	}
}
