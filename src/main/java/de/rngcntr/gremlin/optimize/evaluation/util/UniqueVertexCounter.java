package de.rngcntr.gremlin.optimize.evaluation.util;

import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UniqueVertexCounter {

    private static Map<Integer, List<Integer>> seenVertices = new ConcurrentHashMap<>();

    public static void registerVertex(int stepInstance, Vertex vertex) {
        if (seenVertices.containsKey(stepInstance)) {
            seenVertices.get(stepInstance).add(vertex.id().hashCode());
        } else {
            List<Integer> vertices = new ArrayList<>();
            vertices.add(vertex.id().hashCode());
            seenVertices.put(stepInstance, vertices);
        }
    }

    public static Map<Integer, Set<Integer>> resetCount() {
        Map<Integer, Set<Integer>> returnValue = new HashMap<>();
        seenVertices.forEach((k, v) -> returnValue.put(k, new HashSet<>(v)));
        seenVertices = new ConcurrentHashMap<>();
        return returnValue;
    }
}
