package de.rngcntr.gremlin.optimize.evaluation;

import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.janusgraph.core.JanusGraph;
import org.janusgraph.core.JanusGraphTransaction;
import org.janusgraph.core.PropertyKey;
import org.janusgraph.core.schema.JanusGraphManagement;

public class ExampleGraphFactory {

    public static StatisticsProvider graphStatistics = new ExampleGraphStatistics();

    public static void load(final JanusGraph graph) {
        makeSchema(graph);
        populateGraph(graph);
    }

    private static void makeSchema(final JanusGraph graph) {
        JanusGraphManagement mgmt = graph.openManagement();

        final PropertyKey name = mgmt.makePropertyKey("name").dataType(String.class).make();

        mgmt.buildIndex("customerByName", Vertex.class).addKey(name).buildCompositeIndex();
        mgmt.buildIndex("companyByName", Vertex.class).addKey(name).buildCompositeIndex();

        mgmt.makeVertexLabel("country").make();
        mgmt.makeVertexLabel("store").make();
        mgmt.makeVertexLabel("customer").make();
        mgmt.makeVertexLabel("company").make();

        mgmt.makeEdgeLabel("buys_at").make();
        mgmt.makeEdgeLabel("belongs_to").make();
        mgmt.makeEdgeLabel("located_in").make();

        mgmt.commit();
    }

    private static void populateGraph(final JanusGraph graph) {
        JanusGraphTransaction tx = graph.newTransaction();

        Vertex[] countries = new Vertex[200];
        for (int i = 0; i < countries.length; i++) {
            countries[i] = addVertex(tx, "country", "name", "country" + i);
        }

        Vertex[] stores = new Vertex[10_000];
        for (int i = 0; i < stores.length; i++) {
            stores[i] = addVertex(tx, "store", "name", "store" + i);
        }

        Vertex[] customers = new Vertex[10_000];
        for (int i = 0; i < customers.length; i++) {
            if (i % 100 == 0) {
                customers[i] = addVertex(tx, "customer", "name", "Bob");
            } else {
                customers[i] = addVertex(tx, "customer", "name", "Alice");
            }
        }

        Vertex[] companies = new Vertex[100];
        companies[0] = addVertex(tx, "company", "name", "Apple");
        for (int i = 1; i < companies.length; i++) {
            companies[i] = addVertex(tx, "company", "name", "Google" + i);
        }

        addEdges(stores, countries, 1, "located_in");
        addEdges(customers, stores, 19, "buys_at");
        addEdges(stores, companies, 1, "belongs_to");

        tx.commit();
    }

    private static void addEdges(Vertex[] as, Vertex[] bs, int bsPerA, String label) {
        int nextB = 0;
        for (Vertex a : as) {
            int currentB;
            for (currentB = nextB; currentB < nextB + bsPerA; ++currentB) {
                a.addEdge(label, bs[currentB % bs.length]);
            }
            nextB = currentB;
        }
    }

    private static Vertex addVertex(JanusGraphTransaction tx, String label, String propKey, String propVal) {
        return propKey != null
                ? tx.addVertex(T.label, label, propKey, propVal)
                : tx.addVertex(T.label, label);
    }

    private static Edge addEdge(Vertex a, Vertex b, String label) {
        return a.addEdge(label, b);
    }
}