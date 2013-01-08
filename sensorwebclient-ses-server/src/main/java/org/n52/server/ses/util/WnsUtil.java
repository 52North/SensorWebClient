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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.n52.server.ses.Config;
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
    public static boolean isAvailable(){
	        HttpURLConnection connect = null;

	        try {
	            URL url = new URL(Config.wns);
	            connect = (HttpURLConnection) url.openConnection();
	            connect.setRequestProperty("Content-Type", "text/xml");
	            connect.setRequestMethod("POST");
	            connect.setDoOutput(true);
	            connect.setDoInput(true);
	            PrintWriter pw = new PrintWriter(connect.getOutputStream());
	            pw.write(createCapabilitiesRequest());
	            pw.close();
	            BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
	            
	            in.close();
	            connect.disconnect();
	            
	            return true;
	        } catch (Exception e) {
        		LOGGER.trace("WNS is not available");
	            if (connect != null) {
	            	connect.disconnect();
				}
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
        URL url;
        String[] UserID = null;
        StringBuffer sb = new StringBuffer();
        String result;

        url = new URL(Config.wns);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestProperty("Content-Type", "text/xml");
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setDoInput(true);
        PrintWriter pw = new PrintWriter(connect.getOutputStream());
        pw.write(createNewUserMailRequest(userName, mail));
        pw.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        // Antwort merken
        do {
            sb.append(in.readLine() + "\n");
        } while (in.ready());
        in.close();
        connect.disconnect();
        // WNS UserID filtern
        UserID = sb.toString().split("UserID");
        result = UserID[1].substring(1, UserID[1].length() - 2);
    	LOGGER.debug("WNS_USER_ID: {} ", result);
        return result;
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
        URL url;
        StringBuffer sb = new StringBuffer();

        url = new URL(Config.wns);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestProperty("Content-Type", "text/xml");
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setDoInput(true);
        PrintWriter pw = new PrintWriter(connect.getOutputStream());
        pw.write(createUpdateSingleUserMailRequest(wnsID, mail, oldMail));
        pw.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        do {
            sb.append(in.readLine() + "\n");
        } while (in.ready());
        in.close();
        connect.disconnect();
    	LOGGER.trace(sb.toString());
    }

    /**
     * Update Phone number of subscribed user with the wnsID
     * 
     * @param wnsID
     * @param handy
     * @param oldHandy
     * @throws Exception
     */
    public static void updateToWNSSMS(String wnsID, String handy, String oldHandy) throws Exception {
        URL url;
        StringBuffer sb = new StringBuffer();

        url = new URL(Config.wns);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestProperty("Content-Type", "text/xml");
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setDoInput(true);
        PrintWriter pw = new PrintWriter(connect.getOutputStream());
        pw.write(createUpdateSingleUserSMSRequest(wnsID, handy, oldHandy));
        pw.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        do {
            sb.append(in.readLine() + "\n");
        } while (in.ready());
        in.close();
        connect.disconnect();
    	LOGGER.trace(sb.toString());
    }

    /**
     * Send SMS to user
     * 
     * @param userName
     * @param handy
     * @return {@link String}
     * @throws Exception
     */
    public static String sendToWNSSMS(String userName, String handy) throws Exception {
        URL url;
        String[] UserID = null;
        StringBuffer sb = new StringBuffer();
        String result;

        url = new URL(Config.wns);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestProperty("Content-Type", "text/xml");
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setDoInput(true);
        PrintWriter pw = new PrintWriter(connect.getOutputStream());
        pw.write(createNewUserSMSRequest(userName, handy));
        pw.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        // Antwort merken
        do {
            sb.append(in.readLine() + "\n");
        } while (in.ready());
        in.close();
        connect.disconnect();
        // WNS UserID filtern
        UserID = sb.toString().split("UserID");
        result = UserID[1].substring(1, UserID[1].length() - 2);
    	LOGGER.debug("WNS_USER_ID: {}", result);
        return result;
    }

    /**
     * Unregister user with given userID
     * 
     * @param userID
     * @return {@link String}
     * @throws Exception
     */
    public static String sendToWNSUnregister(String userID) throws Exception {
        URL url;
        StringBuffer sb = new StringBuffer();
        String result;

        url = new URL(Config.wns);
        HttpURLConnection connect = (HttpURLConnection) url.openConnection();
        connect.setRequestProperty("Content-Type", "text/xml");
        connect.setRequestMethod("POST");
        connect.setDoOutput(true);
        connect.setDoInput(true);
        PrintWriter pw = new PrintWriter(connect.getOutputStream());
        pw.write(createUnregisterUserRequest(userID));
        pw.close();
        BufferedReader in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
        // Antwort merken
        do {
            sb.append(in.readLine() + "\n");
        } while (in.ready());
        in.close();
        connect.disconnect();
        result = sb.toString();
        // UserID = sb.toString().split("UserID");
        // result = UserID[1].substring(1, UserID[1].length() - 2);
        // TODO WHAT? Please specify this!
    	LOGGER.debug("WNS_RESULT: {} ", result);
        return result;
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
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0 ../wns.xsd\"\n");
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
     * @param userName
     * @param handy
     * @return {@link String}
     */
    private static String createNewUserSMSRequest(String userName, String handy) {
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<Register xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0 ../wns.xsd\"\n");
        sb.append("service=\"WNS\" version=\"1.0.0\">\n");
        sb.append("<SingleUser>\n");
        sb.append("<Name>" + userName + "</Name>\n");
        sb.append("<CommunicationProtocol>\n");
        sb.append("<SMS>" + handy + "</SMS>\n");
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
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0 ../wns.xsd\"\n");
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
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0\n");
        sb.append("../wns.xsd\" service=\"WNS\" version=\"1.0.0\">\n");
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

    /**
     * @param wnsID
     * @param handy
     * @param oldHandy
     * @return
     */
    private static String createUpdateSingleUserSMSRequest(String wnsID, String handy, String oldHandy) {
        StringBuffer sb = new StringBuffer();
        String WNSrequest = null;

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("<UpdateSingleUserRegistration xmlns=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0\"\n");
        sb.append("../wns.xsd\" service=\"WNS\" version=\"1.0.0\">\n");
        sb.append("<UserID>" + wnsID + "</UserID>\n");
        sb.append("<addCommunicationProtocol>\n");
        sb.append("<SMS>" + handy + "</SMS>\n");
        sb.append("</addCommunicationProtocol>\n");
        sb.append("<removeCommunicationProtocol>\n");
        sb.append("<SMS>" + oldHandy + "</SMS>\n");
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
        sb.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("xsi:schemaLocation=\"http://www.opengis.net/wns/0.0 ../wnsGetCapabilities.xsd\"\n");
        sb.append("service=\"WNS\"/>");
        
        WNSrequest = sb.toString();
        
    	LOGGER.trace("\n * * * Beginn Request * * * \n" + WNSrequest + "\n * * * Ende Request * * * \n");
        return WNSrequest;
    }
}