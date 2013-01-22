package org.n52.client.ses.ui.subscribe;

import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TitleOrientation;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

public class OverUndershootRuleTemplate extends RuleTemplate {

    private static LinkedHashMap<String, String> ruleOperators;
    
    {
        Map<String, String> operatorHashMap = new HashMap<String, String>();
        operatorHashMap.put("=", "=");
        operatorHashMap.put("<>", "<>");
        operatorHashMap.put(">", ">");
        operatorHashMap.put("&lt;", "&lt;");
        operatorHashMap.put(">=", ">=");
        operatorHashMap.put("<=", "<=");
        Map<String, String> unmodifiableMap = Collections.unmodifiableMap(operatorHashMap);
        ruleOperators = new LinkedHashMap<String, String>(unmodifiableMap);
    }
    
    private SelectItem operatorRuleItem;

    public OverUndershootRuleTemplate(final EventSubscriptionController controller) {
        super(controller);

        // TODO load rule templates
        
    }
    
    @Override
    public SimpleRuleType getRuleType() {
        return OVER_UNDERSHOOT;
    }

    @Override
    public Canvas createEditCanvas() {
        operatorRuleItem = new SelectItem();
        operatorRuleItem.setTitle(i18n.operator());
        operatorRuleItem.setValueMap(ruleOperators);
        operatorRuleItem.setDefaultValue(">");
        operatorRuleItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
//                if (operatorCondItem != null) {
//                    operatorCondItem.setValue(getInverseOperator((String)event.getValue()));
//                }
            }
        });
        
        // TODO Auto-generated method stub
        Label label = new Label(OVER_UNDERSHOOT.name());
        label.setHeight("40px");
        return label;
    }
}

