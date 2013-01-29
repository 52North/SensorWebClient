
package org.n52.server.ses.feeder.connector;

import static org.n52.oxf.ses.adapter.ISESRequestBuilder.NOTIFY_SES_URL;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.NOTIFY_TOPIC;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.NOTIFY_TOPIC_DIALECT;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.NOTIFY_XML_MESSAGE;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_FROM;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_LIFETIME_DURATION;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_SENSORML;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_SES_URL;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_TOPIC;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.REGISTER_PUBLISHER_TOPIC_DIALECT;
import static org.n52.oxf.ses.adapter.SESAdapter.NOTIFY;
import static org.n52.oxf.ses.adapter.SESAdapter.REGISTER_PUBLISHER;
import static org.n52.server.ses.feeder.FeederConfig.getFeederConfig;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.sensorML.x101.SensorMLDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.oxf.ses.adapter.SESRequestBuilder_00;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.feeder.FeederConfig;
import org.n52.server.ses.feeder.SosSesFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SESConnector class manages the communication between the feeder and the SES.
 */
public class SESConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SESConnector.class);

    private SESAdapter sesAdapter;

    private String serviceUrl;

    private String topic;

    private String dialect;

    private boolean closed = false;

    public SESConnector() {
        try {
            sesAdapter = new SESAdapter();
            topic = FeederConfig.getFeederConfig().getSesDefaultTopic();
            dialect = FeederConfig.getFeederConfig().getSesDefaultTopicDialect();
            serviceUrl = SesConfig.sesEndpoint;
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available.", e);
        }
    }

    /**
     * Register sensor description as publisher at the SES.
     * 
     * @param sensorML
     *        The sensor description as SensorML 1.0.1.
     * @return The publisher id given by the SES.
     * @throws ExceptionReport
     *         if registering fails.
     */
    public String registerPublisher(SensorMLDocument sensorML) throws ExceptionReport {
        LOGGER.trace("registerPublisher with sensorML: \n{}", sensorML.xmlText());
        try {
            String defaultTopicDialect = getFeederConfig().getSesDefaultTopicDialect();
            String defaultTopic = getFeederConfig().getSesDefaultTopic();
            String lifetime = getFeederConfig().getSesLifetimeDuration();
            String localEndpoint = getFeederConfig().getSesEndpoint();
            
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(REGISTER_PUBLISHER_SES_URL, serviceUrl);
            parameter.addParameterShell(REGISTER_PUBLISHER_SENSORML, sensorML.xmlText());
            parameter.addParameterShell(REGISTER_PUBLISHER_TOPIC_DIALECT, defaultTopicDialect);
            parameter.addParameterShell(REGISTER_PUBLISHER_LIFETIME_DURATION, lifetime);
            parameter.addParameterShell(REGISTER_PUBLISHER_TOPIC, defaultTopic);
            parameter.addParameterShell(REGISTER_PUBLISHER_FROM, localEndpoint);

            Operation operation = new Operation(REGISTER_PUBLISHER, serviceUrl + "?", serviceUrl);
            OperationResult result = sesAdapter.doOperation(operation, parameter);
            XmlObject response = sesAdapter.handleResponse(REGISTER_PUBLISHER, result.getIncomingResultAsStream());
            LOGGER.trace("RegisterPublisher response: \n {}", response.xmlText());
            
            // TODO use XPath to get Publisher ID
            String tmp = response.toString();
            String sesID = tmp.substring(tmp.indexOf('>', tmp.indexOf("ResourceId")) + 1,
                                         tmp.indexOf('<', tmp.indexOf("ResourceId")));

            return sesID;
        }
        catch (OXFException e) {
            LOGGER.error("Error while sending registerPublisher request to SES.", e);
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available (anymore).", e);
        }
        return null;
    }

    /**
     * Send a given Observation to the SES.
     * 
     * @param obsPropType
     *        The given Observation
     * @return true - when sending successful
     * @throws OXFException
     * @throws ExceptionReport
     */
    public boolean publishObservation(ObservationPropertyType obsPropType) throws OXFException, ExceptionReport {
        LOGGER.trace("publishObservation()");

        try {
            for (String observation : getSingleObservations(obsPropType)) {
                if (SosSesFeeder.active) {
                    letSesFilter(observation);
                }
                else {
                    LOGGER.info("Feeding stopped. Service is going to shutdown.");
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Error while splitting observations. Send observations in one request.", e);
            sendNotificationFor(obsPropType.xmlText());
            return true;
        }
        return false;
    }

    private void letSesFilter(String observation) throws ExceptionReport {
        sendNotificationFor(observation);
        try {
            Thread.sleep(500);
        }
        catch (InterruptedException e) {
            //
        }
    }

    private void sendNotificationFor(String singleObservation) throws ExceptionReport {
        try {
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(NOTIFY_SES_URL, serviceUrl);
            parameter.addParameterShell(NOTIFY_TOPIC, topic);
            parameter.addParameterShell(NOTIFY_TOPIC_DIALECT, dialect);
            parameter.addParameterShell(NOTIFY_XML_MESSAGE, singleObservation);

            LOGGER.trace("Notify request: \n {}", new SESRequestBuilder_00().buildNotifyRequest(parameter));

            Operation operation = new Operation(NOTIFY, serviceUrl + "?", serviceUrl);
            OperationResult doOperation = sesAdapter.doOperation(operation, parameter);
            
            LOGGER.trace("Notify response: \n {}", XmlObject.Factory.parse(doOperation.getIncomingResultAsStream()));
        }
        catch (XmlException e) {
            LOGGER.warn("Could not parse notify response.", e);
        }
        catch (IOException e) {
            LOGGER.warn("Could not read notify response.", e);
        }
        catch (OXFException e) {
            LOGGER.error("Error while sending notify message to SES.", e);
        }
        catch (NullPointerException e) {
            LOGGER.debug("Response of notify is null.", e);
        }
    }

    private String[] getSingleObservations(ObservationPropertyType observations) {

        String obsPropType = observations.xmlText();
        
        // Determining how many observations are contained in the observation collection
        Pattern countPattern = Pattern.compile("<swe:value>(.*?)</swe:value>");
        Matcher countMatcher = countPattern.matcher(obsPropType);
        String countString = null;
        if (countMatcher.find()) {
            countString = countMatcher.group(1).trim();
        }
        int observationCount = Integer.parseInt(countString);

        // This array will contain one observation string for each observation of the observation
        // collection
        String[] outputStrings;

        // If the observation collection contains only one value it can be directly returned
        if (observationCount == 1) {
            outputStrings = new String[] {obsPropType};
        }

        // If the observation collection contains more than one value it must be split
        else {

            // Extracting the values that are contained in the observation collection and creating a
            // StringTokenizer that allows to access the values
            Pattern valuesPattern = Pattern.compile("<swe:values>(.*?)</swe:values>");
            Matcher valuesMatcher = valuesPattern.matcher(obsPropType);
            String valuesString = null;
            if (valuesMatcher.find()) {
                valuesString = valuesMatcher.group(1).trim();
            }

            StringTokenizer valuesTokenizer = new StringTokenizer(valuesString, ";");
            // If only the latest observation is wished, find youngest
            // observation.
            if (FeederConfig.getFeederConfig().isOnlyYoungestName()) {
                DateTime youngest = new DateTime(0);
                String youngestValues = "";
                DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
                while (valuesTokenizer.hasMoreElements()) {
                    String valueString = (String) valuesTokenizer.nextElement();
                    DateTime time = fmt.parseDateTime(valueString.split(",")[0]);
                    if (time.isAfter(youngest.getMillis())) {
                        youngest = time;
                        youngestValues = valueString;
                    }
                }
                outputStrings = new String[] {createSingleObservationString(obsPropType, youngestValues)};
            }
            else {
                outputStrings = new String[observationCount];

                for (int i = 0; i < observationCount; i++) {

                    // Add the extracted observation to an array containing
                    // all extracted observations
                    outputStrings[i] = createSingleObservationString(obsPropType, valuesTokenizer.nextToken());
                }
            }

        }
        // Returning the extracted observations
        return outputStrings;
    }

    private String createSingleObservationString(String observation, String individualValuesString) {
        // This string will contain the next extracted single observation
        String singleObservationString = observation;

        // // Replace the id of the observation collection
        // singleObservationString = singleObservationString.replaceFirst(idString, idString + "_" + i);

        // Replace the time period of the observation with the
        // time of the contained observation
        Pattern timePattern = Pattern.compile("(.*?),");
        Matcher timeMatcher = timePattern.matcher(individualValuesString);
        String timeString = "";
        if (timeMatcher.find()) {
            timeString = timeMatcher.group(1).trim();
        }
        singleObservationString = singleObservationString.replaceAll("<gml:beginPosition>[^<]*</gml:beginPosition>",
                                                                     "<gml:beginPosition>" + timeString
                                                                             + "</gml:beginPosition>");
        singleObservationString = singleObservationString.replaceAll("<gml:endPosition>[^<]*</gml:endPosition>",
                                                                     "<gml:endPosition>" + timeString
                                                                             + "</gml:endPosition>");

        // Set the number of elements in the observation
        // collection to 1
        singleObservationString = singleObservationString.replaceAll("<swe:value>[^<]*</swe:value>",
                                                                     "<swe:value>1</swe:value>");

        // Replace the elements in the values element with one
        // singe value
        singleObservationString = singleObservationString.replaceAll("<swe:values>[^<]*</swe:values>", "<swe:values>"
                + individualValuesString + ";</swe:values>");

        return singleObservationString;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

}
