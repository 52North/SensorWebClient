/****************************************************************************
 * Copyright (C) 2010
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
 * 
 * Author: Jan Schulte
 * Created: 18.05.2010
 *****************************************************************************/
package org.n52.sos.feeder.baw.connector;

import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER;
import static org.n52.oxf.sos.adapter.ISOSRequestBuilder.GET_CAPABILITIES_SERVICE_PARAMETER;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.namespace.QName;

import net.opengis.gml.TimePeriodType;
import net.opengis.gml.TimePositionType;
import net.opengis.ogc.BinaryTemporalOpType;
import net.opengis.ogc.PropertyNameType;
import net.opengis.om.x10.ObservationCollectionDocument;
import net.opengis.ows.x11.ExceptionReportDocument;
import net.opengis.ows.x11.ExceptionType;
import net.opengis.sensorML.x101.SensorMLDocument;
import net.opengis.sos.x10.DescribeSensorDocument;
import net.opengis.sos.x10.DescribeSensorDocument.DescribeSensor;
import net.opengis.sos.x10.GetObservationDocument;
import net.opengis.sos.x10.GetObservationDocument.GetObservation;
import net.opengis.sos.x10.GetObservationDocument.GetObservation.EventTime;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.ServiceDescriptor;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.sos.capabilities.ObservationOffering;
import org.n52.oxf.sos.capabilities.SOSContents;
import org.n52.server.util.TimeUtil;
import org.n52.sos.feeder.baw.Configuration;
import org.n52.sos.feeder.baw.utils.IOHelper;
import org.n52.sos.feeder.baw.utils.SOSAdapter_01;
import org.n52.sos.feeder.baw.utils.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SOSConnector class manages the communication between the feeder and a
 * given SOS.
 *
 * @author Jan Schulte
 */
public class SOSConnector {

    /** The Constant log. */
    private static final Logger LOGGER = LoggerFactory.getLogger(SOSConnector.class);

    /** The sos url. */
    private String sosURL;

    /** The service version. */
    private String serviceVersion;

    /** The sos adapter. */
    private SOSAdapter_01 sosAdapter;

    /** The desc. */
    private ServiceDescriptor desc;

    /**
     * Instantiates a new SOSConnector.
     *
     * @param sosURL The url to send request to the SOS
     */
    public SOSConnector(String sosURL) {
        try {
            this.sosURL = sosURL;
            this.serviceVersion = Configuration.getInstance().getSosVersion();
            this.sosAdapter = new SOSAdapter_01(this.serviceVersion);
        }
        catch (IllegalStateException e) {
            LOGGER.debug("Configuration is not available (anymore).",e);
        }
    }

