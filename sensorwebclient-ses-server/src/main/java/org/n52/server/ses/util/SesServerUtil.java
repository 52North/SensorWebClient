/**
 * ﻿Copyright (C) 2012
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
 */
package org.n52.server.ses.util;

import static org.n52.oxf.ses.adapter.ISESRequestBuilder.SUBSCRIBE_CONSUMER_REFERENCE_ADDRESS;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.SUBSCRIBE_FILTER_MESSAGE_CONTENT;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.SUBSCRIBE_FILTER_MESSAGE_CONTENT_DIALECT;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.SUBSCRIBE_SES_URL;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.UNSUBSCRIBE_REFERENCE;
import static org.n52.oxf.ses.adapter.ISESRequestBuilder.UNSUBSCRIBE_SES_URL;
import static org.n52.oxf.ses.adapter.SESAdapter.GET_CAPABILITIES;
import static org.n52.oxf.ses.adapter.SESAdapter.SUBSCRIBE;
import static org.n52.oxf.ses.adapter.SESAdapter.UNSUBSCRIBE;

import java.io.IOException;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.ows.ExceptionReport;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.oxf.ses.adapter.ISESRequestBuilder;
import org.n52.oxf.ses.adapter.SESAdapter;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.eml.Constants;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.Subscription;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SesServerUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SesServerUtil.class);

    /**
     * @param serviceVersion
     * @param sesEndpoint
     * @param consumerReference
     * @param content = EML
     * @return {@link OperationResult}
     * @throws Exception
     */
    public synchronized static OperationResult subscribe(String serviceVersion, String sesEndpoint, String consumerReference, String content) throws Exception{
        SESAdapter adapter = new SESAdapter(serviceVersion);
        String brokerUrl = SesServerUtil.getBrokerUrl(sesEndpoint);

        ParameterContainer parameter = new ParameterContainer();
        parameter.addParameterShell(SUBSCRIBE_SES_URL, brokerUrl);
        parameter.addParameterShell(SUBSCRIBE_CONSUMER_REFERENCE_ADDRESS, consumerReference);
        parameter.addParameterShell(SUBSCRIBE_FILTER_MESSAGE_CONTENT_DIALECT, "http://www.opengis.net/ses/filter/level3");
        parameter.addParameterShell(SUBSCRIBE_FILTER_MESSAGE_CONTENT, content);

        Operation op = new Operation(SUBSCRIBE, null, brokerUrl);
        OperationResult opResult = adapter.doOperation(op, parameter);
        LOGGER.debug("operation result:" + opResult.toString());

        return opResult;
    }

    /**
     * @param serviceVersion
     * @param sesEndpoint
     * @param museResource
     * @return {@link OperationResult}
     * @throws Exception 
     */
    public synchronized static OperationResult unSubscribe(String serviceVersion, String sesEndpoint, String museResource) throws Exception{
        SESAdapter adapter = new SESAdapter(serviceVersion);
        String subscriptionManagerUrl = getSubscriptionManagerUrl(sesEndpoint);

        ParameterContainer paramCon = new ParameterContainer();
        paramCon.addParameterShell(UNSUBSCRIBE_SES_URL, subscriptionManagerUrl);
        paramCon.addParameterShell(UNSUBSCRIBE_REFERENCE, museResource);

        Operation op = new Operation(UNSUBSCRIBE, null, subscriptionManagerUrl);
        OperationResult opResult = adapter.doOperation(op, paramCon);
        LOGGER.debug(opResult.toString());
        
        // TODO destroy also SubscriptionResource when SESAapter supports destroy operation
        
        return opResult;
    }

    /**
     * This method parses the OperationResult (SES response) to get the RessourceID
     * 
     * @param result
     * @return {@link String}
     * @throws Exception
     */
    public synchronized static String getSubscriptionIDfromSES(OperationResult result) throws Exception{
        String tag = "muse-wsa:ResourceId";

        //build meta document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(result.getIncomingResultAsStream());

        NodeList list = doc.getElementsByTagName(tag);
        if (list.getLength() != 0) {
            Node node = list.item(0);
            return node.getTextContent();
        }
        return null;
    }

    /**
     * This method returns all used sensors without duplicates in the given EML file
     * 
     * @param eml
     * @return stationID
     * @throws Exception
     */
    public synchronized static ArrayList<String> getTimeseriesIdsFromEML(String eml) throws Exception {
        final String simplePattern = "SimplePattern";
        final String propertyValue = "value";
        final String propertyName = "name";
        final String sensorID = "sensorID"; // matches to timeseriesFeedId

        ArrayList<String> timeseriesFeedIds = new ArrayList<String>();

        //build document from eml
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(eml));
        Document doc = docBuilder.parse(is);

        // parse <SimplePattern>
        NodeList simplePatternList = doc.getElementsByTagName(simplePattern);
        for (int i = 0; i < simplePatternList.getLength(); i++) {
            Node fstNode = simplePatternList.item(i);
            Element fstElement = (Element) fstNode; 

            NodeList propertyRestrictionNameList = fstElement.getElementsByTagName(propertyName);
            for (int j = 0; j < propertyRestrictionNameList.getLength(); j++) {
                Node nameNode = propertyRestrictionNameList.item(j);
                String nameValue = nameNode.getTextContent();
                if (nameValue.equals(sensorID)) {

                    // <Value> of <PropertyRestrictions>
                    NodeList propertyRestrictiosnList = fstElement.getElementsByTagName(propertyValue);
                    if (propertyRestrictiosnList.getLength() != 0) {
                        Node value = propertyRestrictiosnList.item(1);
                        String timeseriesFeedId = value.getTextContent();

                        if ((timeseriesFeedId != null) && (!timeseriesFeedId.equals(""))) {
                            timeseriesFeedIds.add(timeseriesFeedId);
                        }
                    } 
                }
            }
        }
        // remove duplicates
        HashSet<String> hs = new HashSet<String>();
        hs.addAll(timeseriesFeedIds);
        
        timeseriesFeedIds.clear();
        timeseriesFeedIds.addAll(hs);
        
        return timeseriesFeedIds;
    }
    
    /**
     * 
     * @param eml
     * @return {@link ArrayList} with all used phenomena in given eml
     * @throws Exception
     */
    public synchronized static ArrayList<String> getPhenomenaFromEML(String eml) throws Exception {
        final String simplePattern = "SimplePattern";
        final String propertyValue = "value";
        final String propertyName = "name";
        final String observedProperty = "observedProperty";

        HashSet<String> phenomenonList = new HashSet<String>();

        //build document from eml
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        InputSource is = new InputSource();
        is.setCharacterStream(new StringReader(eml));
        Document doc = docBuilder.parse(is);

        // parse <SimplePattern>
        NodeList simplePatternList = doc.getElementsByTagName(simplePattern);
        for (int i = 0; i < simplePatternList.getLength(); i++) {
            Node fstNode = simplePatternList.item(i);
            Element fstElement = (Element) fstNode; 

            NodeList propertyRestrictionNameList = fstElement.getElementsByTagName(propertyName);
            for (int j = 0; j < propertyRestrictionNameList.getLength(); j++) {
                Node nameNode = propertyRestrictionNameList.item(j);
                String nameValue = nameNode.getTextContent();
                if (nameValue.equals(observedProperty)) {

                    // <Value> of <PropertyRestrictions>
                    NodeList propertyRestrictiosnList = fstElement.getElementsByTagName(propertyValue);
                    if (propertyRestrictiosnList.getLength() != 0) {
                        Node value = propertyRestrictiosnList.item(0);
                        String phenomenon = value.getTextContent();

                        // add phenomenon to list
                        if ((phenomenon != null) && (!phenomenon.equals(""))) {
                            phenomenonList.add(phenomenon);
                        }
                    } 
                }
            }
        }
        
        return new ArrayList<String>(phenomenonList);
    }

    /**
     * @return true if SES is available. "Available" means that the client gets only
     * a response from the SES. The content of the response is not checked here.
     */
    public static boolean isAvailable(){
        SESAdapter adapter = new SESAdapter(SesConfig.serviceVersion);
        String brokerUrl = getBrokerUrl(SesConfig.sesEndpoint);

        try {
            ParameterContainer parameter = new ParameterContainer();
            parameter.addParameterShell(ISESRequestBuilder.GET_CAPABILITIES_SES_URL, SesConfig.sesEndpoint);
            Operation op = new Operation(GET_CAPABILITIES, null, brokerUrl);
            OperationResult opResult = adapter.doOperation(op, parameter);

            return true;
        } catch (OXFException e) {
            LOGGER.trace("SES  is not available: {}", SesConfig.sesEndpoint);
            return false;
        } catch (ExceptionReport e) {
            LOGGER.error("Could not connect due to server error.", e);
            return false;
        }
    }

    /**
     * @param sensorID
     * @return {@link ArrayList} with {@link User}
     */
    public synchronized static ArrayList<User> getUserBySensorID(String sensorID){
        ArrayList<User> userList = new ArrayList<User>();
        ArrayList<String> sensorIDList = new ArrayList<String>();

        // get all subscriptions
        List<Subscription> subscriptions = HibernateUtil.getAllSubscriptions();

        // get all subscribed rules
        for (int i = 0; i < subscriptions.size(); i++) {
            Subscription subscription = subscriptions.get(i);
            BasicRule basic = HibernateUtil.getBasicRuleByID(subscription.getRuleID());
            ComplexRule complex = HibernateUtil.getComplexRuleByID(subscription.getRuleID());

            //parse all rules to get sensorIDs
            if (basic != null) {
                sensorIDList = parseEMLForSensorID(basic.getEml());
            }
            if (complex != null) {
                sensorIDList = parseEMLForSensorID(complex.getEml());
            }
            
            // compare sensorIDs
            for (int j = 0; j < sensorIDList.size(); j++) {
                if (sensorIDList.get(j).equals(sensorID)) {
                    userList.add(HibernateUtil.getUserBy(subscription.getUserID()));
                }
            }
        }

        return userList;
    }

    /**
     * This method returns an ArrayList with all used sensors in given EML document
     */
    private synchronized static ArrayList<String> parseEMLForSensorID(String eml){
        HashSet<String> stationIDList = new HashSet<String>();

        //build document from eml
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            docBuilder = docFac.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(eml));
            Document doc = docBuilder.parse(is);

            // parse <SimplePattern>
            NodeList simplePatternList = doc.getElementsByTagName(Constants.simplePattern);
            for (int i = 0; i < simplePatternList.getLength(); i++) {
                Node fstNode = simplePatternList.item(i);
                Element fstElement = (Element) fstNode;

                NodeList propertyRestrictionNameList = fstElement.getElementsByTagName(Constants.name);
                for (int j = 0; j < propertyRestrictionNameList.getLength(); j++) {
                    Node nameNode = propertyRestrictionNameList.item(j);
                    String nameValue = nameNode.getTextContent();
                    if (nameValue.equals(Constants.sensorID)) {

                        // <Value> of <PropertyRestrictions>
                        NodeList propertyRestrictiosnList = fstElement.getElementsByTagName(Constants.propertyValue);
                        if (propertyRestrictiosnList.getLength() != 0) {
                            Node value = propertyRestrictiosnList.item(1);
                            String station = value.getTextContent();

                            if ((station != null) && (!station.equals(""))) {
                                stationIDList.add(station);
                            }
                        } 
                    }
                }
            }
        } catch (ParserConfigurationException e) {
            LOGGER.error("Could not parse eml \n{}", eml, e);
        } catch (SAXException e) {
            LOGGER.error("Could not parse eml \n{}", eml, e);
        } catch (IOException e) {
            LOGGER.error("Could not parse eml \n{}", eml, e);
        }
        
        ArrayList<String> finalList = new ArrayList<String>(stationIDList);
        
        return finalList;
    }
    
    /**
     * create MD5 hash to code the password
     */
    public static String createMD5(String password) {
        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(password.getBytes());

            byte[] result = md5.digest();

            for (int i = 0; i < result.length; i++) {
                buffer.append(Integer.toHexString(0xFF & result[i]));
            }
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Unkown MD5 algorithm.", e);
        }
        return buffer.toString();
    }
    

    /**
     * Operations:
     * <ul>
     * <li>Subscribe</li>
     * <li>Notify</li>
     * <li>GetCapabilities</li>
     * <li>RegisterPublisher</li>
     * <li>DescribeSensor</li>
     * </ul>
     * 
     * @param serviceUrl SES services base URL.
     * @return the broker URL.
     */
    public static String getBrokerUrl(String serviceUrl) {
        return serviceUrl.endsWith("/") ? serviceUrl + "Broker" : serviceUrl + "/Broker";
    }
    
    /**
     * Operations:
     * <ul>
     * <li>Unsubscribe</li>
     * <li>RenewSubscription</li>
     * <li>PauseSubscription</li>
     * <li>ResumeSubscription</li>
     * </ul>
     * 
     * @param serviceUrl SES services base URL.
     * @return the SubscriptionManager URL.
     */
    public static String getSubscriptionManagerUrl(String serviceUrl) {
        return serviceUrl.endsWith("/") ? serviceUrl + "SubscriptionManager" : serviceUrl + "/SubscriptionManager";
    }
    
    /**
     * Operations:
     * <ul>
     * <li>Destroy</li>
     * </ul>
     * 
     * @param serviceUrl SES services base URL.
     * @return the PublisherRegistrationManager URL.
     */
    public static String getPublisherRegistrationManagerUrl(String serviceUrl) {
        return serviceUrl.endsWith("/") ? serviceUrl + "PublisherRegistrationManager" : serviceUrl + "/PublisherRegistrationManager";
    }
}