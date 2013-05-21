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

package org.n52.server.service;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.servlet.ServletOutputStream;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.entity.StandardEntityCollection;
import org.n52.client.service.EESDataService;
import org.n52.server.oxf.util.generator.EESGenerator;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterSet;
import org.n52.server.service.rest.model.ImageDataResult;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetImageService extends DataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetImageService.class);

    private EESDataService imageDataService;

    private int defaultWidth;

    private int defaultHeight;

    private boolean renderGrid;

    public ImageDataResult createTimeSeriesChart(ParameterSet parameterSet, String instance) {
        DesignOptions options = createDesignOptions(parameterSet, instance);
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
            throw new InternalServiceException();
        }
    }

    public void writeTimeSeriesChart(ParameterSet parameterSet, String instance, ServletOutputStream outputStream) {
        DesignOptions options = createDesignOptions(parameterSet, instance);
        performChartRendering(options, outputStream);
    }

    private DesignOptions createDesignOptions(ParameterSet parameterSet, String instance) {
        SOSMetadata metadata = getServiceMetadata(instance);
        ArrayList<TimeSeriesProperties> tsProperties = new ArrayList<TimeSeriesProperties>();
        createTimeSeriesRequest(parameterSet, metadata, tsProperties);
        return createDesignOptions(parameterSet, tsProperties, isRenderGrid());
    }

    private void performChartRendering(DesignOptions options, OutputStream outputStream) {
        try {
            EESGenerator chartGenerator = new EESGenerator();
            ChartRenderingInfo renderingInfo = new ChartRenderingInfo(new StandardEntityCollection());
            chartGenerator.createChartToOutputStream(options, renderingInfo, outputStream);
        }
        catch (Exception e) {
            LOGGER.error("Could not render time series chart.", e);
            throw new InternalServiceException();
        }
    }

    @Override
    protected TimeSeriesProperties decorateProperties(TimeSeriesProperties timeSeriesProperties, ParameterSet parameterSet) throws Exception {
        timeSeriesProperties = decoratePropertiesWithImageSize(timeSeriesProperties, parameterSet);
        timeSeriesProperties = decoradeWithSensorMetadataProperties(timeSeriesProperties);
        timeSeriesProperties = decoratePropertiesWithRandomColor(timeSeriesProperties);
        return timeSeriesProperties;
    }

    private TimeSeriesProperties decoratePropertiesWithImageSize(TimeSeriesProperties timeSeriesProperties, ParameterSet parameterSet) {
        int width = parameterSet.getWidth() > 0 ? parameterSet.getWidth() : defaultWidth;
        int height = parameterSet.getHeight() > 0 ? parameterSet.getHeight() : defaultHeight;
        timeSeriesProperties.setWidth(width);
        timeSeriesProperties.setHeight(height);
        return timeSeriesProperties;
    }

    private TimeSeriesProperties decoratePropertiesWithRandomColor(TimeSeriesProperties timeSeriesProperties) {
        timeSeriesProperties.setHexColor(getRandomHexColor());
        return timeSeriesProperties;
    }

    public static String getRandomHexColor() {
        String redHex = getNextFormattedRandomNumber();
        String yellowHex = getNextFormattedRandomNumber();
        String blueHex = getNextFormattedRandomNumber();
        return "#" + redHex + yellowHex + blueHex;
    }

    private static String getNextFormattedRandomNumber() {
        String randomHex = Integer.toHexString(new Random().nextInt(256));
        if (randomHex.length() == 1) {
            // ensure two digits
            randomHex = "0" + randomHex;
        }
        return randomHex;
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
