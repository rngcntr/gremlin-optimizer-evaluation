package de.rngcntr.gremlin.optimize.evaluation.graph.grid;

import de.rngcntr.gremlin.optimize.filter.LabelFilter;
import de.rngcntr.gremlin.optimize.filter.PropertyFilter;
import de.rngcntr.gremlin.optimize.statistics.StatisticsProvider;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class GridStatistics implements StatisticsProvider {

    @Override
    public <E extends Element> double totals(Class<E> aClass) {
        if (aClass == Vertex.class) {
            return 1_000_000D;
        } else {
            return 100_000_000D;
        }
    }

    @Override
    public <E extends Element> double withLabel(LabelFilter<E> labelFilter) {
        switch (labelFilter.getLabel()) {
            case "File":
                return 3_342D;
            case "UrlMeta":
                return 835_500D;
            case "UrlHourly":
            case "UrlHourlyToUrlDaily":
                return 835_500L * 27D;
            case "UrlDaily":
            case "UrlDailyToUrlMeta":
                return 835_500L * 22D;
            case "DownloadedFrom":
                return 102_435_479D;
            default:
                System.out.println("Unexpected Label Request");
                return 0D;
        }
    }

    @Override
    public <E extends Element> double withProperty(LabelFilter<E> labelFilter, PropertyFilter<E> propertyFilter) {
        if (propertyFilter.getKey().equals("UrlString") || propertyFilter.getKey().equals("Sha256")) {
            return 1D;
        }
        System.out.println("Unexpected Property Request");
        return 0D;
    }

    @Override
    public <E1 extends Element, E2 extends Element> double connections(LabelFilter<E1> inLabel, LabelFilter<E2> outLabel) {
        switch (inLabel.getLabel() + "->" + outLabel.getLabel()) {
            case "File->DownloadedFrom":
                return 35_479D; // imaginary value
            case "DownloadedFrom->UrlHourly":
                return 102_435_479D;
            case "UrlHourly->UrlHourlyToUrlDaily":
            case "UrlHourlyToUrlDaily->UrlDaily":
                return 835_500L * 27D;
            case "UrlDaily->UrlDailyToUrlMeta":
            case "UrlDailyToUrlMeta->UrlMeta":
                return 835_500L * 22D;
            default:
                System.out.println("Unexpected Connection Request");
                return 0D;
        }
    }
}
