package de.rngcntr.gremlin.optimize.evaluation.util;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class UniqueVertexCounter {

    private static Map<Integer, Set<Integer>> seenVertices = new ConcurrentHashMap<>();

    public static void registerVertex(int stepInstance, Vertex vertex) {
        if (seenVertices.containsKey(stepInstance)) {
            seenVertices.get(stepInstance).add(vertex.id().hashCode());
        } else {
            Set<Integer> set = new ConcurrentSkipListSet<>();
            set.add(vertex.id().hashCode());
            seenVertices.put(stepInstance, set);
        }
    }

    public static Map<Integer, Set<Integer>> resetCount() {
        Map<Integer, Set<Integer>> returnValue = seenVertices;
        seenVertices = new ConcurrentHashMap<>();
        return returnValue;
    }
}
