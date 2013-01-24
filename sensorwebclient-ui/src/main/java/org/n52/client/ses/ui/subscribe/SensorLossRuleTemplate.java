package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.SENSOR_LOSS;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

        SelectItem unitItem = createUnitsItem();
        unitItem.addChangedHandler(createEntryUnitChangedHandler());
        unitItem.setValueMap(createTimeunitsMap());
        unitItem.setWidth(2 * EDIT_ITEMS_WIDTH);
        
        TextItem valueItem = createValueItem();
        valueItem.addChangedHandler(createValueChangedHandler());
        valueItem.setWidth(EDIT_ITEMS_WIDTH);
        
        return assembleEditConditionForm(label, unitItem, valueItem);
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

    private LinkedHashMap<String, String> createTimeunitsMap() {
        Map<String, String> timeUnits = new LinkedHashMap<String, String>();
        timeUnits.put("S", i18n.seconds());
        timeUnits.put("M", i18n.minutes());
        timeUnits.put("H", i18n.hours());
        return new LinkedHashMap<String, String>(Collections.unmodifiableMap(timeUnits));
        
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

}
