package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;


public abstract class RuleTemplate {
    
    protected static final int EDIT_ITEMS_WIDTH = 50;
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

//    protected Canvas createLabel(String labelText) {
//        HLayout layout = new HLayout();
//        layout.addMember(new Label(labelText + ":"));
//        layout.setTop("50%");
//        return layout;
//    }

    protected Canvas alignVerticalCenter(Canvas canvasToAlign) {
        VLayout layout = new VLayout();
        layout.addMember(new LayoutSpacer());
        layout.addMember(canvasToAlign);
        layout.addMember(new LayoutSpacer());
        return layout;
    }

    protected TextItem createValueItem() { 
        TextItem valueItem = new TextItem();
        valueItem.setTitle(i18n.value());
        valueItem.setTitleOrientation(TOP);
        valueItem.setKeyPressFilter("[0-9]");
        return valueItem;
    }

    protected SelectItem createUnitsItem() {
        SelectItem unitSelectItem = new SelectItem();
        unitSelectItem.setTitle(i18n.unit());
        unitSelectItem.setTitleOrientation(TOP);
        unitSelectItem.addChangedHandler(createUnitSelectionChangedHandler());
        
        // TODO get unit values
//        entryOperator.setValueMap(getRuleOperators());
//        String operator = getOperatorFrom(operatorIndex);
//        if (getRuleOperators().containsKey(operator)) {
//            entryOperator.setDefaultValue(operator);
//        }
        
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

    protected Canvas assembleEditConditionForm(FormItem... formItems) {
        DynamicForm form = new DynamicForm();
        form.setNumCols(formItems.length + 1);
        form.setFields(formItems);
        return alignVerticalCenter(form);
    }

    protected StaticTextItem createLabelItem(String labelText) {
        StaticTextItem labelItem = new StaticTextItem();
        labelItem.setTitle(labelText);
//        labelItem.setColSpan(0);
        labelItem.setWidth(1);
        return labelItem;
    }
    
}
