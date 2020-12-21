package de.rngcntr.gremlin.optimize.evaluation.strategy;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

public class UniqueElementCountStrategyJanusGraphTest extends UniqueElementCountStrategyTest {
    @Override
    @BeforeEach
    public void initializeGraph() {
        JanusGraph graph = JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                .open();
        GraphOfTheGodsFactory.loadWithoutMixedIndex(graph, true);
        g = graph.traversal();
    }

    private static Stream<Arguments> testedTraversals() {
        return Stream.of(
                Arguments.of(12, 0, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()),
                Arguments.of(1, 0, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")),
                Arguments.of(1, 5, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .outE()),
                Arguments.of(1, 5, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .out()),
                Arguments.of(1, 3, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .out("battled")),
                Arguments.of(1, 3, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .outE("battled")
                                .has("time", P.gt(10))
                                .inV()),
                Arguments.of(2, 3, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .outE("battled")
                                .has("time", P.gt(10))
                                .inV()
                                .in("battled")),
                Arguments.of(1, 3, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V().has("name", "hercules")
                                .match(__.as("a").outE("battled").has("time", P.gt(10)).as("b"))
                                .select("b")
                                .inV())
        );
    }
}
