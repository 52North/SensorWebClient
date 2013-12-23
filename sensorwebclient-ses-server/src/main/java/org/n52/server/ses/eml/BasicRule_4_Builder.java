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

import static org.n52.oxf.xmlbeans.tools.XmlUtil.getXmlAnyNodeFrom;
import static org.n52.oxf.xmlbeans.tools.XmlUtil.qualifySubstitutionGroup;
import static org.n52.shared.util.MathSymbolUtil.EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_OR_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_OR_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.NOT_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.getSymbolIndexForFilter;
import net.opengis.eml.x001.ComplexPatternDocument.ComplexPattern;
import net.opengis.eml.x001.EMLDocument;
import net.opengis.eml.x001.EMLDocument.EML.ComplexPatterns;
import net.opengis.eml.x001.EMLDocument.EML.SimplePatterns;
import net.opengis.eml.x001.GuardType;
import net.opengis.eml.x001.SimplePatternType;
import net.opengis.fes.x20.BinaryComparisonOpType;
import net.opengis.fes.x20.ComparisonOpsType;
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

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.server.ses.SesConfig;
import org.n52.shared.serializable.pojos.BasicRule;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

/**
 * Overshoot and Undershoot Rule
 */
public class BasicRule_4_Builder extends BasicRuleBuilder {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(BasicRule_4_Builder.class);

    private final int INDEX_SIMPLE_PATTERN_INTIAL_COUNT = 0;

    private final int INDEX_EXIT_CONDITION_PATTERN = 1;

    private final int INDEX_ENTRY_CONDITION_PATTERN = 2;

    private final int INDEX_COMPLEX_PATTERN_INTIAL_ENTRY = 0;

    private final int INDEX_ENTRY_NOTIFICATION_PATTERN = 1;

    private final int INDEX_EXIT_NOTIFICATION_PATTERN = 2;

    private final int INDEX_COMPLEX_PATTERN_INTIAL_EXIT = 3;


    private final String overshoot = "_overshoot";

    private final String overshootStream = "_overshoot_stream";

    private final String undershootStream = "_undershoot_stream";

    private final String undershoot = "_undershoot";

    private final String overshootNotification = "_overshoot_notification";

    private final String overshootNotificationStream = "_overshoot_notification_stream";

    private final String undershootNotificationStream = "_undershoot_notification";

    private final String undershootNotification = "_undershoot_notification_stream";

    private final String INPUT_STREAM_NAME = "input/doubleValue";
    
    private final String INITIAL_STREAM_NAME = "initial_count_stream/doubleValue";
    
    private final String enter = "_enter";

    private final String exit = "_exit";

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
            SimplePatternType staticInitPattern = simplePatterns.getSimplePatternArray(INDEX_SIMPLE_PATTERN_INTIAL_COUNT);
            processPropertyRestrictions(staticInitPattern, rule.getTimeseriesMetadata());
            
            RuleFilter entryFilter = createEntryFilter(rule);
            SimplePatternType ruleUndershoot = simplePatterns.getSimplePatternArray(INDEX_ENTRY_CONDITION_PATTERN);
            processSimplePattern(ruleUndershoot, overshootPatternId, overshootEventName);
            processPropertyRestrictions(ruleUndershoot, rule.getTimeseriesMetadata());
            processFilterGuard(ruleUndershoot.getGuard(), entryFilter, INPUT_STREAM_NAME);

            RuleFilter exitFilter = createExitFilter(rule);
            SimplePatternType ruleOvershoot = simplePatterns.getSimplePatternArray(INDEX_EXIT_CONDITION_PATTERN);
            processSimplePattern(ruleOvershoot, undershootPatternId, undershootEventName);
            processPropertyRestrictions(ruleOvershoot, rule.getTimeseriesMetadata());
            processFilterGuard(ruleOvershoot.getGuard(), exitFilter, INPUT_STREAM_NAME);
            
            // set patternID of complexPatterns
            ComplexPattern entryClause = complexPatterns.getComplexPatternArray(INDEX_ENTRY_NOTIFICATION_PATTERN);
            processComplexPattern(entryClause, entryNotificationPatternId, entryEventName, output_enter);
            entryClause.getFirstPattern().setPatternReference(undershootPatternId);
            entryClause.getSecondPattern().setPatternReference(overshootPatternId);

