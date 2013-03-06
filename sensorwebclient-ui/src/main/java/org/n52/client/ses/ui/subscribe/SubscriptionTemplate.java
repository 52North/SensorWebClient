package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemErrorFormatter;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;


public abstract class SubscriptionTemplate {
    
    protected static final int EDIT_ITEMS_WIDTH = 50;
    protected EventSubscriptionController controller;
    private Canvas content;

    public SubscriptionTemplate(final EventSubscriptionController controller) {
        this.controller = controller;
        this.content = createEditCanvas();
    }
    
    public Canvas getTemplateContent() {
        return content;
    }
    
    public abstract SimpleRuleType getRuleType();

    protected abstract Canvas createEditCanvas();
    
    public abstract boolean validateTemplate();
    
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
        valueItem.setKeyPressFilter("[0-9\\.]");
        return valueItem;
    }

    protected DynamicForm assembleEditConditionForm(FormItem... formItems) {
        DynamicForm form = new DynamicForm();
        form.setNumCols(formItems.length + 1);
        form.setFields(formItems);
        return form;
    }

    protected StaticTextItem createLabelItem(String labelText) {
        StaticTextItem labelItem = new StaticTextItem();
        labelItem.setTitle(labelText);
        labelItem.setValue("");
        return labelItem;
    }


    protected void declareAsRequired(FormItem formItem) {
        formItem.setErrorFormatter(createErrorFormatter("invalid", i18n.invalidInputs()));
        formItem.setRequired(true);
    }
    
    private FormItemErrorFormatter createErrorFormatter(final String alt, final String title) {
        return new FormItemErrorFormatter() {
            @Override
            public String getErrorHTML(String[] errors) {
                StringBuilder sb = new StringBuilder();
                sb.append("<img src='../img/icons/exclamation.png' ");
                sb.append("alt='").append(alt).append("'" );
                sb.append("title='").append(title).append("'" );
                sb.append(" />");
                return sb.toString();
            }
        };
        
    }
}
