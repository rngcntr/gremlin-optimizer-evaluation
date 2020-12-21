package de.rngcntr.gremlin.optimize.evaluation.graph.airroutes;

import de.rngcntr.gremlin.optimize.filter.LabelFilter;
import de.rngcntr.gremlin.optimize.filter.PropertyFilter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class AirRoutesStatistics implements StatisticsProvider {

    @Override
    public <E extends Element> double totals(Class<E> aClass) {
        if (aClass == Vertex.class) {
            return 3_619D;
        } else {
            return 50_148D;
        }
    }

    @Override
    public <E extends Element> double withLabel(LabelFilter<E> labelFilter) {
        switch (labelFilter.getLabel()) {
            case "country":
                return 237D;
            case "continent":
                return 7D;
            case "airport":
                return 3_374D;
            case "version":
                return 1D;
            case "route":
                return 43_400D;
            case "contains":
                return 6_748D;
            default:
                return 0D;
        }
    }

    @Override
    public <E extends Element> double withProperty(LabelFilter<E> labelFilter, PropertyFilter<E> propertyFilter) {
        if (propertyFilter.getKey().equals("code")) {
            return 1D;
        }
        if (propertyFilter.getKey().equals("city")) {
            return 1.04D;
        }

        return 0D;
    }

    @Override
    public <E1 extends Element, E2 extends Element> double connections(LabelFilter<E1> inLabel, LabelFilter<E2> outLabel) {
        switch (inLabel.getLabel() + "->" + outLabel.getLabel()) {
            case "airport->route":
            case "route->airport":
                return 43_400D;
            case "continent->contains":
            case "country->contains":
                return 3_374D;
            case "contains->airport":
                return 6_748D;
            default:
                return 0D;
        }
    }
}
