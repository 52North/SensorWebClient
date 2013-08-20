
package org.n52.io.img;

import org.n52.io.IOFactory;
import org.n52.io.IOHandler;
import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.StyleProperties;
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

    /**
     * @param timeseriesStyles
     *        the style definitions for each timeseries.
     * @param timeseriesMetadatas
     *        the metadata for each timeseries.
     * @throws NullPointerException
     *         if any of the given arguments is <code>null</code>.
     * @throws IllegalStateException
     *         if amount of timeseries described by the given arguments is not in sync.
     * @return a rendering context to be used by {@link IOFactory} to create an {@link IOHandler}.
     */
    public static RenderingContext createContextWith(DesignedParameterSet timeseriesStyles,
                                                     TimeseriesMetadataOutput... timeseriesMetadatas) {
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

    public static RenderingContext createContextForSingleTimeseries(TimeseriesMetadataOutput metadata,
                                                                    StyleProperties style) {
        DesignedParameterSet parameters = new DesignedParameterSet();
        parameters.addTimeseriesWithStyleOptions(metadata.getId(), style);
        return RenderingContext.createContextWith(parameters, metadata);
    }

    public void setDimensions(int width, int height) {
        chartStyleDefinitions.setWidth(width);
        chartStyleDefinitions.setHeight(height);
    }

    public DesignedParameterSet getChartStyleDefinitions() {
        return chartStyleDefinitions;
    }

    public TimeseriesMetadataOutput[] getTimeseriesMetadatas() {
        return timeseriesMetadatas;
    }

}
