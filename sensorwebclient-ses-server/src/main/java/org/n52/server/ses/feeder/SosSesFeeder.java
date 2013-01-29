package org.n52.server.ses.feeder;

import static org.n52.server.ses.feeder.FeederConfig.getFeederConfig;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;

import net.opengis.sensorML.x101.SensorMLDocument;

import org.n52.oxf.ows.ExceptionReport;
import org.n52.server.ses.feeder.connector.SESConnector;
import org.n52.server.ses.feeder.connector.SOSConnector;
import org.n52.server.ses.feeder.task.ObservationsTask;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SosSesFeeder {

    private static final Logger LOGGER = LoggerFactory.getLogger(SosSesFeeder.class);

    private Map<String, SOSConnector> sosConnections = new HashMap<String, SOSConnector>();
    
    private ObservationsTask obsTask;
    
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
    	FeederConfig.getFeederConfig();

        // start description timer task
        this.obsTask = new ObservationsTask(CURRENTLY_FEEDED_SENSORS);
        
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
        this.timer.schedule(this.obsTask, 2000, getFeederConfig().getObsTime());
    }
    
    @Override
    public void finalize() {
        try {
        	// FIXME should be removed during code review before next release
            active = false;
            this.obsTask.stopObservationFeeds();
            // wait until ObsTask is finished
            while (this.obsTask.isActive()) {
                LOGGER.debug("ObservationTask is active : " + obsTask.isActive());
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                	LOGGER.error("Error during destroy of SosSesFeeder servlet.", e1);
                }
            } 
            
            stopFeeding();
            
            HibernateUtil.getSessionFactory().close();

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
    
    public void enableTimeseriesForFeeding(TimeseriesFeed timeseriesFeed) {
    	// check if sensor already registered 
        TimeseriesMetadata timeseriesMetadata = timeseriesFeed.getTimeseriesMetadata();
    	boolean timeseriesIsKnown = DatabaseAccess.isKnownTimeseriesFeed(timeseriesMetadata);
    	if (!timeseriesIsKnown) {
    		// get sensorML document from SOS
    		try {
    		    SOSConnector sosConnector = getSosConnector(timeseriesMetadata);
				SensorMLDocument sensorML = sosConnector.getSensorML(timeseriesMetadata);
				
				// send sensorML document to SES
				SESConnector sesConn = new SESConnector();
				
                // TODO generate unique id (e.g. from FeedingMetadata) and exchange sensorML id
                
				String sesID = sesConn.registerPublisher(sensorML);
				// save in database
				timeseriesFeed.setSesId(sesID);
				DatabaseAccess.saveTimeseriesFeed(timeseriesFeed);
			} catch (ExceptionReport e) {
				LOGGER.error("Error while register sensor in SES, ", e);
			}
    	} else {
    		DatabaseAccess.increaseSensorUse(timeseriesFeed);
    	}
    }

    SOSConnector getSosConnector(TimeseriesMetadata timeseriesMetadata) {
        SOSConnector sosConn = null;
        String serviceUrl = timeseriesMetadata.getServiceUrl();
        if (!sosConnections.containsKey(serviceUrl)) {
            sosConnections.put(serviceUrl, new SOSConnector(serviceUrl));
        }
        sosConn = sosConnections.get(serviceUrl);
        return sosConn;
    }
    
    public void disableTimeseriesFeed(TimeseriesFeed timeseriesFeed) {
    	DatabaseAccess.decreaseSensorUse(timeseriesFeed);
    }

	
}
