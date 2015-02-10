/**
 * Copyright (C) 2012-2015 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.client.ses.ui.subscribe;

import static com.smartgwt.client.types.Alignment.CENTER;
import static com.smartgwt.client.types.TitleOrientation.TOP;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.view.gui.elements.layouts.SimpleRuleType.OVER_UNDERSHOOT;
import static org.n52.shared.util.MathSymbolUtil.GREATER_THAN_INT;
import static org.n52.shared.util.MathSymbolUtil.LESS_THAN_OR_EQUAL_TO_INT;
import static org.n52.shared.util.MathSymbolUtil.getInverse;
import static org.n52.shared.util.MathSymbolUtil.getMathSymbols;
import static org.n52.shared.util.MathSymbolUtil.getSymbolForIndex;

import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.view.gui.elements.layouts.SimpleRuleType;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class OverUndershootRuleTemplate extends SubscriptionTemplate {
    
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
        controller.clearSelectionData();
        Layout layout = new VLayout();
        layout.setStyleName("n52_sensorweb_client_create_abo_template_overundershootcondition");
        layout.addMember(alignVerticalCenter(createEntryConditionEditCanvas()));
        layout.addMember(alignVerticalCenter(createExitConditionEditCanvas()));
        return layout;
    }

    @Override
    public boolean validateTemplate() {
        return entryConditionForm.validate(false) && exitConditionForm.validate(false);
    }

    private Canvas createEntryConditionEditCanvas() {
        StaticTextItem labelItem = createLabelItem(i18n.enterCondition());
        
        OverUndershootSelectionData data = controller.getOverUndershootEntryConditions();
        SelectItem entryOperatorItem = createOperatorItem(data, GREATER_THAN_INT);
        entryOperatorItem.addChangedHandler(createEntryOperatorChangedHandler());
        entryOperatorItem.setWidth(EDIT_ITEMS_WIDTH);

        TextItem entryValueItem = createValueItem();
        entryValueItem.addChangedHandler(createEntryValueChangedHandler());
        entryValueItem.setWidth(EDIT_ITEMS_WIDTH);
        declareAsRequired(entryValueItem);
        
        StaticTextItem entryUnitItem = createStaticUnitItem(data);
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
                String inverseOperator = getInverse(operator);
                controller.getOverUndershootEntryConditions().setOperator(operator);
                exitOperatorItem.setValue(inverseOperator);
                controller.getOverUndershootExitConditions().setOperator(inverseOperator);
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
    

    private StaticTextItem createStaticUnitItem(OverUndershootSelectionData data) {
        StaticTextItem unitItem = new StaticTextItem();
        unitItem.setTitle(i18n.unit());
        unitItem.setTitleOrientation(TOP);
        TimeseriesLegendData unitOfMeasure = controller.getTimeSeries();
        unitItem.setValue(unitOfMeasure.getUnitOfMeasure());
        data.setUnit(unitOfMeasure.getUnitOfMeasure());
        return unitItem;
    }

    private Canvas createExitConditionEditCanvas() {
        StaticTextItem labelItem = createLabelItem(i18n.exitCondition());

        OverUndershootSelectionData data = controller.getOverUndershootExitConditions();
        exitOperatorItem = createOperatorItem(data, LESS_THAN_OR_EQUAL_TO_INT);
        exitOperatorItem.addChangedHandler(createExitOperatorChangedHandler());
        exitOperatorItem.setWidth(EDIT_ITEMS_WIDTH);

        exitValueItem = createValueItem();
        exitValueItem.addChangedHandler(createExitValueChangedHandler());
        exitValueItem.setWidth(EDIT_ITEMS_WIDTH);
        declareAsRequired(exitValueItem);

        StaticTextItem exitUnitItem = createStaticUnitItem(data);
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
    
    private SelectItem createOperatorItem(OverUndershootSelectionData data, int initialOperator) {
        SelectItem operatorItem = new SelectItem();
        operatorItem.setTitle(i18n.operator());
        operatorItem.setTitleOrientation(TOP);
        operatorItem.setTextAlign(CENTER);
        
        operatorItem.setValueMap(getMathSymbols());
        String operator = getSymbolForIndex(initialOperator);
        if (getMathSymbols().containsKey(operator)) {
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

