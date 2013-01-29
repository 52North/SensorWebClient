
package org.n52.server.ses.feeder.connector;

import static org.n52.oxf.ses.adapter.ISESRequestBuilder.GET_CAPABILITIES_SES_URL;
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
import static org.n52.oxf.ses.adapter.SESAdapter.GET_CAPABILITIES;
import static org.n52.oxf.ses.adapter.SESAdapter.NOTIFY;
import static org.n52.oxf.ses.adapter.SESAdapter.REGISTER_PUBLISHER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.opengis.om.x10.ObservationPropertyType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.ses.x00.CapabilitiesDocument;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.oxf.ses.adapter.SESRequestBuilder_00;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.feeder.FeederConfig;
import org.n52.server.ses.feeder.SosSesFeeder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;

/**
 * The SESConnector class manages the communication between the feeder and the SES.
 */
public class SESConnector {

    private static final Logger LOGGER = LoggerFactory.getLogger(SESConnector.class);

    private SESAdapter sesAdapter;

    private String sesUrl;

    private String topic;

    private String dialect;
    
    private boolean closed = false;

    public SESConnector() {
        this.sesAdapter = new SESAdapter();
        try {
            this.topic = FeederConfig.getInstance().getSesDefaultTopic();
            this.dialect = FeederConfig.getInstance().getSesDefaultTopicDialect();
            this.sesUrl = SesConfig.sesEndpoint;
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available (anymore).", e);
        }
    }

    /**
     * @return true - if service is running
     */
    public boolean initService() {
        LOGGER.trace("initService()");

        try {
            ServiceDescriptor desc = this.sesAdapter.initService(sesUrl);
            LOGGER.trace(desc.toXML());
        }
        catch (ExceptionReport e) {
            LOGGER.error("SES not accessible.", e);
            return false;
        }
        catch (OXFException e) {
            LOGGER.error("SES '{}' not accessible.", sesUrl, e);
            return false;
        }
        return true;
    }

    /**
     * Register the publisher by sending a sensorML document to the SES.
     * 
     * @param sensorML
     *        The sensorML document
     * @return The ID given by the SES
     * @throws ExceptionReport 
     */
    public String registerPublisher(SensorMLDocument sensorML) throws ExceptionReport {
        LOGGER.trace("registerPublisher");

        ParameterContainer parameter = new ParameterContainer();

        OperationResult opRes;

        try {

            String defaultTopicDialect = FeederConfig.getInstance().getSesDefaultTopicDialect();
            String defaultTopic = FeederConfig.getInstance().getSesDefaultTopic();
            String lifetime = FeederConfig.getInstance().getSesLifetimeDuration();
            String localEndpoint = FeederConfig.getInstance().getSesEndpoint();
            String sensorMLString = sensorML.toString();
            parameter.addParameterShell(REGISTER_PUBLISHER_SES_URL, sesUrl);
            parameter.addParameterShell(REGISTER_PUBLISHER_SENSORML, sensorMLString);
            parameter.addParameterShell(REGISTER_PUBLISHER_TOPIC_DIALECT, defaultTopicDialect);
            parameter.addParameterShell(REGISTER_PUBLISHER_TOPIC, defaultTopic);
            parameter.addParameterShell(REGISTER_PUBLISHER_LIFETIME_DURATION, lifetime);
            parameter.addParameterShell(REGISTER_PUBLISHER_FROM, localEndpoint);

            opRes = sesAdapter.doOperation(new Operation(REGISTER_PUBLISHER,
                                                              this.sesUrl + "?",
                                                              this.sesUrl), parameter);

            XmlObject response = sesAdapter.handleResponse(REGISTER_PUBLISHER, opRes.getIncomingResultAsStream());

            LOGGER.debug("RegisterPublisher response: \n" + response);
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

        String observationXML = obsPropType.xmlText();
        String[] observations;

        // split in individual observations
        try {
            observations = splitObservations(observationXML);
        }
        catch (Exception e1) {
            LOGGER.error("Error while splitting observations. Send observations in one request.");
            createAndSendRequest(observationXML);
            return true;
        }

        // send request as single observation
        for (String observation : observations) {
            if (SosSesFeeder.active) {
                createAndSendRequest(observation);
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    //
                }
            } else {
                throw new OXFException("Servlet is stopped");
            }
        }

        return false;
    }

    private void createAndSendRequest(String observationXML) throws ExceptionReport {
        ParameterContainer parameter = new ParameterContainer();
        try {
            parameter.addParameterShell(NOTIFY_SES_URL, sesUrl);
            parameter.addParameterShell(NOTIFY_TOPIC, topic);
            parameter.addParameterShell(NOTIFY_TOPIC_DIALECT, dialect);
            parameter.addParameterShell(NOTIFY_XML_MESSAGE, observationXML);

            LOGGER.trace("Notify request: \n" + new SESRequestBuilder_00().buildNotifyRequest(parameter));

            OperationResult doOperation = this.sesAdapter.doOperation(new Operation(NOTIFY, this.sesUrl + "?", this.sesUrl), parameter);
            try {
				LOGGER.trace("Notify response: \n" + XmlObject.Factory.parse(doOperation.getIncomingResultAsStream()));
			} catch (XmlException e) {
				LOGGER.warn("Could not parse notify response.", e);
			} catch (IOException e) {
                LOGGER.warn("Could not read notify response.", e);
			}

        }
        catch (OXFException e) {
            LOGGER.error("Error while sending notify message to SES.", e);
        }
        catch (NullPointerException e) {
            LOGGER.debug("Response of notify is null.", e);
        }
    }

