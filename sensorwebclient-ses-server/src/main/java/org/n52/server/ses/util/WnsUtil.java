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

import static org.apache.http.entity.ContentType.TEXT_XML;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.util.web.HttpClient;
import org.n52.oxf.util.web.ProxyAwareHttpClient;
import org.n52.oxf.util.web.SimpleHttpClient;
import org.n52.server.ses.SesConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FIXME: checking of successful response is missing for each method!
 */
public class WnsUtil {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WnsUtil.class);

    /**
     * returns true if WNS delivers a response.
     */
    public static boolean isAvailable() {
	        try {
	            ProxyAwareHttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
	            HttpResponse response = httpClient.executePost(SesConfig.wns, createCapabilitiesRequest(), TEXT_XML);
	            return response.getStatusLine().getStatusCode() < 400;
	        } catch (Exception e) {
        		LOGGER.trace("WNS is not available: {}", SesConfig.wns);
	            return false;
	        }
	    }

	/**
	 * Send E-Mail to user
	 * 
     * @param userName
     * @param mail
     * @return {@link String}
     * @throws Exception
     */
    public static String sendToWNSMail(String userName, String mail) throws Exception {
        HttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
        String request = createNewUserMailRequest(userName, mail);
        HttpResponse response = httpClient.executePost(SesConfig.wns, request, TEXT_XML);
        XmlObject xmlResponse = readXmlResponse(response);
        String[] userIDs = xmlResponse.xmlText().split("UserID");
        String result = userIDs[1].substring(1, userIDs[1].length() - 2);
        LOGGER.debug("WNS_USER_ID: {}", result);
        return result;
    }

    protected static XmlObject readXmlResponse(HttpResponse response) throws IOException {
        InputStream content = response.getEntity().getContent();
        StringBuilder sb = new StringBuilder();
        try {
            String line = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            return XmlObject.Factory.parse(sb.toString());
        }
        catch (XmlException e) {
            LOGGER.error("Failed to parse WNS response: {}", sb.toString(), e);
            return XmlObject.Factory.newInstance();
        }
    }

    /**
     * Update E-Mail address of a subscribed user with the wnsID
     * 
     * @param wnsID
     * @param mail
     * @param oldMail
     * @throws Exception
     */
    public static void updateToWNSMail(String wnsID, String mail, String oldMail) throws Exception {
        ProxyAwareHttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
        HttpResponse response = httpClient.executePost(SesConfig.wns, createUpdateSingleUserMailRequest(wnsID, mail, oldMail), TEXT_XML);
        XmlObject xmlResponse = readXmlResponse(response);
        LOGGER.trace(xmlResponse.xmlText());
    }

    /**
     * Unregister user with given userID
     * 
     * @param userID
     * @return {@link String}
     * @throws Exception
     */
    public static String sendToWNSUnregister(String userID) throws Exception {
        ProxyAwareHttpClient httpClient = new ProxyAwareHttpClient(new SimpleHttpClient());
        HttpResponse response = httpClient.executePost(SesConfig.wns, createUnregisterUserRequest(userID), TEXT_XML);
        XmlObject xmlResponse = readXmlResponse(response);
        LOGGER.trace(xmlResponse.xmlText());
        // UserID = sb.toString().split("UserID");
        // result = UserID[1].substring(1, UserID[1].length() - 2);
        // TODO WHAT? Please specify this!
        return xmlResponse.xmlText();
    }

    /**
     * 
     * @param userName
     * @param mail
     * @return {@link String}
     */
    private static String createNewUserMailRequest(String userName, String mail) {
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<Register xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("service=\"WNS\" version=\"1.0.0\">\n");
        sb.append("<SingleUser>\n");
        sb.append("<Name>" + userName + "</Name>\n");
        sb.append("<CommunicationProtocol>\n");
        sb.append("<Email>" + mail + "</Email>\n");
        sb.append("</CommunicationProtocol>\n");
        sb.append("</SingleUser>\n");
        sb.append("</Register>");

        WNSrequest = sb.toString();
        
    	LOGGER.trace("\n * * * Beginn Request * * * \n" + WNSrequest + "\n * * * Ende Request * * * \n");
        return WNSrequest;
    }


    /**
     * 
     * @param userID
     * @return {@link String}
     */
    private static String createUnregisterUserRequest(String userID) {
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<Unregister xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("service=\"WNS\" version=\"1.0.0\">\n");
        sb.append("<ID>" + userID + "</ID>\n");
        sb.append("</Unregister>");

        WNSrequest = sb.toString();
        
    	LOGGER.trace("\n * * * Beginn Request * * * \n" + WNSrequest + "\n * * * Ende Request * * * \n");
        return WNSrequest;
    }

    /**
     * @param wnsID
     * @param mail
     * @param oldMail
     * @return
     */
    private static String createUpdateSingleUserMailRequest(String wnsID, String mail, String oldMail) {
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<UpdateSingleUserRegistration xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("service=\"WNS\" version=\"1.0.0\">\n");
        sb.append("<UserID>" + wnsID + "</UserID>\n");
        sb.append("<addCommunicationProtocol>\n");
        sb.append("<Email>" + mail + "</Email>\n");
        sb.append("</addCommunicationProtocol>\n");
        sb.append("<removeCommunicationProtocol>\n");
        sb.append("<Email>" + oldMail + "</Email>\n");
        sb.append("</removeCommunicationProtocol>\n");
        sb.append("</UpdateSingleUserRegistration>\n");

        WNSrequest = sb.toString();
        
    	LOGGER.trace("\n * * * Beginn Request * * * \n" + WNSrequest + "\n * * * Ende Request * * * \n");
        return WNSrequest;
    }

    private static String createCapabilitiesRequest(){
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;
        
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<GetCapabilities xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("service=\"WNS\"/>");
        
        WNSrequest = sb.toString();
        
    	LOGGER.trace("\n * * * Beginn Request * * * \n" + WNSrequest + "\n * * * Ende Request * * * \n");
        return WNSrequest;
    }
}