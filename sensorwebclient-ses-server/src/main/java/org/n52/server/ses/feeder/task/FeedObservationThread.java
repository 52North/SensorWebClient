
package org.n52.server.ses.feeder.task;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.xml.namespace.QName;

import net.opengis.gml.AbstractTimeObjectType;
import net.opengis.gml.TimePeriodType;
import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.om.x10.ObservationType;
import net.opengis.swe.x101.DataArrayDocument;
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
import org.n52.server.ses.feeder.hibernate.SensorToFeed;
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This thread manages the collection of the observations for a Sensor. It
 * requests the observation document from the SOS and sends it to the SES by a
 * notify request.
 * 
 * @author Jan Schulte
 * 
 */
public class FeedObservationThread extends Thread {

    /** The Constant log. */
    private static final Logger log = LoggerFactory.getLogger(FeedObservationThread.class);

    /** The sensor. */
    private SensorToFeed sensor;

    private boolean running = true;
    
    private SESConnector sesCon;

    private Vector<String> currentlyFeedingSensors;

    /**
     * @return the running
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @param running
     *            the running to set
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Instantiates a new feed observation thread.
     * 
     * @param sensor
     *            The sensor for the observation collection is done.
     * @param minDelay
     */
    public FeedObservationThread(SensorToFeed sensor, Vector<String> v) {
        super("Observation_" + sensor.getProcedure());
        this.sensor = sensor;
        this.currentlyFeedingSensors = v;
    }

