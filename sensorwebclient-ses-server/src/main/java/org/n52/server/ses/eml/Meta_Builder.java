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
package org.n52.server.ses.eml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.server.ses.SesConfig;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class generates the <BAW_META> tag which is used to store the notification
 * messages of the rules and the WNS ID of the user who should receive the notification.
 */
public class Meta_Builder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Meta_Builder.class);
    
    private static final String messageTag = "<Message>";
    private static final String endMessageTag = "</Message>";
    private static final String message = "Message";

    private static final String xml = "XML";
    //    private static final String sesOutput = "SES_OUTPUT";


    /**
     * @param user. This is the owner of the rule
     * @param ruleName
     * @param medium. The selected communication ways. (e.g SMS, E-Mail, ...)
     * @return {@link String}
     * @throws Exception 
     */
    public static synchronized String createTextMeta(User user, String ruleName, String medium) throws Exception {
        
    	// final <BAW_Meta> pattern

    	String finalMeta = "";
        
        // This is the WNS ID of the user. Each user is subscribed to the WNS with an E-Mail address.
        String wnsID;

        // get the wnsID
        if (medium.equals("SMS")) {
            wnsID = user.getWnsSmsId();
        } else {
            wnsID = user.getWnsEmailId();
        }

        // location of the meta file
        URL metaURL = new URL(SesConfig.resLocation_meta_text);

        // create document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(metaURL.openStream());

        // transformer for final output
        Transformer transormer = TransformerFactory.newInstance().newTransformer();
        transormer.setOutputProperty(OutputKeys.INDENT, "yes");

        // parse document
        NodeList messageList = doc.getElementsByTagName(Constants.message);
        Node messageNode = messageList.item(0);

        // replace values
        String tempMessage = messageNode.getTextContent();

        // must exist
        if (!tempMessage.contains("wnsID") || !tempMessage.contains("_ruleName_")) {
            throw new Exception();
        }

        tempMessage = tempMessage.replace("wnsID", wnsID);
        tempMessage = tempMessage.replace("_ruleName_", ruleName);

        // set new Text content
        messageNode.setTextContent(tempMessage);

        // build final output
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transormer.transform(source, result);
        finalMeta = result.getWriter().toString();
        // remove the first line of the XML file
        finalMeta = finalMeta.substring(finalMeta.indexOf("<SimplePattern"));

        return finalMeta;
    }

    /**
     * 
     * @param user. The owner of the rule
     * @param rule
     * @param medium. The selected communication ways. (e.g SMS, E-Mail, ...)
     * @param sensor. Sensor ID
     * @return meta for BR5: Sensor failure
     * @throws Exception 
     */
    public static synchronized String createTextFailureMeta(User user, BasicRule rule, String medium, String sensor) throws Exception {
    	// final <BAW_Meta> pattern
    	String finalMeta = "";
        
    	// This is the WNS ID of the user. Each user is subscribed to the WNS with an E-Mail address.
    	String wnsID;
    	
        String regelName = rule.getName();

        // get the wnsID
        if (medium.equals("SMS")) {
            wnsID = user.getWnsSmsId();
        } else {
            wnsID = user.getWnsEmailId();
        }

        // location of the meta file
        URL metaURL = new URL(SesConfig.resLocation_meta_text);

        // message with place holders
        String message =
            "_T_userID="
            + wnsID
            + "_T_shortMessageEinstieg=SM Regel "
            + regelName
            + " hat einen Alarm ausgeloest. "
            + "Fuer den Sensor " + sensor + " kommen keine Daten mehr. Zeitpunkt:_R__T_MessageEinstieg=Regel "
            + regelName
            + " hat einen Alarm ausgeloest. "
            + "Fuer den Sensor " + sensor + " kommen keine Daten mehr. Zeitpunkt:_R_._T_shortMessageAusstieg=SM Regel "
            + regelName
            + " hat den Alarmzustand beendet. "
            + "Fuer den Sensor " + sensor + " kommen wieder Daten. Zeitpunkt:_R__T_MessageAusstieg=Regel "
            + regelName
            + " hat den Alarmzustand beendet. "
            + "Fuer den Sensor " + sensor + " kommen wieder Daten. Zeitpunkt:_R_!_T_";

        // build meta document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(metaURL.openStream());

        // transformer for final output
        Transformer transormer = TransformerFactory.newInstance().newTransformer();
        transormer.setOutputProperty(OutputKeys.INDENT, "yes");

        // parse document
        NodeList eventNameList = doc.getElementsByTagName(Constants.selectFunction);
        Node eventNameNode = eventNameList.item(0);
        eventNameNode.getAttributes().getNamedItem(Constants.newEventName).setTextContent("BAW_META_AUSFALL");
        
        // set message
        NodeList messageList = doc.getElementsByTagName(Constants.message);
        Node messageNode = messageList.item(0);
        messageNode.setTextContent(message);

        // build final output
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transormer.transform(source, result);
        finalMeta = result.getWriter().toString();
        finalMeta = finalMeta.substring(38);

        return finalMeta;
    }

    /**
     * @param user. The owner of the rule
     * @param ruleName
     * @param medium. The selected communication ways (e.g SMS, E-Mail, ...)
     * @param format. The selected format (e.g. XML, EML)
     * @return meta
     * @throws Exception 
     */
    public static synchronized String createXMLMeta(User user, String ruleName, String medium, String format) throws Exception {
    	// final <BAW_Meta> pattern
    	String finalMeta = "";
        
    	// This is the WNS ID of the user. Each user is subscribed to the WNS with an E-Mail address.
    	String wnsID;

        // get the wnsID
        if (medium.equals("SMS")) {
            wnsID = user.getWnsSmsId();
        } else {
            wnsID = user.getWnsEmailId();
        }

        // location of the meta file
        URL metaURL = null;

        if (format.equals("XML")) {
            metaURL = new URL(SesConfig.resLocation_meta_XML);
        } else if (format.equals("EML")) {
            metaURL = new URL(SesConfig.resLocation_meta_EML);
        }

        // build meta document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(metaURL.openStream());

        // transformer for final output
        Transformer transormer = TransformerFactory.newInstance().newTransformer();
        transormer.setOutputProperty(OutputKeys.INDENT, "yes");

        // build the output
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transormer.transform(source, result);

        finalMeta = result.getWriter().toString();

        int start = finalMeta.indexOf(messageTag)+10;
        int end = finalMeta.indexOf(endMessageTag);
        String tempMessage = finalMeta.substring(start, end);

        // notification text contains <format>
        if (tempMessage.contains("<format>")) {
            
        	// parse document
            // <userWNSID>
            NodeList userIDList = doc.getElementsByTagName("userID");
            Node userIDNode = userIDList.item(0);
            userIDNode.setTextContent(wnsID);

            // replace place holders
            if (format.equals(Meta_Builder.xml)) {

                // <shortMessageEnter>
                NodeList shortMessageEnterList = doc.getElementsByTagName("shortMessageEnter");
                Node shortMessageEnterNode = shortMessageEnterList.item(0);
                String shortMessageEnter = shortMessageEnterNode.getTextContent();
                if (!shortMessageEnter.contains("_ruleName_")) {
                    throw new Exception();
                }
                shortMessageEnter = shortMessageEnter.replace("_ruleName_", ruleName);
                shortMessageEnterNode.setTextContent(shortMessageEnter);

                // <shortMessageExit>
                NodeList shortMessageExitList = doc.getElementsByTagName("shortMessageExit");
                Node shortMessageExitNode = shortMessageExitList.item(0);
                String shortMessageExit = shortMessageExitNode.getTextContent();
                if (!shortMessageExit.contains("_ruleName_")) {
                    throw new Exception();
                }
                shortMessageExit = shortMessageExit.replace("_ruleName_", ruleName);
                shortMessageExitNode.setTextContent(shortMessageExit);

                // <longMessageEnter>
                NodeList longMessageEnterList = doc.getElementsByTagName("longMessageEnter");
                Node longMessageEnterNode = longMessageEnterList.item(0);
                String longMessageEnter = longMessageEnterNode.getTextContent();
                if (!longMessageEnter.contains("_ruleName_")) {
                    throw new Exception();
                }
                longMessageEnter = longMessageEnter.replace("_ruleName_", ruleName);
                longMessageEnterNode.setTextContent(longMessageEnter);

                // <longMessageExit>
                NodeList longMessageExitList = doc.getElementsByTagName("longMessageExit");
                Node longMessageExitNode = longMessageExitList.item(0);
                String longMessageExit = longMessageExitNode.getTextContent();
                if (!longMessageExit.contains("_ruleName_")) {
                    throw new Exception();
                }
                longMessageExit = longMessageExit.replace("_ruleName_", ruleName);
                longMessageExitNode.setTextContent(longMessageExit);

            } else if (format.equals("EML")) {
                // nothing to do
            }

            // build temp output
            StreamResult result2 = new StreamResult(new StringWriter());
            DOMSource source2 = new DOMSource(doc);
            transormer.transform(source2, result2);

            String temp = result2.getWriter().toString();

            int start2 = temp.indexOf(messageTag)+10; // XXX use regex filter!
            int end2 = temp.indexOf(endMessageTag);
            temp = temp.substring(start2, end2);
            temp = temp.trim();
            StringBuilder sb = new StringBuilder();

            try {
                BufferedReader in = new BufferedReader(new StringReader(temp));
                String zeile = null;
                while ((zeile = in.readLine()) != null) {
                    sb.append(zeile);
                }
            } catch (IOException e) {
                LOGGER.error("Could not read message", e);
            }
            temp = sb.toString();

            // <Message>
            NodeList messageList = doc.getElementsByTagName(Meta_Builder.message);
            Node messageNode = messageList.item(0);
            messageNode.setTextContent(temp);

            // build final output
            StreamResult result3 = new StreamResult(new StringWriter());
            DOMSource source3 = new DOMSource(doc);
            transormer.transform(source3, result3);

            finalMeta = result3.getWriter().toString();
            finalMeta = finalMeta.substring(38);
            finalMeta  = finalMeta.replace("&#13;", "");
        }

        return finalMeta;
    }
}