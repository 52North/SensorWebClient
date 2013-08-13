package org.n52.io.render;

import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.TimeseriesMetadata;

public class RenderingContext {
    
    private DesignedParameterSet styledTimeseries;
    
    private TimeseriesMetadata[] timeseriesMetadatas;
    
    // use static constructors
    private RenderingContext(DesignedParameterSet timeseriesStyles) {
        timeseriesMetadatas = new TimeseriesMetadata[0];
    }
    
    public static RenderingContext createWith(DesignedParameterSet timeseriesStyles, TimeseriesMetadata ... timeseriesMetadatas ) {
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
        RenderingContext context = new RenderingContext(timeseriesStyles);
        context.timeseriesMetadatas = timeseriesMetadatas;
        return context;
    }

    public DesignedParameterSet getStyledTimeseries() {
        return styledTimeseries;
    }

    public TimeseriesMetadata[] getTimeseriesMetadatas() {
        return timeseriesMetadatas;
    }
    
}
