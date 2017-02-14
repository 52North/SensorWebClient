/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.series.api.proxy.v0.srv;

import java.io.OutputStream;
import java.util.ArrayList;

import javax.servlet.ServletOutputStream;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.StandardEntityCollection;
import org.n52.series.api.proxy.v0.io.DesignedParameterSet;
import org.n52.series.api.proxy.v0.io.ParameterSet;
import org.n52.series.api.proxy.v0.out.ImageDataResult;
import org.n52.client.service.EESDataService;
import org.n52.server.io.EESGenerator;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.web.InternalServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetImageService extends DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetImageService.class);

    private EESDataService imageDataService;

    private int defaultWidth;

    private int defaultHeight;

    private boolean renderGrid;

    public ImageDataResult createTimeSeriesChart(DesignedParameterSet parameterSet) {
        DesignOptions options = createDesignOptions(parameterSet);
        return performChartRendering(options);
    }

    private ImageDataResult performChartRendering(DesignOptions options) {
        try {
            EESGenerator chartGenerator = new EESGenerator();
            ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());
            String chartUrl = chartGenerator.createChart(options, renderingInfo);
            return new ImageDataResult(chartUrl);
        }
        catch (Exception e) {
            LOGGER.error("Could not render time series chart.", e);
            throw new InternalServerException("internal error!", e);
        }
    }

    public void writeTimeSeriesChart(DesignedParameterSet parameterSet, ServletOutputStream outputStream) {
        DesignOptions options = createDesignOptions(parameterSet);
        performChartRendering(options, outputStream);
    }

    private DesignOptions createDesignOptions(DesignedParameterSet parameterSet) {
        ArrayList<TimeseriesProperties> tsProperties = new ArrayList<TimeseriesProperties>();
        prepareTimeseriesResults(parameterSet, tsProperties);
        return createDesignOptions(parameterSet, tsProperties, isRenderGrid());
    }

    private void performChartRendering(DesignOptions options, OutputStream outputStream) {
        try {
            EESGenerator chartGenerator = new EESGenerator();
            ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());
            chartGenerator.createChartToOutputStream(options, renderingInfo, outputStream);
        }
        catch (Exception e) {
            LOGGER.error("Could not render timeseries chart.", e);
            throw new InternalServerException("internal error!", e);
        }
    }

    @Override
    protected TimeseriesProperties decorateProperties(TimeseriesProperties timeSeriesProperties,
                                                      ParameterSet parameterSet) throws Exception {
        timeSeriesProperties = decoratePropertiesWithImageSize(timeSeriesProperties, parameterSet);
        timeSeriesProperties = decoradeWithSensorMetadataProperties(timeSeriesProperties);
        return timeSeriesProperties;
    }

    private TimeseriesProperties decoratePropertiesWithImageSize(TimeseriesProperties timeSeriesProperties,
                                                                 ParameterSet parameterSet) {
        int width = parameterSet.getWidth() > 0 ? parameterSet.getWidth() : defaultWidth;
        int height = parameterSet.getHeight() > 0 ? parameterSet.getHeight() : defaultHeight;
        timeSeriesProperties.setWidth(width);
        timeSeriesProperties.setHeight(height);
        return timeSeriesProperties;
    }

    public EESDataService getImageDataService() {
        return imageDataService;
    }

    public void setImageDataService(EESDataService imageDataService) {
        this.imageDataService = imageDataService;
    }

    public int getDefaultWidth() {
        return defaultWidth;
    }

    public void setDefaultWidth(int defaultWidth) {
        this.defaultWidth = defaultWidth;
    }

    public int getDefaultHeight() {
        return defaultHeight;
    }

    public void setDefaultHeight(int defaultHeight) {
        this.defaultHeight = defaultHeight;
    }

    public boolean isRenderGrid() {
        return renderGrid;
    }

    public void setRenderGrid(boolean renderGrid) {
        this.renderGrid = renderGrid;
    }

}
