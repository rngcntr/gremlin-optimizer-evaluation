package de.rngcntr.gremlin.optimize.evaluation.graph.grid;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;

import java.io.*;

public class StatCollector {
    private static final String UPSERTS_LOCATION = "upserts2.csv";
    private static final String STATS_LOCATION = "collectedStats2.csv";

    public static void main(String[] args) throws IOException {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);
        JanusGraph graph = JanusGraphFactory.build()
                //.set("storage.backend", "inmemory")
                .set("storage.backend", "cql")
                .set("storage.cql.keyspace", "janusgraph_grid")
                .set("storage.hostname", "grid-storage-101.ptata.gdata.de")
                .set("index.search.hostname", "grid-index-001.ptata.gdata.de,grid-index-002.ptata.gdata.de,grid-index-003.ptata.gdata.de")
                .open();

        BufferedWriter bw = new BufferedWriter(new FileWriter(STATS_LOCATION));
        try (BufferedReader br = new BufferedReader(new FileReader(UPSERTS_LOCATION))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (values.length == 2) {
                    try {
                        collect(bw, graph.traversal(), Long.parseLong(values[0]), Long.parseLong(values[1]));
                    } catch (Exception ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void collect(BufferedWriter bw, GraphTraversalSource g, long fromVertex, long toVertex) throws IOException {
        if (!g.V(fromVertex).hasNext() || !g.V(toVertex).hasNext() || !g.V(fromVertex).values("Sha256").hasNext() || !g.V(toVertex).values("UrlString").hasNext()) {
            return;
        }
        Object sha256 = g.V(fromVertex).values("Sha256").next();
        Object urlString = g.V(toVertex).values("UrlString").next();
        Long forwardHourlies = g.V(fromVertex).outE("DownloadedFrom").count().next();
        Long backwardDailies = g.V(toVertex).inE("UrlDailyToUrlMeta").count().next();
        Long backwardHourlies = g.V(toVertex).in("UrlDailyToUrlMeta").inE("UrlHourlyToUrlDaily").count().next();
        Long backwardFiles = g.V(toVertex).in("UrlDailyToUrlMeta").in("UrlHourlyToUrlDaily").in().dedup().count().next();
        // FromID, ToID, ForwardHourlies, BackwardDailies, BackwardHourlies, BackwardFiles, Sha256, UrlString
        String output = String.format("%d,%d,%d,%d,%d,%d,%s,%s\n", fromVertex, toVertex, forwardHourlies, backwardDailies, backwardHourlies, backwardFiles, sha256, urlString);
        System.out.print(output);
        bw.write(output);
        bw.flush();
    }
}
