package org.processmining.plugins.InductiveMiner.efficienttree;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.models.graphbased.directed.petrinet.elements.Arc;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class EfficientTree2AcceptingPetriNet {

	public static AtomicInteger placeCounter = new AtomicInteger();

	private static class PN {
		PetrinetImplExt pn = new PetrinetImplExt("converted from efficient tree");
		ArrayList<Arc> arcs = new ArrayList<>();
	}

	public static AcceptingPetriNet convert(EfficientTree tree) {
		return convert(tree, new TIntObjectHashMap<Transition>(10, 0.5f, -1));
	}

	public static AcceptingPetriNet convert(EfficientTree tree, TIntObjectMap<Transition> node2transition) {
		PN petriNet = new PN();
		Place source = petriNet.pn.addPlace("net source");
		Place sink = petriNet.pn.addPlace("net sink");
		Marking initialMarking = new Marking();
		initialMarking.add(source);
		Marking finalMarking = new Marking();
		finalMarking.add(sink);

		int root = tree.getRoot();

		convertNode(petriNet, tree, root, source, sink, node2transition);

		petriNet.pn.addArcsWithoutCheck(petriNet.arcs);
		return AcceptingPetriNetFactory.createAcceptingPetriNet(petriNet.pn, initialMarking, finalMarking);
	}

	private static void convertNode(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		if (tree.isTau(node)) {
			convertTau(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isActivity(node)) {
			convertTask(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isConcurrent(node)) {
			convertAnd(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isSequence(node)) {
			convertSeq(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isXor(node)) {
			convertXor(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isLoop(node)) {
			convertLoop(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isOr(node)) {
			convertOr(petriNet, tree, node, source, sink, node2transition);
		} else if (tree.isInterleaved(node)) {
			convertInterleaved(petriNet, tree, node, source, sink, node2transition);
		} else {
			throw new RuntimeException("not implemented");
		}
	}

	private static void convertTau(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		Transition t = petriNet.pn.addTransition("tau from tree");
		t.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(source, t));
		petriNet.arcs.add(PetrinetImplExt.createArc(t, sink));
		node2transition.put(node, t);
	}

	private static void convertTask(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		Transition t = petriNet.pn.addTransition(tree.getActivityName(node));
		petriNet.arcs.add(PetrinetImplExt.createArc(source, t));
		petriNet.arcs.add(PetrinetImplExt.createArc(t, sink));
		node2transition.put(node, t);
	}

	private static void convertXor(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		for (int child : tree.getChildren(node)) {
			convertNode(petriNet, tree, child, source, sink, node2transition);
		}
	}

	private static void convertSeq(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		int last = tree.getNumberOfChildren(node);
		int i = 0;
		Place lastSink = source;
		for (int child : tree.getChildren(node)) {
			Place childSink;
			if (i == last - 1) {
				childSink = sink;
			} else {
				childSink = petriNet.pn.addPlace("sink " + placeCounter.incrementAndGet());
			}

			convertNode(petriNet, tree, child, lastSink, childSink, node2transition);
			lastSink = childSink;
			i++;
		}
	}

	private static void convertAnd(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		//add split tau
		Transition t1 = petriNet.pn.addTransition("tau split");
		t1.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(source, t1));

		//add join tau
		Transition t2 = petriNet.pn.addTransition("tau join");
		t2.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(t2, sink));

		//add for each child a source and sink place
		for (int child : tree.getChildren(node)) {
			Place childSource = petriNet.pn.addPlace("source " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(t1, childSource));

			Place childSink = petriNet.pn.addPlace("sink " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(childSink, t2));

			convertNode(petriNet, tree, child, childSource, childSink, node2transition);
		}
	}

	private static void convertLoop(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		if (tree.getNumberOfChildren(node) != 3) {
			//a loop must have precisely three children: body, redo and exit
			throw new RuntimeException("A loop should have precisely three children");
		}

		Place middlePlace = petriNet.pn.addPlace("middle " + placeCounter.incrementAndGet());

		//add an extra tau
		Transition t = petriNet.pn.addTransition("tau start");
		t.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(source, t));
		//replace the source
		source = petriNet.pn.addPlace("replacement source " + placeCounter.incrementAndGet());
		petriNet.arcs.add(PetrinetImplExt.createArc(t, source));

		//body
		convertNode(petriNet, tree, tree.getChild(node, 0), source, middlePlace, node2transition);
		//redo
		convertNode(petriNet, tree, tree.getChild(node, 1), middlePlace, source, node2transition);
		//exit
		convertNode(petriNet, tree, tree.getChild(node, 2), middlePlace, sink, node2transition);
	}

	private static void convertOr(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {

		Transition start = petriNet.pn.addTransition("tau start");
		start.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(source, start));

		Place notDoneFirst = petriNet.pn.addPlace("notDoneFirst " + placeCounter.incrementAndGet());
		petriNet.arcs.add(PetrinetImplExt.createArc(start, notDoneFirst));

		Place doneFirst = petriNet.pn.addPlace("doneFirst " + placeCounter.incrementAndGet());
		Transition end = petriNet.pn.addTransition("tau finish");
		end.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(doneFirst, end));
		petriNet.arcs.add(PetrinetImplExt.createArc(end, sink));

		for (int child : tree.getChildren(node)) {
			Place childSource = petriNet.pn.addPlace("childSource " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(start, childSource));
			Place childSink = petriNet.pn.addPlace("childSink " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(childSink, end));
			Place doChild = petriNet.pn.addPlace("doChild " + placeCounter.incrementAndGet());

			//skip
			Transition skipChild = petriNet.pn.addTransition("tau skipChild");
			skipChild.setInvisible(true);
			petriNet.arcs.add(PetrinetImplExt.createArc(childSource, skipChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(skipChild, childSink));
			petriNet.arcs.add(PetrinetImplExt.createArc(skipChild, doneFirst));
			petriNet.arcs.add(PetrinetImplExt.createArc(doneFirst, skipChild));

			//first do
			Transition firstDoChild = petriNet.pn.addTransition("tau firstDoChild");
			firstDoChild.setInvisible(true);
			petriNet.arcs.add(PetrinetImplExt.createArc(childSource, firstDoChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(notDoneFirst, firstDoChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(firstDoChild, doneFirst));
			petriNet.arcs.add(PetrinetImplExt.createArc(firstDoChild, doChild));

			//later do
			Transition laterDoChild = petriNet.pn.addTransition("tau laterDoChild");
			laterDoChild.setInvisible(true);
			petriNet.arcs.add(PetrinetImplExt.createArc(childSource, laterDoChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(laterDoChild, doChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(laterDoChild, doneFirst));
			petriNet.arcs.add(PetrinetImplExt.createArc(doneFirst, laterDoChild));

			convertNode(petriNet, tree, child, doChild, childSink, node2transition);
		}
	}

	private static void convertInterleaved(PN petriNet, EfficientTree tree, int node, Place source, Place sink,
			TIntObjectMap<Transition> node2transition) {
		Transition start = petriNet.pn.addTransition("tau start");
		start.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(source, start));

		Place mileStone = petriNet.pn.addPlace("milestone place " + placeCounter.incrementAndGet());
		petriNet.arcs.add(PetrinetImplExt.createArc(start, mileStone));

		Transition end = petriNet.pn.addTransition("tau end");
		end.setInvisible(true);
		petriNet.arcs.add(PetrinetImplExt.createArc(mileStone, end));
		petriNet.arcs.add(PetrinetImplExt.createArc(end, sink));

		for (int child : tree.getChildren(node)) {
			Place childTodo = petriNet.pn.addPlace("child todo " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(start, childTodo));

			Transition startChild = petriNet.pn.addTransition("tau start child");
			startChild.setInvisible(true);
			petriNet.arcs.add(PetrinetImplExt.createArc(childTodo, startChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(mileStone, startChild));

			Place childSource = petriNet.pn.addPlace("child source " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(startChild, childSource));

			Place childSink = petriNet.pn.addPlace("child sink " + placeCounter.incrementAndGet());

			Transition endChild = petriNet.pn.addTransition("tau end child");
			endChild.setInvisible(true);
			petriNet.arcs.add(PetrinetImplExt.createArc(childSink, endChild));
			petriNet.arcs.add(PetrinetImplExt.createArc(endChild, mileStone));

			Place childDone = petriNet.pn.addPlace("child done " + placeCounter.incrementAndGet());
			petriNet.arcs.add(PetrinetImplExt.createArc(endChild, childDone));
			petriNet.arcs.add(PetrinetImplExt.createArc(childDone, end));

			convertNode(petriNet, tree, child, childSource, childSink, node2transition);
		}
	}
}