/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.io.img;

import static org.n52.io.img.LineRenderer.createStyledLineRenderer;
import static org.n52.io.style.LineStyle.createLineStyle;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import org.jfree.data.general.DatasetGroup;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.n52.io.style.LineStyle;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesData;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesValue;

public class MultipleChartsRenderer extends ChartRenderer {

    public MultipleChartsRenderer(RenderingContext context, String language) {
        super(context, language);
    }

    @Override
    public void generateOutput(TimeseriesDataCollection data) {
        Map<String, TimeseriesData> allTimeseries = data.getAllTimeseries();
        TimeseriesMetadataOutput[] timeseriesMetadatas = getTimeseriesMetadataOutputs();
        for (int rendererIndex = 0; rendererIndex < timeseriesMetadatas.length; rendererIndex++) {
            
            /*
             * For each index put data and a corresponding renderer specific to a style
             */
            
            TimeseriesMetadataOutput timeseriesMetadata = timeseriesMetadatas[rendererIndex];
            String timeseriesLabel = createTimeseriesLabel(timeseriesMetadata);
            TimeseriesData timeseriesData = allTimeseries.get(timeseriesMetadata.getId());
            putDataAtIndex(rendererIndex, timeseriesData, timeseriesLabel);
            putRendererAtIndex(rendererIndex, timeseriesMetadata.getId());
            configureRangeAxis(timeseriesMetadata, rendererIndex);
        }
    }

    private String createTimeseriesLabel(TimeseriesMetadataOutput metadata) {
        FeatureOutput feature = metadata.getParameters().getFeature();
        StringBuilder timeseriesLabel = new StringBuilder();
        timeseriesLabel.append(feature.getLabel());
        timeseriesLabel.append(" (").append(metadata.getId()).append(")");
        return timeseriesLabel.toString();
    }

    private void putRendererAtIndex(int rendererIndex, String timeseriesId) {

        /*
         * As each timeseries may define its custom styling and different chart types we have to loop over all
         * styles and process chart rendering iteratively
         */

        StyleProperties properties = getTimeseriesStyleFor(timeseriesId);
        if (isLineStyle(properties)) {
            // do line chart rendering
            LineStyle lineStyle = createLineStyle(properties);
            LineRenderer lineRenderer = createStyledLineRenderer(lineStyle);
            getXYPlot().setRenderer(rendererIndex, lineRenderer.getXYRenderer());
            lineRenderer.setColorForSeriesAt(rendererIndex);
        }
        else if (isBarStyle(properties)) {
            
            // TODO do bar chart rendering

        }
    }

    private void putDataAtIndex(int rendererIndex, TimeseriesData timeseriesData, String dataId) {
        TimeseriesData sortedData = sortTimeseriesData(timeseriesData);
        getXYPlot().setDataset(rendererIndex, createTimeseriesCollection(dataId, sortedData));
    }

    private TimeSeriesCollection createTimeseriesCollection(String dataId, TimeseriesData sortedData) {
        TimeSeriesCollection timeseriesCollection = new TimeSeriesCollection();
        timeseriesCollection.addSeries(createTimeseriesToRender(dataId, sortedData));
        timeseriesCollection.setGroup(new DatasetGroup(dataId));
        return timeseriesCollection;
    }

    private TimeSeries createTimeseriesToRender(String dataId, TimeseriesData timeseriesData) {
        TimeSeries timeseries = new TimeSeries(dataId);
        for (TimeseriesValue value : timeseriesData.getValues()) {
            timeseries.add(new Second(new Date(value.getTimestamp())), value.getValue());
        }
        return timeseries;
    }

    private TimeseriesData sortTimeseriesData(TimeseriesData timeseriesData) {
        Arrays.sort(timeseriesData.getValues());
        return timeseriesData;
    }

}
