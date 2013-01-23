package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;


public class SensorLossRuleTemplate extends RuleTemplate {

    public SensorLossRuleTemplate(final EventSubscriptionController controller) {
        super(controller);
    }
    
    @Override
    public SimpleRuleType getRuleType() {
        return SENSOR_LOSS;
    }

    @Override
    public Canvas createEditCanvas() {
        Layout layout = new VLayout();
        layout.setStyleName("n52_sensorweb_client_create_abo_template_sensorlosscondition");
        layout.addMember(alignVerticalCenter(createEditConditionCanvas()));
        return layout;
    }

    private Canvas createEditConditionCanvas() {
        StaticTextItem label = createLabelItem(i18n.sensorFailure());
        
        TextItem valueItem = createValueItem();
        valueItem.addChangedHandler(createValueChangedHandler());
        valueItem.setWidth(EDIT_ITEMS_WIDTH);
        
        SelectItem unitItem = createUnitsItem();
        unitItem.addChangedHandler(createEntryUnitChangedHandler());
        unitItem.setWidth(EDIT_ITEMS_WIDTH);
        
        return assembleEditConditionForm(label, valueItem, unitItem);
    }

    private ChangedHandler createValueChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                TextItem valueItem = (TextItem) event.getSource();
                String thresholdValue = valueItem.getValueAsString();
                controller.getSensorLossConditions().setValue(thresholdValue);
            }
        };
    }

    private ChangedHandler createEntryUnitChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                SelectItem valueItem = (SelectItem) event.getSource();
                String unitValue = valueItem.getValueAsString();
                controller.getSensorLossConditions().setUnit(unitValue);
            }
        };
        
    }

}
