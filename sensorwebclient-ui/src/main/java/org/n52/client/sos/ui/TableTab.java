/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import org.n52.client.ctrl.DataControls;
import org.n52.client.sos.ctrl.TableTabController;
import org.n52.client.sos.legend.TimeseriesLegendData;
import org.n52.client.ui.DataPanelTab;
import org.n52.client.ui.legend.LegendElement;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VStack;

/**
 * Representation of the TableTab.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class TableTab extends DataPanelTab {

    /** The title. */
    private String title;

    /** The parameterId. */
    private String id;

    /** The grid. */
    private ListGrid grid = new ListGrid();

    /** The controller. */
    private TableTabController controller;

    /** The values field. */
    private ListGridField valuesField;

    /** The date field. */
    private ListGridField dateField;

    /**
     * Instantiates a new table tab.
     * 
     * @param parameterId
     *            the parameterId
     * @param title
     *            the title
     */
    public TableTab(String id, String title) {
        super("TableTab");
        this.controller = new TableTabController(this);

        this.id = id;
        this.title = title;
        
        VStack dragStack = new VStack();
        
        dragStack.setCanAcceptDrop(true);
        
        dragStack.addMember(createDraggableStack("1"));
        dragStack.addMember(createDraggableStack("3"));
        
        setPane(dragStack);
//        init();
    }
    
    private VStack createDraggableStack(String text){
    	VStack dragStack = new VStack();
    	
    	dragStack.setCanDrag(true);
    	dragStack.setCanDrop(true);
    	
    	dragStack.addMember(new Label(text));
    	
    	return dragStack;
    }

    /**
     * Resize to.
     * 
     * @param w
     *            the w
     * @param h
     *            the h
     */
    public void resizeTo(int w, int h) {
        // grid.setWidth(w);
        // grid.setHeight(h-100);
        // h = h - this.controller.getControls().getControlHeight();
        // dateField.setWidth(w2);
        // dateField.setw
        // valuesField.setWidth(w2);
        //        this.grid.setSize(w + "px", h + "px"); //$NON-NLS-1$ //$NON-NLS-2$
        // grid.redraw();
    }

    /**
     * Inits the.
     */
    private void init() {

        setID(this.id);
        setTitle(this.title);
        setIcon("../img/icons/table.png"); //$NON-NLS-1$
        setPane(this.grid);

        // grid.setWidth100();
        // grid.setHeight100();
        // grid.setHeight(View.getInstance().getHeightForDataPanel());
        this.grid.setEmptyMessage(i18n.noData());

        this.dateField = new ListGridField("date", i18n.date()); //$NON-NLS-1$
        this.dateField.setAlign(Alignment.CENTER);
        this.dateField.setType(ListGridFieldType.TEXT);
        this.dateField.setCanFilter(false);
        this.dateField.setCanGroupBy(false);
        this.dateField.setCanSort(true);
        this.dateField.setCanToggle(false);
        // dateField.setWidth(View.getInstance().getWidthForDataPanel() / 2);

        this.valuesField = new ListGridField("value", "values"); //$NON-NLS-1$ //$NON-NLS-2$
        this.valuesField.setAlign(Alignment.CENTER);
        this.valuesField.setType(ListGridFieldType.TEXT);
        this.valuesField.setCanFilter(false);
        this.valuesField.setCanGroupBy(false);
        this.valuesField.setCanSort(false);
        this.valuesField.setCanToggle(false);
        // valuesField.setWidth(View.getInstance().getWidthForDataPanel() / 2);

        this.grid.setFields(this.dateField, this.valuesField);
        // grid.setCanResizeFields(true);

    }

    public ArrayList<Long> getValueOrder(HashMap<Long, Double> values) {
        ArrayList<Long> order = new ArrayList<Long>();
        Long[] sorted = values.keySet().toArray(new Long[values.size()]);
        Arrays.sort(sorted);
        for (int i = 0; i < sorted.length; i++) {
            order.add(sorted[i]);
        }
        return order;
    }

    public void update(HashMap<Long, Double> data, LegendElement le) {
        try {
            TimeseriesLegendData ts = (TimeseriesLegendData) le.getDataWrapper();
            this.valuesField.setTitle(ts.getUnitOfMeasure());

            ListGridRecord[] records = new ListGridRecord[data.size()];
            if (data.size() > 0) {
                ArrayList<Long> valueKeys = getValueOrder(data);
                for (int i = 0; i < valueKeys.size(); i++) {
                    records[i] = new ListGridRecord();
                    DateTimeFormat dateFormat = DateTimeFormat.getFormat("dd-MM-yyyy HH:mm:ssZ");
                    String formattedDate = dateFormat.format(new Date(valueKeys.get(i)));
                    records[i].setAttribute("date", formattedDate);
                    records[i].setAttribute("value", data.get(valueKeys.get(i)));
                }
            }
            this.grid.setData(records);
            this.grid.refreshFields();
        } catch (ClassCastException e) {
            // dataWrapper not of type TimeSeries
            this.valuesField.setTitle("values");
            this.grid.setData(new ListGridRecord[]{});
            this.grid.refreshFields();  
        } catch (Exception e) {
            if (!GWT.isProdMode()) {
                GWT.log("", e);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.elements.interfaces.DataPanelTab#getDataControls
     * ()
     */
    @Override
    public DataControls getDataControls() {
        return this.controller.getControls();
    }
}