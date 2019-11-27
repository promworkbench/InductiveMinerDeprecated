package org.processmining.plugins.InductiveMiner.reduceacceptingpetrinet;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.petrinet.reduction.MurataUtils;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

/**
 * Reduce a Petri net using Murata's Fusion of Parallel Places rule. However,
 * preserve the language of the net, i.e. only remove invisible transitions.
 * 
 * Adapted from Eric Verbeek's code in the Murata package of ProM.
 * 
 * @author sander
 *
 */

public class MurataFPPkeepLanguage {

	public static boolean reduce(AcceptingPetriNet anet, Canceller canceller) {
		Petrinet net = anet.getNet();

		Map<Place, Set<Arc>> inputMap = new THashMap<>();
		Map<Place, Set<Arc>> outputMap = new THashMap<>();
		/*
		 * Iterate over all places. Build inputMap and outputMap if all incident
		 * edges regular.
		 */
		for (Place place : net.getPlaces()) {

			if (canceller.isCancelled()) {
				return false;
			}

			/*
			 * Check that all incident edges are regular.
			 */
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> inputArcs = net.getInEdges(place);
			Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> outputArcs = net
					.getOutEdges(place);
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
			for (Place siblingPlace : inputMap.keySet()) {
				if (siblingPlace == place) {
					continue;
				}
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> siblingInputArcs = net
						.getInEdges(siblingPlace);
				Collection<PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode>> siblingOutputArcs = net
						.getOutEdges(siblingPlace);
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
							break;
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
							break;
						}
					}
				}
				if (equal) {
					/*
					 * Found a sibling with identical inputs and outputs. Remove
					 * the sibling.
					 */

					/*
					 * Move tokens from siblingPlace to place in the initial and
					 * final markings
					 */
					{
						{
							int tokens = anet.getInitialMarking().occurrences(siblingPlace);
							anet.getInitialMarking().add(place, tokens);
							MurataUtils.updateLabel(place, anet.getInitialMarking());
							MurataUtils.resetPlace(anet.getInitialMarking(), siblingPlace);
						}

						for (Marking marking : anet.getFinalMarkings()) {
							int tokens = marking.occurrences(siblingPlace);
							marking.add(place, tokens);
							MurataUtils.updateLabel(place, marking);
							MurataUtils.resetPlace(marking, siblingPlace);
						}

						//the markings might have changed, so we need to re-index the final markings
						anet.setFinalMarkings(new THashSet<>(anet.getFinalMarkings()));
					}
					net.removePlace(siblingPlace);
					return true; // The sibling has been removed.
				}
			}
		}
		return false;
	}

}
