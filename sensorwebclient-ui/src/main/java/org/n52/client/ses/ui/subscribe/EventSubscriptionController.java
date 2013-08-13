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

package org.n52.client.ses.ui.subscribe;

import static java.lang.Integer.parseInt;
import static org.n52.client.util.ClientSessionManager.getLoggedInUserId;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;
import static org.n52.shared.util.MathSymbolUtil.getIndexFor;

import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.RuleBuilder;
import org.n52.shared.serializable.pojos.TimeseriesMetadata;

class EventSubscriptionController {

    private final static SimpleRuleType DEFAULT_RULE_TEMPLATE = OVER_UNDERSHOOT;

    private EventSubscriptionWindow eventSubscriptionWindow;

    private EventNameForm eventNameForm;

    private TimeseriesLegendData timeseries;

    private String selectedAbonnementName;

    private SubscriptionTemplate selectedSubsciptionTemplate;

    private OverUndershootSelectionData overUndershootEntryConditions;

    private OverUndershootSelectionData overUndershootExitConditions;
    
    private SensorLossSelectionData sensorLossConditions;

    void setEventSubscription(EventSubscriptionWindow eventSubsciptionWindow) {
        this.eventSubscriptionWindow = eventSubsciptionWindow;
    }
    
    void setEventNameForm(EventNameForm eventNameForm) {
        this.eventNameForm = eventNameForm;
    }

    public void setTimeseries(TimeseriesLegendData timeseries) {
        this.timeseries = timeseries;
    }

    public TimeseriesLegendData getTimeSeries() {
        return timeseries;
    }
    
    public String getServiceUrl() {
        return timeseries.getSosUrl();
    }
    
    public String getOffering() {
        return timeseries.getOfferingId();
    }
    
    public String getPhenomenon() {
        return timeseries.getPhenomenonId();
    }
    
    public String getProcedure() {
        return timeseries.getProcedureId();
    }
    
    public String getFeatureOfInterest() {
        return timeseries.getFeatureId();
    }

    public void setSelectedAbonnementName(String currentAbonnementName) {
        this.selectedAbonnementName = currentAbonnementName;
    }
    
    public String getSelectedAbonnementName() {
        return selectedAbonnementName;
    }

	public boolean isSelectionValid() {
		// TODO validate template
//		 return selectedRuleTemplate.validateTemplate();
		if (selectedSubsciptionTemplate instanceof OverUndershootRuleTemplate) {
			if (overUndershootEntryConditions.getValue() != null
					&& overUndershootExitConditions.getValue() != null
					&& selectedAbonnementName != null) {
				return true;
			}
		} else if (selectedSubsciptionTemplate instanceof SensorLossRuleTemplate) {
			if (sensorLossConditions != null
					&& sensorLossConditions.getUnit() != null
					&& sensorLossConditions.getValue() != null) {
				return true;
			}
		}
		return false;
	}
    
    public void setSelectedSubscriptionTemplate(SubscriptionTemplate template) {
        selectedSubsciptionTemplate = template;
    }
    
    public void updateSelectedRuleTemplate(SubscriptionTemplate template) {
        setSelectedSubscriptionTemplate(template);
        eventSubscriptionWindow.updateSubscriptionEditingCanvas(template);
        eventNameForm.updateSuggestedAbonnementName(createSuggestedAbonnementName());
    }
    
