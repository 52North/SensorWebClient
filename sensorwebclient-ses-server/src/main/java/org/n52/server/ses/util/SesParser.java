/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.server.ses.util;

import static org.n52.oxf.ses.adapter.SESAdapter.GET_CAPABILITIES;
import static org.n52.server.ses.util.SesServerUtil.getBrokerUrl;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.xmlbeans.XmlCursor;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.ISESRequestBuilder;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.oxf.ses.adapter.SESUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3.x2003.x05.soapEnvelope.Body;
import org.w3.x2003.x05.soapEnvelope.Envelope;
import org.w3.x2003.x05.soapEnvelope.EnvelopeDocument;
import org.w3.x2003.x05.soapEnvelope.Header;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class SesParser {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SesParser.class);

    // 1. GetCapabilities --> registeredSensors --> SensorID
    // 2. DescribeSensor(SensorID)
    // 3. get all values

    // get Station: DescribeSensor
    // get Sensor: GetCapabilities
    // get Phenomenon: DescribeSensor

    private ArrayList<String> stations;
    private ArrayList<String> sensors;
    private ArrayList<String> phenomena;

    private String serviceVersion;
    private String sesEndpoint;

    /**
     * @param serviceVersion
     * @param sesEndpoint
     */
    public SesParser(String serviceVersion, String sesEndpoint){
        this.serviceVersion = serviceVersion;
        this.sesEndpoint = sesEndpoint;
    }


    /**
     * @param sensor
     * @return phenomena from given sensor
     */
    public synchronized ArrayList<String> getPhenomena(String sensor) {
        this.phenomena = new ArrayList<String>();

        String phenomenon = parseDescribeSensor_getPhenomenon(sensor);
        if (phenomenon != null) {
            this.phenomena.add(phenomenon);
        }

        return this.phenomena;
    }

    /**
     * @return registeredSensors from SES
     */
    @Deprecated
    public synchronized ArrayList<String> getRegisteredSensors(){
        this.sensors = getCapabilities(this.serviceVersion, this.sesEndpoint);
        return this.sensors;
    }

    /**
     * get sensors from capability request
     */
    @Deprecated
    private synchronized ArrayList<String> getCapabilities(String serviceVersion, String sesEndpoint) {
        //List of all availible sensors
        ArrayList<String> sensors = new ArrayList<String>();

        String SensorID = "SensorID";
        
        try {
            SESAdapter adapter = new SESAdapter(serviceVersion);

            Operation op = new Operation(GET_CAPABILITIES, null, getBrokerUrl(sesEndpoint));

            ParameterContainer paramCon = new ParameterContainer();

            paramCon.addParameterShell(ISESRequestBuilder.GET_CAPABILITIES_SES_URL, sesEndpoint);
            OperationResult opResult = adapter.doOperation(op, paramCon);

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            
//            StringBuilder sb = new StringBuilder();
//            BufferedReader reader = new BufferedReader(new InputStreamReader(opResult.getIncomingResultAsStream()));
//            String line = null;
//            while ((line = reader.readLine()) != null) {
//                sb.append(line);
//            }
//            
//            LOGGER.debug(sb.toString());
            
            Document doc = docBuilder.parse(opResult.getIncomingResultAsStream());

            //parse <SensorID>
            NodeList sensorIDList = doc.getElementsByTagName(SensorID);
            for (int i = 0; i < sensorIDList.getLength(); i++) {
                Node sensorIDNode = sensorIDList.item(i);
                sensors.add(sensorIDNode.getTextContent());
            }

        } catch (OXFException e) {
            LOGGER.error("Could not getCapabilities", e); // XXX clearer msg 
        } catch (ExceptionReport e) {
            LOGGER.error("Could not getCapabilities", e); // XXX clearer msg 
        } catch (SAXException e) {
            LOGGER.error("Could not getCapabilities", e); // XXX clearer msg 
        } catch (IOException e) {
            LOGGER.error("Could not getCapabilities", e); // XXX clearer msg 
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not getCapabilities", e); // XXX clearer msg 
        }
        return sensors;
    }

    /**
     * Describe sensor request to get phenomenon
     */
    @Deprecated
    private synchronized String describeSensor(String sensorID, String sesEndpoint){
        LOGGER.debug("describeSensor: " + sensorID);
        String field = "swe:field";
        String value = "swe:value";
        try {
            // build parameterContainer for describeSensorRequest
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SES_URL, sesEndpoint);
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SENSOR_ID, sensorID);

            // describeSensor
            URL url = new URL(sesEndpoint);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestProperty("Content-Type", "text/xml");
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setDoInput(true);
            PrintWriter pw = new PrintWriter(connect.getOutputStream());
            pw.write(buildDescribeSensorRequest(parameter));
            pw.close();

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(connect.getInputStream());

            // parse <swe:field>
            NodeList sensorIDList = doc.getElementsByTagName(field);
            for (int i = 0; i < sensorIDList.getLength(); i++) {
                if (sensorIDList.item(i).getAttributes().item(0).getTextContent().equals("river")) {
                    Node sensorIDNode = sensorIDList.item(i);
                    Element fstElement = (Element) sensorIDNode;

                    // parse <swe:value>
                    NodeList valueList = fstElement.getElementsByTagName(value);
                    return valueList.item(0).getTextContent();
                }
            }
        } catch (OXFException e) {
            LOGGER.error("describeSensor failed for: {}", sensorID, e); // XXX clearer msg 
        } catch (SAXException e) {
            LOGGER.error("describeSensor failed for: {}", sensorID, e); // XXX clearer msg 
        } catch (IOException e) {
            LOGGER.error("describeSensor failed for: {}", sensorID, e); // XXX clearer msg 
        } catch (ParserConfigurationException e) {
            LOGGER.error("describeSensor failed for: {}", sensorID, e); // XXX clearer msg 
        }
        return null;
    }

    private synchronized String parseDescribeSensor_getPhenomenon(String sensorID){
    	// TODO Adjust to be more generice (use FUTURE expected XML structure)
    	//
    	//
    	// FIXME put in configuration + add documentation (include xml example!)
    	String classifier = "sml:classifier";
        String value = "sml:value";
        String phenomenon = "phenomenon";
        /*
         *
         *
         * Expected XML structure now
         *	
         * <sml:SensorML>
         * [...]
         * <sml:classification>
         * 	   <sml:ClassifierList>
         * 			[...]
         *			<sml:classifier name="phenomenon">
         *       		<sml:Term>
         * 					<sml:value>PHENOMENON</sml:value>
         * 
         * 
         * FUTURE (more generic because each sensor which observes something 
         * 			has an output)
		 *
         * <sml:SensorML>
         * [...]
         * <sml:member>
         * 		<sml:System>
         * 			[...]
         * 			<sml:outputs>
         * 				<sml:OutputList>
         * 					[...]
         * 					<sml:output name="Temperatur">
         * 					   <swe:ObservableProperty definition="Temperatur"/>
         * 						<!-- take from the definiton ^^^ -->
         * 
         * 
         */
        try {
//            SESAdapter adapter = new SESAdapter(this.serviceVersion);
            
            // build parameterContainer for describeSensorRequest
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SES_URL, this.sesEndpoint);
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SENSOR_ID, sensorID);
            
