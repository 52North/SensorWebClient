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
package org.n52.client.ui;

import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.client.ui.Toaster.createToasterInstance;

import java.util.ArrayList;

import org.n52.client.ses.i18n.SesStringsAccessor;
import org.n52.client.ses.ui.SesTab;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.i18n.SosStringsAccessor;
import org.n52.client.sos.ui.DiagramTab;
import org.n52.client.ui.btn.Button;
import org.n52.client.ui.legend.Legend;
import org.n52.client.util.ClientUtils;

import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public class View {

    private static View view;

    /** Hold to switch from normal to extended tooltips. */
    private ArrayList<Button> tooltipElements;

    private boolean showExtendedTooltip = false;

    private MainPanel mainPanel;

    private DataPanel datapanel;

    private Legend legend;

    private VLayout legendHider;

    private Img hideButton;

	private DataPanelTab sesTab;
	
	private DataPanelTab diagramTab;

    protected View() {
        // singleton, keep private
    }

    public static View getView() {
        if (view == null) {
            view = new View();
            view.init();
        }
        return view;
    }

    public void init() {
        tooltipElements = new ArrayList<Button>();

        mainPanel = new MainPanel();
        SosStringsAccessor.init();
        SesStringsAccessor.init();
        
        int fadeout = getPropertiesManager().getParamaterAsInt("toasterFadeout", 5);
        createToasterInstance("toaster", 400, 200, i18n.loggerWindowTitle(), mainPanel, fadeout * 1000);
        VLayout vLayout = createLayoutStack();
        mainPanel.addMember(vLayout);

        diagramTab = new DiagramTab("diagram", i18n.diagram());
        registerTabWidget(diagramTab);
        if (ClientUtils.isSesEnabled()) {
        	sesTab = new SesTab("SES", "SES");
        	registerTabWidget(sesTab);
        }
        
        getMainEventBus().fireEvent(new TabSelectedEvent(datapanel.getTab(0)));
    }

    private void registerTabWidget(DataPanelTab widget) {
        ArrayList<String> configuredTabs = getPropertiesManager().getTabsFromPropertiesFile();
        if (configuredTabs.contains(DataPanelTab.getTabIdentifier())) {
            datapanel.addTab(widget);
        }
    }
    
    private VLayout createLayoutStack() {
        VLayout vLayout = new VLayout();
        vLayout.addMember(getHeader());
        vLayout.addMember(getDataViewAndLegendPanel());
        return vLayout;
    }


    private Layout getDataViewAndLegendPanel() {
        HLayout hLayout = new HLayout();
        hLayout.setOverflow(Overflow.HIDDEN);
        hLayout.addMember(getLegend());
        hLayout.addMember(getLegendHider());
        hLayout.addMember(getDataPanel());
        hLayout.setTabIndex( -1);
        return hLayout;
    }

    private Canvas getHeader() {
        HLayout header = new Header("header");
         return header;
     }
    
    public Legend getLegend() {
        if (legend == null) {
            legend = new Legend("legend");
        }
        return legend;
    }

    private Layout getLegendHider() {

        if (legendHider == null) {
            legendHider = new VLayout();
            legendHider.setCursor(Cursor.POINTER);
            legendHider.setCanHover(true);
            legendHider.setWidth(9);

            hideButton = new Img("../img/icons/prev_hider.png", 9, 14);
            hideButton.setCursor(Cursor.POINTER);
            LayoutSpacer spacer = new LayoutSpacer();
            spacer.setHeight("*");
            spacer.setWidth(9);

            legendHider.addMember(spacer);
            legendHider.addMember(hideButton);
            legendHider.addMember(spacer);

            legendHider.setStyleName("n52_sensorweb_client_legendHider");
            legendHider.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    hideLegend();
                }
            });
        }
        return legendHider;
    }
    
    public void hideLegend(){
        if (View.this.legend.isVisible()) {

            View.this.legend.hide();
            View.this.legend.setVisible(false);
            View.this.setHideButtonSrc("../img/icons/next_hider.png");
            View.this.datapanel.reflow();
        } else {

            View.this.legend.show();
            View.this.legend.setVisible(true);
            View.this.setHideButtonSrc("../img/icons/prev_hider.png");
            View.this.datapanel.reflow();
        }
    }

    private void setHideButtonSrc(String source) {
        hideButton.setSrc(source);
    }

    public DataPanelTab getCurrentTab() {
        return this.datapanel.getCurrentTab();
    }
    
    public DataPanel getDataPanel() {
        if (datapanel == null) {
            datapanel = new DataPanel("dataPanel");
        }
        return datapanel;
    }

    public int getDataPanelWidth() {
        return mainPanel.getWidth() - legend.getWidth() - legendHider.getWidth();
    }

    public int getDataPanelHeight() {
        return datapanel.getPanelHeight();
    }

    public void registerTooltip(Button elem) {
        tooltipElements.add(elem);
    }

    /**
     * @see also {@link #switchDetailedTooltips()}
     */
    public boolean isShowExtendedTooltip() {
        return this.showExtendedTooltip;
    }

    /**
     * Switches from normal to detailed tooltips and vice versa.
     */
    public void switchDetailedTooltips() {
        showExtendedTooltip = !showExtendedTooltip;
        if (showExtendedTooltip) {
            for (Button elem : tooltipElements) {
                elem.setExtendedTooltip();
            }
        }
        else {
            for (Button elem : tooltipElements) {
                elem.setShortTooltip();
            }
        }
    }

    public int getLegendWidth() {
        return (int) legend.getWidth();
    }

    public int getLegendHeight() {
        return (int) legend.getHeight();
    }
    
    public DataPanelTab getSesTab(){
    	return this.sesTab;
    }

    public DataPanelTab getDiagramTab() {
		return diagramTab;
	}

}