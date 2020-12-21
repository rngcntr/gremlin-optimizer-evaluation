package de.rngcntr.gremlin.optimize.evaluation.graph.examplegraph;

import de.rngcntr.gremlin.optimize.evaluation.graph.Runner;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.janusgraph.core.JanusGraph;

import java.io.PrintStream;

public class ExampleGraphRunner extends Runner {

    @Override
    protected PrintStream getOutput() {
        return System.out;
    }

    public void run(JanusGraph graph, boolean init) {
        if (init) {
            ExampleGraphFactory.load(graph);
        }
        GraphTraversalSource g = graph.traversal();

        /*
        optimizeAndEvaluateTraversal(g.V());

        optimizeAndEvaluateTraversal(g.V().hasLabel("store"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store")
                .out("belongs_to").hasLabel("company"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store")
                .out("belongs_to").hasLabel("company").has("name", "Apple"));

        optimizeAndEvaluateTraversal(g.V().has("customer", "name", "Bob").as("b")
                .match(
                        __.as("b").out("buys_at").hasLabel("store").as("s"),
                        __.as("s").out("belongs_to").has("company", "name", "Apple").as("a"),
                        __.as("s").out("located_in").hasLabel("country").as("c")
                )
                .select("b", "a", "c"));

        optimizeAndEvaluateTraversal(g.V().has("customer", "name", "Bob").as("b")
                .out("buys_at").hasLabel("store").as("s")
                .out("belongs_to").has("company", "name", "Apple").as("a")
                .select("s").out("located_in").hasLabel("country").as("c")
                .select("b", "a", "c"));

        optimizeAndEvaluateTraversal(g.V().has("customer", "name", "Bob")
                .out("buys_at").hasLabel("store").as("s")
                .out("located_in").hasLabel("country").as("c")
                .select("s").out("belongs_to").has("company", "name", "Apple")
                .select("c"));

        optimizeAndEvaluateTraversal(g.V().has("customer", "name", "Bob").as("b")
                .match(
                        __.as("b").out("buys_at").hasLabel("store").as("s"),
                        __.as("s").out("belongs_to").has("company", "name", "Apple").as("a")
                )
                .select("s").out("located_in").hasLabel("country").as("c")
                .select("b", "a", "c"));

        optimizeAndEvaluateTraversal(g.V().has("company", "name", "Apple")
                .in("belongs_to").hasLabel("store").as("s")
                .in("buys_at").has("customer", "name", "Bob")
                .select("s").out("located_in").hasLabel("country"));

        optimizeAndEvaluateTraversal(g.V().has("company", "name", "Apple")
                .in("belongs_to").hasLabel("store").as("s")
                .out("located_in").hasLabel("country").as("c")
                .select("s").in("buys_at").has("customer", "name", "Bob")
                .select("c"));

        optimizeAndEvaluateTraversal(g.V().hasLabel("store").as("s")
                .out("belongs_to").has("company", "name", "Apple")
                .select("s").in("buys_at").has("customer", "name", "Bob")
                .select("s").out("located_in").hasLabel("country"));
         */

    }

    @Override
    protected StatisticsProvider getGraphStatistics() {
        return new ExampleGraphStatistics();
    }
}
