package org.n52.client.ses.ui.subscribe;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;


public abstract class RuleTemplate {
    
    protected EventSubscriptionController controller;

    public RuleTemplate(final EventSubscriptionController controller) {
        this.controller = controller;
        
        // TODO Auto-generated constructor stub
        
    }
    
    public abstract SimpleRuleType getRuleType();

    public abstract Canvas createEditCanvas();
    
    protected String getServiceUrl() {
        return controller.getServiceUrl();
    }
    
    protected String getOffering() {
        return controller.getOffering();
    }

    protected String getPhenomenon() {
        return controller.getPhenomenon();
    }
    
    protected String getProcedure() {
        return controller.getProcedure();
    }

    protected String getFeatureOfInterest() {
        return controller.getFeatureOfInterest();
    }
    
}