    /**
     * Starts the thread and collects the observation an sends it to the SES.
     * 
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
        // init SOS connection

        if (isRunning()) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Start Observation thread for Sensor: " + this.sensor.getProcedure());
                }

                SOSConnector sosCon = new SOSConnector(this.sensor.getServiceURL());
                sesCon = new SESConnector();

                // do an observation request to sos for the sensor
                Calendar startUpdate = this.sensor.getLastUpdate();
                if(log.isDebugEnabled()) {
                	log.debug("Start Time of Database for " + 
                			sensor.getProcedure() + ": " + 
                			(startUpdate!=null?startUpdate.getTimeInMillis():
                				"not defined => first time feeded") );
                }
                Calendar endUpdate = null;
                if (startUpdate == null) {
                    // create start timestamp for feeding observations
                    Calendar firstUpdateTime = new GregorianCalendar();
                    firstUpdateTime.add(Calendar.MILLISECOND, -FeederConfig.getInstance().getStartTimestamp());
                    sensor.setLastUpdate(firstUpdateTime);
                    log.debug("Start Time generated for first feeding of " + sensor.getProcedure() +": "+ sensor.getLastUpdate().getTimeInMillis());
                    // FIXME save to database the new defined start time for this sensor
                    /*
                     * The problem is here to not have a start time that's increasing if the observations are not reguarly inserted into the SOS
                     * 
                     * Re-think this before implementing. Might be a cold-start or use case/configuration specific problem.
                     * 
                     * Solution could be in line 197:
                     * if(endUpdate == null) // else {
                     * 	this.sensor.setLastupdate(startUpdate);
                     * }
                     */
                }
                
				ObservationCollectionDocument obsCollDoc = sosCon.getObservation(sensor);
				ObservationPropertyType[] memberArray = obsCollDoc.getObservationCollection().getMemberArray();
				// tell the others, that we are trying to feed observations
				boolean addResult = this.currentlyFeedingSensors.add(this.sensor.getProcedure());
				if (log.isDebugEnabled()) {
					log.debug("Added sensor \"" + this.sensor.getProcedure() + "\" to feeding list? " + addResult);
				}
				for (ObservationPropertyType obsPropType : memberArray) {
					// start feeding
					ObservationType observation = obsPropType.getObservation();
					if (observation != null && observation.getProcedure().getHref().equals(this.sensor.getProcedure())) {
						endUpdate = getLastUpdateTime(observation.getSamplingTime());
						this.sensor.setUpdateInterval(getUpdateInterval(observation, startUpdate));
						log.info(this.sensor.getProcedure() + " from " + startUpdate.getTime() + " to " + endUpdate.getTime() + " for " + sensor.getOffering()); 
						// create a Notify Message to the SES
						obsPropType = checkObservations(obsPropType);
						if (!(obsPropType == null) && !sesCon.isClosed()) {
							sesCon.publishObservation(obsPropType);
							log.info(this.sensor.getProcedure() + " with " + sensor.getOffering() + " added to SES");
						} else {
							log.info(String.format("No data received for procedure '%s'.", this.sensor.getProcedure()));
						}
					} else {
						log.info("No new Observations for "	+ this.sensor.getProcedure());
					}
				}
                if (endUpdate != null) {
                    // to prevent receiving observation two times
                    log.debug("End Time before adding for " + sensor.getProcedure() +": "+ endUpdate.getTimeInMillis());
                    endUpdate.add(Calendar.MILLISECOND, 1);
                    log.debug("End Time after adding for " + sensor.getProcedure() +": "+ endUpdate.getTimeInMillis());
                    this.sensor.setLastUpdate(endUpdate);
                }
                DatabaseAccess.saveSensor(this.sensor);
            } catch (IllegalStateException e) {
                log.warn("Failed to create SOS/SES Connection.", e);
                return; // maybe shutdown .. try again
            } catch (InterruptedException e) {
                log.trace(e.getMessage(), e);
            } catch (HibernateException e) {
                log.warn("Datebase problem has occured: " + e.getMessage(), e);
            } catch (Exception e) {
                log.warn("Could not request and publish Observation to SES: " + e.getMessage(), e);
            } finally {
                //
                // Feeding finished or failed because of exception
                // => remove this sensor from the list of
                // currentlyFeedingSensors
                // remove() returns only true if the element was contained in
                // the list
                //
                boolean removeResult = this.currentlyFeedingSensors.removeElement(this.sensor.getProcedure());
                if (log.isDebugEnabled()) {
                    log.debug("removed sensor \"" + this.sensor.getProcedure() + "\" from currently feeding list? "
                            + removeResult);
                }
            }
        }
    }

    /**
     * Check observations.
     * 
     * @param obsPropType
     *            the obs prop type
     * @return the observation property type
     */
    private ObservationPropertyType checkObservations(ObservationPropertyType obsPropType) {
        // zum tag hin navigieren
        XmlCursor cResult = obsPropType.getObservation().getResult().newCursor();
        cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
        DataArrayDocument dataArrayDoc = null;
        try {
            dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
        } catch (XmlException e) {
            log.error(e.getMessage());
        }
        TextBlock textBlock = dataArrayDoc.getDataArray1().getEncoding().getTextBlock();
        String tokenSeparator = textBlock.getTokenSeparator();
        String blockSeperator = textBlock.getBlockSeparator();

        String values = dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().getNodeValue();
        String[] blocks = values.split(blockSeperator);
        StringBuffer newValues = new StringBuffer();
        try {
            List<String> noDatas = FeederConfig.getInstance().getNoDatas();
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
                if (!noDataMatch) {
                    newValues.append(block + blockSeperator);
                }
            }
        } catch (IllegalStateException e) {
            log.debug("Configuration not available (anymore).", e);
        }

        if (newValues.toString().equals("")) {
            return null;
        }
        dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().setNodeValue(newValues.toString());
        obsPropType.getObservation().getResult().set(dataArrayDoc);
        return obsPropType;
    }

    /**
     * Gets the update interval.
     * 
     * @param observation
     *            the observation
     * @param newestUpdate
     *            the newest update
     * @return the update interval
     */
    private long getUpdateInterval(ObservationType observation, Calendar newestUpdate) {
        long updateInterval = 0;
        try {

            updateInterval = FeederConfig.getInstance().getUpdateInterval();
            XmlCursor cResult = observation.getResult().newCursor();
            cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
            DataArrayDocument dataArrayDoc = null;
            try {
                dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
            } catch (XmlException e) {
                log.error("Error when parsing DataArray: " + e.getMessage());
            }
            // get Seperators
            TextBlock textBlock = dataArrayDoc.getDataArray1().getEncoding().getTextBlock();
            String tokenSeparator = textBlock.getTokenSeparator();
            String blockSeparator = textBlock.getBlockSeparator();

            // get values
            String values = dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().getNodeValue();

            // get updateInterval
            String[] blockArray = values.split(blockSeparator);
            DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
            Date latest = newestUpdate.getTime();
            for (String value : blockArray) {
                String[] valueArray = value.split(tokenSeparator);
                try {
                    DateTime dateTime = fmt.parseDateTime(valueArray[0]);
                    Date temp = dateTime.toDate();
                    long interval = (temp.getTime() - latest.getTime());
                    if (interval < updateInterval) {
                        updateInterval = (int) interval;
                    }
                    latest = temp;
                } catch (Exception e) {
                    log.error("Error when parsing Date: " + e.getMessage(), e);
                }
            }

            if (blockArray.length >= 2) {
                String[] valueArrayFirst = blockArray[blockArray.length - 2].split(tokenSeparator);
                String[] valueArrayLast = blockArray[blockArray.length - 1].split(tokenSeparator);
                DateTime dateTimeFirst = fmt.parseDateTime(valueArrayFirst[0]);
                DateTime dateTimeLast = fmt.parseDateTime(valueArrayLast[0]);
                updateInterval = dateTimeLast.getMillis() - dateTimeFirst.getMillis();
            }

            if (updateInterval <= FeederConfig.getInstance().getUpdateInterval()) {
                return FeederConfig.getInstance().getUpdateInterval();
            }
        } catch (IllegalStateException e) {
            log.debug("Configuration is not available (anymore).", e);
        }

        return updateInterval;
    }

    /**
     * Gets the last update time.
     * 
     * @param samplingTime
     *            the sampling time
     * @return the last update time
     */
    private Calendar getLastUpdateTime(TimeObjectPropertyType samplingTime) {
        AbstractTimeObjectType timeObject = samplingTime.getTimeObject();
        TimePeriodType timePeriod = (TimePeriodType) timeObject;
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        Date date = null;
        try {
            DateTime dateTime = fmt.parseDateTime(timePeriod.getEndPosition().getStringValue());
            date = dateTime.toDate();
        } catch (Exception e) {
            log.error("Error when parsing Date: " + e.getMessage(), e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
