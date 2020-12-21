package de.rngcntr.gremlin.optimize.evaluation.graph;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import de.rngcntr.gremlin.optimize.evaluation.strategy.UniqueElementCountStrategy;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueEdgeCounter;
import de.rngcntr.gremlin.optimize.evaluation.util.UniqueVertexCounter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import de.rngcntr.gremlin.optimize.structure.PatternGraph;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.janusgraph.core.JanusGraph;

import java.io.PrintStream;
import java.util.*;

public abstract class Runner {
    private static final int measuredRuns = 100;

    protected abstract StatisticsProvider getGraphStatistics();

    public abstract void run(JanusGraph graph, boolean init);

    protected abstract PrintStream getOutput();

    protected void optimizeAndEvaluateTraversal(String key, GraphTraversal<?, ?> traversal, boolean detailed) {
        GraphTraversal<?,?> unoptimizedTraversal = traversal.asAdmin().clone();
        if (detailed) { getOutput().println("Running optimized..."); }
        GraphTraversal<?,?>[] optimizedTraversal = new GraphTraversal[measuredRuns];
        long start = System.nanoTime();
        for (int i = 0; i < measuredRuns; ++i) {
            PatternGraph pg = new PatternGraph(traversal.asAdmin().clone());
            optimizedTraversal[i] = pg.optimize(getGraphStatistics());
        }
        long stop = System.nanoTime();
        consume(optimizedTraversal);
        if (detailed) { getOutput().printf("Optimization took:  % 8.2fms\n", (stop - start) / measuredRuns / 1_000_000f); }
        final List<Object> optimizedOutput = executeTraversal(key, optimizedTraversal[0], detailed, true, ((double) stop - start) / measuredRuns / 1_000);
        if (detailed) { getOutput().println("\n"); }

        if (detailed) { getOutput().println("Running unoptimized..."); }
        final List<Object> unoptimizedOutput = executeTraversal(key, unoptimizedTraversal, detailed, false, 0.0);

        ensureEquality(unoptimizedOutput, optimizedOutput);
    }

    protected void evaluateTraversal(String key, GraphTraversal<?,?> traversal, boolean detailed) {
        executeTraversal(key, traversal, detailed, false, 0.0D);
    }

    private List<Object> executeTraversal(String key, GraphTraversal<?,?> traversal, boolean detailed, boolean optimized, double optimizerTime) {
        GraphTraversal<?,?> decoratedTraversal = traversal.asAdmin().clone();
        final TraversalStrategies strategies = traversal.asAdmin().getStrategies().clone();
        strategies.addStrategies(UniqueElementCountStrategy.instance());
        decoratedTraversal.asAdmin().setStrategies(strategies);
        List<GraphTraversal<?,?>> measuredTraversals = new ArrayList<>();
        for (int i = 0; i < measuredRuns; ++i) {
            measuredTraversals.add(traversal.asAdmin().clone());
        }
        long start = System.nanoTime();
        for (GraphTraversal<?,?> measuredTraversal : measuredTraversals) {
            measuredTraversal.toList();
        }
        long stop = System.nanoTime();
        final List<Object> objects = (List<Object>) decoratedTraversal.toList();
        Map<Integer, Set<Integer>> vCount = UniqueVertexCounter.resetCount();
        Map<Integer, Set<Integer>> eCount = UniqueEdgeCounter.resetCount();
        if (detailed) {
            getOutput().printf("Traversed Vertices: % 5d %s\nTraversed Edges:    % 5d %s\nExecution took:     % 8.2fms\n",
                    accumulate(vCount), shortForm(vCount), accumulate(eCount), shortForm(eCount), (stop - start) / measuredRuns / 1_000_000f);
            getOutput().printf("Traversal was: %s\n", traversal.toString());
            getOutput().printf("With Decoration: %s\n", decoratedTraversal.toString());
            getOutput().printf("Result was: %s\n", objects);
        } else {
            getOutput().printf("RESULT query=%s vertices=%d edges=%d ms=%.2f optimized=%s optµs=%.0f\n",
                    key, accumulate(vCount), accumulate(eCount), (stop-start) / measuredRuns / 1_000_000f, optimized, optimizerTime);
        }

        return objects;
    }

    private Map<Integer, Integer> shortForm(Map<Integer, Set<Integer>> map) {
        Map<Integer, Integer> counts = new HashMap<>();
        map.forEach((k, v) -> counts.put(k, v.size()));
        return counts;
    }

    public static int accumulate(Map<Integer, Set<Integer>> map) {
        Set<Integer> totals = new HashSet<>();
        map.forEach((k, v) -> totals.addAll(v));
        return totals.size();
    }

    private void consume(GraphTraversal[] traversals) {
        int code = 0;
        for (GraphTraversal gt : traversals) {
            code += gt.hashCode();
        }
        System.out.print(code % 2 + 2);
        System.out.print('\b');
    }

    private void ensureEquality(List<Object> unoptimizedOutput, List<Object> optimizedOutput) {
        Multiset<Object> unoptimizedMultiset = HashMultiset.create();
        unoptimizedMultiset.addAll(unoptimizedOutput);
        Multiset<Object> optimizedMultiset = HashMultiset.create();
        optimizedMultiset.addAll(optimizedOutput);

        if (!unoptimizedMultiset.equals(optimizedMultiset)) {
            throw new RuntimeException("Non matching result found!");
        }
    }
}
