/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.server.io;

import org.n52.shared.exceptions.GeneratorException;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadata;
import static org.n52.server.util.TimeUtil.createIso8601Formatter;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.servlet.ServletUtilities;
import org.n52.oxf.OXFRuntimeException;
import org.n52.oxf.feature.OXFFeatureCollection;
import org.n52.oxf.feature.sos.ObservationSeriesCollection;
import org.n52.oxf.ows.capabilities.ITime;
import org.n52.oxf.valueDomains.time.TimeFactory;
import org.n52.server.da.AccessException;
import org.n52.server.da.oxf.ObservationAccessor;
import org.n52.server.da.oxf.TimePosition_OXFExtension;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.server.mgmt.GeneralizationConfiguration;
import org.n52.shared.responses.RepresentationResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.TimeseriesParametersLookup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Generator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Generator.class);

    private SimpleDateFormat dateFormat = createIso8601Formatter();

    protected String folderPostfix;

    /**
     * can be executed to produce a presentation of sensor data.<br>
     * IMPORTANT: do not close the OutputStream within this method.
     * 
     * @param options
     *        the options
     * @return RepresentationResponse
     * @throws GeneratorException
     *         if generating presentation fails.
     */
    public abstract RepresentationResponse producePresentation(DesignOptions options) throws GeneratorException;
    
    protected TimeseriesParametersLookup getParameterLookup(String serviceUrl) {
        try {
            SOSMetadata metadata = getSOSMetadata(serviceUrl);
            return metadata.getTimeseriesParametersLookup();
        } catch (Exception e) {
            throw new IllegalStateException("No parameter lookup available for service '" + serviceUrl + "'.", e);
        }
    }

    /**
     * returns an object of type Map<String, OXFFeatureCollection>. The key is a composed String object
     * consisting of the offering-ID and the SOS URL from which the observations of the corresponding
     * OXFFeatureCollection have been requested. The key-String looks like this: "<offeringID>@<sosURL>".
     * 
     * @param options
     *        the options
     * @param onlyActiveTimeseries
     *        the only active timeseries
     * @return the map
     * @throws AccessException 
     */
    protected Map<String, OXFFeatureCollection> getFeatureCollectionFor(DesignOptions options, boolean generalize) throws AccessException {
        ITime time = null;
        if (options.getTimeParam() == null) {
            time = getTimeFrom(options);
        }
        else {
            time = new TimePosition_OXFExtension(options.getTimeParam());
        }

        for (TimeseriesProperties con : options.getProperties()) {
            SOSMetadata meta = ConfigurationContext.getSOSMetadata(con.getServiceUrl());
            if (meta.canGeneralize() && generalize) {
                String phenomenon = con.getPhenomenon();
                try {
                    String generalizer = GeneralizationConfiguration.getProperty(phenomenon);
                    if (generalizer != null) {
                        String procedure = con.getProcedure();
                        LOGGER.debug("Using generalizer '{}' for phenomenon '{}' and procedure '{}'",
                                     generalizer,
                                     phenomenon,
                                     procedure);
                    }
                }
                catch (PropertyException e) {
                    LOGGER.error("Error loading generalizer property for '{}'.", phenomenon, e);
                }
            }
        }
        Map<String, OXFFeatureCollection> collectionResult = sendRequest(options, time);
        updateTimeSeriesPropertiesForHavingData(options, collectionResult);
        return collectionResult;
    }

    private void updateTimeSeriesPropertiesForHavingData(DesignOptions options,
                                                         Map<String, OXFFeatureCollection> entireCollMap) {
        for (TimeseriesProperties prop : options.getProperties()) {

            OXFFeatureCollection obsColl = entireCollMap.get(prop.getOffering() + "@" + prop.getServiceUrl());

            String foiID = prop.getFeature();
            String obsPropID = prop.getPhenomenon();
            String procID = prop.getProcedure();
            // if (procID.contains("urn:ogc:generalizationMethod:")) {
            // procID = procID.split(",")[0];
            // }
            ObservationSeriesCollection seriesCollection =
                    new ObservationSeriesCollection(obsColl, new String[] {foiID}, new String[] {obsPropID},
                                                    new String[] {procID}, true);

            if (seriesCollection.getSortedTimeArray().length > 0) {
                prop.setHasData(true);
            }
            else {
                prop.setHasData(false);
            }
        }
    }

    protected String formatDate(Date date) {
        return dateFormat.format(date);
    }

    public String getFolderPostfix() {
        return this.folderPostfix;
    }

    protected String createAndSaveImage(DesignOptions options, JFreeChart chart, ChartRenderingInfo renderingInfo) throws GeneratorException {
        int width = options.getWidth();
        int height = options.getHeight();
        BufferedImage image = chart.createBufferedImage(width, height, renderingInfo);
        Graphics2D chartGraphics = image.createGraphics();
        chartGraphics.setColor(Color.white);
        chartGraphics.fillRect(0, 0, width, height);
        chart.draw(chartGraphics, new Rectangle2D.Float(0, 0, width, height));

        try {
            return ServletUtilities.saveChartAsPNG(chart, width, height, renderingInfo, null);
        }
        catch (IOException e) {
            throw new GeneratorException("Could not save PNG!", e);
        }
    }

    private Map<String, OXFFeatureCollection> sendRequest(DesignOptions options, ITime time) throws AccessException {
        try {
            List<RequestConfig> requests = createRequestList(options, time);
            return new ObservationAccessor().sendRequests(requests);
        } catch (OXFRuntimeException e) {
            throw new AccessException("Error during GetObservation request.", e);
        }
    }

    private List<RequestConfig> createRequestList(DesignOptions options, ITime time) {
        List<RequestConfig> requests = new ArrayList<RequestConfig>();
        for (TimeseriesProperties property : options.getProperties()) {
            List<String> fois = new ArrayList<String>();
            List<String> procedures = new ArrayList<String>();
            List<String> observedProperties = new ArrayList<String>();

            // extract request parameters from offering
            observedProperties.add(property.getPhenomenon());
            procedures.add(property.getProcedure());
            fois.add(property.getFeature());

            String sosUrl = property.getServiceUrl();
            String offeringId = property.getOffering();
            
            ITime resultTime = getResultTimeFrom(options); 
            requests.add(new RequestConfig(sosUrl, offeringId, fois, observedProperties, procedures, time, resultTime));
        }
        return requests;
    }
    
    protected ITime getResultTimeFrom(DesignOptions options) {
        if (options.getResultTime() != null) {
            Calendar resTime = Calendar.getInstance();
            resTime.setTimeInMillis(options.getResultTime());
            String resultTime = dateFormat.format(resTime.getTime());
            return TimeFactory.createTime(resultTime);
        }
        return null;
    }

    /**
     * @param options
     *        the design options to read the set time from.
     * @return a time instance representing an ISO8601 period.
     */
    protected ITime getTimeFrom(DesignOptions options) {
        Calendar beginPos = Calendar.getInstance();
        beginPos.setTimeInMillis(options.getBegin());
        Calendar endPos = Calendar.getInstance();
        endPos.setTimeInMillis(options.getEnd());
        String begin = dateFormat.format(beginPos.getTime());
        String end = dateFormat.format(endPos.getTime());
        return TimeFactory.createTime(begin + "/" + end);
    }

}