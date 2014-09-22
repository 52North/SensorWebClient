/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.server.ses.eml;

import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;
import static org.n52.oxf.xmlbeans.tools.XmlUtil.qualifySubstitutionGroup;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.opengis.eml.x001.ComplexPatternDocument.ComplexPattern;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML.ComplexPatterns;
import net.opengis.eml.x001.EMLDocument.EML.SimplePatterns;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.eml.x001.UserParameterType;
import net.opengis.eml.x001.ViewType.UserDefinedView;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralDocument;
import net.opengis.fes.x20.LiteralType;
import net.opengis.fes.x20.PropertyIsEqualToDocument;
import net.opengis.fes.x20.PropertyIsNotEqualToDocument;
import net.opengis.fes.x20.ValueReferenceDocument;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlObject;
import org.n52.oxf.xmlbeans.tools.XmlUtil;
import org.n52.server.ses.SesConfig;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BasicRule_5_Builder extends BasicRuleBuilder {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicRule_5_Builder.class);

    // TAG NAMES
    final static String propertyValue = "value";

    final static String simplePattern = "SimplePattern";

    final static String complexPattern = "ComplexPattern";

    final static String selectFunction = "SelectFunction";

    final static String patternReference = "PatternReference";

    final static String selectEvent = "SelectEvent";

    final static String userParameterValue = "UserParameterValue";

    final static String eventCount = "EventCount";

    final static String fesFilter = "fes:Filter";

    final static String valuereference = "fes:ValueReference";

    final static String fesLiteral = "fes:Literal";

    // ATTRIBUTE NAMES
    final static String patternID = "patternID";

    final static String newEventName = "newEventName";

    final static String eventName = "eventName";

    final static String outputName = "outputName";

    
    public BasicRule_5_Builder() {
        super(SesConfig.resLocation_5);
    }

    /**
     * Sensor Failure
     * 
     * This method builds the rule type "Sensor Failure" by loading and filling a template file. The location
     * of this file is defined in /properties/ses-client.properties in the variable "resLocation". File name
     * must be BR_5.xml.
     * 
     * @param rule
     * @throws Exception
     * @return {@link BasicRule}
     */
    public BasicRule create_BR_5(Rule rule) throws Exception {

        try {
            String finalEml;
            String title = rule.getTitle();

            // Pre-defined pattern IDs and event names. All names start with the title of the rule.
            // This is important to have unique names.
            
            String incomingObservationsCountPatternId = title + "_incoming_observations_count_stream";
            String incomingObservationsEventName = title + "_incoming_observations_count";
            
            String noObservationsReceivedStreamPatternId = title + "_no_observation_received_stream";
            String observationsReceivedStreamPatternId = title + "_observation_received_stream";
            String noObservationsNotificationPatternId = title + "_no_observation_notification";
            String observationsNotificationPatternId = title + "_observation_notification";

            String noObservationsReceivedEventName = title + "_no_observation_received";
            String observationsReceivedEventName = title + "_observation_received";
            
            String noObservationsOutputName = title + "_no_observation_output";
            String observationsOutputName = title + "_observation_output";


            EMLDocument emlTemplateDoc = getEmlTemplate();
            SimplePatterns simplePatterns = emlTemplateDoc.getEML().getSimplePatterns();
            SimplePatternType incomingObservationCount = simplePatterns.getSimplePatternArray(0);
            processSimplePattern(incomingObservationCount, incomingObservationsCountPatternId, incomingObservationsEventName);
            processPropertyRestrictions(incomingObservationCount, rule.getTimeseriesMetadata());
            processDurationValue(incomingObservationCount, rule);
            
            // XXX check: no user paramter EventCount present in template 
//            NodeList eventCountList = fstElement.getElementsByTagName(eventCount);
//            if (eventCountList.getLength() != 0) {
//                Node eventCountNode = eventCountList.item(0);
//                eventCountNode.setTextContent(rule.getEntryCount());
//            }
            
            ComplexPatterns complexPatterns = emlTemplateDoc.getEML().getComplexPatterns();
            ComplexPattern noObservationsReceived = complexPatterns.getComplexPatternArray(0);
            processComplexPattern(noObservationsReceived, noObservationsReceivedStreamPatternId, noObservationsReceivedEventName);
            setSelectEventName(noObservationsReceived, incomingObservationsEventName);
            noObservationsReceived.getFirstPattern().setPatternReference(incomingObservationsCountPatternId);
            noObservationsReceived.getSecondPattern().setPatternReference(incomingObservationsCountPatternId);
            processEqualToFilterGuard(noObservationsReceived, incomingObservationsEventName);

            ComplexPattern observationsReceived = complexPatterns.getComplexPatternArray(1);
            processComplexPattern(observationsReceived, observationsReceivedStreamPatternId, observationsReceivedEventName);
            setSelectEventName(observationsReceived, incomingObservationsEventName);
            observationsReceived.getFirstPattern().setPatternReference(incomingObservationsCountPatternId);
            observationsReceived.getSecondPattern().setPatternReference(incomingObservationsCountPatternId);
            processNotEqualToFilterGuard(observationsReceived, incomingObservationsEventName);
            
            ComplexPattern noObservationsReceivedNotification = complexPatterns.getComplexPatternArray(2);
            processComplexPattern(noObservationsReceivedNotification, noObservationsNotificationPatternId, noObservationsReceivedEventName);
            setOutputName(noObservationsReceivedNotification, noObservationsOutputName);
            noObservationsReceivedNotification.getFirstPattern().setPatternReference(observationsReceivedStreamPatternId);
            noObservationsReceivedNotification.getSecondPattern().setPatternReference(noObservationsReceivedStreamPatternId);

            ComplexPattern observationsReceivedNotification = complexPatterns.getComplexPatternArray(3);
            processComplexPattern(observationsReceivedNotification, observationsNotificationPatternId, observationsReceivedEventName);
            setOutputName(observationsReceivedNotification, observationsOutputName);
            observationsReceivedNotification.getFirstPattern().setPatternReference(noObservationsReceivedStreamPatternId);
            observationsReceivedNotification.getSecondPattern().setPatternReference(observationsReceivedStreamPatternId);
            
            finalEml = emlTemplateDoc.xmlText();

            User user = getUserFrom(rule);
            BasicRule basicRule = new BasicRule(rule.getTitle(),
                                 "B",
                                 "BR5",
                                 rule.getDescription(),
                                 rule.isPublish(),
                                 user.getId(),
                                 finalEml,
                                 false);
            basicRule.setUuid(rule.getUuid());
            return basicRule;
        } catch (Exception e) {
            // TODO improve exception handling in a whole!
            LOGGER.error("Error creating rule", e);
            return null;
        }
    }

    private void processDurationValue(SimplePatternType incomingObservationCount, Rule rule) {
        UserDefinedView userDefinedView = incomingObservationCount.getView().getUserDefinedView();
        UserParameterType durationParameter = userDefinedView.getViewParameters().getViewParameterArray(2);
        durationParameter.setUserParameterValue("PT" + rule.getEntryTime() + rule.getEntryTimeUnit()); // XXX period or iso?
    }

    private void processEqualToFilterGuard(ComplexPattern pattern, String eventName) {
        FilterType filter = pattern.getGuard().getFilter();
        PropertyIsEqualToDocument equalToDoc = PropertyIsEqualToDocument.Factory.newInstance();
        setFilterProperty(filter, eventName, equalToDoc.addNewPropertyIsEqualTo());
        qualifyComparisonType(filter, equalToDoc.schemaType());
    }

    private void processNotEqualToFilterGuard(ComplexPattern pattern, String eventName) {
        FilterType filter = pattern.getGuard().getFilter();
        PropertyIsNotEqualToDocument notEqualToDoc = PropertyIsNotEqualToDocument.Factory.newInstance();
        setFilterProperty(filter, eventName, notEqualToDoc.addNewPropertyIsNotEqualTo());
        qualifyComparisonType(filter, notEqualToDoc.schemaType());
    }

    protected void setFilterProperty(FilterType filter, String eventName, BinaryComparisonOpType binaryOperator) {
        processDoubleValueExpression(binaryOperator, eventName);
        filter.setComparisonOps(binaryOperator);
    }

    private void processDoubleValueExpression(BinaryComparisonOpType binaryComparison, String eventName) {
        ValueReferenceDocument valueReference = ValueReferenceDocument.Factory.newInstance();
        valueReference.setValueReference(eventName + "/doubleValue");
        binaryComparison.set(valueReference);
        
        LiteralType literalType = LiteralDocument.Factory.newInstance().addNewLiteral();
        XmlUtil.setTextContent(literalType, "0");
        XmlObject expression = binaryComparison.addNewExpression().set(literalType);
        qualifySubstitutionGroup(expression, LiteralDocument.type.getDocumentElementName());
    }
    
    protected XmlObject qualifyComparisonType(FilterType filter, SchemaType schemaType) {
        return qualifySubstitutionGroup(filter.getComparisonOps(), schemaType.getDocumentElementName());
    }

    /**
     * 
     * This method is used to parse an EML file and return a Rule class with rule specific attributes. The
     * method is called if user want to edit this rule type.
     * 
     * @param basicRule
     * @return {@link Rule}
     */
    public Rule getRuleByEML(BasicRule basicRule) {
        Rule rule = new Rule();
        rule.setTimeseriesMetadata(basicRule.getTimeseriesMetadata());

        try {
            String eml = basicRule.getEml();
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(eml.getBytes()));

            NodeList userParameterValueList = doc.getElementsByTagName(userParameterValue);
            Node userParameterValueNode = userParameterValueList.item(2);
            String temp = userParameterValueNode.getTextContent();
            temp.substring(2);

            // rTime: time value
            rule.setrTime(temp.substring(2, temp.length() - 1));

            // rTimeUnit: time unit
            rule.setrTimeUnit(temp.substring(temp.length() - 1));

            // set rule type
            rule.setRuleType(SENSOR_LOSS);

        }
        catch (Exception e) {
            LOGGER.error("Error parsing EML rule", e);
        }

        return rule;
    }
}