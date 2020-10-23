package de.rngcntr.gremlin.optimize.evaluation.util;

import org.apache.tinkerpop.gremlin.structure.Edge;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class UniqueEdgeCounter {

    private static Map<Integer, Set<Integer>> seenEdges = new ConcurrentHashMap<>();

    public static void registerEdge(int stepInstance, Edge edge) {
        if (seenEdges.containsKey(stepInstance)) {
            seenEdges.get(stepInstance).add(edge.id().hashCode());
        } else {
            Set<Integer> set = new ConcurrentSkipListSet<>();
            set.add(edge.id().hashCode());
            seenEdges.put(stepInstance, set);
        }
    }

    public static Map<Integer, Set<Integer>> resetCount() {
        Map<Integer, Set<Integer>> returnValue = seenEdges;
        seenEdges = new ConcurrentHashMap<>();
        return returnValue;
    }
}