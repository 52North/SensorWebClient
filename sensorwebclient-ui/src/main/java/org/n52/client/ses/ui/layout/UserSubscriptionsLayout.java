/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */
package org.n52.client.ses.ui.layout;

import static org.n52.client.ses.ctrl.SesRequestManager.COOKIE_USER_ID;
import static org.n52.client.ses.ctrl.SesRequestManager.COOKIE_USER_ROLE;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;
import static org.n52.client.ses.ui.RuleRecord.FORMAT;
import static org.n52.client.ses.ui.RuleRecord.MEDIUM;
import static org.n52.client.ses.ui.RuleRecord.NAME;
import static org.n52.client.ses.ui.RuleRecord.SUBSCRIBED;
import static org.n52.client.ses.ui.RuleRecord.UUID;

import java.util.ArrayList;
import java.util.UUID;

import org.n52.client.bus.EventBus;
import org.n52.client.ses.data.RuleDataSource;
import org.n52.client.ses.event.DeleteRuleEvent;
import org.n52.client.ses.event.SubscribeEvent;
import org.n52.client.ses.event.UnsubscribeEvent;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.ses.ui.RuleRecord;
import org.n52.client.ui.btn.SmallButton;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

import com.google.gwt.user.client.Cookies;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.SortDirection;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;

/**
 * The Class UserSubscriptionsLayout.
 * 
 * With this view the user can see all his subscriptions in a table.
 * Each subscription has informatio about the medium and the format
 * of the subscruption
 * 
 * @author <a href="mailto:osmanov@52north.org">Artur Osmanov</a>
 */
public class UserSubscriptionsLayout extends FormLayout {

    /** The own rules grid. */
    private ListGrid subscriptionsGrid;
    
    private RuleDataSource dataSource;
    
    private static final String NAME_FIELD = "name";
    private static final String ACTIVATED_FIELD = "activate";
    private static final String DELETE_FIELD = "delete";
    
    /**
     * Instantiates a new user rule layout.
     */
    public UserSubscriptionsLayout() {
        super(i18n.subscriptions());
        setStyleName("n52_sensorweb_client_form_content");
        
        // init DataSource
        this.dataSource = new RuleDataSource();
        
        init();
    }

    /**
     * Inits the layout.
     */
    private void init() {

        this.subscriptionsGrid = new ListGrid() {
            private String delImg = "../img/icons/del.png";

			@Override
            protected Canvas createRecordComponent(final ListGridRecord record, Integer colNum) {
                if (record != null) {

                    String fieldName = this.getFieldName(colNum);

                    if (fieldName.equals(DELETE_FIELD)) {
                    	Canvas delButton = new SmallButton(new Img(delImg) , "", "");
                    	delButton.setMargin(1);
                    	delButton.setPrompt(i18n.unsubscribeThisRule());
                    	delButton.setLayoutAlign(Alignment.CENTER);
                    	delButton.addClickHandler(new ClickHandler() {
							@Override
							public void onClick(ClickEvent event) {
								String role = Cookies.getCookie(COOKIE_USER_ROLE);
								EventBus.getMainEventBus().fireEvent(new DeleteRuleEvent(record.getAttribute(UUID), role));
							}
						});
                        return delButton;
                    } else if (fieldName.equals(ACTIVATED_FIELD)) {
                    	DynamicForm form = new DynamicForm();
                    	form.setAutoWidth();
                    	CheckboxItem checkBox = new CheckboxItem("activate"," ");
                    	Boolean checked = Boolean.valueOf(record.getAttribute(SUBSCRIBED));
                    	checkBox.setValue(checked);
                    	checkBox.addChangeHandler(new ChangeHandler() {
							@Override
							public void onChange(ChangeEvent event) {
								Object value = event.getValue();
								if (value instanceof Boolean) {
									boolean checked = (Boolean) value;
									String userID = Cookies.getCookie(COOKIE_USER_ID);
									if(checked) {
										// TODO subscribe
										EventBus.getMainEventBus().fireEvent(new SubscribeEvent(record.getAttribute(NAME), userID, record.getAttribute(MEDIUM),record.getAttribute(FORMAT)));
									} else {
										// TODO unsubscribe 
										EventBus.getMainEventBus().fireEvent(new UnsubscribeEvent(record.getAttribute(NAME), userID, record.getAttribute(MEDIUM),record.getAttribute(FORMAT)));
									}
								}
							}
						});
                    	form.setItems(checkBox);
                    	return form;
                    }
                    return null;
                }
                return null;
            }
        };

        this.subscriptionsGrid.setShowRecordComponents(true);
        this.subscriptionsGrid.setShowRecordComponentsByCell(true);
        this.subscriptionsGrid.setWidth100();
        this.subscriptionsGrid.setHeight100();
        this.subscriptionsGrid.setCanGroupBy(false);
        this.subscriptionsGrid.setDataSource(this.dataSource);
        this.subscriptionsGrid.setShowFilterEditor(true);
        this.subscriptionsGrid.setFilterOnKeypress(true);
        this.subscriptionsGrid.setAutoFetchData(true);
        this.subscriptionsGrid.setShowRollOver(false);
        this.subscriptionsGrid.sort(1, SortDirection.ASCENDING);

        ListGridField nameField = new ListGridField(NAME_FIELD, i18n.name());
        nameField.setAlign(Alignment.CENTER);

        ListGridField activatedField = new ListGridField(ACTIVATED_FIELD, i18n.active());
        activatedField.setWidth("10%");
        activatedField.setAlign(Alignment.CENTER);
        activatedField.setCanFilter(false);
        activatedField.setCanSort(false);
        
        ListGridField deleteField = new ListGridField(DELETE_FIELD, " ");
        deleteField.setWidth("10%");
        deleteField.setAlign(Alignment.CENTER);
        deleteField.setCanFilter(false);
        deleteField.setCanSort(false);

        // set Fields
        this.subscriptionsGrid.setFields(nameField, activatedField, deleteField);
        this.subscriptionsGrid.setCanResizeFields(false);

        this.form.setFields(this.headerItem);

        addMember(this.form);
        addMember(this.subscriptionsGrid);
    }

    /**
     * Fills the grid
     * 
     * @param basicRules 
     * @param complexRules 
     */
    public void setData(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        RuleRecord rule;
        BasicRuleDTO ruleDTO;
        ComplexRuleDTO complexDTO;
        
        for (int i = 0; i < basicRules.size(); i++) {
            ruleDTO = basicRules.get(i);
            rule = new RuleRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), ruleDTO.getMedium(), ruleDTO.getFormat(), ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());
            
            this.subscriptionsGrid.addData(rule);
        }

        for (int i = 0; i < complexRules.size(); i++) {
            complexDTO = complexRules.get(i);
            rule = new RuleRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), complexDTO.getFormat(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID"); // TODO add UUID to complex rule

            this.subscriptionsGrid.addData(rule);
        }
        
        this.subscriptionsGrid.fetchData();
    }

	public void clearGrid() {
		this.subscriptionsGrid.selectAllRecords();
		this.subscriptionsGrid.removeSelectedData();
	}
}