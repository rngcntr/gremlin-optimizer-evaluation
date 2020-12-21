package de.rngcntr.gremlin.optimize.evaluation.graph.grid;

import de.rngcntr.gremlin.optimize.evaluation.graph.Runner;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;

import java.io.*;

public class GridRunner extends Runner {

    private final String INPUT_LOCATION = "collectedStats.csv";
    private final String OUTPUT_LOCATION = "measurements_ignore_edges.csv";

    private PrintStream outputStream;

    @Override
    protected PrintStream getOutput() {
        return outputStream;
    }

    public void run(JanusGraph graph, boolean init) {
        GraphTraversalSource g = graph.traversal();

        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_LOCATION))) {
            outputStream = new PrintStream(OUTPUT_LOCATION);
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                runTraversals(g, values[0], values[7], values[8]);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void runTraversals(GraphTraversalSource g, String key, String sha, String url) {
        optimizeAndEvaluateTraversal("Q" + key + "B", g.V().has("UrlMeta", "UrlString", url)
                .in("UrlDailyToUrlMeta").hasLabel("UrlDaily")
                .in("UrlHourlyToUrlDaily").hasLabel("UrlHourly")
                .in("DownloadedFrom")
                .has("File", "Sha256", sha), false);
    }

    @Override
    protected StatisticsProvider getGraphStatistics() {
        return new GridStatistics();
    }
}
