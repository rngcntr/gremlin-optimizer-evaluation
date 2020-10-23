package de.rngcntr.gremlin.optimize.evaluation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.rngcntr.gremlin.optimize.evaluation.strategy.UniqueElementCountStrategy;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueEdgeCounter;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueVertexCounter;
import de.rngcntr.gremlin.optimize.structure.PatternGraph;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.janusgraph.graphdb.tinkerpop.optimize.JanusGraphStepStrategy;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Main {

    public static void main(String[] args) {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);
        JanusGraph graph = JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                .open();
        ExampleGraphFactory.load(graph);
        GraphTraversalSource g = graph.traversal();

        optimizeAndEvaluateTraversal(g.V());

        optimizeAndEvaluateTraversal(g.V().hasLabel("store"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store")
                .out("belongs_to").hasLabel("company"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store")
                .out("belongs_to").hasLabel("company").has("name", "Apple"));

        optimizeAndEvaluateTraversal(g.V().has("customer", "name", "Bob")
                .out("buys_at").hasLabel("store")
                .where(__.out("belongs_to").has("company", "name", "Apple"))
                .out("located_in").hasLabel("country"));

        optimizeAndEvaluateTraversal(g.V().has("company", "name", "Apple")
                .in("belongs_to").hasLabel("store")
                .where(__.in("buys_at").has("customer", "name", "Bob"))
                .out("located_in").hasLabel("country"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store")
                .where(__.out("belongs_to").has("company", "name", "Apple"))
                .where(__.in("buys_at").has("customer", "name", "Bob"))
                .out("located_in").hasLabel("country"));
        System.exit(0);
    }

    private static void optimizeAndEvaluateTraversal(GraphTraversal<?,?> traversal) {
        GraphTraversal<?,?> unoptimizedTraversal = traversal.asAdmin().clone();
        System.out.println("Running unoptimized...");
        executeTraversal(unoptimizedTraversal);

        System.out.println("Running optimized...");
        long start = System.nanoTime();
        PatternGraph pg = new PatternGraph(traversal.asAdmin().clone());
        GraphTraversal<?,?> optimizedTraversal = pg.optimize(ExampleGraphFactory.graphStatistics);
        long stop = System.nanoTime();
        System.out.printf("Optimization took   %dms\n", (stop - start) / 1_000_000L);
        executeTraversal(optimizedTraversal);
        System.out.println("\n");
    }

    private static void executeTraversal(GraphTraversal<?,?> traversal) {
        GraphTraversal<?,?> decoratedTraversal = traversal.asAdmin().clone();
        TraversalStrategies strategies = TraversalStrategies.GlobalCache.getStrategies(Graph.class);
        strategies.addStrategies(JanusGraphStepStrategy.instance(), UniqueElementCountStrategy.instance());
        decoratedTraversal.asAdmin().setStrategies(strategies);
        decoratedTraversal.toList();
        Map<Integer, Set<Integer>> vCount = UniqueVertexCounter.resetCount();
        Map<Integer, Set<Integer>> eCount = UniqueEdgeCounter.resetCount();
        long start = System.nanoTime();
        traversal.toList();
        long stop = System.nanoTime();
        System.out.printf("Traversed Vertices: %5d %s\nTraversed Edges:    %5d %s\nExecution took:     %dms\n",
                accumulate(vCount), shortForm(vCount), accumulate(eCount), shortForm(eCount), (stop - start) / 1_000_000L);
        System.out.printf("Traversal was: %s\n", traversal.toString());
        System.out.printf("With Decoration: %s\n", decoratedTraversal.toString());
    }

    private static Map<Integer, Integer> shortForm(Map<Integer, Set<Integer>> map) {
        Map<Integer, Integer> counts = new HashMap<>();
        map.forEach((k, v) -> counts.put(k, v.size()));
        return counts;
    }
    private static int accumulate(Map<Integer, Set<Integer>> map) {
        Set<Integer> totals = new HashSet<>();
        map.forEach((k, v) -> totals.addAll(v));
        return totals.size();
    }
}
