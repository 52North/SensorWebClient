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
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.server.ses.Config;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.ComplexRule;
import org.n52.shared.serializable.pojos.ComplexRuleData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 *
 */
public class ComplexRule_Builder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ComplexRule_Builder.class);
	
    /**
     * 
     * This method combines two rules to a Complex rule. Rule types of both rules are not important.
     * 
     * @param operator (AND, OR, AND_NOT)
     * @param rules ArrayList with two rule names
     * @param ruleData 
     * @return {@link ComplexRule}
     * @throws Exception 
     */
    public static ComplexRule combine2Rules(String operator, ArrayList<Object> rules,  ComplexRuleData ruleData) throws Exception {
        String eml;
        String finalEml;
        
        ArrayList<String> ruleNames = new ArrayList<String>();

        try {
            final String ruleName = ruleData.getTitle();

            // count of patterns which must be added to be able to combine two rules
            final int logicalCount = 2;
            final int structuralCount = 1;
            final int newPatterns = 3;

            // new patternIDs
            ArrayList<String> patterIDName = new ArrayList<String>();
            patterIDName.add(ruleName + "_enter_condition_notification_stream");
            patterIDName.add(ruleName + "_exit_condition_stream");
            patterIDName.add(ruleName + "_exit_conditions_notification_stream");

            // new newEventNames
            ArrayList<String> newEventNames = new ArrayList<String>();
            newEventNames.add(ruleName + "_enter_conditions_notification");
            newEventNames.add(ruleName + "_exit_condition");
            newEventNames.add(ruleName + "_exit_conditions_notification");

            // new outputNames
            ArrayList<String> outputNames = new ArrayList<String>();
            outputNames.add(ruleName + "_overshoot_output");
            outputNames.add(ruleName + "_undershoot_output");

            // messages
            ArrayList<String> mess = new ArrayList<String>();
            mess.add("Einstiegsklausel");
            mess.add("EXIT");
            mess.add("Ausstiegsklausel");

            // pairwise: enter condition, exit condition
            ArrayList<String> patterIDs = new ArrayList<String>();

            // transformer for final output
            Transformer transormer = TransformerFactory.newInstance().newTransformer();
            transormer.setOutputProperty(OutputKeys.INDENT, "yes");

            // get the EMLs of the two rules
            ArrayList<String> emlList = new ArrayList<String>();
            for (int i = 0; i < rules.size(); i++) {
                Object o = rules.get(i);
                if (o instanceof BasicRule) {
                    BasicRule rule = (BasicRule)o;
                    emlList.add(rule.getEml());
                    ruleNames.add(rule.getName());
                } else if (o instanceof ComplexRule) {
                    ComplexRule rule = (ComplexRule)o;
                    emlList.add(rule.getEml());
                    ruleNames.add(rule.getName());
                }
            }
            
            // read the two rules
            BufferedReader reader = new BufferedReader(new StringReader(emlList.get(0)));
            BufferedReader reader2 = new BufferedReader(new StringReader(emlList.get(1)));
            
            // URLs of patterns with logical and structural operators
            URL logicalURL = new URL(Config.resLocation_logical);
            URL structuralURL = new URL(Config.resLocation_structural);

            // DocumentBuilder
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();

            // Documents to merge
            // rule 1
            Document doc = docBuilder.parse(new InputSource(reader));
            
            // rule 2
            Document doc2 = docBuilder.parse(new InputSource(reader2));
            
            // pattern with logical operator
            Document doc3 = docBuilder.parse(logicalURL.openStream());
            
            // pattern with structural operator
            Document doc4 = docBuilder.parse(structuralURL.openStream());

            // MERGE SimplePattern
            NodeList simplePatternsList = doc.getElementsByTagName(Constants.simplePatterns);
            Node simpleRoot = simplePatternsList.item(0);

            NodeList simplePatternsList2 = doc2.getElementsByTagName(Constants.simplePattern);
            for (int i = 0; i < simplePatternsList2.getLength(); i++) {
                Node second = simplePatternsList2.item(i);
                Node nd = doc.importNode(second, true);
                simpleRoot.appendChild(nd);
            }

            // MERGE ComplexPattern
            NodeList complexPatternsList = doc.getElementsByTagName(Constants.complexPatterns);
            Node complexRoot = complexPatternsList.item(0);

            NodeList complexPatternsList2 = doc2.getElementsByTagName(Constants.complexPattern);
            for (int i = 0; i < complexPatternsList2.getLength(); i++) {
                Node second = complexPatternsList2.item(i);
                Node nd = doc.importNode(second, true);
                complexRoot.appendChild(nd);
            }

            // add logical patterns. In this case these are 2
            for (int l = 0; l < logicalCount; l++) {
                // logical operator
                NodeList complexPatternsList3 = doc3.getElementsByTagName(Constants.complexPattern);
                for (int i = 0; i < complexPatternsList3.getLength(); i++) {
                    Node second = complexPatternsList3.item(i);
                    Node nd = doc.importNode(second, true);
                    complexRoot.appendChild(nd);
                }
            }

            // add structural patterns. In this case only one
            for (int s = 0; s < structuralCount; s++) {
                // structural operator
                NodeList complexPatternsList4 = doc4.getElementsByTagName(Constants.complexPattern);
                for (int i = 0; i < complexPatternsList4.getLength(); i++) {
                    Node second = complexPatternsList4.item(i);
                    Node nd = doc.importNode(second, true);
                    complexRoot.appendChild(nd);
                }
            }


            // parse <ComplexPattern> to remove outputNames and save the patternIDs of the condition (enter/exit) patterns
            NodeList complexPatternList = doc.getElementsByTagName(Constants.complexPattern);
            for (int i = 0; i < complexPatternList.getLength(); i++) {
                Node fstNode = complexPatternList.item(i);
                Element fstElement = (Element) fstNode;

                // newEventName of SelectFunction
                NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
                Node selectFunctionNode = selectFunctionList.item(0);
                if (selectFunctionNode.getAttributes().getNamedItem(Constants.outputName) != null) {
                    // remove outputName
                    if (i<complexPatternList.getLength()) {
                        selectFunctionNode.getAttributes().removeNamedItem(Constants.outputName);
                    }

                    // save patternID
                    patterIDs.add(fstNode.getAttributes().getNamedItem(Constants.patternID).getTextContent());
                }
                // <Message> set message to EMPTY. The SES-WNS-Translator will ignore this messages and do not forward it to the user
                NodeList messageList = fstElement.getElementsByTagName(Constants.message);
                if (messageList.getLength() != 0) {
                    Node messageNode = messageList.item(0);
                    messageNode.setTextContent("EMPTY");
                }
            }

            // ======================================================================================
            // fill last three patterns (Logical, Logical, Structural)
            for (int j = complexPatternList.getLength()-newPatterns; j < complexPatternList.getLength(); j++) {
            	// value==0 is the first of the new logical patterns
                int value = j-(complexPatternList.getLength()-newPatterns);
                Node fstNode2 = complexPatternList.item(j);
                Element fstElement2 = (Element) fstNode2;

                // set patternID
                Node patternIdNode = complexPatternList.item(j);
                patternIdNode.getAttributes().getNamedItem(Constants.patternID).setTextContent(patterIDName.get(value));

                // set newEventName of SelectFunction
                NodeList selectFunctionList2 = fstElement2.getElementsByTagName(Constants.selectFunction);
                Node selectFunctionNode2 = selectFunctionList2.item(0);
                selectFunctionNode2.getAttributes().getNamedItem(Constants.newEventName).setTextContent(newEventNames.get(value));
                if (value == 0) {
                    selectFunctionNode2.getAttributes().setNamedItem(doc.createAttribute(Constants.outputName));
                    selectFunctionNode2.getAttributes().getNamedItem(Constants.outputName).setTextContent(outputNames.get(0));
                } else if (value == 2) {{
                    selectFunctionNode2.getAttributes().setNamedItem(doc.createAttribute(Constants.outputName));
                    selectFunctionNode2.getAttributes().getNamedItem(Constants.outputName).setTextContent(outputNames.get(1));
                }
                }

                // set <PatternReference>
                NodeList patterReferenceList = fstElement2.getElementsByTagName(Constants.patternReference);
                for (int k = 0; k < patterReferenceList.getLength(); k++) {
                    if (value != logicalCount) {
                        Node patterReferenceNode = patterReferenceList.item(k);
                        if (k == 0) {
                            patterReferenceNode.setTextContent(patterIDs.get(k+value));
                        } else {
                            patterReferenceNode.setTextContent(patterIDs.get(k + value+1));
                        }
                    } else {
                        Node patterReferenceNode = patterReferenceList.item(k);
                        if (k == 0) {
                            patterReferenceNode.setTextContent(patterIDName.get(k));
                        } else {
                            patterReferenceNode.setTextContent(patterIDName.get(k));
                        }
                    }

                }

                // set output messages
                NodeList messageList = fstElement2.getElementsByTagName(Constants.message);
                Node messageNode = messageList.item(0);
                messageNode.setTextContent(mess.get(value));

                // <LogicalOperator>
                NodeList logicalOperatorList = fstElement2.getElementsByTagName(Constants.logicalOperator);
                if (logicalOperatorList.getLength() != 0) {
                    Node logicalOperatorNode = logicalOperatorList.item(0);
                    if (value == 0) {
                        // first pattern
                        Node operatorNode = doc.createElement(operator);
                        logicalOperatorNode.appendChild(operatorNode);
                    } else if (value == 1) {
                        // second pattern
                    	// exit conditions are always combined with the operator OR
                        Node operatorNode = doc.createElement(Constants.OR);
                        logicalOperatorNode.appendChild(operatorNode);
                    }
                }
            }

            // final EML document. Convert document to string for saving in DB
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transormer.transform(source, result);

            eml = result.getWriter().toString();
            finalEml = eml;
            finalEml = finalEml.substring(finalEml.indexOf("<EML"));

            LOGGER.debug(finalEml);

        } catch (Exception e) {
            throw new Exception("build complex rule failed!", e);
        }
        
        StringBuilder sb = new StringBuilder();
        
        // rule names with separator _T_ are saved in DB and used for quick search
        for (int i = 0; i < ruleNames.size(); i++) {
            sb.append(ruleNames.get(i));
            sb.append("_T_");
        }
        sb.append(operator);
        
        return new ComplexRule(ruleData.getTitle(), "K", ruleData.getDescription(), ruleData.isPublish(), ruleData.getUserID(), finalEml, false, "", "", sb.toString());
    }
}