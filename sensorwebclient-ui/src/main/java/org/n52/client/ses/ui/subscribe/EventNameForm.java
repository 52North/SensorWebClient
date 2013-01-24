
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.FormItemErrorFormatter;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

public class EventNameForm extends DynamicForm {

    private final EventSubscriptionController controller;

    public EventNameForm(final EventSubscriptionController controller) {
        this.setStyleName("n52_sensorweb_client_create_abo_eventname");
        this.controller = controller;
        setFields(createAbonnementNameItem());
    }
    
    private TextItem createAbonnementNameItem() {
        
        // TODO add check if rule already exists
        
//        getTab().getSimpleRuleLayout().getTitleItem().setErrorFormatter(new FormItemErrorFormatter() {
//            public String getErrorHTML(String[] errors) {
//                return "<img src='../img/icons/exclamation.png' alt='rule name allready exists' title='rule name allready exists'/>";
//            }
//        });
        
        final TextItem nameItem = new TextItem();
        nameItem.setTextBoxStyle("n52_sensorweb_client_abo_name_textbox");
        nameItem.setName("AbonnementName");
        nameItem.setTitle(i18n.aboName());
        nameItem.setWidth("*"); // fill form column
        nameItem.setValue(controller.createSuggestedAbonnementName());
        nameItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                String currentAbonnementName = (String) nameItem.getValue();
                controller.setAbonnementName(currentAbonnementName);
            }
        });
        return nameItem;
    }

}
