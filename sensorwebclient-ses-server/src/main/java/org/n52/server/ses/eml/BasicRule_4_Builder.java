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

import static org.n52.oxf.xmlbeans.tools.XmlUtil.qualifySubstitutionGroup;
import static org.n52.shared.util.MathSymbolUtil.EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_OR_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_OR_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.NOT_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.getSymbolIndexForFilter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import net.opengis.eml.x001.ComplexPatternDocument.ComplexPattern;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML.ComplexPatterns;
import net.opengis.eml.x001.EMLDocument.EML.SimplePatterns;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.FilterType;
import net.opengis.fes.x20.LiteralDocument;
import net.opengis.fes.x20.LiteralType;
import net.opengis.fes.x20.PropertyIsEqualToDocument;
import net.opengis.fes.x20.PropertyIsGreaterThanDocument;
import net.opengis.fes.x20.PropertyIsGreaterThanOrEqualToDocument;
import net.opengis.fes.x20.PropertyIsLessThanDocument;
import net.opengis.fes.x20.PropertyIsLessThanOrEqualToDocument;
import net.opengis.fes.x20.PropertyIsNotEqualToDocument;
import net.opengis.fes.x20.ValueReferenceDocument;
import net.opengis.swe.x101.QuantityDocument;
import net.opengis.swe.x101.QuantityDocument.Quantity;

import org.apache.xmlbeans.XmlObject;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.server.ses.SesConfig;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Overshoot and Undershoot Rule
 */
public class BasicRule_4_Builder extends BasicRuleBuilder {
    
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

    // Pattern names
    private String overshootPatternId;

    private String undershootPatternId;

    private String entryNotificationPatternId;

    private String exitNotificationPatternId;

    // newEventNames
    private String overshootEventName;

    private String undershootEventName;

    private String entryEventName;

    private String exitEventName;

    // output names
    private String output_enter;

    private String output_exit;

    private String eml;

    private String finalEml;
    
    public BasicRule_4_Builder() {
        super(SesConfig.resLocation_4);
    }

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
        this.overshootPatternId = title + this.overshootStream;
        this.undershootPatternId = title + this.undershootStream;
        this.entryNotificationPatternId = title + this.overshootNotificationStream;
        this.exitNotificationPatternId = title + this.undershootNotificationStream;

        this.overshootEventName = title + this.overshoot;
        this.undershootEventName = title + this.undershoot;
        this.entryEventName = title + this.overshootNotification;
        this.exitEventName = title + this.undershootNotification;

        this.output_enter = title + this.enter;
        this.output_exit = title + this.exit;

        try {
            EMLDocument emlTemplateDoc = getEmlTemplate();

            SimplePatterns simplePatterns = emlTemplateDoc.getEML().getSimplePatterns();
            ComplexPatterns complexPatterns = emlTemplateDoc.getEML().getComplexPatterns();

            // set patternID of simplePatterns
            SimplePatternType ruleUndershoot = simplePatterns.getSimplePatternArray(0);
            processSimplePattern(ruleUndershoot, overshootPatternId, overshootEventName);
            processPropertyRestrictions(ruleUndershoot, rule.getTimeseriesMetadata());
            processFilterGuard(ruleUndershoot, createEntryFilter(rule));
            
            SimplePatternType ruleOvershoot = simplePatterns.getSimplePatternArray(1);
            processSimplePattern(ruleOvershoot, undershootPatternId, undershootEventName);
            processPropertyRestrictions(ruleOvershoot, rule.getTimeseriesMetadata());
            processFilterGuard(ruleOvershoot, createExitFilter(rule));
            
            // set patternID of complexPatterns
            ComplexPattern entryClause = complexPatterns.getComplexPatternArray(0);
            processComplexPattern(entryClause, entryNotificationPatternId, entryEventName, output_enter);
            entryClause.getFirstPattern().setPatternReference(undershootPatternId);
            entryClause.getSecondPattern().setPatternReference(overshootPatternId);
            
            ComplexPattern exitClause = complexPatterns.getComplexPatternArray(1);
            processComplexPattern(exitClause, exitNotificationPatternId, exitEventName, output_exit);
            exitClause.getFirstPattern().setPatternReference(overshootPatternId);
            exitClause.getSecondPattern().setPatternReference(undershootPatternId);
            
            eml = emlTemplateDoc.xmlText();
            finalEml = eml;

        } catch (Exception e) {
            LOGGER.error("Error creating rule", e);
            return null;
        }
        
        // Get current user. This user is also the owner of the new rule
        User user = getUserFrom(rule);
        
