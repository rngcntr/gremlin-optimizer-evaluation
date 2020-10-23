package de.rngcntr.gremlin.optimize.evaluation;

import de.rngcntr.gremlin.optimize.filter.LabelFilter;
import de.rngcntr.gremlin.optimize.filter.PropertyFilter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class ExampleGraphStatistics implements StatisticsProvider {

    @Override
    public <E extends Element> long totals(Class<E> aClass) {
        if (aClass == Vertex.class) {
            return 20_300L;
        } else {
            return 210_000L;
        }
    }

    @Override
    public <E extends Element> long withLabel(LabelFilter<E> labelFilter) {
        switch (labelFilter.getLabel()) {
            case "country":
                return 200L;
            case "company":
                return 100;
            case "buys_at":
                return 190_000L;
            case "store":
            case "belongs_to":
            case "located_in":
            case "customer":
                return 10_000L;
            default:
                return 0L;
        }
    }

    @Override
    public <E extends Element> long withProperty(LabelFilter<E> labelFilter, PropertyFilter<E> propertyFilter) {
        if (propertyFilter.getKey().equals("name")) {
            if (labelFilter.getLabel().equals("customer")) {
                return 100L;
            } else if (labelFilter.getLabel().equals("company")) {
                return 1L;
            }
        }

        return 0L;
    }

    @Override
    public <E1 extends Element, E2 extends Element> long connections(LabelFilter<E1> inLabel, LabelFilter<E2> outLabel) {
        if (inLabel.getLabel().equals("located_in") || outLabel.getLabel().equals("located_in")) {
            return 10_000L;
        } else if (inLabel.getLabel().equals("belongs_to") || outLabel.getLabel().equals("belongs_to")) {
            return 10_000L;
        } else if (inLabel.getLabel().equals("buys_at") || outLabel.getLabel().equals("buys_at")) {
            return 190_000L;
        }
        return 0L;
    }
}
