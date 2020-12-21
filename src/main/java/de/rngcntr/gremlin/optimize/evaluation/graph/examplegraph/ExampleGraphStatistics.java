package de.rngcntr.gremlin.optimize.evaluation.graph.examplegraph;

import de.rngcntr.gremlin.optimize.filter.LabelFilter;
import de.rngcntr.gremlin.optimize.filter.PropertyFilter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExampleGraphStatistics implements StatisticsProvider {

    @Override
    public <E extends Element> double totals(Class<E> aClass) {
        if (aClass == Vertex.class) {
            return 20_300D;
        } else {
            return 110_000D;
        }
    }

    @Override
    public <E extends Element> double withLabel(LabelFilter<E> labelFilter) {
        switch (labelFilter.getLabel()) {
            case "country":
                return 200D;
            case "company":
                return 100;
            case "buys_at":
                return 90_000D;
            case "store":
            case "belongs_to":
            case "located_in":
            case "customer":
                return 10_000D;
            default:
                return 0D;
        }
    }

    @Override
    public <E extends Element> double withProperty(LabelFilter<E> labelFilter, PropertyFilter<E> propertyFilter) {
        if (propertyFilter.getKey().equals("name")) {
            if (labelFilter.getLabel().equals("customer")) {
                return 20D;
            } else if (labelFilter.getLabel().equals("company")) {
                return 1D;
            }
        }

        return 0D;
    }

    @Override
    public <E1 extends Element, E2 extends Element> double connections(LabelFilter<E1> inLabel, LabelFilter<E2> outLabel) {
        if (inLabel.getLabel().equals("located_in") || outLabel.getLabel().equals("located_in")) {
            return 10_000D;
        } else if (inLabel.getLabel().equals("belongs_to") || outLabel.getLabel().equals("belongs_to")) {
            return 10_000D;
        } else if (inLabel.getLabel().equals("buys_at") || outLabel.getLabel().equals("buys_at")) {
            return 90_000D;
        }
        return 0D;
    }
}
