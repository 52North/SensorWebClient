/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui;

import static org.n52.client.bus.EventBus.getMainEventBus;

import java.util.ArrayList;

import org.n52.client.Application;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.DataControls;
import org.n52.client.sos.event.InitEvent;
import org.n52.client.sos.event.ResizeEvent;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.handler.InitEventHandler;
import org.n52.client.sos.event.handler.TabSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Label;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Side;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * Representation for the DataPanel with the DiagramTab and the TableTab.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class DataPanel extends VLayout {

    private TabSet panel;

    private ArrayList<DataPanelTab> tabs = new ArrayList<DataPanelTab>();

    private DataControls ctrls;

    private String elemID;

    private DataPanelTab currentTab;

    private Integer widthPanel = 0;

    private Integer heightPanel = 0;

    public static Label requestCounter;

    public DataPanel(String id) {
        this.elemID = id;
        new DataPanelEventBroker();
        generateDataPanel();
    }

    public DataPanelTab getTab(int idx) {
        return this.getTabs().get(idx);
    }

    public void addTab(DataPanelTab tab) {
        this.getTabs().add(tab);
        this.getPanel().addTab(tab);
    }

    private void generateDataPanel() {
        addResizedHandler(new ResizedHandler() {

            public void onResized(ResizedEvent event) {
                try {
                    int tmpW = getWidth();
                    int tmpH = getHeight();
                    int confInt = 2;

                    int lW = DataPanel.this.getWidthPanel() - confInt;
                    int hW = DataPanel.this.getWidthPanel() + confInt;
                    int lH = DataPanel.this.getHeightPanel() - confInt;
                    int hH = DataPanel.this.getHeightPanel() + confInt;

                    if (tmpW > hW || tmpW < lW || tmpH > hH || tmpH < lH) {
						if (Application.isHasStarted()) {
							GWT.log("before: " + DataPanel.this.getWidthPanel()
									+ "x" + DataPanel.this.getHeightPanel()
									+ "    after: " + tmpW + "x" + tmpH);
							DataPanel.this.setWidthPanel(getWidth());
							DataPanel.this.setHeightPanel(getHeight());
							EventBus.getMainEventBus().fireEvent(
									new ResizeEvent(DataPanel.this
											.getWidthPanel(), DataPanel.this
											.getHeightPanel()));
						}
                    } else {
                        // DataPanel.this.setWidthPanel(getWidth());
                        // DataPanel.this.setHeightPanel(getHeight());
                    }

                } catch (Exception e) {
                    if (!GWT.isProdMode()) {
                        GWT.log("", e);
                    }
                }
            }
        });

        TabSet tabSet = new TabSet();
//        tabSet.setTabBarThickness(0);

        this.setPanel(tabSet);

        Canvas c = new Canvas();
        c.setHeight(30);
        c.setAlign(Alignment.RIGHT);
        LoadingSpinner loader = new LoadingSpinner("../img/loader_wide.gif", 43, 11);
        loader.setPadding(2);
        requestCounter = new Label();
        requestCounter.setWidth("150px");

        HLayout loaders = new HLayout();
        loaders.setTabIndex(-1);
        loaders.setHeight(30);
        loaders.addMember(loader);
        loaders.addMember(requestCounter);
        
        c.addChild(loaders);
        
        //this.getPanel().setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, c);

         //panel.setHeight("*");
         //panel.setWidth("77%");

        setHeight100();
        setWidth100();

        this.getPanel().setTabBarThickness(1);
//        this.getPanel().setTop(-2);
        
//        this.getPanel().setShowTabPicker(false);
//        this.getPanel().setShowTabScroller(false);
        this.getPanel().setTabBarPosition(Side.RIGHT);
        this.getPanel().setWidth100();
        this.getPanel().setHeight100();
        this.getPanel().setOverflow(Overflow.HIDDEN);

        addMember(this.getPanel());
        this.getPanel().addTabSelectedHandler(new TabSelectedHandler() {

            public void onTabSelected(
                    com.smartgwt.client.widgets.tab.events.TabSelectedEvent event) {
                if (Application.isHasStarted()) {
                    DataPanelTab tab = (DataPanelTab) event.getTab();
                    DataPanel.this.getPanel().setWidth100();
                    DataPanel.this.getPanel().setHeight100();

                    org.n52.client.sos.event.TabSelectedEvent customEvent;
                    customEvent = new org.n52.client.sos.event.TabSelectedEvent(tab);
                    EventBus.getMainEventBus().fireEvent(customEvent);
                }
            }
        });
    }

    public DataPanelTab getCurrentTab() {
        return this.currentTab;
    }

    public int getPanelWidth() {
        return this.getPanel().getWidth() - 10;
    }

    public int getPanelHeight() {
        return this.getPanel().getHeight() - this.getPanel().getTabBarThickness() - 15;
    }

    public void displayControls(DataControls c) {
        if (this.ctrls != null && hasMember(this.ctrls)) {
            removeMember(this.ctrls);
        }
        if (c != null && c.isVisible()) {
            this.ctrls = c;
            addMember(this.ctrls);
        }
        setHeight100();
    }

    public void update() {
        displayControls(currentTab.getDataControls());
    }

    public String getId() {
        return this.elemID;
    }

    public void setWidthPanel(Integer widthPanel) {
        this.widthPanel = widthPanel;
    }

    public Integer getWidthPanel() {
        return this.widthPanel;
    }

    public void setHeightPanel(Integer heightPanel) {
        this.heightPanel = heightPanel;
    }

    public Integer getHeightPanel() {
        return this.heightPanel;
    }

    public void setPanel(TabSet panel) {
        this.panel = panel;
    }

    public TabSet getPanel() {
        return this.panel;
    }

    public void setCurrentTab(DataPanelTab currentTab) {
        this.currentTab = currentTab;
    }

    public ArrayList<DataPanelTab> getTabs() {
        return this.tabs;
    }

    private class DataPanelEventBroker implements TimeSeriesChangedEventHandler, TabSelectedEventHandler,
            InitEventHandler {

        public DataPanelEventBroker() {
            getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE, this);
            getMainEventBus().addHandler(TabSelectedEvent.TYPE, this);
            getMainEventBus().addHandler(InitEvent.TYPE, this);
        }

        public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
             // update();
        }

        public void onSelected(TabSelectedEvent evt) {
            DataPanel.this.setCurrentTab(evt.getTab());
            update();
        }

        public void onInit(InitEvent evt) {
            Integer width = DataPanel.this.getWidthPanel();
            Integer height = DataPanel.this.getHeightPanel();
            ResizeEvent event = new ResizeEvent(width, height);
            getMainEventBus().fireEvent(event);
        }

    }

}