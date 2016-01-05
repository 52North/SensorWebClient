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
package org.n52.server.ses.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import org.n52.client.service.SesTimeseriesFeedService;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.mail.MailSender;
import org.n52.server.ses.util.SesParser;
import org.n52.server.ses.util.SesServerUtil;
import org.n52.shared.responses.SesClientResponse;
import org.n52.shared.responses.SesClientResponseType;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

    
public class SesTimeseriesFeedServiceImpl implements SesTimeseriesFeedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SesTimeseriesFeedServiceImpl.class);

    private static SesParser parser;

    private static SesParser getParser(){
        if (SesTimeseriesFeedServiceImpl.parser == null) {
            return new SesParser(SesConfig.serviceVersion, SesConfig.sesEndpoint);
        }
        return parser;
    }
    
    @Override
    public SesClientResponse getTimeseriesFeeds() throws Exception {
        try {
            LOGGER.debug("get registered timeseriesFeeds from DB");
            List<TimeseriesFeed> timeseriesFeeds = HibernateUtil.getTimeseriesFeeds();
            return new SesClientResponse(SesClientResponseType.REGISTERED_TIMESERIES_FEEDS, timeseriesFeeds);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public void updateTimeseriesFeed(String timeseriesFeedId, boolean active) throws Exception {
        try {
            LOGGER.debug("updateTimeseriesFeed: {}, Active: {}", timeseriesFeedId, active);
            updateTimeseriesFeed(timeseriesFeedId, active);
            
            if (!active) {
                // sensor was deactivated
                // inform all subscriber
                ArrayList<User> userList = SesServerUtil.getUserBySensorID(timeseriesFeedId);
                
                // iterate over the user
                for (int i = 0; i < userList.size(); i++) {
                    User user = userList.get(i);
                    // inform user
                    MailSender.sendSensorDeactivatedMail(user.geteMail(), timeseriesFeedId);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getStations() throws Exception {
        try {
            LOGGER.debug("getStations");
            ArrayList<TimeseriesMetadata> finalList = new ArrayList<TimeseriesMetadata>();
            HashSet<TimeseriesMetadata> timeseriesMetadatas = new HashSet<TimeseriesMetadata>();
            
            // DB request
            List<TimeseriesFeed> timeseriesFeeds = HibernateUtil.getActiveTimeseriesFeeds();
            for (TimeseriesFeed timeseriesFeed : timeseriesFeeds) {
                timeseriesMetadatas.add(timeseriesFeed.getTimeseriesMetadata());
            }
            
            finalList.addAll(timeseriesMetadatas);
            
            // TODO make FeedingMetadata comparable
//            Collections.sort(finalList);
    
            return new SesClientResponse(SesClientResponseType.STATIONS, finalList);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse getPhenomena(String timeseriesId) throws Exception {
        try {
            LOGGER.debug("getPhenomena for timeseriesId: " + timeseriesId);
            ArrayList<String> finalList = new ArrayList<String>();
            ArrayList<String> unit = new ArrayList<String>();

            ArrayList<String> phenomena = getParser().getPhenomena(timeseriesId);
            
            // TODO get the unit of measurement from internal/mapped id
            
            unit.add(getParser().getUnit(timeseriesId));
            for (int i = 0; i < phenomena.size(); i++) {
                LOGGER.debug(phenomena.get(i));
                finalList.add(phenomena.get(i)); 
            }
            
            Collections.sort(finalList);
            return new SesClientResponse(SesClientResponseType.PHENOMENA, finalList, unit);
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

    @Override
    public SesClientResponse deleteTimeseriesFeed(String timeseriesId) throws Exception {
        try {
            LOGGER.debug("delete timeseries feed: " + timeseriesId);
            if (HibernateUtil.deleteTimeseriesFeed(timeseriesId)) {
                return new SesClientResponse(SesClientResponseType.DELETE_SENSOR_OK);
            }
            throw new Exception("delete timeseries feed: " + timeseriesId + " " + "failed");
        }
        catch (Exception e) {
            LOGGER.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
