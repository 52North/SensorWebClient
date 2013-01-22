package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.util.RuleOperatorUtil.getInverseOperator;
import static org.n52.client.ses.util.RuleOperatorUtil.getOperatorFrom;
import static org.n52.client.ses.util.RuleOperatorUtil.getRuleOperators;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.shared.serializable.pojos.Rule.GREATER_THAN;
import static org.n52.shared.serializable.pojos.Rule.LESS_THAN_OR_EQUAL_TO;

import org.n52.client.ses.util.RuleOperatorUtil;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class OverUndershootRuleTemplate extends RuleTemplate {
    
    private SelectItem exitOperatorSelectItem;

    public OverUndershootRuleTemplate(final EventSubscriptionController controller) {
        super(controller);
    }
    
    @Override
    public SimpleRuleType getRuleType() {
        return OVER_UNDERSHOOT;
    }

    @Override
    public Canvas createEditCanvas() {
        Layout layout = new VLayout();
        layout.addMember(createEntryConditionEditCanvas());
        layout.addMember(createExitConditionEditCanvas());
        return layout;
    }

    private Canvas createEntryConditionEditCanvas() {
        Layout layout = new HLayout();
        layout.setStyleName("n52_sensorweb_client_create_abo_template_overundershootcondition");
        layout.addMember(createVerticalCenteredLabel(i18n.enterCondition()));
        layout.addMember(createEntryConditionOperatorsCanvas());
        return layout;
    }

    private Canvas createEntryConditionOperatorsCanvas() {
        SelectItem entryOperatorSelectItem = createOperatorItem(GREATER_THAN);
        entryOperatorSelectItem.addChangedHandler(createEntryOperatorChangedHandler());

        TextItem valueItem = createValueItem();
        valueItem.addChangedHandler(createEntryValueChangedHandler());
        // TODO add unit field

        DynamicForm form = new DynamicForm();
        form.setFields(entryOperatorSelectItem, valueItem);
        return alignVerticalCenter(form);
    }
    
    private ChangedHandler createEntryOperatorChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                SelectItem selectItem = (SelectItem) event.getSource();
                String operator = selectItem.getValueAsString();
                controller.getOverUndershootEntryConditions().setOperator(operator);
                exitOperatorSelectItem.setValue(getInverseOperator(operator));
            }
        };
    }

    private ChangedHandler createEntryValueChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                TextItem valueItem = (TextItem) event.getSource();
                String value = valueItem.getValueAsString();
                controller.getOverUndershootEntryConditions().setValue(value);
            }
        };
    }

    private Canvas createExitConditionEditCanvas() {
        Layout layout = new HLayout();
        layout.setStyleName("n52_sensorweb_client_create_abo_template_overundershootcondition");
        layout.addMember(createVerticalCenteredLabel(i18n.exitCondition()));
        layout.addMember(createExitConditionOperatorsCanvas());
        return layout;
    }

    private Canvas createExitConditionOperatorsCanvas() {
        exitOperatorSelectItem = createOperatorItem(LESS_THAN_OR_EQUAL_TO);
        exitOperatorSelectItem.addChangedHandler(createExitOperatorChangedHandler());

        TextItem valueItem = createValueItem();
        valueItem.addChangedHandler(createExitValueChangedHandler());
        // TODO add unit field

        DynamicForm form = new DynamicForm();
        form.setFields(exitOperatorSelectItem, valueItem);
        return alignVerticalCenter(form);
    }

    private ChangedHandler createExitOperatorChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                SelectItem selectItem = (SelectItem) event.getSource();
                String operator = selectItem.getValueAsString();
                controller.getOverUndershootExitConditions().setOperator(operator);
            }
        };
    }
    

    private ChangedHandler createExitValueChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                TextItem valueItem = (TextItem) event.getSource();
                String value = valueItem.getValueAsString();
                controller.getOverUndershootExitConditions().setValue(value);
            }
        };
    }

    private Canvas createVerticalCenteredLabel(String labelText) {
        return alignVerticalCenter(new Label(labelText));
    }
    
    private Canvas alignVerticalCenter(Canvas canvasToAlign) {
        VLayout layout = new VLayout();
        layout.addMember(new LayoutSpacer());
        layout.addMember(canvasToAlign);
        layout.addMember(new LayoutSpacer());
        return layout;
    }

    private SelectItem createOperatorItem(int operatorIndex) {
        SelectItem entryOperator = new SelectItem();
        entryOperator.setTitle(i18n.operator());
        entryOperator.setTitleOrientation(TOP);
        entryOperator.setValueMap(getRuleOperators());
        String operator = getOperatorFrom(operatorIndex);
        if (getRuleOperators().containsKey(operator)) {
            entryOperator.setDefaultValue(operator);
        }
        return entryOperator;
    }

    private TextItem createValueItem() {
        TextItem valueItem = new TextItem();
        valueItem.setTitle(i18n.value());
        valueItem.setTitleOrientation(TOP);
        valueItem.setKeyPressFilter("[0-9]");
        return valueItem;
    }

}

