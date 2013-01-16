
package org.n52.server.ses.feeder.task;

import java.util.List;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.server.ses.feeder.hibernate.SensorToFeed;
import org.n52.server.ses.feeder.util.DatabaseAccess;
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

    /**
     * Reference to the overall list of currently feeded sensors to prohibit
     * double feeding
     */
    private Vector<String> currentyFeededSensors;

    public ObservationsTask(Vector<String> currentlyFeededSensors) {
        this.currentyFeededSensors = currentlyFeededSensors;
    }

    @Override
    public void run() {
        log.info("Currenty feeded sensors: " + currentyFeededSensors.size());
        isActive = true;
        try {
            log.info("############## Prepare Observations task ################");
            List<SensorToFeed> sensors = DatabaseAccess.getUsedSensors();
            log.info("Number of GetObservations: " + sensors.size());
            long time = System.currentTimeMillis();
            for (SensorToFeed sensor : sensors) {
                if (sensor.getLastUpdate() == null
                        || (time - sensor.getUpdateInterval() > sensor.getLastUpdate().getTimeInMillis())) {
                    // 
                    // start only threads for sensors which are currently not
                    // feeding
                    //
                    if (!this.currentyFeededSensors.contains(sensor.getProcedure())) {
                        FeedObservationThread obsThread =
                                new FeedObservationThread(sensor, this.currentyFeededSensors);
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
                e.printStackTrace();
                log.error("Error during stop of observations threads: " + e.getLocalizedMessage(),e);
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