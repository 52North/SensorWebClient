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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.server.ses.SesConfig;
import org.n52.server.ses.hibernate.HibernateUtil;
import org.n52.server.ses.util.RulesUtil;
import org.n52.server.ses.util.SESUnitConverter;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.FeedingMetadata;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 * 
 */
public class BasicRule_4_Builder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicRule_4_Builder.class);

    private final String overshoot = "_overshoot";

    private final String overshootStream = "_overshoot_stream";

    private final String undershootStream = "_undershoot_stream";

    private final String undershoot = "_undershoot";

    private final String overshootNotification = "_overshoot_notification";

    private final String overshootNotificationStream = "_overshoot_notification_stream";

    private final String undershootNotificationStream = "_undershoot_notification";

    private final String undershootNotification = "_undershoot_notification_stream";

    private final String enter = "_enter";

    private final String exit = "_exit";

    // TAG NAMES
    private final String fesFilter = "fes:Filter";

    private final String propertyValue = "value";

    private final String valuereference = "fes:ValueReference";

    private final String fesLiteral = "fes:Literal";

    private final String simplePattern = "SimplePattern";

    private final String complexPattern = "ComplexPattern";

    private final String selectFunction = "SelectFunction";

    private final String patternReference = "PatternReference";

    // Pattern names
    private String simple1;

    private String simple2;

    private String complex1;

    private String complex2;

    // newEventNames
    private String simpleName1;

    private String simpleName2;

    private String complexName1;

    private String complexName2;

    // output names
    private String output_enter;

    private String output_exit;

    private String eml;

    private String finalEml;

    /**
     * Overshoot and Undershoot
     * 
     * This method builds the rule type "Overshoot and Undershoot" by loading and filling a template file.
     * The location of this file is defined in /properties/ses-client.properties 
     * in the variable "resLocation". File name must be BR_4.xml.
     * 
     * @param rule
     * @return {@link BasicRule}
     */
    public BasicRule create(Rule rule) {
        String title = rule.getTitle();

        // Pre-defined pattern IDs and event names. All names start with the title of the rule.
        // This is important to have unique names
        this.simple1 = title + this.overshootStream;
        this.simple2 = title + this.undershootStream;
        this.complex1 = title + this.overshootNotificationStream;
        this.complex2 = title + this.undershootNotificationStream;

        this.simpleName1 = title + this.overshoot;
        this.simpleName2 = title + this.undershoot;
        this.complexName1 = title + this.overshootNotification;
        this.complexName2 = title + this.undershootNotification;

        this.output_enter = title + this.enter;
        this.output_exit = title + this.exit;

        try {
        	// URL adress of the BR_4.xml file
            URL url = new URL(SesConfig.resLocation_4);

            // build document
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(url.openStream());

            // transformer for final output
            Transformer transormer = TransformerFactory.newInstance().newTransformer();
            transormer.setOutputProperty(OutputKeys.INDENT, "yes");

            // set user input
            // set patternID of simplePatterns
            NodeList simplePatternIDList = doc.getElementsByTagName(this.simplePattern);
            Node s1 = simplePatternIDList.item(0);
            s1.getAttributes().item(1).setTextContent(this.simple1);
            Node s2 = simplePatternIDList.item(1);
            s2.getAttributes().item(1).setTextContent(this.simple2);

            // set patternID of complexPatterns
            NodeList complexPatternIDLIst = doc.getElementsByTagName(this.complexPattern);
            Node c1 = complexPatternIDLIst.item(0);
            c1.getAttributes().item(0).setTextContent(this.complex1);
            Node c2 = complexPatternIDLIst.item(1);
            c2.getAttributes().item(0).setTextContent(this.complex2);

            // set selectFunction newEventName
            NodeList selectFunctionList = doc.getElementsByTagName(this.selectFunction);
            Node selectFunction1 = selectFunctionList.item(0);
            selectFunction1.getAttributes().item(1).setTextContent(this.simpleName1);
            Node selectFunction2 = selectFunctionList.item(1);
            selectFunction2.getAttributes().item(1).setTextContent(this.simpleName2);

            Node selectFunction3 = selectFunctionList.item(2);
            selectFunction3.getAttributes().item(1).setTextContent(this.complexName1);
            selectFunction3.getAttributes().item(2).setTextContent(this.output_enter);
            Node selectFunction4 = selectFunctionList.item(3);
            selectFunction4.getAttributes().item(1).setTextContent(this.complexName2);
            selectFunction4.getAttributes().item(2).setTextContent(this.output_exit);

            // set PatternReference
            NodeList patternReferenceList = doc.getElementsByTagName(this.patternReference);
            Node patternRef1 = patternReferenceList.item(0);
            patternRef1.setTextContent(this.simple2);
            Node patternRef2 = patternReferenceList.item(1);
            patternRef2.setTextContent(this.simple1);
            Node patternRef3 = patternReferenceList.item(2);
            patternRef3.setTextContent(this.simple1);
            Node patternRef4 = patternReferenceList.item(3);
            patternRef4.setTextContent(this.simple2);

            // property restrictions
            FeedingMetadata metadata = rule.getFeedingMetadata();
            NodeList propertyRestrictionsList = doc.getElementsByTagName(this.propertyValue);
            Node n1 = propertyRestrictionsList.item(0);
            n1.setTextContent(metadata.getPhenomenon());
            Node n2 = propertyRestrictionsList.item(1);
            n2.setTextContent(metadata.getProcedure());

            Node n4 = propertyRestrictionsList.item(2);
            n4.setTextContent(metadata.getPhenomenon());
            Node n5 = propertyRestrictionsList.item(3);
            n5.setTextContent(metadata.getProcedure());

            // fes:Filter
            NodeList filterList = doc.getElementsByTagName(this.fesFilter);
            for (int i = 0; i < filterList.getLength(); i++) {

                Node n = filterList.item(i);

                Node filterNode = null;
                Node filterNode2 = null;

                // first filter
                if (rule.getEntryOperatorIndex() == Rule.LESS_THAN) {
                    filterNode = doc.createElement("fes:PropertyIsLessThan");
                } else if (rule.getEntryOperatorIndex() == Rule.GREATER_THAN) {
                    filterNode = doc.createElement("fes:PropertyIsGreaterThan");
                } else if (rule.getEntryOperatorIndex() == Rule.EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.GREATER_THAN_OR_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsGreaterThanOrEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.LESS_THAN_OR_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsLessThanOrEqualTo");
                } else if (rule.getEntryOperatorIndex() == Rule.NOT_EQUAL_TO) {
                    filterNode = doc.createElement("fes:PropertyIsNotEqualTo");
                }

                // second filter
                if (rule.getExitOperatorIndex() == Rule.LESS_THAN) {
                    filterNode2 = doc.createElement("fes:PropertyIsLessThan");
                } else if (rule.getExitOperatorIndex() == Rule.GREATER_THAN) {
                    filterNode2 = doc.createElement("fes:PropertyIsGreaterThan");
                } else if (rule.getExitOperatorIndex() == Rule.EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.GREATER_THAN_OR_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsGreaterThanOrEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.LESS_THAN_OR_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsLessThanOrEqualTo");
                } else if (rule.getExitOperatorIndex() == Rule.NOT_EQUAL_TO) {
                    filterNode2 = doc.createElement("fes:PropertyIsNotEqualTo");
                }

                Node valueReferenceNode = doc.createElement(this.valuereference);
                valueReferenceNode.setTextContent("input/doubleValue");

                // Unit Conversion
                SESUnitConverter converter = new SESUnitConverter();
                Object[] resultrUnit = converter.convert(rule.getEntryUnit(), Double.valueOf(rule.getEntryValue()));
                Object[] resultcUnit = converter.convert(rule.getExitUnit(), Double.valueOf(rule.getExitValue()));

                Node fesLiteralNode = doc.createElement(this.fesLiteral);

                // add first filter to document
                if (i == 0) {
                    fesLiteralNode.setTextContent(resultrUnit[1].toString());

                    if (filterNode != null) {
                        n.appendChild(filterNode);
                        filterNode.appendChild(valueReferenceNode);
                        filterNode.appendChild(fesLiteralNode);
                    }

                // add second filter to document
                } else if (i == 1) {
                    fesLiteralNode.setTextContent(resultcUnit[1].toString());

                    if (filterNode2 != null) {
                        n.appendChild(filterNode2);
                        filterNode2.appendChild(valueReferenceNode);
                        filterNode2.appendChild(fesLiteralNode);
                    }

                }
            }

            // final EML document. Convert document to string for saving in DB
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            transormer.transform(source, result);

            this.eml = result.getWriter().toString();
            this.finalEml = this.eml;
            this.finalEml = finalEml.substring(finalEml.indexOf("<EML"));
            LOGGER.info(this.finalEml);

        } catch (Exception e) {
            LOGGER.error("Error creating rule", e);
            return null;
        }
        
        // Get current user. This user is also the owner of the new rule
        User user = HibernateUtil.getUserBy(rule.getUserID());
        
        return new BasicRule(rule.getTitle(), "B", "BR4", rule.getDescription(), rule.isPublish(), user.getId(),
                this.finalEml, false);
    }

    /**
     * 
     * This method is used to parse an EML file and return a Rule class with rule specific attributes. 
     * The method is called if user want to edit this rule type.
     * 
     * @param basicRule
     * @return {@link Rule}
     */
    public Rule getRuleByEML(BasicRule basicRule) {
        Rule rule = new Rule();
        rule.setFeedingMetadata(basicRule.getFeedingMetadata());

        try {
            String eml = basicRule.getEml();
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(eml.getBytes()));

            NodeList filterList = doc.getElementsByTagName(this.fesFilter);
            Node filterNode = filterList.item(0);
            String property = filterNode.getChildNodes().item(1).getNodeName();
            
            // rOperatorIndex: first filter
            if (property.equals("fes:PropertyIsLessThan")) {
                rule.setEntryOperatorIndex(Rule.LESS_THAN);
            } else if (property.equals("fes:PropertyIsGreaterThan")) {
                rule.setEntryOperatorIndex(Rule.GREATER_THAN);
            } else if (property.equals("fes:PropertyIsEqualTo")) {
                rule.setEntryOperatorIndex(Rule.EQUAL_TO);
            } else if (property.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
                rule.setEntryOperatorIndex(Rule.GREATER_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsLessThanOrEqualTo")) {
                rule.setEntryOperatorIndex(Rule.LESS_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsNotEqualTo")) {
                rule.setEntryOperatorIndex(Rule.NOT_EQUAL_TO);
            }
            
            filterNode = filterList.item(1);
            property = filterNode.getChildNodes().item(1).getNodeName();
            
            // cOperatorIndex: senond filter
            if (property.equals("fes:PropertyIsLessThan")) {
                rule.setExitOperatorIndex(Rule.LESS_THAN);
            } else if (property.equals("fes:PropertyIsGreaterThan")) {
                rule.setExitOperatorIndex(Rule.GREATER_THAN);
            } else if (property.equals("fes:PropertyIsEqualTo")) {
                rule.setExitOperatorIndex(Rule.EQUAL_TO);
            } else if (property.equals("fes:PropertyIsGreaterThanOrEqualTo")) {
                rule.setExitOperatorIndex(Rule.GREATER_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsLessThanOrEqualTo")) {
                rule.setExitOperatorIndex(Rule.LESS_THAN_OR_EQUAL_TO);
            } else if (property.equals("fes:PropertyIsNotEqualTo")) {
                rule.setExitOperatorIndex(Rule.NOT_EQUAL_TO);
            }
            
            NodeList literalList = doc.getElementsByTagName(this.fesLiteral);
            Node literalNode = literalList.item(0);
            
            // rValue: Value
            rule.setEntryValue(literalNode.getFirstChild().getNodeValue()); 

            // rUnit: Value unit. Default value is meter
            rule.setEntryUnit("m");

            literalNode = literalList.item(1);
            
            // cValue: exit condition value
            rule.setExitValue(literalNode.getFirstChild().getNodeValue());
            
            // cUnit: exit condition value unit. Default value is meter
            rule.setExitUnit("m");
            
            // exit condition != enter condition?
            if (RulesUtil.reverseOperator(rule.getEntryOperatorIndex(), rule.getExitOperatorIndex()) && rule.getEntryValue().equals(rule.getExitValue())) {
                rule.setEnterEqualsExitCondition(true);
            } else {
                rule.setEnterEqualsExitCondition(false);
            }
            
            // set rule Type
            rule.setRuleType(SimpleRuleType.OVER_UNDERSHOOT);

        } catch (Exception e) {
            LOGGER.error("Error parsing EML rule", e);
        }

        return rule;
    }
}