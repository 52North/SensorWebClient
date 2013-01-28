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

import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.TENDENCY_OVER_COUNT;
import static org.n52.shared.util.MathSymbolUtil.getFesFilterFor;
import static org.n52.shared.util.MathSymbolUtil.getSymbolIndexForFilter;

import java.io.ByteArrayInputStream;
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

import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.util.SESUnitConverter;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BasicRule_1_Builder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicRule_1_Builder.class);

    /**
     * Trend over Count
     * 
     * This method builds the rule type "Trend over Count" by loading and filling a template file.
     * The location of this file is defined in /properties/ses-client.properties 
     * in the variable "resLocation". File name must be BR_1.xml.
     * 
     * @param rule
     * @throws Exception
     * @return {@link BasicRule}
     */
    public static BasicRule create_BR_1(Rule rule) throws Exception {
        
    	// Get current user. This user is also the owner of the new rule
        User user = HibernateUtil.getUserBy(rule.getUserID());

        String eml;
        String finalEml;
        String title = rule.getTitle();

        // Pre-defined pattern IDs and event names. All names start with the title of the rule.
        // This is important to have unique names.
        ArrayList<String> simplePatternID = new ArrayList<String>();
        simplePatternID.add(title + "_first_event_stream");
        simplePatternID.add(title + "_last_event_stream");

        ArrayList<String> simpleNewEventName = new ArrayList<String>();
        simpleNewEventName.add(title + "_first_event");
        simpleNewEventName.add(title + "_last_event");

        ArrayList<String> complexPatternID = new ArrayList<String>();
        complexPatternID.add(title + "_simple_trend_stream");
        complexPatternID.add(title + "_trend_overshoot_stream");
        complexPatternID.add(title + "_trend_undershoot_stream");
        complexPatternID.add(title + "_overshoot_notification_stream");
        complexPatternID.add(title + "_undershoot_notification_stream");

        ArrayList<String> complexNewEventName = new ArrayList<String>();
        complexNewEventName.add(title + "_simple_trend");
        complexNewEventName.add(title + "_trend_overshoot");
        complexNewEventName.add(title + "_trend_undershoot");
        complexNewEventName.add(title + "_overshoot_notification");
        complexNewEventName.add(title + "_undershoot_notification");

        ArrayList<String> complexOutputname = new ArrayList<String>();
        complexOutputname.add(title + "_overshoot_output");
        complexOutputname.add(title + "_undershoot_output");

        // This ArrayList defines the references in the PatternReference tags. 
        // The references are ordered from first to the last pattern.
        ArrayList<String> patternReferenceText = new ArrayList<String>();
        patternReferenceText.add(simplePatternID.get(0));
        patternReferenceText.add(simplePatternID.get(1));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(0));
        patternReferenceText.add(complexPatternID.get(2));
        patternReferenceText.add(complexPatternID.get(1));
        patternReferenceText.add(complexPatternID.get(1));
        patternReferenceText.add(complexPatternID.get(2));

    	// URL adress of the BR_1.xml file
        URL url = new URL(SesConfig.resLocation_1);

        // build document
        DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFac.newDocumentBuilder();
        Document doc = docBuilder.parse(url.openStream());

        // transformer for final output
        Transformer transormer = TransformerFactory.newInstance().newTransformer();
        transormer.setOutputProperty(OutputKeys.INDENT, "yes");

        // parse <SimplePattern>
        NodeList simplePatternList = doc.getElementsByTagName(Constants.simplePattern);
        for (int i = 0; i < simplePatternList.getLength(); i++) {
            Node fstNode = simplePatternList.item(i);
            Element fstElement = (Element) fstNode;

            // set patternIDs
            Node patternIdSimple = simplePatternList.item(i);
            patternIdSimple.getAttributes().getNamedItem(Constants.patternID).setTextContent(simplePatternID.get(i));

            // set newEventName of SelectFunction
            NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().getNamedItem(Constants.newEventName)
                    .setTextContent(simpleNewEventName.get(i));

            // set propertyRestrictions
            NodeList propertyRestrictiosnList = fstElement.getElementsByTagName(Constants.propertyValue);
            Node value_1 = propertyRestrictiosnList.item(0);
            value_1.setTextContent(rule.getTimeseriesMetadata().getPhenomenon());
            Node value_2 = propertyRestrictiosnList.item(1);
            value_2.setTextContent(rule.getTimeseriesMetadata().getGlobalSesId());

            // set EventCount. This count represents the last measurements
            NodeList eventCountList = fstElement.getElementsByTagName(Constants.eventCount);
            if (eventCountList.getLength() != 0) {
                Node eventCountNode = eventCountList.item(0);
                eventCountNode.setTextContent(rule.getEntryCount());
            }

            // set UserParameterValue. This count represents last measurements
            NodeList userParameterValueList = fstElement.getElementsByTagName(Constants.userParameterValue);
            if (i == 1) {
                Node userParameterValueNode = userParameterValueList.item(2);
                userParameterValueNode.setTextContent(rule.getEntryCount());
            }
        }

        // parse <ComplexPatterns>
        NodeList complexPatternList = doc.getElementsByTagName(Constants.complexPattern);
        for (int i = 0; i < complexPatternList.getLength(); i++) {
            Node fstNode = complexPatternList.item(i);
            Element fstElement = (Element) fstNode;

            // set patternIDs
            Node patternIdNode = complexPatternList.item(i);
            patternIdNode.getAttributes().getNamedItem(Constants.patternID).setTextContent(complexPatternID.get(i));

            // set newEventName of SelectFunction
            NodeList selectFunctionList = fstElement.getElementsByTagName(Constants.selectFunction);
            Node selectFunctionNode = selectFunctionList.item(0);
            selectFunctionNode.getAttributes().getNamedItem(Constants.newEventName).setTextContent(
                    complexNewEventName.get(i));
            if (selectFunctionNode.getAttributes().getNamedItem(Constants.outputName) != null) {
                selectFunctionNode.getAttributes().getNamedItem(Constants.outputName).setTextContent(
                        complexOutputname.get(i - 3));
            }

            // set UserParameterValue of UserDefinedSelectFunction
            NodeList userParameterValueList = fstElement.getElementsByTagName(Constants.userParameterValue);
            if (userParameterValueList.getLength() != 0) {
                Node userParameterValueNode_1 = userParameterValueList.item(0);
                userParameterValueNode_1.setTextContent(simpleNewEventName.get(1) + "/doubleValue");
                Node userParameterValueNode_2 = userParameterValueList.item(1);
                userParameterValueNode_2.setTextContent(simpleNewEventName.get(0) + "/doubleValue");
            }

            // set PatternReference in the right order
            NodeList patterReferenceList = fstElement.getElementsByTagName(Constants.patternReference);
            for (int j = 0; j < patterReferenceList.getLength(); j++) {
                Node patterReferenceNode = patterReferenceList.item(j);
                if (j == 0) {
                    patterReferenceNode.setTextContent(patternReferenceText.get(2 * i));
                } else {
                    patterReferenceNode.setTextContent(patternReferenceText.get((2 * i) + 1));
                }
            }

            // set eventName of selectEvent
            NodeList selectEventList = fstElement.getElementsByTagName(Constants.selectEvent);
            if (selectEventList.getLength() != 0) {
                Node selectEventNode = selectEventList.item(0);
                selectEventNode.getAttributes().getNamedItem(Constants.eventName).setTextContent(complexNewEventName.get(0));
            }

            // set <fes:Filter>. This tag must be empty
            NodeList filterList = doc.getElementsByTagName(Constants.fesFilter);
            for (int j = 0; j < filterList.getLength(); j++) {

                Node n = filterList.item(j);


                Node entryFilter = doc.createElement(getFesFilterFor(rule.getEntryOperatorIndex()));
                Node exitFilter = doc.createElement(getFesFilterFor(rule.getExitOperatorIndex()));

                Node valueReferenceNode = doc.createElement(Constants.valueReference);
                valueReferenceNode.setTextContent(complexNewEventName.get(0) + "/doubleValue");

                // Unit Conversion
                SESUnitConverter converter = new SESUnitConverter();
//                Object[] resultrUnit = converter.convert(rule.getEntryUnit(), Double.valueOf(rule.getEntryValue()));
//                Object[] resultcUnit = converter.convert(rule.getExitUnit(), Double.valueOf(rule.getExitValue()));

                Node fesLiteralNode = doc.createElement(Constants.fesLiteral);

                // add first filter to document
                if ((j == 0) && (i == 0)) {
//                    fesLiteralNode.setTextContent(resultrUnit[1].toString());
                    fesLiteralNode.setTextContent(rule.getEntryValue());

                    if (entryFilter != null) {
                        n.appendChild(entryFilter);
                        entryFilter.appendChild(valueReferenceNode);
                        entryFilter.appendChild(fesLiteralNode);
                    }
                    
                // add second filter to the document
                } else if ((j == 1) && (i == 0)) {
//                    fesLiteralNode.setTextContent(resultcUnit[1].toString());
                    fesLiteralNode.setTextContent(rule.getExitValue());

                    if (exitFilter != null) {
                        n.appendChild(exitFilter);
                        exitFilter.appendChild(valueReferenceNode);
                        exitFilter.appendChild(fesLiteralNode);
                    }
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

        return new BasicRule(rule.getTitle(), "B", "BR1", rule.getDescription(), rule.isPublish(), user.getId(),
                finalEml, false);
    }

    /**
     * 
     * This method is used to parse an EML file and return a Rule class with rule specific attributes. 
     * The method is called if user want to edit this rule type.
     * 
     * @param basicRule 
     * @return {@link Rule}
     */
    public static Rule createRuleBy(BasicRule basicRule) {
    	
    	// This class stores all rule attributes whcih should be displayed in the client.
        Rule rule = new Rule();
        rule.setTimeseriesMetadata(basicRule.getTimeseriesMetadata());

        try {

            String eml = basicRule.getEml();
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(eml.getBytes()));

            NodeList filterList = doc.getElementsByTagName(Constants.fesFilter);
            Node entryOperatorNode = filterList.item(0);
            String entryFilter = entryOperatorNode.getChildNodes().item(1).getNodeName();
            rule.setEntryOperatorIndex(getSymbolIndexForFilter(entryFilter));

            Node exitOperatorNode = filterList.item(1);
            String exitFilter = exitOperatorNode.getChildNodes().item(1).getNodeName();
            rule.setExitOperatorIndex(getSymbolIndexForFilter(exitFilter));
            
            rule.setEnterEqualsExitCondition(rule.determineEqualEntryExitCondition());

            NodeList literalList = doc.getElementsByTagName(Constants.fesLiteral);
            Node literalNode = literalList.item(0);

            // rValue: Trend value
            rule.setEntryValue(literalNode.getFirstChild().getNodeValue());

            // rUnit: Trend value unit. Default unit is meter
            rule.setEntryUnit("m");

            literalNode = literalList.item(1);
            
            // cValue: TRend condition value
            rule.setExitValue(literalNode.getFirstChild().getNodeValue());

            // cUnit: Trend condition value unit. Default unit is meter
            rule.setExitUnit("m");


            // rCount: Event count value
            NodeList eventCountList = doc.getElementsByTagName(Constants.eventCount);
            Node eventCountNode = eventCountList.item(0);
            rule.setCount(eventCountNode.getTextContent());

            // cCount
            // TODO
            
            // set rule type
            rule.setRuleType(TENDENCY_OVER_COUNT);
            
        } catch (Exception e) {
            LOGGER.error("Error parsing EML rule.", e);
        }
        return rule;
    }
}