    /**
     * Gets the content lists.
     * 
     * @return the content lists
     * @throws ExceptionReport 
     */
    public List<String> getContentLists() throws ExceptionReport {
        ArrayList<String> registeredSensors = new ArrayList<String>();

        ParameterContainer parameter = new ParameterContainer();

        OperationResult opsRes;
        try {
            parameter.addParameterShell(GET_CAPABILITIES_SES_URL, this.sesUrl);
            opsRes = this.sesAdapter.doOperation(new Operation(GET_CAPABILITIES,
                                                               this.sesUrl + "?",
                                                               this.sesUrl), parameter);

            Envelope env = EnvelopeDocument.Factory.parse(opsRes.getIncomingResultAsStream()).getEnvelope();
            Body body = env.getBody();

            XmlObject result = XmlObject.Factory.parse(body.toString());
            if (result instanceof CapabilitiesDocument) {
                CapabilitiesDocument capDoc = (CapabilitiesDocument) result;
                if (capDoc.getCapabilities().getContents().isSetRegisteredSensors()) {
                    String[] sensors = capDoc.getCapabilities().getContents().getRegisteredSensors().getSensorIDArray();
                    for (int i = 0; i < sensors.length; i++) {
                        registeredSensors.add(sensors[i]);
                        LOGGER.debug("Sensor is in the SES: " + sensors[i]);
                    }
                }
            }
            else {
                LOGGER.error("Get no valid capabilities!");
                registeredSensors = null;
                LOGGER.debug(result.xmlText());
            }
        }
        catch (OXFException e) {
            LOGGER.error("Error while init SES.", e);
        }
        catch (XmlException e) {
            LOGGER.error("Error while init SES.", e);
        }
        catch (IOException e) {
            LOGGER.error("Error while init SES.", e);
        }
        return registeredSensors;
    }

    private String[] splitObservations(String inputObservation) {

        // Determining how many observations are contained in the observation collection
        Pattern countPattern = Pattern.compile("<swe:value>(.*?)</swe:value>");
        Matcher countMatcher = countPattern.matcher(inputObservation);
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
            outputStrings = new String[]{inputObservation};
        }

        // If the observation collection contains more than one value it must be split
        else {

            // Extracting the values that are contained in the observation collection and creating a
            // StringTokenizer that allows to access the values
            Pattern valuesPattern = Pattern.compile("<swe:values>(.*?)</swe:values>");
            Matcher valuesMatcher = valuesPattern.matcher(inputObservation);
            String valuesString = null;
            if (valuesMatcher.find()) {
                valuesString = valuesMatcher.group(1).trim();
            }
            
            // Read the id of the observation collection
            Pattern idPattern =
                    Pattern.compile("ObservationCollection gml:id=\"(.*?)\"(.*?)xsi:schemaLocation=");
            Matcher idMatcher = idPattern.matcher(inputObservation);
            String idString = "";
            if (idMatcher.find()) {
                idString = idMatcher.group(1).trim();
            }
            
            StringTokenizer valuesTokenizer = new StringTokenizer(valuesString, ";");
            // If only the latest observation is wished, find youngest
            // observation.
            if (FeederConfig.getInstance().isOnlyYoungestName()) {
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
                outputStrings = new String[]{createSingleObservationString(inputObservation, youngestValues)};
            } else {
                outputStrings = new String[observationCount];

                for (int i = 0; i < observationCount; i++) {
                    
                    // Add the extracted observation to an array containing
                    // all extracted observations
                    outputStrings[i] = createSingleObservationString(inputObservation, valuesTokenizer.nextToken());
                }
            }

        }
        // Returning the extracted observations
        return outputStrings;
    }

    private String createSingleObservationString(String observation, String individualValuesString) {
        // This string will contain the next extracted single observation
        String singleObservationString = observation;

//        // Replace the id of the observation collection
//        singleObservationString = singleObservationString.replaceFirst(idString, idString + "_" + i);

        // Replace the time period of the observation with the
        // time of the contained observation
        Pattern timePattern = Pattern.compile("(.*?),");
        Matcher timeMatcher = timePattern.matcher(individualValuesString);
        String timeString = "";
        if (timeMatcher.find()) {
            timeString = timeMatcher.group(1).trim();
        }
        singleObservationString =
                singleObservationString.replaceAll("<gml:beginPosition>[^<]*</gml:beginPosition>",
                        "<gml:beginPosition>" + timeString + "</gml:beginPosition>");
        singleObservationString =
                singleObservationString.replaceAll("<gml:endPosition>[^<]*</gml:endPosition>",
                        "<gml:endPosition>" + timeString + "</gml:endPosition>");

        // Set the number of elements in the observation
        // collection to 1
        singleObservationString =
                singleObservationString.replaceAll("<swe:value>[^<]*</swe:value>",
                        "<swe:value>1</swe:value>");

        // Replace the elements in the values element with one
        // singe value
        singleObservationString =
                singleObservationString.replaceAll("<swe:values>[^<]*</swe:values>", "<swe:values>"
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