            ComplexPattern exitClause = complexPatterns.getComplexPatternArray(INDEX_EXIT_NOTIFICATION_PATTERN);
            processComplexPattern(exitClause, exitNotificationPatternId, exitEventName, output_exit);
            exitClause.getFirstPattern().setPatternReference(overshootPatternId);
            exitClause.getSecondPattern().setPatternReference(undershootPatternId);
            
            /*
             * A rule shall also match directly for new created subscriptions, 
             * i.e. when the first and initial value matches the rule applies.
             */
            ComplexPattern initialEntryClause = complexPatterns.getComplexPatternArray(INDEX_COMPLEX_PATTERN_INTIAL_ENTRY);
            ComplexPattern initialExitClause = complexPatterns.getComplexPatternArray(INDEX_COMPLEX_PATTERN_INTIAL_EXIT);
            processFilterGuard(initialExitClause.getGuard(), exitFilter, INITIAL_STREAM_NAME);
            processFilterGuard(initialEntryClause.getGuard(), entryFilter, INITIAL_STREAM_NAME);
            
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

    private void processFilterGuard(GuardType guardType, RuleFilter ruleFilter, String stream) {
        FilterType filter = guardType.getFilter();
        processComparisonFilter(filter, ruleFilter, stream);
    }

    void processComparisonFilter(FilterType filter, RuleFilter ruleFilter, String stream) {
        if (ruleFilter.getOperator() == LESS_THAN_INT) {
            PropertyIsLessThanDocument lessThanDoc = PropertyIsLessThanDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = lessThanDoc.addNewPropertyIsLessThan();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), lessThanDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == GREATER_THAN_INT) {
            PropertyIsGreaterThanDocument greaterThanDoc = PropertyIsGreaterThanDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = greaterThanDoc.addNewPropertyIsGreaterThan();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator); 
            qualifySubstitutionGroup(filter.getComparisonOps(), greaterThanDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == EQUAL_TO_INT) {
            PropertyIsEqualToDocument equalToDoc = PropertyIsEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = equalToDoc.addNewPropertyIsEqualTo();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), equalToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == GREATER_THAN_OR_EQUAL_TO_INT) {
            PropertyIsGreaterThanOrEqualToDocument greaterOrEqualToDoc = PropertyIsGreaterThanOrEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = greaterOrEqualToDoc.addNewPropertyIsGreaterThanOrEqualTo();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), greaterOrEqualToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == LESS_THAN_OR_EQUAL_TO_INT) {
            PropertyIsLessThanOrEqualToDocument lessThanOrEqualToDoc = PropertyIsLessThanOrEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = lessThanOrEqualToDoc.addNewPropertyIsLessThanOrEqualTo();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), lessThanOrEqualToDoc.schemaType().getDocumentElementName());
        } else if (ruleFilter.getOperator() == NOT_EQUAL_TO_INT) {
            PropertyIsNotEqualToDocument notEqualToDoc = PropertyIsNotEqualToDocument.Factory.newInstance();
            BinaryComparisonOpType binaryOperator = notEqualToDoc.addNewPropertyIsNotEqualTo();
            processStreamReferenceExpression(binaryOperator, ruleFilter, stream);
            filter.setComparisonOps(binaryOperator);
            qualifySubstitutionGroup(filter.getComparisonOps(), notEqualToDoc.schemaType().getDocumentElementName());
        } 
    }

    private void processStreamReferenceExpression(BinaryComparisonOpType binaryComparison, RuleFilter ruleFilter, String stream) {
        ValueReferenceDocument valueReference = ValueReferenceDocument.Factory.newInstance();
        valueReference.setValueReference(stream);
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
            EMLDocument emlDocument = EMLDocument.Factory.parse(eml);
            SimplePatterns simplePatterns = emlDocument.getEML().getSimplePatterns();
            setEntryConditions(rule, simplePatterns.getSimplePatternArray(INDEX_ENTRY_CONDITION_PATTERN));
            setExitConditions(rule, simplePatterns.getSimplePatternArray(INDEX_EXIT_CONDITION_PATTERN));
            rule.setRuleType(SimpleRuleType.OVER_UNDERSHOOT);

        } catch (Exception e) {
            LOGGER.error("Error parsing EML rule", e);
        }
        return rule;
    }

    private void setEntryConditions(Rule rule, SimplePatternType entryCondition) {
        ComparisonOpsType comparisonOps = getComparisonType(entryCondition);
        String filterType = comparisonOps.getDomNode().getLocalName();
        int binaryOperatorIndex = getSymbolIndexForFilter(filterType);
        Quantity entryFilterValues = parseFilterQuantity(comparisonOps, binaryOperatorIndex);
        
        rule.setEntryValue("" + entryFilterValues.getValue());
        rule.setEntryUnit(entryFilterValues.getUom().getCode());
        rule.setEntryOperatorIndex(binaryOperatorIndex);
    }

    private void setExitConditions(Rule rule, SimplePatternType exitCondition) {
        ComparisonOpsType comparisonOps = getComparisonType(exitCondition);
        String filterType = comparisonOps.getDomNode().getLocalName();
        int binaryOperatorIndex = getSymbolIndexForFilter(filterType);
        Quantity exitFilterValues = parseFilterQuantity(comparisonOps, binaryOperatorIndex);
        
        rule.setExitValue("" + exitFilterValues.getValue());
        rule.setExitUnit(exitFilterValues.getUom().getCode());
        rule.setExitOperatorIndex(binaryOperatorIndex);
    }

    private ComparisonOpsType getComparisonType(SimplePatternType entryCondition) {
        FilterType entryFilter = entryCondition.getGuard().getFilter();
        return entryFilter.getComparisonOps();
    }

    private Quantity parseFilterQuantity(ComparisonOpsType comparisonOps, int binaryOperatorIndex) {
        try {
            if (binaryOperatorIndex == LESS_THAN_INT) {
                PropertyIsLessThanDocument lessThan = PropertyIsLessThanDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(lessThan.getPropertyIsLessThan());
            } else if (binaryOperatorIndex == GREATER_THAN_INT) {
                PropertyIsGreaterThanDocument binaryOperator = PropertyIsGreaterThanDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(binaryOperator.getPropertyIsGreaterThan());
            } else if (binaryOperatorIndex == EQUAL_TO_INT) {
                PropertyIsEqualToDocument binaryOperator = PropertyIsEqualToDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(binaryOperator.getPropertyIsEqualTo());
            } else if (binaryOperatorIndex == GREATER_THAN_OR_EQUAL_TO_INT) {
                PropertyIsGreaterThanOrEqualToDocument binaryOperator = PropertyIsGreaterThanOrEqualToDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(binaryOperator.getPropertyIsGreaterThanOrEqualTo());
            } else if (binaryOperatorIndex == LESS_THAN_OR_EQUAL_TO_INT) {
                PropertyIsLessThanOrEqualToDocument binaryOperator = PropertyIsLessThanOrEqualToDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(binaryOperator.getPropertyIsLessThanOrEqualTo());
            } else if (binaryOperatorIndex == NOT_EQUAL_TO_INT) {
                PropertyIsNotEqualToDocument binaryOperator = PropertyIsNotEqualToDocument.Factory.parse(comparisonOps.getDomNode());
                return getQuantityFrom(binaryOperator.getPropertyIsNotEqualTo());
            } else {
                throw new IllegalStateException("Unknown ComparisonType in EML: " + comparisonOps.schemaType());
            }
        }
        catch (XmlException e) {
            throw new IllegalStateException("Could not get Quantity from EML filter!", e);
        }
    }

    private Quantity getQuantityFrom(BinaryComparisonOpType comparisonType) throws XmlException {
        Node literalNode = getXmlAnyNodeFrom(comparisonType, "Literal").getDomNode();
        LiteralDocument literal = LiteralDocument.Factory.parse(literalNode);
        return ((QuantityDocument) getXmlAnyNodeFrom(literal.getLiteral(), "Quantity")).getQuantity();
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