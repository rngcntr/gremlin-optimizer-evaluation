package de.rngcntr.gremlin.optimize.evaluation.strategy;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.provider.Arguments;

import java.util.function.Function;
import java.util.stream.Stream;

public class UniqueElementCountStrategyTinkerGraphTest extends UniqueElementCountStrategyTest {
    @Override
    @BeforeEach
    public void initializeGraph() {
        Graph graph = TinkerFactory.createModern();
        g = graph.traversal();
    }

    private static Stream<Arguments> testedTraversals() {
        return Stream.of(
                Arguments.of(6, 0, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()),
                Arguments.of(6, 0, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .has("name", "marko")),
                Arguments.of(6, 0, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person")),
                Arguments.of(6, 2, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person").has("name", "josh")
                                .outE("created")),
                Arguments.of(6, 4, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person")
                                .outE("created").has("weight", 1.0)),
                Arguments.of(6, 4, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person")
                                .out("created")),
                Arguments.of(6, 4, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person").has("name", "josh")
                                .out("created")
                                .hasLabel("software")
                                .in("created")),
                Arguments.of(6, 2, (Function<GraphTraversalSource, GraphTraversal<?,?>>) g ->
                        g.V()
                                .hasLabel("person").has("name", "vadas")
                                .in("knows")
                                .out("created"))
        );
    }
}