        BasicRule basicRule = new BasicRule(rule.getTitle(), "B", "BR4", rule.getDescription(), rule.isPublish(), user.getId(), finalEml, false);
        basicRule.setUuid(rule.getUuid());
        return basicRule;
    }

    private RuleFilter createEntryFilter(Rule rule) {
        return new RuleFilter(rule.getEntryOperatorIndex(), rule.getEntryValue(), rule.getEntryUnit());
    }

    private RuleFilter createExitFilter(Rule rule) {
        return new RuleFilter(rule.getExitOperatorIndex(), rule.getExitValue(), rule.getExitUnit());
    }

    private void processFilterGuard(SimplePatternType pattern, RuleFilter ruleFilter) {
        FilterType filter = pattern.getGuard().getFilter();
        processComparisonFilter(filter, ruleFilter);
    }

    void processComparisonFilter(FilterType filter, RuleFilter ruleFilter) {
        if (ruleFilter.getOperator() == LESS_THAN_INT) {
            PropertyIsLessThanDocument lessThanDoc = PropertyIsLessThanDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = lessThanDoc.addNewPropertyIsLessThan();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), lessThanDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == GREATER_THAN_INT) {
            PropertyIsGreaterThanDocument greaterThanDoc = PropertyIsGreaterThanDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = greaterThanDoc.addNewPropertyIsGreaterThan();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator); 
            qualifySubstitutionGroup(filter.getComparisonOps(), greaterThanDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == EQUAL_TO_INT) {
            PropertyIsEqualToDocument equalToDoc = PropertyIsEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = equalToDoc.addNewPropertyIsEqualTo();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), equalToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == GREATER_THAN_OR_EQUAL_TO_INT) {
            PropertyIsGreaterThanOrEqualToDocument greaterOrEqualToDoc = PropertyIsGreaterThanOrEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = greaterOrEqualToDoc.addNewPropertyIsGreaterThanOrEqualTo();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), greaterOrEqualToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == LESS_THAN_OR_EQUAL_TO_INT) {
            PropertyIsLessThanOrEqualToDocument lessThanOrEqualToDoc = PropertyIsLessThanOrEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = lessThanOrEqualToDoc.addNewPropertyIsLessThanOrEqualTo();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), lessThanOrEqualToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == NOT_EQUAL_TO_INT) {
            PropertyIsNotEqualToDocument notEqualToDoc = PropertyIsNotEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = notEqualToDoc.addNewPropertyIsNotEqualTo();
            processDoubleValueExpression(binaryOperator, ruleFilter);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), notEqualToDoc.schemaType().getDocumentElementName());
        } 
    }

    private void processDoubleValueExpression(BinaryComparisonOpType binaryComparison, RuleFilter ruleFilter) {
        ValueReferenceDocument valueReference = ValueReferenceDocument.Factory.newInstance();
        valueReference.setValueReference("input/doubleValue");
        binaryComparison.set(valueReference);

        LiteralType literalType = LiteralDocument.Factory.newInstance().addNewLiteral();
        literalType.set(createQuantity(ruleFilter));
//        XmlUtil.setTextContent(literalType, value);
        XmlObject expression = binaryComparison.addNewExpression().set(literalType);
        qualifySubstitutionGroup(expression, LiteralDocument.type.getDocumentElementName());
    }

    protected QuantityDocument createQuantity(RuleFilter ruleFilter) {
        QuantityDocument quantityDoc = QuantityDocument.Factory.newInstance();
        Quantity quantity = quantityDoc.addNewQuantity();
        quantity.setValue(Double.parseDouble(ruleFilter.getValue()));
        if (isValidUom(ruleFilter.getUnit())) {
            quantity.addNewUom().setCode(ruleFilter.getUnit());
        }
        return quantityDoc;
    }

    private boolean isValidUom(String unit) {
        return !unit.isEmpty() && !"--".equals(unit);
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
        rule.setTimeseriesMetadata(basicRule.getTimeseriesMetadata());

        try {
            String eml = basicRule.getEml();
            DocumentBuilderFactory docFac = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFac.newDocumentBuilder();
            Document doc = docBuilder.parse(new ByteArrayInputStream(eml.getBytes()));

            NodeList filterList = doc.getElementsByTagName(fesFilter);
            Node entryOperatorNode = filterList.item(0);
            String entryFilter = entryOperatorNode.getChildNodes().item(1).getNodeName();
            rule.setEntryOperatorIndex(getSymbolIndexForFilter(entryFilter));

            Node exitOperatorNode = filterList.item(1);
            String exitFilter = exitOperatorNode.getChildNodes().item(1).getNodeName();
            rule.setExitOperatorIndex(getSymbolIndexForFilter(exitFilter));
            
            rule.setEnterEqualsExitCondition(rule.determineEqualEntryExitCondition());
            
            NodeList literalList = doc.getElementsByTagName(Constants.fesLiteral);
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
            
            // set rule Type
            rule.setRuleType(SimpleRuleType.OVER_UNDERSHOOT);

        } catch (Exception e) {
            LOGGER.error("Error parsing EML rule", e);
        }

        return rule;
    }

    private class RuleFilter {
    
        private int operator;
        private String value;
        private String unit;
    
        public RuleFilter(int operatorIndex, String value, String unit) {
            this.operator = operatorIndex;
            this.value = value;
            this.unit = unit;
        }
    
        public int getOperator() {
            return operator;
        }
    
        public String getValue() {
            return value;
        }
    
        public String getUnit() {
            return unit;
        }
        
    }
}