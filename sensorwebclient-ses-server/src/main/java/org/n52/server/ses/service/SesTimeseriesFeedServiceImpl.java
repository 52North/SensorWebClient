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
