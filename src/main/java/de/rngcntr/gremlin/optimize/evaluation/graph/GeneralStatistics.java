package de.rngcntr.gremlin.optimize.evaluation.graph;

import de.rngcntr.gremlin.optimize.filter.LabelFilter;
import de.rngcntr.gremlin.optimize.filter.PropertyFilter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.*;

import java.util.*;

public class GeneralStatistics implements StatisticsProvider {

    Map<Class<? extends Element>, Integer> totals = new HashMap<>();
    Map<String, Integer> labels = new HashMap<>();
    Map<String, Map<String, Set<Object>>> properties = new HashMap<>();
    Map<String, Map<String, Integer>> connections = new HashMap<>();

    public GeneralStatistics(Graph graph) {
        final GraphTraversalSource g = graph.traversal();
        for (Vertex vertex : g.V().toList()) {
            totals.compute(Vertex.class, (k,v) -> v == null ? 1 : v + 1);
            labels.compute(vertex.label(), (k,v) -> v == null ? 1 : v + 1);

            // vertex properties
            final Map<String, Set<Object>> propertyMap = properties.computeIfAbsent(vertex.label(), k -> new HashMap<>());
            for (Iterator<VertexProperty<Object>> it = vertex.properties(); it.hasNext(); ) {
                Property<?> property = it.next();
                propertyMap.computeIfAbsent(property.key(), k -> new HashSet<>());
                propertyMap.get(property.key()).add(property.value());
            }
        }
        for (Edge edge : g.E().toList()) {
            totals.compute(Edge.class, (k,v) -> v == null ? 1 : v + 1);
            labels.compute(edge.label(), (k,v) -> v == null ? 1 : v + 1);

            // edge properties
            final Map<String, Set<Object>> propertyMap = properties.computeIfAbsent(edge.label(), k -> new HashMap<>());
            for (Iterator<Property<Object>> it = edge.properties(); it.hasNext(); ) {
                Property<?> property = it.next();
                propertyMap.computeIfAbsent(property.key(), k -> new HashSet<>());
                propertyMap.get(property.key()).add(property.value());
            }

            // edge to vertex
            Map<String, Integer> connectionMap = connections.computeIfAbsent(edge.label(), k -> new HashMap<>());
            connectionMap.compute(edge.inVertex().label(), (k,v) -> v == null ? 1 : v + 1);

            // vertex to edge
            connectionMap = connections.computeIfAbsent(edge.outVertex().label(), k -> new HashMap<>());
            connectionMap.compute(edge.label(), (k,v) -> v == null ? 1 : v + 1);
        }
    }

    @Override
    public <E extends Element> double totals(Class<E> aClass) {
        return totals.getOrDefault(aClass, 0);
    }

    @Override
    public <E extends Element> double withLabel(LabelFilter<E> labelFilter) {
        return labels.getOrDefault(labelFilter.getLabel(), 0);
    }

    @Override
    public <E extends Element> double withProperty(LabelFilter<E> labelFilter, PropertyFilter<E> propertyFilter) {
        return withLabel(labelFilter) / properties.getOrDefault(labelFilter.getLabel(), new HashMap<>()).getOrDefault(propertyFilter.getKey(), new HashSet<>()).size();
    }

    @Override
    public <E1 extends Element, E2 extends Element> double connections(LabelFilter<E1> startLabel, LabelFilter<E2> endLabel) {
        return connections.getOrDefault(startLabel.getLabel(), new HashMap<>()).getOrDefault(endLabel.getLabel(), 0);
    }
}
