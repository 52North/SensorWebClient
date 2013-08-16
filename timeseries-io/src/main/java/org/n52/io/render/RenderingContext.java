package org.n52.io.render;

import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.TimeseriesMetadataOutput;

public class RenderingContext {
    
    private DesignedParameterSet chartStyleDefinitions;
    
    private TimeseriesMetadataOutput[] timeseriesMetadatas;
    
    // use static constructors
    private RenderingContext(DesignedParameterSet timeseriesStyles, TimeseriesMetadataOutput[] timeseriesMetadatas) {
        this.timeseriesMetadatas = timeseriesMetadatas == null ? new TimeseriesMetadataOutput[0] : timeseriesMetadatas;
        this.chartStyleDefinitions = timeseriesStyles;
    }
    
    public static RenderingContext createEmpty() {
        return new RenderingContext(new DesignedParameterSet(), new TimeseriesMetadataOutput[0]);
    }
    
    public static RenderingContext createWith(DesignedParameterSet timeseriesStyles, TimeseriesMetadataOutput ... timeseriesMetadatas ) {
        if (timeseriesStyles == null || timeseriesMetadatas == null) {
            throw new NullPointerException("Designs and metadatas cannot be null.!");
        }
        String[] timeseriesIds = timeseriesStyles.getTimeseries();
        if (timeseriesIds.length != timeseriesMetadatas.length) {
            int amountTimeseries = timeseriesIds.length;
            int amountMetadatas = timeseriesMetadatas.length;
            StringBuilder sb = new StringBuilder();
            sb.append("Size of designs and metadatas do not match: ");
            sb.append("#Timeseries: ").append(amountTimeseries).append(" vs. ");
            sb.append("#Metadatas: ").append(amountMetadatas);
            throw new IllegalStateException(sb.toString());
        }
        return new RenderingContext(timeseriesStyles, timeseriesMetadatas);
    }

    public DesignedParameterSet getChartStyleDefinitions() {
        return chartStyleDefinitions;
    }

    public TimeseriesMetadataOutput[] getTimeseriesMetadatas() {
        return timeseriesMetadatas;
    }
    
}
