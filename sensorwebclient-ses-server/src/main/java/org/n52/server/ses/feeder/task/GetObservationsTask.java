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

package org.n52.server.ses.feeder.task;

import static org.n52.server.ses.feeder.util.DatabaseAccess.getSubscribedTimeseriesFeeds;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages regular GetObservation feeds for each subscribed {@link TimeseriesFeed} stored in database.<br>
 * <br>
 * When a GetObservation feed shall be repeated depends on ... TODO {@link #shallFeed(TimeseriesFeed)}
 * 
 * @see #shallFeed(TimeseriesFeed)
 */
public class GetObservationsTask extends TimerTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetObservationsTask.class);

    private ExecutorService executor = Executors.newFixedThreadPool(5);

    private boolean active = true;

    @Override
    public void run() {
        // ///// TODO remove this tests
        // SosSesFeeder feeder = SosSesFeeder.getInst();
        // FeedingMetadata feedingMetadata = new FeedingMetadata();
        // feedingMetadata.setFeatureOfInterest("Konstanz_0906");
        // feedingMetadata.setOffering("WASSERSTAND_ROHDATEN");
        // feedingMetadata.setPhenomenon("Wasserstand");
        // feedingMetadata.setProcedure("Wasserstand-Konstanz_0906");
        // feedingMetadata.setServiceUrl("http://pegelonline.wsv.de/webservices/gis/gdi-sos");
        //
        // feeder.enableSensorForFeeding(feedingMetadata);
        // ///

        LOGGER.debug("############## Prepare Observation tasks ################");
        List<TimeseriesFeed> subscribedFeeds = getSubscribedTimeseriesFeeds();
        LOGGER.debug("Number of subscribed Feeds: " + subscribedFeeds.size());
        for (TimeseriesFeed subscribedFeed : subscribedFeeds) {
            if (isActive() && shallFeed(subscribedFeed)) {
                if ( !executor.isShutdown()) {
                    LOGGER.trace("Feed GetObservation for '{}'", subscribedFeed.getTimeseriesId());
                    executor.execute(new FeedObservationThread(subscribedFeed, this));
                }
            }
        }
    }

    /**
     * TODO document strategy when a timeseries feed shall be feeded again (update interval, etc.)
     * 
     * @param timeseriesFeed
     * @return
     */
    private boolean shallFeed(TimeseriesFeed timeseriesFeed) {
        boolean noUpdateYet = timeseriesFeed.getLastFeeded() == null;
        return noUpdateYet || isThresholdExceeded(timeseriesFeed);
    }

    private boolean isThresholdExceeded(TimeseriesFeed timeseriesFeed) {
        long now = System.currentTimeMillis();
        long lastUpdate = timeseriesFeed.getLastFeeded().getTimeInMillis();
        return now - timeseriesFeed.getLastConsideredTimeInterval() > lastUpdate;
    }

    public void stopObservationFeeds() {
        LOGGER.info("############## Stop Observations task ################");
        List<Runnable> threads = executor.shutdownNow();
        LOGGER.debug("Threads aktiv: " + threads.size());
        for (Runnable runnable : threads) {
            FeedObservationThread thread = (FeedObservationThread) runnable;
            if (thread != null) {
                thread.setRunning(false);
            }
        }
        while ( !executor.isTerminated()) {
            LOGGER.debug("Wait while ObservationThreads are finished");
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                LOGGER.error("Error during stop of observations threads.", e);
            }
        }
        LOGGER.info("############## Observation task stopped ##############.");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

}