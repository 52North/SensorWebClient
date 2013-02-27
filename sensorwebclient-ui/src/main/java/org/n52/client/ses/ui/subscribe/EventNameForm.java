
package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

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
        aboNameItem = new TextItem();
        aboNameItem.setRequired(true);
        aboNameItem.setTextBoxStyle("n52_sensorweb_client_abo_name_textbox");
        aboNameItem.setTitle(i18n.aboName());
        aboNameItem.setWidth("*"); // fill form column
        aboNameItem.setKeyPressFilter("[0-9a-zA-Z_]");
        aboNameItem.setValue(controller.createSuggestedAbonnementName());
        aboNameItem.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
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
