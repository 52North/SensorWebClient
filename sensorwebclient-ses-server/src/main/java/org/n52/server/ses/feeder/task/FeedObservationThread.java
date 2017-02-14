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
package org.n52.server.ses.feeder.task;

import static java.lang.System.currentTimeMillis;
import static org.n52.server.ses.feeder.FeederConfig.getFeederConfig;
import static org.n52.server.util.TimeUtil.createIso8601Formatter;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractTimeObjectType;
import net.opengis.gml.TimePeriodType;
import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.om.x10.ObservationType;
import net.opengis.swe.x101.DataArrayDocument;
import net.opengis.swe.x101.DataValuePropertyType;
import net.opengis.swe.x101.TextBlockDocument.TextBlock;
import net.opengis.swe.x101.TimeObjectPropertyType;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.server.ses.feeder.FeederConfig;
import org.n52.server.ses.feeder.connector.SESConnector;
import org.n52.server.ses.feeder.connector.SOSConnector;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages the collection of the observations for a Timeseries. It requests the timeseries observation data
 * from the SOS and sends it to the SES as a notify request. If a rule has been registered at the SES for this
 * specific timeseries the SES filters automatically incoming notifications (in this case the observation) and
 * generates outgoing notifications to inform external components if an observation triggers an event.<br>
 * <br>
 * Note that a publisher has to be registered at the SES. In this case the
 * {@link TimeseriesFeed#getTimeseriesMetadata()} should has been registered as publisher.
 */
public class FeedObservationThread extends Thread {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedObservationThread.class);

    private SESConnector sesConnection = new SESConnector();

    // TODO use managingTask.isActive() to interrupt long taking actions
    private GetObservationsTask managingTask;
    
    private TimeseriesFeed timeseriesFeed;

    private boolean running = true;

    public FeedObservationThread(TimeseriesFeed timeseriesFeed, GetObservationsTask managingTask) {
        super("timeseriesId_" + timeseriesFeed.getTimeseriesId());
        this.timeseriesFeed = timeseriesFeed;
        this.managingTask = managingTask;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        if (isRunning()) {
            TimeseriesMetadata metadata = timeseriesFeed.getTimeseriesMetadata();
            try {
                LOGGER.debug("Start feeding observation for {}: ", metadata);
                if (!timeseriesFeed.hasBeenFeededBefore()) {
                    setFirstTimeIntervalToFeed(timeseriesFeed);
                }

                Calendar endUpdate = null;
                for (ObservationPropertyType observationMember : getObservationsFor(metadata)) {
                    ObservationType observation = observationMember.getObservation();
                    if (observation != null && observation.getProcedure().getHref().equals(metadata.getProcedure())) {
                        replaceProcedureWithGlobalSesId(observation, metadata);
                        endUpdate = getLastUpdateTime(observation.getSamplingTime());
                        Calendar latestFeededAt = timeseriesFeed.getLastFeeded();
                        timeseriesFeed.setLastConsideredTimeInterval(createUpdateInterval(observation, latestFeededAt));
                        observationMember = checkObservations(observationMember);
                        if ( ! (observationMember == null) && !sesConnection.isClosed()) {
                            sesConnection.publishObservation(observationMember);
                            LOGGER.info(metadata.getProcedure() + " with " + metadata.getOffering() + " feeded to SES");
                        }
                        else {
                            LOGGER.info(String.format("No data received for procedure '%s'.", metadata.getProcedure()));
                        }
                    }
                    else {
                        LOGGER.info("No new Observations for " + metadata.getProcedure());
                    }
                }
                if (endUpdate != null) {
                    // to prevent receiving observation two times
                    endUpdate.add(Calendar.MILLISECOND, 1);
                    this.timeseriesFeed.setLastFeeded(endUpdate);
                }
                DatabaseAccess.saveTimeseriesFeed(this.timeseriesFeed);
            }
            catch (IllegalStateException e) {
                LOGGER.warn("Failed to create SOS/SES Connection.", e);
                return; // maybe shutdown .. try again
            }
            catch (InterruptedException e) {
                LOGGER.trace(e.getMessage(), e);
            }
            catch (HibernateException e) {
                LOGGER.warn("Datebase problem has occured: " + e.getMessage(), e);
            }
            catch (Exception e) {
                LOGGER.warn("Could not request and publish Observation to SES: " + e.getMessage(), e);
            }
        }
    }

    private void replaceProcedureWithGlobalSesId(ObservationType observation, TimeseriesMetadata metadata) {
        observation.getProcedure().setHref(metadata.getGlobalSesId());
    }

    private ObservationPropertyType[] getObservationsFor(TimeseriesMetadata metadata) throws Exception {
        SOSConnector sosConnection = new SOSConnector(metadata.getServiceUrl());
        ObservationCollectionDocument obsCollDoc = sosConnection.performGetObservation(timeseriesFeed);
        return obsCollDoc.getObservationCollection().getMemberArray();
    }

    private void setFirstTimeIntervalToFeed(TimeseriesFeed timeseriesFeed) {
        long lastConsideredTimeInterval = getFeederConfig().getFirstConsideredTimeInterval();
        long firstConsideredTimeInterval = currentTimeMillis() - lastConsideredTimeInterval;
        Calendar firstUpdate = new GregorianCalendar();
        firstUpdate.setTimeInMillis(firstConsideredTimeInterval);
        timeseriesFeed.setLastFeeded(firstUpdate);
        String iso8601 = createIso8601Formatter().format(firstUpdate.getTime());
        LOGGER.debug("First feeding set to {}.", iso8601);
    }

    private ObservationPropertyType checkObservations(ObservationPropertyType obsPropType) {
        // zum tag hin navigieren
        XmlCursor cResult = obsPropType.getObservation().getResult().newCursor();
        cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
        DataArrayDocument dataArrayDoc = null;
        try {
            dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
        }
        catch (XmlException e) {
            LOGGER.error(e.getMessage());
        }
        TextBlock textBlock = dataArrayDoc.getDataArray1().getEncoding().getTextBlock();
        String tokenSeparator = textBlock.getTokenSeparator();
        String blockSeperator = textBlock.getBlockSeparator();

        String values = dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().getNodeValue();
        String[] blocks = values.split(blockSeperator);
        StringBuffer newValues = new StringBuffer();
        try {
            List<String> noDatas = FeederConfig.getFeederConfig().getNoDataValues();
            for (String block : blocks) {
                String[] value = block.split(tokenSeparator);
                // check if noData values matching
                boolean noDataMatch = false;
                for (String noData : noDatas) {
                    if (value[2].equals(noData)) {
                        noDataMatch = true;
                        break;
                    }
                }
                if ( !noDataMatch) {
                    newValues.append(block + blockSeperator);
                }
            }
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration not available (anymore).", e);
        }

        if (newValues.toString().equals("")) {
            return null;
        }
        dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().setNodeValue(newValues.toString());
        obsPropType.getObservation().getResult().set(dataArrayDoc);
        return obsPropType;
    }

    private long createUpdateInterval(ObservationType observation, Calendar lastFeededAt) {
        long updateObservationPeriod = 0;
        try {
            // TODO for fix observation ranges makes this sense
            updateObservationPeriod = getFeederConfig().getMinimalUpdateIntervalRange();
            
            XmlCursor resultCursor = observation.getResult().newCursor();
            resultCursor.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
            
            String observationResult = "";
            String tokenSeparator = ","; // default
            String blockSeparator = ";"; // default
            try {
                DataArrayDocument dataArray  = DataArrayDocument.Factory.parse(resultCursor.getDomNode());
                DataValuePropertyType dataValues = dataArray.getDataArray1().getValues();
                observationResult = dataValues.getDomNode().getFirstChild().getNodeValue();
                
                TextBlock encoding = dataArray.getDataArray1().getEncoding().getTextBlock();
                tokenSeparator = encoding.getTokenSeparator();
                blockSeparator = encoding.getBlockSeparator();
            }
            catch (XmlException e) {
                LOGGER.error("Error when parsing DataArray.", e);
            }
            

            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            String[] valueBlocks = observationResult.split(blockSeparator);
            
            // get updateInterval
            long endUpdateInterval = lastFeededAt.getTimeInMillis();
            for (String valueBlock : valueBlocks) {
                String[] values = valueBlock.split(tokenSeparator);
                try {
                    DateTime observationTime = fmt.parseDateTime(values[0]);
                    long startUpdateInterval = observationTime.getMillis() - endUpdateInterval;
                    if (startUpdateInterval < updateObservationPeriod) {
                        updateObservationPeriod = startUpdateInterval;
                    }
                    endUpdateInterval = observationTime.getMillis();
                }
                catch (Exception e) {
                    LOGGER.error("Error when parsing Date: " + e.getMessage(), e);
                }
            }

            if (valueBlocks.length >= 2) {
                String[] valueArrayFirst = valueBlocks[valueBlocks.length - 2].split(tokenSeparator);
                String[] valueArrayLast = valueBlocks[valueBlocks.length - 1].split(tokenSeparator);
                DateTime dateTimeFirst = fmt.parseDateTime(valueArrayFirst[0]);
                DateTime dateTimeLast = fmt.parseDateTime(valueArrayLast[0]);
                updateObservationPeriod = dateTimeLast.getMillis() - dateTimeFirst.getMillis();
            }

            if (updateObservationPeriod <= FeederConfig.getFeederConfig().getMinimalUpdateIntervalRange()) {
                return FeederConfig.getFeederConfig().getMinimalUpdateIntervalRange();
            }
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available (anymore).", e);
        }

        return updateObservationPeriod;
    }

    private Calendar getLastUpdateTime(TimeObjectPropertyType samplingTime) {
        AbstractTimeObjectType timeObject = samplingTime.getTimeObject();
        TimePeriodType timePeriod = (TimePeriodType) timeObject;
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        Date date = null;
        try {
            DateTime dateTime = fmt.parseDateTime(timePeriod.getEndPosition().getStringValue());
            date = dateTime.toDate();
        }
        catch (Exception e) {
            LOGGER.error("Error when parsing Date: " + e.getMessage(), e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