    /**
     * Initialize the connection to the SOS.
     *
     * @return true - if service is running
     */
    public boolean initService() {
        try {
            ParameterContainer paramCon = new ParameterContainer();
            paramCon.addParameterShell(GET_CAPABILITIES_ACCEPT_VERSIONS_PARAMETER,
                    this.serviceVersion);
            paramCon.addParameterShell(GET_CAPABILITIES_SERVICE_PARAMETER, "SOS");
            LOGGER.debug("GetCapabilitiesRequest to " + this.sosURL + ":\n"
                    + this.sosAdapter.getRequestBuilder().buildGetCapabilitiesRequest(paramCon));

            Operation getCapOperation = new Operation(SOSAdapter_01.GET_CAPABILITIES, this.sosURL + "?", this.sosURL);
            OperationResult opResult = this.sosAdapter.doOperation(getCapOperation, paramCon);
            this.desc = this.sosAdapter.initService(opResult);
        } catch (ExceptionReport e) {
            LOGGER.error("Error while init SOS service: " + e.getMessage());
            return false;
        } catch (OXFException e) {
            LOGGER.error("Error while init SOS serivce: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return true;
    }

    /**
     * Gets the offerings.
     *
     * @return A List of observation offerings for this SOS
     */
    public List<ObservationOffering> getOfferings() {
        ArrayList<ObservationOffering> obsOffs = new ArrayList<ObservationOffering>();
        SOSContents sosCont = (SOSContents) this.desc.getContents();
        for (int i = 0; i < sosCont.getDataIdentificationCount(); i++) {
            obsOffs.add(sosCont.getDataIdentification(i));
        }
        return obsOffs;
    }

    /**
     * Requests a sensorMLDocument by given procedure.
     *
     * @param procedure The given procedure
     * @return The SensorMLDocument
     */
    public SensorMLDocument getSensorML(String procedure){
        // create request
        DescribeSensorDocument descSensDoc = DescribeSensorDocument.Factory.newInstance();
        DescribeSensor descSens = descSensDoc.addNewDescribeSensor();
        descSens.setProcedure(procedure);
        descSens.setService("SOS");
        descSens.setVersion(serviceVersion);
        descSens.setOutputFormat("text/xml;subtype=\"sensorML/1.0.1\"");

        // send request
        XmlObject response = null;
        try {
            response = sendRequest(descSensDoc);
        } catch (IOException e) {
            LOGGER.error("Error while requesting sensorML document: " + e.getMessage());
        }
        // parse request to SensorML Document
        // FIXME add try catch if response is not SensorML
        try {
        	SensorMLDocument sensorML = (SensorMLDocument) response;
            return sensorML;
        } catch (Exception e) {
        	LOGGER.error("Problems during parsing of describe sensor response: " + e.getMessage(),e);
        }
        return null;
    }

    /**
     * Request a Observation by given parameters.
     *
     * @param procedure The procedure of the sensor
     * @param offering The offering
     * @param lastUpdate The last update of the observations
     * @param set The observed property
     * @return An observationCollection for the parameters
     * @throws Exception the exception
     */
    public ObservationCollectionDocument getObservation(String procedure, String offering, Calendar lastUpdate,
            String[] observedProperties) throws Exception {
        // create request
        GetObservationDocument getObsDoc = GetObservationDocument.Factory.newInstance();
        GetObservation getObs = getObsDoc.addNewGetObservation();
        // set version
        getObs.setVersion(serviceVersion);
        // set serviceType
        getObs.setService("SOS");
        // set Offering
        getObs.setOffering(offering);
        // set time
        EventTime eventTime = getObs.addNewEventTime();
        BinaryTemporalOpType binTempOp = BinaryTemporalOpType.Factory.newInstance();

        XmlCursor cursor = binTempOp.newCursor();
        cursor.toChild(new QName("http://www.opengis.net/ogc", "PropertyName"));
        cursor.setTextValue("urn:ogc:data:time:iso8601");

        SimpleDateFormat ISO8601FORMAT = TimeUtil.createIso8601Formatter();
        TimePeriodType timePeriod = TimePeriodType.Factory.newInstance();

        TimePositionType beginPosition = timePeriod.addNewBeginPosition();
        beginPosition.setStringValue(ISO8601FORMAT.format(lastUpdate.getTime()));

        TimePositionType endPosition = timePeriod.addNewEndPosition();
        Date date = new Date();
        endPosition.setStringValue(ISO8601FORMAT.format(date));
        LOGGER.debug("Update Time for " + procedure +": "+ date.getTime());

        binTempOp.setTimeObject(timePeriod);
        eventTime.setTemporalOps(binTempOp);

        // rename elements
        cursor = eventTime.newCursor();
        cursor.toChild(new QName("http://www.opengis.net/ogc", "temporalOps"));
        cursor.setName(new QName("http://www.opengis.net/ogc", "TM_During"));

        cursor.toChild(new QName("http://www.opengis.net/gml", "_TimeObject"));
        cursor.setName(new QName("http://www.opengis.net/gml", "TimePeriod"));

        getObs.setProcedureArray(new String[] { procedure });
        getObs.setObservedPropertyArray(observedProperties);
        getObs.setResponseFormat("text/xml;subtype=\"om/1.0.0\"");

        // send request
        XmlObject response = null;
        LOGGER.debug("GetObservation Request: " + getObsDoc);
        try {
            response = sendRequest(getObsDoc);
        } catch (IOException e) {
            LOGGER.error("Error while sending getObservation request: " + e.getMessage());
        }
        LOGGER.debug("GetObservation Response: " + response);
        // parse request to ObservationCollectionDocument
        if (response instanceof ObservationCollectionDocument) {
            return (ObservationCollectionDocument) response;
        } else if (response instanceof ExceptionReportDocument) {
            ExceptionReportDocument exRepDoc = (ExceptionReportDocument) response;
            ExceptionType[] exceptionArray = exRepDoc.getExceptionReport().getExceptionArray();
            throw new Exception(exceptionArray[0].getExceptionTextArray(0));
        }
        return null;
    }

    /**
     * Sends the request to the SOS.
     *
     * @param request the request
     * @return the xml object
     * @throws IOException Signals that an I/O exception has occurred.
     */
    private XmlObject sendRequest(XmlObject request) throws IOException {
        XmlObject response = null;
        String requestString = request.xmlText();

        InputStream responseIS = IOHelper.sendPostMessage(this.sosURL, requestString);
        
        try {
            response = replaceSpecialCharacters(XmlObject.Factory.parse(responseIS));
        } catch (XmlException e) {
            LOGGER.error("Error while parsing response stream: " + e.getMessage());
        }

        return response;
    }

    /**
     * Replace special characters.
     *
     * @param xmlObject the xml object
     * @return the xml object
     */
    private XmlObject replaceSpecialCharacters(XmlObject xmlObject) {
        String tempStr = xmlObject.toString();
        tempStr = tempStr.replace("Ä", "Ae");
        tempStr = tempStr.replace("Ö", "Oe");
        tempStr = tempStr.replace("Ü", "Ue");
        tempStr = tempStr.replace("ä", "ae");
        tempStr = tempStr.replace("ö", "oe");
        tempStr = tempStr.replace("ü", "ue");
        tempStr = tempStr.replace("ß", "ss");
        tempStr = tempStr.replace("´", "'");
        tempStr = tempStr.replace("`", "'");
        try {
            return XmlObject.Factory.parse(tempStr);
        } catch (XmlException e) {
            LOGGER.error("Error while replacing special characters: " + e.getMessage());
        }
        return null;
    }

    /**
     * Gets the desc.
     *
     * @return the desc
     */
    public ServiceDescriptor getDesc() {
        return this.desc;
    }

}
