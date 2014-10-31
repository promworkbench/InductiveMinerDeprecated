package org.processmining.plugins.InductiveMiner.graphs;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class ConnectedComponents<V> {
	private boolean[] marked; // marked[v] = has vertex v been marked?
	private int[] id; // id[v] = id of connected component containing v
	private int[] size; // size[id] = number of vertices in given component
	private int count;

	/**
	 * Returns the connected components of G.
	 * 
	 * @param G
	 * @return
	 */
	public static <Y> Set<Set<Y>> compute(Graph<Y> G) {
		ConnectedComponents<Y> cc = new ConnectedComponents<>(G);
		return cc.getResult(G);
	}

	private Set<Set<V>> getResult(Graph<V> G) {
		// compute list of vertices in each strong component
		@SuppressWarnings("unchecked")
		Set<V>[] components = new Set[count];
		for (int i = 0; i < count; i++) {
			components[i] = new HashSet<>();
		}
		for (int v = 0; v < G.getNumberOfVertices(); v++) {
			int component = id[v];
			components[component].add(G.getVertexOfIndex(v));
			//	        	components[component] = Arrays.copyOf(components[component], components[component].length + 1);
			//	        	components[component][components[component].length - 1] = G.getVertexOfIndex(v);
		}

		// print results
		//	        for (int i = 0; i < count; i++) {
		//	            for (X v : components[i]) {
		//	            	System.out.print(v + " ");
		//	            }
		//	            System.out.println();
		//	        }
		return new HashSet<Set<V>>(Arrays.asList(components));
	}

	private ConnectedComponents(Graph<V> G) {
		marked = new boolean[G.getNumberOfVertices()];
		id = new int[G.getNumberOfVertices()];
		size = new int[G.getNumberOfVertices()];
		for (int v = 0; v < G.getNumberOfVertices(); v++) {
			if (!marked[v]) {
				dfs(G, v);
				count++;
			}
		}
	}

	// depth-first search
	private void dfs(Graph<V> G, int v) {
		marked[v] = true;
		id[v] = count;
		size[count]++;
		for (int w = 0; w < G.getNumberOfVertices(); w++) {
			if (w != v && (G.getEdgesArray()[w][v] > 0 || G.getEdgesArray()[v][w] > 0)) {
				if (!marked[w]) {
					dfs(G, w);
				}
			}
		}
	}
}