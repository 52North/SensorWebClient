package org.n52.client.ses.ui.subscribe;

import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;

public class OverUndershootRuleTemplate extends RuleTemplate {

    public OverUndershootRuleTemplate(final EventSubscriptionController controller) {
        super(controller);

        // TODO load rule templates
        
    }
    
    @Override
    public SimpleRuleType getRuleType() {
        return OVER_UNDERSHOOT;
    }

    @Override
    public Canvas createEditCanvas() {
        // TODO Auto-generated method stub
        Label label = new Label(OVER_UNDERSHOOT.name());
        label.setHeight("40px");
        return label;
        
    }
}

