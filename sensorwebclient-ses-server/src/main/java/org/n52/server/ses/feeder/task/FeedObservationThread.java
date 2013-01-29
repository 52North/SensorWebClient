
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
import org.n52.server.ses.feeder.util.DatabaseAccess;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;
import org.n52.shared.serializable.pojos.TimeseriesFeed;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedObservationThread.class);

    private TimeseriesFeed timeseriesFeed;

    private boolean running = true;
    
    private SESConnector sesCon;

    private Vector<String> currentlyFeedingSensors;

    public FeedObservationThread(TimeseriesFeed timeseriesFeed, Vector<String> v) {
        super("timeseriesId_" + timeseriesFeed.getTimeseriesId());
        this.timeseriesFeed = timeseriesFeed;
        this.currentlyFeedingSensors = v;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * Collects and sends observation to the SES.
     */
    @Override
    public void run() {

        if (isRunning()) {
            TimeseriesMetadata metadata = timeseriesFeed.getTimeseriesMetadata();
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Start Observation thread for Sensor: " + metadata.getProcedure());
                }

                SOSConnector sosCon = new SOSConnector(metadata.getServiceUrl());
                sesCon = new SESConnector();

                // do an observation request to sos for the sensor
                Calendar startUpdate = this.timeseriesFeed.getLastUpdate();
                if(LOGGER.isDebugEnabled()) {
                	LOGGER.debug("Start Time of Database for " + 
                			metadata.getProcedure() + ": " + 
                			(startUpdate!=null?startUpdate.getTimeInMillis():
                				"not defined => first time feeded") );
                }
                Calendar endUpdate = null;
                if (startUpdate == null) {
                    // create start timestamp for feeding observations
                    Calendar firstUpdateTime = new GregorianCalendar();
                    firstUpdateTime.add(Calendar.MILLISECOND, -FeederConfig.getFeederConfig().getStartTimestamp());
                    timeseriesFeed.setLastUpdate(firstUpdateTime);
                    LOGGER.debug("Start Time generated for first feeding of " + metadata.getProcedure() +": "+ timeseriesFeed.getLastUpdate().getTimeInMillis());
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
                
				ObservationCollectionDocument obsCollDoc = sosCon.getObservation(timeseriesFeed);
				ObservationPropertyType[] memberArray = obsCollDoc.getObservationCollection().getMemberArray();
				// tell the others, that we are trying to feed observations
				boolean addResult = this.currentlyFeedingSensors.add(metadata.getProcedure());
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Added sensor \"" + metadata.getProcedure() + "\" to feeding list? " + addResult);
				}
				for (ObservationPropertyType obsPropType : memberArray) {
					// start feeding
					ObservationType observation = obsPropType.getObservation();
					if (observation != null && observation.getProcedure().getHref().equals(metadata.getProcedure())) {
						endUpdate = getLastUpdateTime(observation.getSamplingTime());
						this.timeseriesFeed.setUpdateInterval(getUpdateInterval(observation, startUpdate));
						LOGGER.info(metadata.getProcedure() + " from " + startUpdate.getTime() + " to " + endUpdate.getTime() + " for " + metadata.getOffering()); 
						// create a Notify Message to the SES
						obsPropType = checkObservations(obsPropType);
						if (!(obsPropType == null) && !sesCon.isClosed()) {
							sesCon.publishObservation(obsPropType);
							LOGGER.info(metadata.getProcedure() + " with " + metadata.getOffering() + " added to SES");
						} else {
							LOGGER.info(String.format("No data received for procedure '%s'.", metadata.getProcedure()));
						}
					} else {
						LOGGER.info("No new Observations for "	+ metadata.getProcedure());
					}
				}
                if (endUpdate != null) {
                    // to prevent receiving observation two times
                    LOGGER.debug("End Time before adding for " + metadata.getProcedure() +": "+ endUpdate.getTimeInMillis());
                    endUpdate.add(Calendar.MILLISECOND, 1);
                    LOGGER.debug("End Time after adding for " + metadata.getProcedure() +": "+ endUpdate.getTimeInMillis());
                    this.timeseriesFeed.setLastUpdate(endUpdate);
                }
                DatabaseAccess.saveTimeseriesFeed(this.timeseriesFeed);
            } catch (IllegalStateException e) {
                LOGGER.warn("Failed to create SOS/SES Connection.", e);
                return; // maybe shutdown .. try again
            } catch (InterruptedException e) {
                LOGGER.trace(e.getMessage(), e);
            } catch (HibernateException e) {
                LOGGER.warn("Datebase problem has occured: " + e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.warn("Could not request and publish Observation to SES: " + e.getMessage(), e);
            } finally {
                //
                // Feeding finished or failed because of exception
                // => remove this sensor from the list of
                // currentlyFeedingSensors
                // remove() returns only true if the element was contained in
                // the list
                //
                boolean removeResult = this.currentlyFeedingSensors.removeElement(metadata.getProcedure());
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("removed sensor \"" + metadata.getProcedure() + "\" from currently feeding list? "
                            + removeResult);
                }
            }
        }
    }

    private ObservationPropertyType checkObservations(ObservationPropertyType obsPropType) {
        // zum tag hin navigieren
        XmlCursor cResult = obsPropType.getObservation().getResult().newCursor();
        cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
        DataArrayDocument dataArrayDoc = null;
        try {
            dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
        } catch (XmlException e) {
            LOGGER.error(e.getMessage());
        }
        TextBlock textBlock = dataArrayDoc.getDataArray1().getEncoding().getTextBlock();
        String tokenSeparator = textBlock.getTokenSeparator();
        String blockSeperator = textBlock.getBlockSeparator();

        String values = dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().getNodeValue();
        String[] blocks = values.split(blockSeperator);
        StringBuffer newValues = new StringBuffer();
        try {
            List<String> noDatas = FeederConfig.getFeederConfig().getNoDatas();
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
            LOGGER.debug("Configuration not available (anymore).", e);
        }

        if (newValues.toString().equals("")) {
            return null;
        }
        dataArrayDoc.getDataArray1().getValues().getDomNode().getFirstChild().setNodeValue(newValues.toString());
        obsPropType.getObservation().getResult().set(dataArrayDoc);
        return obsPropType;
    }

    private long getUpdateInterval(ObservationType observation, Calendar newestUpdate) {
        long updateInterval = 0;
        try {

            updateInterval = FeederConfig.getFeederConfig().getUpdateInterval();
            XmlCursor cResult = observation.getResult().newCursor();
            cResult.toChild(new QName("http://www.opengis.net/swe/1.0.1", "DataArray"));
            DataArrayDocument dataArrayDoc = null;
            try {
                dataArrayDoc = DataArrayDocument.Factory.parse(cResult.getDomNode());
            } catch (XmlException e) {
                LOGGER.error("Error when parsing DataArray: " + e.getMessage());
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
                    LOGGER.error("Error when parsing Date: " + e.getMessage(), e);
                }
            }

            if (blockArray.length >= 2) {
                String[] valueArrayFirst = blockArray[blockArray.length - 2].split(tokenSeparator);
                String[] valueArrayLast = blockArray[blockArray.length - 1].split(tokenSeparator);
                DateTime dateTimeFirst = fmt.parseDateTime(valueArrayFirst[0]);
                DateTime dateTimeLast = fmt.parseDateTime(valueArrayLast[0]);
                updateInterval = dateTimeLast.getMillis() - dateTimeFirst.getMillis();
            }

            if (updateInterval <= FeederConfig.getFeederConfig().getUpdateInterval()) {
                return FeederConfig.getFeederConfig().getUpdateInterval();
            }
        } catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available (anymore).", e);
        }

        return updateInterval;
    }

    private Calendar getLastUpdateTime(TimeObjectPropertyType samplingTime) {
        AbstractTimeObjectType timeObject = samplingTime.getTimeObject();
        TimePeriodType timePeriod = (TimePeriodType) timeObject;
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        Date date = null;
        try {
            DateTime dateTime = fmt.parseDateTime(timePeriod.getEndPosition().getStringValue());
            date = dateTime.toDate();
        } catch (Exception e) {
            LOGGER.error("Error when parsing Date: " + e.getMessage(), e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
