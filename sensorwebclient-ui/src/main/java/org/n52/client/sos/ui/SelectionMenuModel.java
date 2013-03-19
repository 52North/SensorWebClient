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
package org.n52.client.sos.ui;

import java.util.HashMap;
import java.util.Map;

import org.n52.client.sos.data.SOSDataSource;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

class SelectionMenuModel {

	private final ListGrid listGrid;
	
	private final StationSelector parent;
	
	private final Map<String, DynamicForm> expansionComponents;
	
	static ListGrid createListGrid(final StationSelector stationPicker) {
		return new SelectionMenuModel(stationPicker).getListGrid();
	}

	private SelectionMenuModel(final StationSelector parent) {
		this.expansionComponents = new HashMap<String, DynamicForm>();
		this.parent = parent;
		listGrid = new ListGrid() {
			@Override
			protected Canvas getExpansionComponent(ListGridRecord record) {
				String serviceURL = getServiceURLFromRecord(record);
				if (expansionComponents.containsKey(serviceURL)) {
					DynamicForm dynamicForm = expansionComponents.get(serviceURL);
					dynamicForm.setWidth100();
                    return dynamicForm;
				}
				DynamicForm form = new DynamicForm();
				expansionComponents.put(serviceURL, form);
				form.setItems(parent.createFilterCategorySelectionGroup(serviceURL));
				form.setWidth100();
				return form;
			}
			
			private String getServiceURLFromRecord(ListGridRecord record) {
				return record.getAttribute("url");
			}
		};
		listGrid.setAutoSaveEdits(false);
		listGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				ListGridRecord[] records = event.getSelection();
				for (ListGridRecord record : records) {
					if (record != null) {
						listGrid.expandRecord(record);	
					}	
				}
			}
		});
		listGrid.setWidth100();
		listGrid.setHeight100();
		initData();
	}

	private ListGrid getListGrid() {
		return listGrid;
	}
	
	private void initData() {
		listGrid.addDataArrivedHandler(new DataArrivedHandler() {
			@Override
			public void onDataArrived(DataArrivedEvent event) {
				listGrid.expandRecord(listGrid.getRecord(0));
				listGrid.selectRecord(0);
			}
		});
		setListGridSelectionBehavior();
		loadDataIntoListGrid();
	}
	
	private void setListGridSelectionBehavior() {
		listGrid.addSelectionChangedHandler(parent.getSOSSelectionHandler());
		listGrid.addClickHandler(parent.getSOSClickedHandler());
		listGrid.setSelectionType(SelectionStyle.SINGLE);
		listGrid.setCanExpandMultipleRecords(false);
	}

	private void loadDataIntoListGrid() {
		listGrid.setDataSource(SOSDataSource.getInstance());
		listGrid.setFields(new ListGridField("itemName", "Service"));
		listGrid.setAutoFetchData(true);
		listGrid.setShowHeader(false);
		listGrid.setLeaveScrollbarGap(false);
	}

}
