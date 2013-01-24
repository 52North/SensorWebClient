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

import static com.google.gwt.user.client.Cookies.getCookie;
import static java.lang.Integer.parseInt;
import static org.n52.client.ses.ctrl.SesRequestManager.COOKIE_USER_ID;
import static org.n52.client.ses.util.RuleOperatorUtil.getOperatorIndex;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;
import org.n52.shared.serializable.pojos.FeedingMetadata;
import org.n52.shared.serializable.pojos.Rule;
import org.n52.shared.serializable.pojos.RuleBuilder;

class EventSubscriptionController {

    private EventSubscriptionWindow eventSubscriptionWindow;

    private EventNameForm eventNameForm;

    private TimeSeries timeseries;

    private String selectedAbonnementName;

    private RuleTemplate selectedRuleTemplate;

    private OverUndershootSelectionData overUndershootEntryConditions;

    private OverUndershootSelectionData overUndershootExitConditions;
    
    private SensorLossSelectionData sensorLossConditions;

    void setEventSubscription(EventSubscriptionWindow eventSubsciptionWindow) {
        this.eventSubscriptionWindow = eventSubsciptionWindow;
    }
    
    void setEventNameForm(EventNameForm eventNameForm) {
        this.eventNameForm = eventNameForm;
    }

    public void setTimeseries(TimeSeries timeseries) {
        this.timeseries = timeseries;
    }

    public TimeSeries getTimeSeries() {
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

    /**
     * @return an abonnement name suggestion for current timeseries.
     */
    public String createSuggestedAbonnementName() {
        if (timeseries == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append(timeseries.getProcedureId());
        if (getSelectedRuleTemplate() == OVER_UNDERSHOOT) {
            sb.append("_").append(OVER_UNDERSHOOT.toString());
        } else if (getSelectedRuleTemplate() == SENSOR_LOSS) {
            sb.append("_").append(SENSOR_LOSS.toString());
        }
        return sb.toString();
    }

    public void setSelectedRuleTemplate(RuleTemplate template) {
        selectedRuleTemplate = template;
        eventSubscriptionWindow.updateRuleEditCanvas(template);
        eventNameForm.updateSuggestedAbonnementName(createSuggestedAbonnementName());
    }
    
    public SimpleRuleType getSelectedRuleTemplate() {
        return selectedRuleTemplate.getRuleType();
    }

    public OverUndershootSelectionData getOverUndershootEntryConditions() {
        return (overUndershootEntryConditions == null)
                ? new OverUndershootSelectionData()
                : overUndershootEntryConditions;
    }

    public OverUndershootSelectionData getOverUndershootExitConditions() {
        return (overUndershootExitConditions == null)
                ? new OverUndershootSelectionData()
                : overUndershootExitConditions;
    }
    
    public SensorLossSelectionData getSensorLossConditions() {
        return (sensorLossConditions == null) 
                ? new SensorLossSelectionData()
                : sensorLossConditions;
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
                .setCookie(parseInt(getCookie(COOKIE_USER_ID)))
                .setDescription("Auto-Generated Rule from Template.")
                .setFeedingMetadata(createFeedingMetadataFrom())
                .setEntryOperatorIndex(getOperatorIndex(entryConditions.getOperator()))
                .setEntryValue(entryConditions.getValue())
                .setEntryUnit(entryConditions.getUnit())
                .setEnterIsSameAsExitCondition(false)
                .setExitOperatorIndex(getOperatorIndex(exitConditions.getOperator()))
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
                .setRuleType(OVER_UNDERSHOOT)
                .setCookie(parseInt(getCookie(COOKIE_USER_ID)))
                .setDescription("Auto-Generated Rule from Template.")
                .setFeedingMetadata(createFeedingMetadataFrom())
                .setEntryTime(condition.getValue())
                .setEntryTimeUnit(condition.getUnit())
                .setPublish(false)
                .build();
    }
    
    private FeedingMetadata createFeedingMetadataFrom() {
        final TimeSeries timeseries = this.timeseries;
        FeedingMetadata metadata = new FeedingMetadata();
        metadata.setServiceUrl(timeseries.getSosUrl());
        metadata.setOffering(timeseries.getOfferingId());
        metadata.setProcedure(timeseries.getFeatureId());
        metadata.setPhenomenon(timeseries.getPhenomenonId());
        metadata.setFeatureOfInterest(timeseries.getFeatureId());
        return metadata;
    }

    
}
