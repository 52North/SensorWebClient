package org.n52.server.ses.feeder;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Timer;
import java.util.Vector;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.n52.oxf.ows.ExceptionReport;
import org.n52.server.ses.feeder.connector.SESConnector;
import org.n52.server.ses.feeder.connector.SOSConnector;
import org.n52.server.ses.feeder.hibernate.InitSessionFactory;
import org.n52.server.ses.feeder.hibernate.SensorToFeed;
import org.n52.server.ses.feeder.task.DescriptionTask;
import org.n52.server.ses.feeder.task.ObservationsTask;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.shared.serializable.pojos.FeedingMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosSesFeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosSesFeeder.class);
    
    private ObservationsTask obsTask;
    
    private DescriptionTask descTask;
    
    private static SosSesFeeder instance;
    
    public static boolean active = true;
    
    /**
     * List of currently feeding sensors (used to prohibit double feeding)
     * <br>
     * We use ONLY the <b>procedureId</b> of the sensor!
     * <br> 
     * TODO make config parameter of initial capacity and capacity increment
     * @deprecated we want to register new timeseries to feed by interface
     */
    @Deprecated
    private static Vector<String> CURRENTLY_FEEDED_SENSORS = new Vector<String>();
    
    private Timer timer = new Timer("Feeder-Timer");

    /**
     * @throws IllegalStateException if feeder could not be initialized from config file. 
     */
    private SosSesFeeder() {
    	
    	// initialize feeder configurations
    	FeederConfig.getInstance();

        // start description timer task
        this.obsTask = new ObservationsTask(CURRENTLY_FEEDED_SENSORS);
        this.descTask = new DescriptionTask();
        
        // start the tasks
        startFeeding();
    }
    
    public static SosSesFeeder getInst() {
		if (instance == null) {
			instance = new SosSesFeeder();
		}
		return instance;
	}
    
    private void startFeeding() {
        LOGGER.info("Start feeding registered timeseries to SES.");
//        this.timer.schedule(this.descTask, 1, FeederConfig.getInstance().getCapTime());
        this.timer.schedule(this.obsTask, FeederConfig.getInstance().getObsTime(), FeederConfig.getInstance().getObsTime());
    }
    
    @Override
    public void finalize() {
        try {
        	// FIXME should be removed during code review before next release
            active = false;
            // stop descTask
            this.descTask.cancel();
            // stop obsTask
            this.obsTask.stopObservationFeeds();
            // wait until ObsTask is finished
            while (this.descTask.isActive() || this.obsTask.isActive()) {
                LOGGER.debug("DescriptionTask is active : " + descTask.isActive());
                LOGGER.debug("ObservationTask is active : " + obsTask.isActive());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                	LOGGER.error("Error during destroy of SosSesFeeder servlet: " + e1.getLocalizedMessage(),e1);
                    e1.printStackTrace();
                }
            } 
            
            stopFeeding();
            
            InitSessionFactory.getInstance().close();

            // This manually deregisters JDBC driver, which prevents Tomcat 7 from complaining about memory leaks wrto this class
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                try {
                    DriverManager.deregisterDriver(driver);
                    LOGGER.debug(String.format("deregistering jdbc driver: %s", driver));
                } catch (SQLException e) {
                    LOGGER.error(String.format("Error deregistering driver %s", driver), e);
                }
            }
            LOGGER.info("########## Feeder complete stopped ##########");
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Connection already closed, because of shutdown process ...", e);
        }
    }

    public void stopFeeding() {
        LOGGER.info("Stop feeding registered timeseries to SES.");
        this.timer.cancel();
    }
    
    public void enableSensorForFeeding(FeedingMetadata feedingMetadata) {
    	// check if sensor already registered 
    	boolean sensorRegistered = DatabaseAccess.isSensorRegistered(feedingMetadata);
    	if (!sensorRegistered) {
    		// get sensorML document from SOS
    		try {
				SOSConnector sosConn = new SOSConnector(feedingMetadata.getServiceUrl());
				SensorMLDocument sensorML = sosConn.getSensorML(feedingMetadata.getProcedure());
				// send sensorML document to SES
				SESConnector sesConn = new SESConnector();
				String sesID = sesConn.registerPublisher(sensorML);
				// save in database
				DatabaseAccess.registerSensor(createSensorToFeed(feedingMetadata, sesID));
			} catch (ExceptionReport e) {
				LOGGER.error("Error while register sensor in SES, ", e);
			}
    	} else {
    		DatabaseAccess.increaseSensorUse(feedingMetadata);
    	}
    }
    
    public void disableSensorForFeeding(FeedingMetadata feedingMetadata) {
    	// decrease counter for sensor
    	DatabaseAccess.decreaseSensorUse(feedingMetadata);
    }

	private SensorToFeed createSensorToFeed(FeedingMetadata feedingMetadata, String sesID) {
		SensorToFeed sensor = new SensorToFeed();
	    sensor.setOffering(feedingMetadata.getOffering());
	    sensor.setPhenomenon(feedingMetadata.getPhenomenon());
	    sensor.setProcedure(feedingMetadata.getProcedure());
	    sensor.setFeatureOfInterest(feedingMetadata.getFeatureOfInterest());
	    sensor.setLastUpdate(null);
	    sensor.setServiceURL(feedingMetadata.getServiceUrl());
	    sensor.setSesId(sesID);
	    sensor.setUpdateInterval(FeederConfig.getInstance().getUpdateInterval());
	    sensor.setUsedCounter(0);
		return sensor;
	}
}
