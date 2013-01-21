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
}
