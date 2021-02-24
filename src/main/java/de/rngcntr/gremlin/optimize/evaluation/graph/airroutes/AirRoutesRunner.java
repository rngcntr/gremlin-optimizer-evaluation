package de.rngcntr.gremlin.optimize.evaluation.graph.airroutes;

import de.rngcntr.gremlin.optimize.evaluation.graph.Runner;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.janusgraph.core.JanusGraph;

import java.io.PrintStream;

public class AirRoutesRunner extends Runner {

    protected PrintStream getOutput() {
        return System.out;
    }

    public void run(Graph graph) {
        initializeStatistics(graph);
        GraphTraversalSource g = graph.traversal();


        for (int i = 0; i < 3; ++i) {
            /*

            // Q1
            // Countries on one stop routes between DTM and Larnaca

            optimizeAndEvaluateTraversal("Q1.1", g.V().hasLabel("airport").as("a")
                    .in("route").has("airport", "code", "DTM")
                    .select("a").out("route").has("airport", "city", "Larnaca")
                    .select("a").in("contains").hasLabel("country"), false);

            optimizeAndEvaluateTraversal("Q1.2", g.V().has("airport", "city", "Larnaca")
                    .in("route").hasLabel("airport").as("a")
                    .in("route").has("airport", "code", "DTM")
                    .select("a").in("contains").hasLabel("country"), false);

            optimizeAndEvaluateTraversal("Q1.3", g.V().has("airport", "code", "DTM")
                    .out("route").hasLabel("airport").as("a")
                    .out("route").has("airport", "city", "Larnaca")
                    .select("a").in("contains").hasLabel("country"), false);

            // Q2
            // All airports reachable in one stop range from DUS

            optimizeAndEvaluateTraversal("Q2.1", g.V().has("airport", "code", "DUS")
                    .out("route").hasLabel("airport")
                    .out("route").hasLabel("airport"), false);

            // Q3
            // All stops on three stop flights from Qaanaaq to the Copenhagen

            optimizeAndEvaluateTraversal("Q3.1", g.V().has("airport", "city", "Copenhagen")
                    .in("route").hasLabel("airport")
                    .in("route").hasLabel("airport")
                    .in("route").hasLabel("airport")
                    .in("route").has("airport", "city", "Qaanaaq"), false);

            optimizeAndEvaluateTraversal("Q3.2", g.V().has("airport", "city", "Qaanaaq")
                    .out("route").hasLabel("airport")
                    .out("route").hasLabel("airport")
                    .out("route").hasLabel("airport")
                    .out("route").has("airport", "city", "Copenhagen"), false);

            // Q4
            // Direct route from DUS to AMS

            optimizeAndEvaluateTraversal("Q4.1", g.V().has("airport", "code", "DUS")
                    .outE("route").as("r")
                    .inV().has("airport", "code", "AMS")
                    .select("r"), false);

            optimizeAndEvaluateTraversal("Q4.2", g.V().has("airport", "code", "AMS")
                    .inE("route").as("r")
                    .outV().has("airport", "code", "DUS")
                    .select("r"), false);

            optimizeAndEvaluateTraversal("Q4.3", g.V().has("airport", "code", "DUS").as("a")
                    .match(
                            __.as("a").outE("route").as("r"),
                            __.as("r").inV().has("airport", "code", "AMS")
                    )
                    .select("r"), false);

            optimizeAndEvaluateTraversal("Q4.4", g.V().has("airport", "code", "AMS").as("a")
                    .match(
                            __.as("a").inE("route").as("r"),
                            __.as("r").outV().has("airport", "code", "DUS")
                    )
                    .select("r"), false);

            // Q5
            // All airports in Germany

            optimizeAndEvaluateTraversal("Q5.1", g.V().hasLabel("airport").as("a")
                    .in("contains")
                    .has("country", "code", "DE")
                    .select("a"), false);

            optimizeAndEvaluateTraversal("Q5.2", g.V().has("country", "code", "DE")
                    .out("contains")
                    .hasLabel("airport"), false);
            */

            // Q6
            // Complete net, depth=1

            evaluateTraversal("Q6.0a", g.V().has("airport", "code", "DUS").out("route").out("route").out("route"), false);
            evaluateTraversal("Q6.0a", g.V().has("airport", "code", "DUS").out("route").as("a").out("route").out("route").select("a"), false);

            /*
            optimizeAndEvaluateTraversal("Q6.1", g.V().hasLabel("airport").as("a")
                    .out("route")
                    .hasLabel("airport")
                    .as("b")
                    .select("a", "b"), false);
             */

            System.out.println();
        }
    }
}
