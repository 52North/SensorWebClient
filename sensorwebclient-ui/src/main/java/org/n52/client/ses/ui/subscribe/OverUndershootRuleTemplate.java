package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.util.RuleOperatorUtil.getOperatorFrom;
import static org.n52.client.ses.util.RuleOperatorUtil.getRuleOperators;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.shared.serializable.pojos.Rule.GREATER_THAN;
import static org.n52.shared.serializable.pojos.Rule.LESS_THAN_OR_EQUAL_TO;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class OverUndershootRuleTemplate extends RuleTemplate {

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
        layout.addMember(new Label(i18n.enterCondition()));
        layout.addMember(createEntryConditionOperatorsCanvas());
        return layout;
    }

    private Canvas createEntryConditionOperatorsCanvas() {
        SelectItem entryOperatorSelectItem = createOperator(GREATER_THAN);
        entryOperatorSelectItem.addChangedHandler(createEntryOperatorChangedHandler());
        
        // TODO add entry field 
        // TODO add unit field

        DynamicForm form = new DynamicForm();
        form.setFields(entryOperatorSelectItem);
        return form;
    }

    private Canvas createExitConditionOperatorsCanvas() {
        SelectItem exitOperatorSelectItem = createOperator(LESS_THAN_OR_EQUAL_TO);
        exitOperatorSelectItem.addChangedHandler(createExitOperatorChangedHandler());

        // TODO add entry field 
        // TODO add unit field

        DynamicForm form = new DynamicForm();
        form.setFields(exitOperatorSelectItem);
        return form;
    }

    private Canvas createExitConditionEditCanvas() {
        Layout layout = new HLayout();
        layout.addMember(new Label(i18n.exitCondition()));
        layout.addMember(createExitConditionOperatorsCanvas());
        return layout;
    }
    
    private SelectItem createOperator(int operatorIndex) {
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
    
    private ChangedHandler createEntryOperatorChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
    private ChangedHandler createExitOperatorChangedHandler() {
        return new ChangedHandler() {
            @Override
            public void onChanged(ChangedEvent event) {
                // TODO Auto-generated method stub
                
            }
        };
    }
    
    class OverUndershootSelectionEntry {
        private String operator;
    }

}

