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

package org.n52.client.ui;

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.ArrayList;

import org.n52.client.control.PropertiesManager;
import org.n52.client.eventBus.EventBus;
import org.n52.client.ses.i18n.SesStringsAccessor;
import org.n52.client.ses.ui.SesTab;
import org.n52.client.sos.event.TabSelectedEvent;
import org.n52.client.sos.i18n.SosStringsAccessor;
import org.n52.client.sos.ui.EESTab;
import org.n52.client.view.gui.elements.DataPanel;
import org.n52.client.view.gui.elements.DataPanelTab;
import org.n52.client.view.gui.elements.Header;
import org.n52.client.view.gui.elements.MainPanel;
import org.n52.client.view.gui.elements.legend.Legend;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.client.view.gui.widgets.buttons.Button;

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
	
	private DataPanelTab eesTab;

    private View() {
        // keep private
    }

    public static View getInstance() {
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
        
        int fadeout = PropertiesManager.getInstance().getParamaterAsInt("toasterFadeout", 5);
        Toaster.createInstance("toaster", 400, 200, i18n.loggerWindowTitle(), mainPanel, fadeout * 1000);
        VLayout vLayout = createLayoutStack();
        mainPanel.addMember(vLayout);

        // TODO change tabs to widgets
        eesTab = new EESTab("ees", i18n.diagram());
        registerTabWidget(eesTab);
        if (PropertiesManager.getInstance().getTabsFromPropertiesFile().contains("SesTab")) {
        	sesTab = new SesTab("SES", "SES");
        	registerTabWidget(sesTab);
        }
        
        EventBus.getMainEventBus().fireEvent(new TabSelectedEvent(datapanel.getTab(0)));
    }

    private void registerTabWidget(DataPanelTab widget) {
        ArrayList<String> configuredTabs = PropertiesManager.getInstance().getTabsFromPropertiesFile();
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

            legendHider.setStyleName("sensorweb_client_legendHider");
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

    public DataPanelTab getEesTab() {
		return eesTab;
	}

}