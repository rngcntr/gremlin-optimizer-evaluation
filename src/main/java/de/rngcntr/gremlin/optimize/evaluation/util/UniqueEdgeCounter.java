package de.rngcntr.gremlin.optimize.evaluation.util;

import org.apache.tinkerpop.gremlin.structure.Edge;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class UniqueEdgeCounter {

    private static Map<Integer, List<Integer>> seenEdges = new ConcurrentHashMap<>();

    public static void registerEdge(int stepInstance, Edge edge) {
        if (seenEdges.containsKey(stepInstance)) {
            seenEdges.get(stepInstance).add(edge.id().hashCode());
        } else {
            List<Integer> edges = new ArrayList<>();
            edges.add(edge.id().hashCode());
            seenEdges.put(stepInstance, edges);
        }
    }

    public static Map<Integer, Set<Integer>> resetCount() {
        Map<Integer, Set<Integer>> returnValue = new HashMap<>();
        seenEdges.forEach((k, v) -> returnValue.put(k, new HashSet<>(v)));
        seenEdges = new ConcurrentHashMap<>();
        return returnValue;
    }
}