//            Operation op = new Operation(SESAdapter.DESCRIBE_SENSOR, this.sesEndpoint + "?", this.sesEndpoint);
////            
//            OperationResult opResult = adapter.doOperation(op, parameter);

            // describeSensor
            URL url = new URL(this.sesEndpoint);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestProperty("Content-Type", "text/xml");
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setDoInput(true);
            PrintWriter pw = new PrintWriter(connect.getOutputStream());
            pw.write(buildDescribeSensorRequest(parameter));
            pw.close();

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(connect.getInputStream());

            // parse <sml:classifier>
            NodeList sensorIDList = doc.getElementsByTagName(classifier);
            for (int i = 0; i < sensorIDList.getLength(); i++) {
                if (sensorIDList.item(i).getAttributes().item(0).getTextContent().equals(phenomenon)) {
                    Node sensorIDNode = sensorIDList.item(i);
                    Element fstElement = (Element) sensorIDNode;

                    // parse <sml:value>
                    NodeList valueList = fstElement.getElementsByTagName(value);
                    return valueList.item(0).getTextContent();
                }
            }
            connect.disconnect();
        } catch (OXFException e) {
            LOGGER.error("Could not parse phenomenon", e); // XXX clearer msg 
        } catch (SAXException e) {
            LOGGER.error("Could not parse phenomenon", e); // XXX clearer msg 
        } catch (IOException e) {
            LOGGER.error("Could not parse phenomenon", e); // XXX clearer msg 
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not parse phenomenon", e); // XXX clearer msg 
        } 
        return null;
    }

    private synchronized String parseDescribeSensor_getSensor(String sensorID, String station){
        String field = "swe:field";
        String value = "swe:value";

        try {
            // build parameterContainer for describeSensorRequest
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SES_URL, this.sesEndpoint);
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SENSOR_ID, sensorID);

            // describeSensor
            URL url = new URL(this.sesEndpoint);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestProperty("Content-Type", "text/xml");
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setDoInput(true);
            PrintWriter pw = new PrintWriter(connect.getOutputStream());
            pw.write(buildDescribeSensorRequest(parameter));
            pw.close();

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(connect.getInputStream());

            // parse <swe:field>
            NodeList sensorIDList = doc.getElementsByTagName(field);
            for (int i = 0; i < sensorIDList.getLength(); i++) {
                if (sensorIDList.item(i).getAttributes().item(0).getTextContent().equals("river")) {
                    Node sensorIDNode = sensorIDList.item(i);
                    Element fstElement = (Element) sensorIDNode;

                    // parse <swe:value>
                    NodeList valueList = fstElement.getElementsByTagName(value);
                    if (valueList.item(0).getTextContent().equals(station)) {
                        return sensorID;
                    }
                }
            }
        } catch (OXFException e) {
            LOGGER.error("Could not get sensor from sensorML", e); // XXX clearer msg 
        } catch (SAXException e) {
            LOGGER.error("Could not get sensor from sensorML", e); // XXX clearer msg 
        } catch (IOException e) {
            LOGGER.error("Could not get sensor from sensorML", e); // XXX clearer msg 
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not get sensor from sensorML", e); // XXX clearer msg 
        }
        return null;
    }

    /**
     * build describe sensor request. This was a work-around because the implemented
     * request in the OX-Framework didn't work.
     */
    @Deprecated
    private synchronized String buildDescribeSensorRequest(ParameterContainer parameter) {
        String ns_addressing = "http://www.w3.org/2005/08/addressing";
        String request = "";
        String ns_ses = "http://www.opengis.net/ses/0.0";

        EnvelopeDocument envDoc = EnvelopeDocument.Factory.newInstance();
        Envelope env = envDoc.addNewEnvelope();
        Header header = env.addNewHeader();
        Body body = env.addNewBody();
        String sesURL = (String)parameter.getParameterShellWithCommonName(ISESRequestBuilder.DESCRIBE_SENSOR_SES_URL).getSpecifiedValue();
        XmlCursor cur = null;

        SESUtils.addNamespacesToEnvelope_000(env);

        cur = header.newCursor();

        cur.toFirstContentToken();
        cur.insertElementWithText(new QName(ns_addressing,"To","wsa"),
                sesURL);
        cur.insertElementWithText(new QName(ns_addressing,"Action","wsa"),
        "http://www.opengis.net/ses/DescribeSensorRequest");
        cur.insertElementWithText(new QName(ns_addressing,"MessageID","wsa"),
                UUID.randomUUID().toString());
        cur.beginElement(new QName(ns_addressing,"From","wsa"));
        cur.insertElementWithText(new QName(ns_addressing,"Address","wsa"),
        "http://www.w3.org/2005/08/addressing/role/anonymous");
        cur.dispose();

        cur = body.newCursor();

        cur.toFirstContentToken();
        cur.beginElement(new QName(ns_ses,"DescribeSensor","ses"));
        cur.insertAttributeWithValue("service","SES");
        cur.insertAttributeWithValue("version", "1.0.0");
        cur.insertElementWithText(new QName(ns_ses,"SensorID","ses"), 
                (String)parameter.getParameterShellWithCommonName(ISESRequestBuilder.DESCRIBE_SENSOR_SENSOR_ID).getSpecifiedValue());
        cur.dispose();

        request = envDoc.xmlText();

        return request;
    }
    
    /**
     * 
     * @param sensorID
     * @return unit of measurement from describeSensor request
     */
    public synchronized String getUnit(String sensorID){
        String sweField = "swe:field";
        String value = "swe:value";
        String unit = "unit";
        
        try {
            // build parameterContainer for describeSensorRequest
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SES_URL, this.sesEndpoint);
            parameter.addParameterShell(ISESRequestBuilder.DESCRIBE_SENSOR_SENSOR_ID, sensorID);
            
            // describeSensor
            URL url = new URL(this.sesEndpoint);
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            connect.setRequestProperty("Content-Type", "text/xml");
            connect.setRequestMethod("POST");
            connect.setDoOutput(true);
            connect.setDoInput(true);
            PrintWriter pw = new PrintWriter(connect.getOutputStream());
            pw.write(buildDescribeSensorRequest(parameter));
            pw.close();

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(connect.getInputStream());

            // parse <swe:field>
            NodeList sensorIDList = doc.getElementsByTagName(sweField);
            for (int i = 0; i < sensorIDList.getLength(); i++) {
                if (sensorIDList.item(i).getAttributes().item(0).getTextContent().equals(unit)) {
                    Node sensorIDNode = sensorIDList.item(i);
                    Element fstElement = (Element) sensorIDNode;

                    // parse <swe:value>
                    NodeList valueList = fstElement.getElementsByTagName(value);
                    return valueList.item(0).getTextContent();
                }
            }
            connect.disconnect();
        } catch (OXFException e) {
            LOGGER.error("Could not get unit", e); // XXX clearer msg 
        } catch (SAXException e) {
            LOGGER.error("Could not get unit", e); // XXX clearer msg 
        } catch (IOException e) {
            LOGGER.error("Could not get unit", e); // XXX clearer msg 
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not get unit", e); // XXX clearer msg 
        } 
        return null;
    }
}