package org.n52.client.ses.ui.subscribe;

import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;


public class SensorLossRuleTemplate extends RuleTemplate {

    public SensorLossRuleTemplate(final EventSubscriptionController controller) {
        super(controller);
        
        // TODO load rule templates
        
    }
    
    @Override
    public SimpleRuleType getRuleType() {
        return SENSOR_LOSS;
    }

    @Override
    public Canvas createEditCanvas() {
        // TODO Auto-generated method stub
        Label label = new Label(SENSOR_LOSS.name());
        label.setHeight("40px");
        return label;
    }

}
