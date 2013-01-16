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
package org.n52.client.ses.ui;

import java.util.Set;

import org.n52.client.sos.legend.TimeSeries;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.TimeSeriesProperties;

import com.google.gwt.user.client.ui.FlexTable;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.Layout;

public class SelectPredefinedAboCanvas extends Layout {

    private CreateEventAbonnementController controller;
    
	private FlexTable entries;
	
    private RadioGroupItem operatorRadioGroup;

    private TextItem value;

    public SelectPredefinedAboCanvas(CreateEventAbonnementController controller) {
        this.setStylePrimaryName("n52_sensorweb_client_abo_selection");
        this.controller = controller;
    	this.entries = new FlexTable();
    	addMember(this.entries);
    }
    
	public void setTimeSeries() {
        TimeSeries timeSeries = controller.getTimeSeries();
		this.entries.removeAllRows();
		
		addRow("Datenanbieter",timeSeries.getSosUrl());
		addRow("Station", timeSeries.getStationName());
		addRow("Parameter", timeSeries.getProperties().getPhenomenon().getLabel());
		
		this.value = new TextItem();
		addRow("Operator", this.value);
		addRow("Wert", "");
		addRow("Maßeinheit", timeSeries.getUnitOfMeasure());

		addRefValueTable(timeSeries);
	}

	private void addRow(String label, FormItem item) {
		
	}

	private void addRow(String label, String value) {
		int row = this.entries.getRowCount();
		entries.setText(row, 0, label);
		entries.getFlexCellFormatter().setStyleName(row, 0, "52n_simpleRuleTable_left_column");
		entries.setText(row, 1, value);
		entries.getFlexCellFormatter().setStyleName(row, 1, "52n_simpleRuleTable_right_column");
	}
	
	private void addRefValueTable(TimeSeries timeSeries) {
		TimeSeriesProperties properties = timeSeries.getProperties();
		Set<String> refValues = properties.getrefValues();
		if (refValues != null && refValues.size() > 0) {
			for (String refValueStr : refValues) {
				ReferenceValue refValue = properties.getRefValue(refValueStr);
				addRow(refValue.getID(), refValue.getValue()+"");
			}
		}
	}

}