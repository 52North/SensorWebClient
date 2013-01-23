package org.n52.server.ses.feeder.task;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Contents;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.server.ses.feeder.FeederConfig;
import org.n52.server.ses.feeder.connector.SESConnector;
import org.n52.server.ses.feeder.connector.SOSConnector;
import org.n52.server.ses.feeder.hibernate.ObservedProperty;
import org.n52.server.ses.feeder.hibernate.Offering;
import org.n52.server.ses.feeder.hibernate.SOS;
import org.n52.server.ses.feeder.hibernate.SensorToFeed;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles the collection of all necessary sensorML documents. It
 * starts for every sensor an own thread.
 * 
 * @author Jan Schulte
 * 
 */
public class DescriptionTask extends TimerTask {

    private static final Logger log = LoggerFactory.getLogger(DescriptionTask.class);

    private SESConnector sesConn = new SESConnector();
    
    private ExecutorService executor = Executors.newFixedThreadPool(1);
    
    private boolean isActive;

    /**
     * Starts a thread for every sensor to collect the descriptions in the
     * integrated SOS.
     * 
     * @see java.util.TimerTask#run()
     */
    @Override
    public void run() {
        isActive = true;
        try {
            log.info("############ Do Description task #############");

            // Maximum number of feeding procedures
            int maxNumProcedures = FeederConfig.getInstance().getMaxNumProc();
            log.debug("MaxNumberProcedures: " + maxNumProcedures);

            // constraints for the procedures
            List<String> constraints = FeederConfig.getInstance().getProcedureNameConstraints();

            // prohibit procedure names
            List<String> prohibits = FeederConfig.getInstance().getProhibitProcedureNames();

            // load SOSes of database
            List<SOS> SOSes = DatabaseAccess.loadSOS();
            log.info("List of SOSes in DB: " + SOSes);

            // get content list of the SES
            log.info("Try to Connect to SES");
            boolean sesInit = false;
            while (!sesInit) {
                try {
                    log.info("No connection to SES - Try again in 5 seconds");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                }
                if (!sesConn.isClosed()) {
                    log.debug("INIT SES");
                    sesInit = sesConn.initService();
                } else {
                    log.debug("STOP DESCRIPTION TASK");
                    isActive = false;
                    return;
                }
            }
           
            if (!sesConn.isClosed()) {
                List<String> sesProcedures = sesConn.getContentLists();
                log.info("Number of sensors in SES: " + sesProcedures.size());
                
                int sensorCount = sesProcedures.size();
                // check sensors of the database
                log.debug("Check sensor of database");
                List<SensorToFeed> sensors = DatabaseAccess.getAllSensors();
                for (SensorToFeed sensor : sensors) {
                    if (!sesProcedures.contains(sensor.getProcedure())) {
                        log.info("Get Sensor: " + sensor);
                        if (!executor.isShutdown()) {
                            executor.execute(new FeedDescriptionThread(sensor, sensor.getSos()));
                            sensorCount++;
                        }
                    }
                }
                log.debug("Check sensor of database finished");
                if (sensorCount < maxNumProcedures) {
                    for (SOS sos : SOSes) {
                        // init connection to SOS (GetCapabilities)
                        SOSConnector sosCon = new SOSConnector(sos.getUrl());
                        if (sosCon == null || !sosCon.initService()) {
                            log.error("Connection error to SOS with URL: " + sos.getUrl());
                            throw new IllegalStateException("SOS is not available.");
                        }

                        Set<SensorToFeed> sensorsNew = new HashSet<SensorToFeed>();
                        // get observation offerings of the current SOS
                        log.debug("Get observation offerings of current sos: " + sos);
                        ServiceDescriptor serviceDescriptor = sosCon.getDesc();
                        Contents contents = serviceDescriptor.getContents();

                        for (int i = 0; i < contents.getDataIdentificationCount(); i++) {
                            ObservationOffering obsOff = (ObservationOffering) contents.getDataIdentification(i);

                            // procedure
                            List<String> procedures = Arrays.asList(obsOff.getProcedures());

                            // observed property
                            List<String> observedProperties = Arrays.asList(obsOff.getObservedProperties());

                            for (String procedure : procedures) {

                                // check if a constraint is matching
                                boolean constraintMatch;
                                if (constraints.size() == 0) {
                                    constraintMatch = true;
                                } else {
                                    constraintMatch = false;
                                }
                                for (String constraint : constraints) {
                                    if (procedure.contains(constraint)) {
                                        constraintMatch = true;
                                        break;
                                    }
                                }

                                // check the prohibitions
                                boolean prohibitMatch = false;
                                for (String prohibit : prohibits) {
                                    if (procedure.contains(prohibit)) {
                                        prohibitMatch = true;
                                        break;
                                    }
                                }

                                if (constraintMatch && !prohibitMatch) {
                                    SensorToFeed sensor = sensorInside(procedure, sensorsNew);
                                    if (sensor != null) {
                                        Set<Offering> offerings = sensor.getOfferings();
                                        Offering offering = new Offering();
                                        offering.setName(obsOff.getIdentifier());
                                        Set<ObservedProperty> obsProps = new HashSet<ObservedProperty>();
                                        for (String obsPropString : observedProperties) {
                                            ObservedProperty obsProp = new ObservedProperty(obsPropString, offering);
                                            obsProps.add(obsProp);
                                        }
                                        offering.setObservedProperties(obsProps);
                                        offerings.add(offering);
                                        log.info("Add new offering to Sensor: " + procedure);
                                    } else {
                                        sensor = new SensorToFeed();
                                        sensor.setProcedure(procedure);
                                        sensor.setLastUpdate(null);
                                        sensor.setUsed(false);
                                        sensor.setUpdateInterval(FeederConfig.getInstance().getUpdateInterval());
                                        Offering offering = new Offering();
                                        offering.setName(obsOff.getIdentifier());
                                        Set<ObservedProperty> obsProps = new HashSet<ObservedProperty>();
                                        for (String obsPropString : observedProperties) {
                                            ObservedProperty obsProp = new ObservedProperty(obsPropString, offering);
                                            obsProps.add(obsProp);
                                        }
                                        offering.setObservedProperties(obsProps);
                                        Set<Offering> offerings = new HashSet<Offering>();
                                        offerings.add(offering);
                                        sensor.setOfferings(offerings);
                                        sensorsNew.add(sensor);
                                    }
                                }
                            }
                        }

                        for (SensorToFeed sensor : sensorsNew) {
                            if (!executor.isShutdown()) {
                                log.info("Get new Sensor for feeding: " + sensor.getProcedure());
                                executor.execute(new FeedDescriptionThread(sensor, sos));
                                sensorCount++;
                                if (sensorCount >= maxNumProcedures) {
                                	log.info("maximum number of procedures (:=" + maxNumProcedures + " reached");
                                    break;
                                }
                            }
                        }
                    }
                } else {
                	log.debug("maximum number of procedures (:=" + maxNumProcedures + " already registered in feeder");
                }
                log.info("Description task finished");
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
            log.error("Error during run: " + e.getLocalizedMessage(), e);
        }
        finally {
        	isActive = false;
        }
    }

    @Override
    public boolean cancel() {
        log.info("############## Stop Description task ################");
        sesConn.setClosed(true);
        List<Runnable> threads = executor.shutdownNow();
        for (Runnable runnable : threads) {
            FeedDescriptionThread thread = (FeedDescriptionThread) runnable;
            thread.setRunning(false);
            thread.interrupt();
            thread = null;
        }
        while (!executor.isTerminated()) {
            log.debug("Wait 1 sec while DescriptionThreads are finished");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("############## Description task stopped ##############");
        return super.cancel();
    }

    /**
     * Check if the sensors set contains the procedure.
     * 
     * @param procedure
     *            the procedure
     * @param sensors
     *            the sensors
     * @return sensor if procedure is already in the set, else null
     */
    private SensorToFeed sensorInside(String procedure, Set<SensorToFeed> sensors) {
        for (SensorToFeed sensor : sensors) {
            if (sensor.getProcedure().equals(procedure)) {
                return sensor;
            }
        }
        return null;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return isActive;
    }
    
}
