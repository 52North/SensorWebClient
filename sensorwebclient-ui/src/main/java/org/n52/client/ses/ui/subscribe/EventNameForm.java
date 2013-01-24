
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

public class EventNameForm extends DynamicForm {

    private TextItem aboNameItem;
    
    private final EventSubscriptionController controller;

    public EventNameForm(final EventSubscriptionController controller) {
        this.setStyleName("n52_sensorweb_client_create_abo_eventname");
        this.controller = controller;
        this.controller.setEventNameForm(this);
        setFields(createAbonnementNameItem());
    }
    
    private TextItem createAbonnementNameItem() {
        
        // TODO add check if rule already exists
        
//        getTab().getSimpleRuleLayout().getTitleItem().setErrorFormatter(new FormItemErrorFormatter() {
//            public String getErrorHTML(String[] errors) {
//                return "<img src='../img/icons/exclamation.png' alt='rule name allready exists' title='rule name allready exists'/>";
//            }
//        });
        
        aboNameItem = new TextItem();
        aboNameItem.setTextBoxStyle("n52_sensorweb_client_abo_name_textbox");
        aboNameItem.setName("AbonnementName");
        aboNameItem.setTitle(i18n.aboName());
        aboNameItem.setWidth("*"); // fill form column
        aboNameItem.setValue(controller.createSuggestedAbonnementName());
        aboNameItem.addKeyPressHandler(new KeyPressHandler() {
            public void onKeyPress(KeyPressEvent event) {
                TextItem nameItem = (TextItem) event.getSource();
                String currentAbonnementName = (String) nameItem.getValue();
                controller.setSelectedAbonnementName(currentAbonnementName);
            }
        });
        return aboNameItem;
    }

    public void updateSuggestedAbonnementName(String suggestedAboName) {
        if (aboNameItem != null) {
            aboNameItem.setValue(suggestedAboName);
        }
    }

}
