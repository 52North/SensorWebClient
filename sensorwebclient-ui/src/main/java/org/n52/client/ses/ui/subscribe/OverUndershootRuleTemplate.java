package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.Alignment.CENTER;
import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.util.RuleOperatorUtil.getInverseOperator;
import static org.n52.client.ses.util.RuleOperatorUtil.getOperatorFrom;
import static org.n52.client.ses.util.RuleOperatorUtil.getRuleOperators;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.shared.serializable.pojos.Rule.GREATER_THAN;
import static org.n52.shared.serializable.pojos.Rule.LESS_THAN_OR_EQUAL_TO;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemErrorFormatter;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class OverUndershootRuleTemplate extends RuleTemplate {
    
    private SelectItem exitOperatorItem;
    private TextItem exitValueItem;
    
    private DynamicForm entryConditionForm;
    private DynamicForm exitConditionForm;

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
        layout.setStyleName("n52_sensorweb_client_create_abo_template_overundershootcondition");
        layout.addMember(alignVerticalCenter(createEntryConditionEditCanvas()));
        layout.addMember(alignVerticalCenter(createExitConditionEditCanvas()));
        return layout;
    }

    @Override
    public boolean validateTemplate() {
        return entryConditionForm.validate() && exitConditionForm.validate();
    }

    private Canvas createEntryConditionEditCanvas() {
        StaticTextItem labelItem = createLabelItem(i18n.enterCondition());
        
        OverUndershootSelectionData data = controller.getOverUndershootEntryConditions();
        SelectItem entryOperatorItem = createOperatorItem(data, GREATER_THAN);
        entryOperatorItem.addChangedHandler(createEntryOperatorChangedHandler());
        entryOperatorItem.setWidth(EDIT_ITEMS_WIDTH);

        TextItem entryValueItem = createValueItem();
        entryValueItem.addChangedHandler(createEntryValueChangedHandler());
        entryValueItem.setWidth(EDIT_ITEMS_WIDTH);
        declareAsRequired(entryValueItem);
        
        StaticTextItem entryUnitItem = createStaticUnitItem();
        entryUnitItem.setWidth(EDIT_ITEMS_WIDTH);
        
        FormItem[] items = new FormItem[] { labelItem, entryOperatorItem, entryValueItem, entryUnitItem };
        entryConditionForm = assembleEditConditionForm(items);
        return alignVerticalCenter(entryConditionForm);
    }


    private ChangedHandler createEntryOperatorChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                SelectItem selectItem = (SelectItem) event.getSource();
                String operator = selectItem.getValueAsString();
                controller.getOverUndershootEntryConditions().setOperator(operator);
                exitOperatorItem.setValue(getInverseOperator(operator));
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
                
                exitValueItem.setValue(value);
                // setting exit value does not invoke ChangedHandler :(
                controller.getOverUndershootExitConditions().setValue(value);
            }
        };
    }
    

    private StaticTextItem createStaticUnitItem() {
        StaticTextItem unitItem = new StaticTextItem();
        unitItem.setTitle(i18n.unit());
        unitItem.setTitleOrientation(TOP);
        String unitOfMeasure = controller.getTimeSeries().getUnitOfMeasure();
        controller.getOverUndershootEntryConditions().setUnit(unitOfMeasure);
        unitItem.setValue(unitOfMeasure);
        return unitItem;
    }

    private Canvas createExitConditionEditCanvas() {
        StaticTextItem labelItem = createLabelItem(i18n.exitCondition());

        OverUndershootSelectionData data = controller.getOverUndershootExitConditions();
        exitOperatorItem = createOperatorItem(data, LESS_THAN_OR_EQUAL_TO);
        exitOperatorItem.addChangedHandler(createExitOperatorChangedHandler());
        exitOperatorItem.setWidth(EDIT_ITEMS_WIDTH);

        exitValueItem = createValueItem();
        exitValueItem.addChangedHandler(createExitValueChangedHandler());
        exitValueItem.setWidth(EDIT_ITEMS_WIDTH);
        declareAsRequired(exitValueItem);

        StaticTextItem exitUnitItem = createStaticUnitItem();
        exitUnitItem.setWidth(EDIT_ITEMS_WIDTH);
        
        FormItem[] items = new FormItem[] { labelItem, exitOperatorItem, exitValueItem, exitUnitItem };
        exitConditionForm = assembleEditConditionForm(items);
        return alignVerticalCenter(exitConditionForm);
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
    
    private SelectItem createOperatorItem(OverUndershootSelectionData data, int operatorIndex) {
        SelectItem operatorItem = new SelectItem();
        operatorItem.setTitle(i18n.operator());
        operatorItem.setTitleOrientation(TOP);
        operatorItem.setTextAlign(CENTER);
        
        operatorItem.setValueMap(getRuleOperators());
        String operator = getOperatorFrom(operatorIndex);
        if (getRuleOperators().containsKey(operator)) {
            operatorItem.setDefaultValue(operator);
            data.setOperator(operator);
        }
        return operatorItem;
    }

    protected SelectItem createUnitsItem() {
        SelectItem unitSelectItem = new SelectItem();
        unitSelectItem.setTitle(i18n.unit());
        unitSelectItem.setTitleOrientation(TOP);
        unitSelectItem.addChangedHandler(createUnitSelectionChangedHandler());
        return unitSelectItem;
    }

    private ChangedHandler createUnitSelectionChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                // TODO Auto-generated method stub
            }
        };
    }

}

