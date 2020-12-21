package de.rngcntr.gremlin.optimize.evaluation.graph.airroutes;

import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.process.traversal.IO;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.Cardinality;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;
import org.janusgraph.core.schema.SchemaAction;
import org.janusgraph.core.schema.SchemaStatus;
import org.janusgraph.graphdb.database.management.ManagementSystem;

import java.util.concurrent.ExecutionException;

import static org.janusgraph.core.Multiplicity.MULTI;
import static org.janusgraph.core.Multiplicity.SIMPLE;

public class AirRoutesFactory {

    public static StatisticsProvider graphStatistics = new AirRoutesStatistics();

    public static void load(final JanusGraph graph) {
        try {
            makeSchema(graph);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        populateGraph(graph);
    }

    private static void makeSchema(final JanusGraph graph) throws ExecutionException, InterruptedException {
        ManagementSystem mgmt = (ManagementSystem) graph.openManagement();
        mgmt.makeEdgeLabel("route").multiplicity(MULTI).make();
        mgmt.makeEdgeLabel("contains").multiplicity(SIMPLE).make();
        mgmt.commit();

// Define vertex labels
        mgmt = (ManagementSystem) graph.openManagement();
        mgmt.makeVertexLabel("version").make();
        mgmt.makeVertexLabel("airport").make();
        mgmt.makeVertexLabel("country").make();
        mgmt.makeVertexLabel("continent").make();
        mgmt.commit();

// Define vertex property keys
        mgmt = (ManagementSystem) graph.openManagement();
        mgmt.makePropertyKey("code").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("icao").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("type").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("city").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("country").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("region").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("desc").dataType(String.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("runways").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("elev").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("lat").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        mgmt.makePropertyKey("lon").dataType(Double.class).cardinality(Cardinality.SINGLE).make();
        mgmt.commit();

// Define edge property keys
        mgmt = (ManagementSystem) graph.openManagement();
        mgmt.makePropertyKey("dist").dataType(Integer.class).cardinality(Cardinality.SINGLE).make();
        mgmt.commit();

// Construct a composite index for a few commonly used property keys
        graph.tx().rollback();
        mgmt=(ManagementSystem) graph.openManagement();

        JanusGraphManagement.IndexBuilder idx1 = mgmt.buildIndex("airportIndex", Vertex.class);
        JanusGraphManagement.IndexBuilder idx2 = mgmt.buildIndex("icaoIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx3 = mgmt.buildIndex("cityIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx4 = mgmt.buildIndex("runwayIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx5 = mgmt.buildIndex("countryIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx6 = mgmt.buildIndex("regionIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx7 = mgmt.buildIndex("typeIndex",Vertex.class);
        JanusGraphManagement.IndexBuilder idx8 = mgmt.buildIndex("distIndex",Edge.class);

        PropertyKey iata = mgmt.getPropertyKey("code");
        PropertyKey icao = mgmt.getPropertyKey("icao");
        PropertyKey city = mgmt.getPropertyKey("city");
        PropertyKey rway = mgmt.getPropertyKey("runways");
        PropertyKey ctry = mgmt.getPropertyKey("country");
        PropertyKey regn = mgmt.getPropertyKey("region");
        PropertyKey type = mgmt.getPropertyKey("type");
        PropertyKey dist = mgmt.getPropertyKey("dist");

        idx1.addKey(iata).buildCompositeIndex();
        idx2.addKey(icao).buildCompositeIndex();
        idx3.addKey(city).buildCompositeIndex();
        idx4.addKey(rway).buildCompositeIndex();
        idx5.addKey(ctry).buildCompositeIndex();
        idx6.addKey(regn).buildCompositeIndex();
        idx7.addKey(type).buildCompositeIndex();
        idx8.addKey(dist).buildCompositeIndex();

        mgmt.commit();

        ManagementSystem.awaitGraphIndexStatus(graph, "airportIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "icaoIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "cityIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "runwayIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "countryIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "regionIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "typeIndex").
                status(SchemaStatus.REGISTERED).call();

        ManagementSystem.awaitGraphIndexStatus(graph, "distIndex").
                status(SchemaStatus.REGISTERED).call();

// Once the index is created force a re-index Note that a reindex is not strictly
// necessary here. It could be avoided by creating the keys and index as part of the
// same transaction. I did it this way just to show an example of re-indexing being
// done. A reindex is always necessary if the index is added after data has been
// loaded into the graph.

        mgmt = (ManagementSystem) graph.openManagement();

        ManagementSystem.awaitGraphIndexStatus(graph, "airportIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("airportIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "icaoIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("icaoIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "cityIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("cityIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "runwayIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("runwayIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "countryIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("countryIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "regionIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("regionIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "typeIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("typeIndex"), SchemaAction.REINDEX).get();

        ManagementSystem.awaitGraphIndexStatus(graph, "distIndex").call();
        mgmt.updateIndex(mgmt.getGraphIndex("distIndex"), SchemaAction.REINDEX).get();

        mgmt.commit();
    }

    private static void populateGraph(final JanusGraph graph) {
        graph.traversal().io("air-routes.graphml").with(IO.reader, IO.graphml).read().iterate();
        graph.tx().commit();
    }
}
