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
package org.n52.client.ses.ui.profile;

import static com.smartgwt.client.types.Alignment.CENTER;
import static org.n52.client.ses.i18n.SesStringsAccessor.i18n;

import java.util.ArrayList;

import org.n52.client.ses.data.SubscriptionListGrid;
import org.n52.client.ses.ui.FormLayout;
import org.n52.client.ses.ui.rules.RuleDataSourceRecord;
import org.n52.shared.serializable.pojos.BasicRuleDTO;
import org.n52.shared.serializable.pojos.ComplexRuleDTO;

import com.smartgwt.client.widgets.grid.ListGridField;

public class SubscriptionsLayout extends FormLayout {
    
    private SubscriptionListGrid subscriptionsGrid;
    
    public SubscriptionsLayout() {
        super(i18n.subscriptions());
        subscriptionsGrid = createSubscriptionListGrid();
        form.setFields(headerItem);

        addMember(form);
        addMember(subscriptionsGrid);
    }

    protected SubscriptionListGrid createSubscriptionListGrid() {
        SubscriptionListGrid grid = new SubscriptionListGrid();
        layoutRuleNameField(grid);
        layoutDeleteRuleField(grid);
        layoutActivateRuleField(grid);
        return grid;
    }

    private void layoutRuleNameField(SubscriptionListGrid grid) {
        ListGridField nameField = grid.getNameField();
        nameField.setAlign(CENTER);
    }

    private void layoutDeleteRuleField(SubscriptionListGrid grid) {
        ListGridField deleteField = grid.getDeleteField();
        deleteField.setWidth("15%");
        deleteField.setAlign(CENTER);
        deleteField.setCanFilter(false);
        deleteField.setCanSort(false);
    }

    private void layoutActivateRuleField(SubscriptionListGrid grid) {
        ListGridField activatedField = grid.getActivatedField();
        activatedField.setWidth("15%");
        activatedField.setAlign(CENTER);
        activatedField.setCanFilter(false);
        activatedField.setCanSort(false);
    }

    public void setData(ArrayList<BasicRuleDTO> basicRules, ArrayList<ComplexRuleDTO> complexRules) {
        clearGrid();
        for (int i = 0; i < basicRules.size(); i++) {
            BasicRuleDTO ruleDTO = basicRules.get(i);
            RuleDataSourceRecord rule = new RuleDataSourceRecord(i18n.basic(), "", "", ruleDTO.getName(), ruleDTO.getDescription(), ruleDTO.getMedium(), ruleDTO.getFormat(), ruleDTO.isRelease(), ruleDTO.isSubscribed(), ruleDTO.getUuid());
            this.subscriptionsGrid.addData(rule);
        }

//        for (int i = 0; i < complexRules.size(); i++) {
//            ComplexRuleDTO complexDTO = complexRules.get(i);
//            RuleDataSourceRecord rule = new RuleDataSourceRecord(i18n.complex(), "", "", complexDTO.getName(), complexDTO.getDescription(), complexDTO.getMedium(), complexDTO.getFormat(), complexDTO.isRelease(), complexDTO.isSubscribed(), "UUID"); // TODO add UUID to complex rule
//            this.subscriptionsGrid.addData(rule);
//        }
        
        subscriptionsGrid.fetchData();
    }

	public void clearGrid() {
		this.subscriptionsGrid.selectAllRecords();
		this.subscriptionsGrid.removeSelectedData();
	}
	
}