
package org.n52.server.ses.feeder.task;

import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.shared.serializable.pojos.FeedingMetadata;
import org.n52.shared.serializable.pojos.TimeseriesToFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the collection of all necessary observations for every
 * registered sensor. It starts a thread for every sensor in the database.
 * 
 * @author Jan Schulte
 * 
 */
public class ObservationsTask extends TimerTask {

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(ObservationsTask.class);

    private ExecutorService executor = Executors.newFixedThreadPool(1);
    
    private boolean isActive;

    private Vector<String> currentlyFeededTimeseries;

    public ObservationsTask(Vector<String> currentlyFeededTimeseries) {
        this.currentlyFeededTimeseries = currentlyFeededTimeseries;
    }

    @Override
    public void run() {
    	/////// TODO remove this tests
//    	SosSesFeeder feeder = SosSesFeeder.getInst();
//    	FeedingMetadata feedingMetadata = new FeedingMetadata();
//    	feedingMetadata.setFeatureOfInterest("Konstanz_0906");
//    	feedingMetadata.setOffering("WASSERSTAND_ROHDATEN");
//    	feedingMetadata.setPhenomenon("Wasserstand");
//    	feedingMetadata.setProcedure("Wasserstand-Konstanz_0906");
//    	feedingMetadata.setServiceUrl("http://pegelonline.wsv.de/webservices/gis/gdi-sos");
//    	
//    	feeder.enableSensorForFeeding(feedingMetadata);
    	/////
    	
        log.info("Currenty feeded sensors: " + currentlyFeededTimeseries.size());
        isActive = true;
        try {
            log.info("############## Prepare Observations task ################");
            List<TimeseriesToFeed> sensors = DatabaseAccess.getUsedSensors();
            log.info("Number of GetObservations: " + sensors.size());
            long time = System.currentTimeMillis();
            for (TimeseriesToFeed sensor : sensors) {
                if (sensor.getLastUpdate() == null
                        || (time - sensor.getUpdateInterval() > sensor.getLastUpdate().getTimeInMillis())) {
                    // 
                    // start only threads for sensors which are currently not
                    // feeding
                    //
                    FeedingMetadata metadata = sensor.getFeedingMetadata();
                    if (!this.currentlyFeededTimeseries.contains(metadata)) {
                        FeedObservationThread obsThread =
                                new FeedObservationThread(sensor, this.currentlyFeededTimeseries);
                        if (!executor.isShutdown()) {
                            executor.execute(obsThread);
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            log.error("Could not parse 'KEY_OBSERVATIONS_TASK_PERIOD'.", e);
        } catch (IllegalStateException e) {
            log.debug("Configuration is not available (anymore).", e);
        }
        finally {
        	isActive = false;
        }
    }
    /*
     * Why do we use here our own method and in DescriptionTask override the 
     * TimerTask.cancel() method?
     */
    public void stopObservationFeeds() {
        log.info("############## Stop Observations task ################");
        List<Runnable> threads = executor.shutdownNow();
        log.debug("Threads aktiv: " + threads.size());
        for (Runnable runnable : threads) {
            FeedObservationThread thread = (FeedObservationThread) runnable;
            if (thread != null) {
                thread.setRunning(false);
            }
        }
        while (!executor.isTerminated()) {
            log.debug("Wait while ObservationThreads are finished");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                log.error("Error during stop of observations threads.", e);
            }
        }
        log.info("############## Observation task stopped ##############.");
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }

}