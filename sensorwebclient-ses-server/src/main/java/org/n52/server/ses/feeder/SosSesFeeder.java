/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.feeder;

import static org.n52.server.ses.feeder.FeederConfig.getFeederConfig;
import static org.n52.server.ses.feeder.util.DatabaseAccess.saveTimeseriesFeed;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.n52.oxf.ows.ExceptionReport;
import org.n52.server.ses.feeder.connector.SESConnector;
import org.n52.server.ses.feeder.connector.SOSConnector;
import org.n52.server.ses.feeder.task.GetObservationsTask;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosSesFeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosSesFeeder.class);

    private Map<String, SOSConnector> sosConnections = new HashMap<String, SOSConnector>();

    private GetObservationsTask observationTask;

    private static SosSesFeeder instance;

    public static boolean active = true;

    private Timer timer = new Timer("Feeder-Timer");

    /**
     * Creates the feeder singleton instance if it was not created before.<br>
     * <br>
     * The feeder is not started automatically. Use {@link #startFeeding()} or {@link #stopFeeding()} to
     * control feeding activity or create the instance via {@link SosSesFeeder#createSosSesFeeder(boolean)}. <br>
     * <br>
     * Uses {@link SosSesFeeder#getSosSesFeederInstance()} but without returning the instance.
     */
    public static void createSosSesFeeder() {
        createSosSesFeeder(false);
    }

    /**
     * Creates the feeder singleton instance if it was not created before.
     * 
     * @param startAutomatically
     *        if the feeder shall start feeding, once it has been created.
     */
    public static void createSosSesFeeder(boolean startAutomatically) {
        if (startAutomatically) {
            getSosSesFeederInstance().startFeeding();
        }
        else {
            getSosSesFeederInstance();
        }
    }

    public static SosSesFeeder getSosSesFeederInstance() {
        if (instance == null) {
            instance = new SosSesFeeder();
        }
        return instance;
    }

    protected SosSesFeeder() {
        // singleton, keep private but testable
    }

    public void startFeeding() {
        LOGGER.info("Start feeding registered timeseries to SES.");
        long elapse = getFeederConfig().getElapseTimeOfGetObservationsUpdate();
        timer.schedule(new GetObservationsTask(), 2000, elapse);
    }

    public void stopFeeding() {
        timer.cancel();
        observationTask.setActive(false);
        LOGGER.info("Feeding stopped.");
    }

    public void enableFeedingFor(TimeseriesFeed timeseriesFeed) {
        TimeseriesMetadata timeseriesMetadata = timeseriesFeed.getTimeseriesMetadata();
        if ( !timeseriesIsAlreadyBeingFeeded(timeseriesMetadata)) {
            saveNewTimeseriesFeed(timeseriesFeed);
        }
        increaseSubscriptionCountFor(timeseriesFeed);
    }

    boolean timeseriesIsAlreadyBeingFeeded(TimeseriesMetadata timeseriesMetadata) {
        return DatabaseAccess.isKnownTimeseriesFeed(timeseriesMetadata);
    }

    private void saveNewTimeseriesFeed(TimeseriesFeed timeseriesFeed) {
        try {
            SensorMLDocument sensorML = getSensorMLfromSos(timeseriesFeed.getTimeseriesMetadata());
            SESConnector sesConnector = new SESConnector();

            // TODO do we have to exchange unique ID in sensorML?

            String publishedId = sesConnector.registerPublisher(sensorML);
            timeseriesFeed.setSesId(publishedId);
            saveTimeseriesFeed(timeseriesFeed);
        }
        catch (ExceptionReport e) {
            LOGGER.error("Error while register sensor in SES, ", e);
        }
    }

    private SensorMLDocument getSensorMLfromSos(TimeseriesMetadata timeseriesMetadata) {
        SOSConnector sosConnector = getSosConnector(timeseriesMetadata);
        return sosConnector.getSensorML(timeseriesMetadata);
    }

    private SOSConnector getSosConnector(TimeseriesMetadata timeseriesMetadata) {
        String serviceUrl = timeseriesMetadata.getServiceUrl();
        SOSConnector sosConnector = sosConnections.get(serviceUrl);
        if ( !sosConnections.containsKey(serviceUrl)) {
            sosConnector = new SOSConnector(serviceUrl);
            sosConnections.put(serviceUrl, sosConnector);
        }
        return sosConnector;
    }

    public void increaseSubscriptionCountFor(TimeseriesFeed timeseriesFeed) {
        DatabaseAccess.increaseSubscriptionCountFor(timeseriesFeed);
    }

    public void decreaseSubscriptionCountFor(TimeseriesFeed timeseriesFeed) {
        DatabaseAccess.decreaseSubscriptionCountFor(timeseriesFeed);
    }

    @Override
    public void finalize() {
        try {
            // FIXME should be removed during code review before next release
            active = false;
            this.observationTask.stopObservationFeeds();
            // wait until ObsTask is finished
            while (this.observationTask.isActive()) {
                LOGGER.debug("ObservationTask is active : " + observationTask.isActive());
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1) {
                    LOGGER.error("Error during destroy of SosSesFeeder servlet.", e1);
                }
            }

            stopFeeding();

            HibernateUtil.closeDatabaseSessionFactory();

            // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory
            // leaks wrto this class
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.debug(String.format("deregistering jdbc driver: %s", driver));
                }
                catch (SQLException e) {
                    LOGGER.error(String.format("Error deregistering driver %s", driver), e);
                }
            }
            LOGGER.info("########## Feeder cleanup completed ##########");
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Connection already closed, because of shutdown process ...", e);
        }
    }

}
