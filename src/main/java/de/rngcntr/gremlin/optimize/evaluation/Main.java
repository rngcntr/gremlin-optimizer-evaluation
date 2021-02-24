package de.rngcntr.gremlin.optimize.evaluation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.rngcntr.gremlin.optimize.evaluation.graph.airroutes.AirRoutesFactory;
import de.rngcntr.gremlin.optimize.evaluation.graph.airroutes.AirRoutesRunner;
import org.apache.tinkerpop.gremlin.tinkergraph.structure.TinkerGraph;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphFactory;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) {
        Logger root = (Logger)LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);
        JanusGraph jGraph = JanusGraphFactory.build()
                .set("storage.backend", "inmemory")
                //.set("storage.backend", "cql")
                //.set("storage.hostname", "capri")
                .open();
        AirRoutesFactory.load(jGraph);
        new AirRoutesRunner().run(jGraph);
        TinkerGraph tGraph = TinkerGraph.open();
        AirRoutesFactory.load(tGraph);
        new AirRoutesRunner().run(tGraph);
        System.exit(0);
    }
}
