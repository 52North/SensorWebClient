/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.sos.ui;

import java.util.HashMap;
import java.util.Map;

import org.n52.client.sos.data.SOSDataSource;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.DataArrivedEvent;
import com.smartgwt.client.widgets.grid.events.DataArrivedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

/**
 * An expandable {@link ListGrid} of service/data providers configured at the client. All configured service
 * providers are read from an {@link SOSDataSource} which reads the sos-instances configuration file shared by
 * client and server part of the Sensor Web Client.<br/>
 * <br/>
 * After the model has been initialized from configuration file for each data provider it provides an
 * expandable {@link FormItem} list containing the phenomenons available. 
 */
class SelectionMenuModel {

    private final ListGrid listGrid;

    private final Map<String, DynamicForm> expansionComponents;

    static ListGrid createListGrid(final StationSelector stationSelector) {
        return new SelectionMenuModel(stationSelector).getListGrid();
    }

    private SelectionMenuModel(final StationSelector parent) {
        this.expansionComponents = new HashMap<String, DynamicForm>();
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
        loadDataIntoListGrid();
    }

    private void loadDataIntoListGrid() {
        listGrid.setDataSource(SOSDataSource.getInstance());
        listGrid.setFields(new ListGridField("itemName", "Service"));
        listGrid.setAutoFetchData(true);
        listGrid.setShowHeader(false);
        listGrid.setLeaveScrollbarGap(false);
    }

}
