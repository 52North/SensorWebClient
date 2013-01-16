
package org.n52.client.ses.ui;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;

public class CreateAbonnementForm extends DynamicForm {

    private final CreateEventAbonnementController controller;

    public CreateAbonnementForm(final CreateEventAbonnementController controller) {
        this.setStyleName("n52_sensorweb_client_create_abo_form");
        this.controller = controller;
        
        setFields(createAbonnementNameTextItem());
        
        // TODO Auto-generated constructor stub

    }
    

    private TextItem createAbonnementNameTextItem() {
        final TextItem nameItem = new TextItem();
        nameItem.setName("AbonnementName");
        nameItem.setTitle(i18n.aboName());
        nameItem.setRequired(true);
        nameItem.setSelectOnFocus(true);
//        nameItem.setLength(100);
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
