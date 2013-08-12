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

package org.n52.server.io;

import static org.n52.server.mgmt.ConfigurationContext.getServiceMetadata;
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
import org.n52.server.io.RequestConfig;
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
     * @throws Exception
     *         if generating presentation fails.
     */
    public abstract RepresentationResponse producePresentation(DesignOptions options) throws Exception;
    
    protected TimeseriesParametersLookup getParameterLookup(String serviceUrl) {
        try {
            SOSMetadata metadata = getServiceMetadata(serviceUrl);
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
     */
    protected Map<String, OXFFeatureCollection> getFeatureCollectionFor(DesignOptions options, boolean generalize) {
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

    protected String createAndSaveImage(DesignOptions options, JFreeChart chart, ChartRenderingInfo renderingInfo) throws Exception {
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
            throw new Exception("Could not save PNG!", e);
        }
    }

    private Map<String, OXFFeatureCollection> sendRequest(DesignOptions options, ITime time) {
        List<RequestConfig> requests = createRequestList(options, time);
        Map<String, OXFFeatureCollection> result = new HashMap<String, OXFFeatureCollection>();
        try {
            result = new ObservationAccessor().sendRequests(requests);
        }
        catch (OXFRuntimeException e) {
            LOGGER.error("Failure when requesting GetObservation.", e);
        }
        catch (AccessException e) {
            LOGGER.error("Could not access features.", e);
        }
        return result;
    }

    private List<RequestConfig> createRequestList(DesignOptions options, ITime time) {
        List<RequestConfig> requests = new ArrayList<RequestConfig>();
        for (TimeseriesProperties property : options.getProperties()) {
            List<String> fois = new ArrayList<String>();
            List<String> procedures = new ArrayList<String>();
            List<String> observedProperties = new ArrayList<String>();

            // extract request parameters from offering
            String offering = property.getOffering();
            observedProperties.add(property.getPhenomenon());
            procedures.add(property.getProcedure());
            fois.add(property.getFeature());

            String sosUrl = property.getServiceUrl();
            String offeringId = property.getOffering();
            requests.add(new RequestConfig(sosUrl, offeringId, fois, observedProperties, procedures, time));
        }
        return requests;
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