    /**
     * @return an abonnement name suggestion for current timeseries.
     */
    public String createSuggestedAbonnementName() {
        if (timeseries == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(timeseries.getTimeSeriesLabel());
        if (getSelectedRuleTemplate() == OVER_UNDERSHOOT) {
            sb.append("_").append(OVER_UNDERSHOOT.toString());
        } else if (getSelectedRuleTemplate() == SENSOR_LOSS) {
            sb.append("_").append(SENSOR_LOSS.toString());
        }
        selectedAbonnementName = normalize(sb.toString());
        return selectedAbonnementName;
    }
    
    String normalize(String toNormalize) {
        return replaceNonAlphaNumerics(replaceAllUmlauts(toNormalize));
    }

    String replaceAllUmlauts(String toReplace) {
        toReplace = toReplace.replaceAll("[ö]", "oe");
        toReplace = toReplace.replaceAll("^Ö", "Oe");
        toReplace = toReplace.replaceAll("[Ö]", "OE");
        toReplace = toReplace.replaceAll("[ä]", "ae");
        toReplace = toReplace.replaceAll("^Ä", "Ae");
        toReplace = toReplace.replaceAll("[Ä]", "AE");
        toReplace = toReplace.replaceAll("[ü]", "ue");
        toReplace = toReplace.replaceAll("^Ü", "Ue");
        toReplace = toReplace.replaceAll("[Ü]", "UE");
        toReplace = toReplace.replaceAll("[ß]", "ss");
        return toReplace;
    }

    String replaceNonAlphaNumerics(String toReplace) {
        return toReplace.replaceAll("[^0-9a-zA-Z_]", "_");
    }

    public SimpleRuleType getSelectedRuleTemplate() {
        return selectedSubsciptionTemplate == null 
                    ? DEFAULT_RULE_TEMPLATE
                    : selectedSubsciptionTemplate.getRuleType();
    }

    public OverUndershootSelectionData getOverUndershootEntryConditions() {
        if (overUndershootEntryConditions == null) {
            overUndershootEntryConditions = new OverUndershootSelectionData();
        }
        return overUndershootEntryConditions;
    }

    public OverUndershootSelectionData getOverUndershootExitConditions() {
        if (overUndershootExitConditions == null) {
            overUndershootExitConditions = new OverUndershootSelectionData();
        }
        return overUndershootExitConditions;
    }
    
    public SensorLossSelectionData getSensorLossConditions() {
        if (sensorLossConditions == null) {
            sensorLossConditions = new SensorLossSelectionData();
        }
        return sensorLossConditions;
    }

    public void clearSelectionData() {
        overUndershootEntryConditions = null;
        overUndershootExitConditions = null;
        sensorLossConditions = null;
    }
    
    public Rule createSimpleRuleFromSelection() {
        SimpleRuleType ruleType = getSelectedRuleTemplate();
        if (ruleType == OVER_UNDERSHOOT) {
            return createOverUndershootRule();
        } else if (ruleType == SENSOR_LOSS) {
            return createSensorLossRule();
        }
        return RuleBuilder.aRule().build();
    }

    private Rule createOverUndershootRule() {
        final String subscriptionName = selectedAbonnementName;
        final OverUndershootSelectionData entryConditions = overUndershootEntryConditions;
        final OverUndershootSelectionData exitConditions = overUndershootExitConditions;
        return RuleBuilder.aRule()
                .setTitle(subscriptionName)
                .setRuleType(OVER_UNDERSHOOT)
                .setUserId(parseInt(getLoggedInUserId()))
                .setDescription("Auto-Generated Rule from Template.")
                .setTimeseriesMetadata(createTimeseriesMetadata())
                .setEntryOperatorIndex(getIndexFor(entryConditions.getOperator()))
                .setEntryValue(entryConditions.getValue())
                .setEntryUnit(entryConditions.getUnit())
                .setEnterIsSameAsExitCondition(false)
                .setExitOperatorIndex(getIndexFor(exitConditions.getOperator()))
                .setExitValue(exitConditions.getValue())
                .setExitUnit(exitConditions.getUnit())
                .setPublish(false)
                .build();
    }

    private Rule createSensorLossRule() {
        final String subscriptionName = selectedAbonnementName;
        final SensorLossSelectionData condition = sensorLossConditions;
        return RuleBuilder.aRule()
                .setTitle(subscriptionName)
                .setRuleType(SENSOR_LOSS)
                .setUserId(parseInt(getLoggedInUserId()))
                .setDescription("Auto-Generated Rule from Template.")
                .setTimeseriesMetadata(createTimeseriesMetadata())
                .setEntryTime(condition.getValue())
                .setEntryTimeUnit(condition.getUnit())
                .setPublish(false)
                .build();
    }
    
    private TimeseriesMetadata createTimeseriesMetadata() {
        final TimeseriesLegendData timeseries = this.timeseries;
        TimeseriesMetadata metadata = new TimeseriesMetadata();
        metadata.setServiceUrl(timeseries.getSosUrl());
        metadata.setOffering(timeseries.getOfferingId());
        metadata.setProcedure(timeseries.getProcedureId());
        metadata.setPhenomenon(timeseries.getPhenomenonId());
        metadata.setFeatureOfInterest(timeseries.getFeatureId());
        return metadata;
    }

    SimpleRuleType getDefaultTemplate() {
        return DEFAULT_RULE_TEMPLATE;
    }
    
}
