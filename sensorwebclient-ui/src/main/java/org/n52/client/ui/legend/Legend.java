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

package org.n52.client.ui.legend;

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.client.ses.ui.profile.ProfileWindow;
import org.n52.client.sos.data.TimeseriesDataStore;
import org.n52.client.sos.event.data.ExportEvent.ExportType;
import org.n52.client.sos.legend.Timeseries;
import org.n52.client.sos.ui.StationSelector;
import org.n52.client.ui.DataPanel;
import org.n52.client.ui.DataPanelTab;
import org.n52.client.ui.Impressum;
import org.n52.client.ui.View;
import org.n52.client.ui.btn.ImageButton;
import org.n52.client.util.ClientUtils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;

public class Legend extends VLayout {
	
	//@TODO: In properties-Datei auslagern!
	public final static String DOWNLOAD_ZIP_URL="/appl/bs/Main.php?do=getZips&messreihen=";
	
	//private static final Logger LOGGER = LoggerFactory.getLogger(Legend.class);

    private ArrayList<LegendElement> legendEntries = new ArrayList<LegendElement>();

    private ArrayList<ImageButton> contributedButtons = new ArrayList<ImageButton>();

    private LegendController controller;

    private LegendElement selectedElement;

    private VStack legend;

    private String elemID;

    private HLayout topButtons;

    private Label exportButton;

    private Label sesTabButton;

    private VLayout exportMenu;

    private HLayout exportLoadingSpinner;

    private LoginWindow profileWindow;

    public Legend(String id) {
        this.elemID = id;
        setStyleName("n52_sensorweb_client_legend");
        this.controller = new LegendController(this);

        generateLegend();
        this.legend.setCanAcceptDrop(true);
        // legend.setAnimateMembers(true);
    }

    private MapWidget createMapContent() {
        OverviewMapController controller = new OverviewMapController();
        return controller.createMap(); // TODO refactor

        // return new LegendMap().getLayout();
    }

    public LegendElement getSelectedLegendelement() {
        return selectedElement;
    }

    public void addLegendElement(LegendElement element) {
        element.setOrdering(legendEntries.size());
        legendEntries.add(element);

        // le.setTargetToDrag(this); // disable legend dragging
        legend.addMember(element.getLayout());

        // legend.addMember(le.getLegendEntry());

    }

    public void contributeTopButtons(ArrayList<ImageButton> buttons) {
        for (ImageButton ib : this.contributedButtons) {
            topButtons.removeMember(ib);
        }

        contributedButtons.clear();

        for (ImageButton button : buttons) {
            button.setMargin(0);
            // ib.setWidth(16);
            // ib.setHeight(16);
            button.setStyleName("n52_sensorweb_client_topButtons");
            topButtons.addMember(button);
        }
        this.contributedButtons.addAll(buttons);
    }

    public void generateLegend() {

        // TODO cleanup/extract method(s)

        setWidth("365px"); // TODO make configurable (Legend width)
        // setWidth("27%");
        setHeight100();
        setMargin(2);
        setMinWidth(220);
        // setOverflow(Overflow.CLIP_H);

        this.topButtons = new HLayout();
        this.topButtons.setTabIndex( -1);
        this.topButtons.setHeight(1);
        this.topButtons.setAlign(Alignment.RIGHT);
        this.topButtons.setReverseOrder(true);

        ImageButton us = new ImageButton("us_lang", "../img/icons/gb.png", i18n.usLang(), i18n.usLangExtended());
        View.getView().registerTooltip(us);
        ImageButton de = new ImageButton("de_lang", "../img/icons/de.png", i18n.deLang(), i18n.deLangExtended());
        View.getView().registerTooltip(de);
        ImageButton ttips = new ImageButton("ttips", "../img/icons/comment.png", i18n.ttips(), i18n.ttipsExtended());
        View.getView().registerTooltip(ttips);
        ImageButton help = new ImageButton("help", "../img/icons/help.png", i18n.help(), i18n.helpExtended());
        View.getView().registerTooltip(help);
        ImageButton logger = new ImageButton("logger", "../img/icons/report.png", i18n.logger(), i18n.loggerExtended());
        View.getView().registerTooltip(logger);

        ImageButton impressum = new ImageButton("impressum",
                                                "../img/icons/information.png",
                                                i18n.Impressum(),
                                                i18n.Impressum());

        Label imprint = new Label(i18n.Impressum());
        imprint.setTooltip(i18n.Impressum());
        imprint.setStyleName("label");
        imprint.setWidth(60);

        View.getView().registerTooltip(impressum);
        View.getView().switchDetailedTooltips();

        imprint.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                new Impressum().show();
            }
        });

        help.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String helpUrl = GWT.getHostPageBaseURL() + i18n.helpPath();
                Window.open(helpUrl, "", "");
            }
        });

        // this.topButtons.addMember(us);
        // this.topButtons.addMember(de);
        // this.topButtons.addMember(ttips);
        // this.topButtons.addMember(help);
        // this.topButtons.addMember(logger);
        // this.topButtons.addMember(imprint);

        addMember(this.topButtons);

        // Button addTS = new Button(I18N.sosClient.addNewTimeseries());
        // addTS.setIcon("../img/icons/chart_curve_add_new.png");

        // ImageButton addTS = new ImageButton("addTSLegend",
        // "../img/icons/chart_curve_add_new.png",
        // I18N.sosClient.picker(),
        // I18N.sosClient.pickerExtended());
        // View.getInstance().registerTooltip(addTS);

        // addTS.addClickHandler(new ClickHandler() {
        // public void onClick(ClickEvent evt) {
        // StationSelector.getInst().show();
        // }
        // });

        // addTS.setPadding(2);
        // addTS.setSize("32px", "32px");
        // addTS.setAlign(Alignment.CENTER);

        // Label addTSLabel = new Label(I18N.sosClient.addNewTimeseries());
        // addTSLabel.setHeight(20);
        // addTSLabel.setWidth100();
        // addTSLabel.setPadding(8);
        // addTSLabel.setStyleName("legendAddBoxLabel");
        // addTSLabel.setCursor(Cursor.HAND);
        // addTSLabel.setAlign(Alignment.CENTER);

        // HLayout add = new HLayout();
        // add.setStyleName("legendAddBox");
        // add.setAutoHeight();
        // add.addMember(addTS);
        // add.addMember(addTSLabel);
        // add.addClickHandler(new ClickHandler() {
        // public void onClick(ClickEvent event) {
        // StationSelector.getInst().show();
        // }
        // });

        // zip-export CSV
        // this.exportZipCSV =
        // new ImageButton("diagExportZipCSV", "../img/icons/folder_csv.png", i18nManager.i18nSOSClient
        // .exportZipCSV(), i18nManager.i18nSOSClient.exportZipCSVExtended());

        // View.getInstance().registerTooltip(this.exportZipCSV);

        // zip-export XLS
        // this.exportPDFallInOne =
        // new ImageButton("diagExportPDFallIneOne", "../img/icons/page_white_acrobat_add.png",
        // i18nManager.i18nSOSClient.exportPDFallInOne(),
        // i18nManager.i18nSOSClient.exportPDFallInOneExtended());
        // View.getInstance().registerTooltip(this.exportPDFallInOne);

        Button exportZipCSV = new Button(i18n.csv());
        exportZipCSV.setIcon("../img/icons/table.png");
        exportZipCSV.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                controller.exportTo(ExportType.CSV_ZIP);
            }
        });

        Button exportPDFallInOne = new Button(i18n.pdf());
        exportPDFallInOne.setStyleName("input");
        exportPDFallInOne.setIcon("../img/icons/page_white_acrobat_add.png");
        exportPDFallInOne.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                controller.exportTo(ExportType.PDF_ALL_IN_ONE);
            }
        });

        createExportLoadingSpinner();

        HStack menuStack = new HStack();
        // menuStack.setAlign(Alignment.CENTER);
        menuStack.setHeight100();
        Label space = new Label();
        Label addTS = createAddTimeSeriesLabelButton();
        exportButton = createExportLabelButton();
        sesTabButton = createSESTabLabelButton();
        space.setWidth("1%");
        exportButton.setWidth("20%");
        exportLoadingSpinner.setTop(5);
        exportLoadingSpinner.setWidth("2%");
        menuStack.addMember(addTS);
        menuStack.addMember(space);
        if (ClientUtils.isSesEnabled()) {
            addTS.setWidth("38%");
            sesTabButton.setWidth("35%");
            menuStack.addMember(sesTabButton);
            menuStack.addMember(space);
        }
        else {
            addTS.setWidth("75%");
        }

        menuStack.addMember(exportButton);
        menuStack.addMember(exportLoadingSpinner);
        setExportButtonActiv(false);

        VStack vMenuStack = new VStack();
        vMenuStack.addMember(menuStack);
        vMenuStack.setHeight(28);
        addMember(vMenuStack);

        // create export menu
        createExportMenu();
        addChild(exportMenu);

        VStack separator = new VStack();
        separator.setHeight("3px");
        addMember(separator);

        this.legend = new VStack();
        this.legend.setOverflow(Overflow.AUTO);
        this.legend.setHeight("*");
        this.legend.setShowResizeBar(true);
        addMember(this.legend);
        // this.intro = new HTMLFlow(I18N.sosClient.intro());
        // this.legend.addMember(this.intro);

        addMember(separator);

        Layout layout = new Layout();
        layout.addMember(createMapContent());
        layout.setWidth100();
        layout.setHeight("40%");
        addMember(layout);

        // this.footer = new HLayout();
        // this.footer.setTabIndex( -1);
        // addMember(this.footer);

    }

    private void createExportLoadingSpinner() {
        this.exportLoadingSpinner = new HLayout();
        Img spinner = new Img("../img/loader_wide.gif", 43, 11);
        // this.exportLoadingSpinner.setWidth100();
        // this.exportLoadingSpinner.setHeight100();
        this.exportLoadingSpinner.setAlign(Alignment.CENTER);
        this.exportLoadingSpinner.addMember(spinner);
        this.exportLoadingSpinner.hide();
    }

    private Label createCSVLabel() {
        Label toCSV = new Label(i18n.toCSV());
        toCSV.setWrap(false);
        toCSV.setAutoFit(true);
        toCSV.setPadding(3);
        toCSV.setWidth100();
        toCSV.setStyleName("n52_sensorweb_client_exportEntry");
        toCSV.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                controller.exportTo(ExportType.CSV_ZIP);
                exportMenu.hide();
            }
        });
        return toCSV;
    }

    private Label createPDFLabel() {
        Label toPDF = new Label(i18n.toPDF());
        toPDF.setWrap(false);
        toPDF.setAutoFit(true);
        toPDF.setPadding(3);
        toPDF.setWidth100();
        toPDF.setStyleName("n52_sensorweb_client_exportEntry");
        toPDF.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                controller.exportTo(ExportType.PDF_ALL_IN_ONE);
                exportMenu.hide();
            }
        });
        return toPDF;
    }

    private Label createZIPLabel() {
        Label toZIP = new Label(i18n.toZIP());
        toZIP.setWrap(false);
        toZIP.setAutoFit(true);
        toZIP.setPadding(3);
        toZIP.setWidth100();
        toZIP.setStyleName("n52_sensorweb_client_exportEntry");
        toZIP.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	/*LOGGER.debug("------------------------------------------------------------");
            	LOGGER.debug("ClickHandler ZIP geklickt");
            	LOGGER.debug("------------------------------------------------------------");*/
            	//System.out.println("ClickHandler ZIP geklickt");
                //controller.exportTo(ExportType.DATADOWNLOAD_ZIP);
            	
            	TimeseriesDataStore dataStore = TimeseriesDataStore.getTimeSeriesDataStore();
        		HashMap<String, Timeseries> dataItems = dataStore.getDataItems();
        		Collection<Timeseries> values = dataItems.values();
            	
        		String infos="";
        		for (Timeseries ts: values) {
        			String paramName=ts.getPhenomenonId();
        			String stationName=ts.getProcedureId();
        			if (stationName.indexOf("/")!=stationName.lastIndexOf("/")) {
        				stationName = stationName.substring(stationName.lastIndexOf("/")+1);
        			}
        			String queryString=paramName+":"+stationName+",";
        			infos+=queryString;
        		}
        		
        		String url=DOWNLOAD_ZIP_URL+infos;
            	Window.open(url, "_blank", "");
            	
                exportMenu.hide();
            }
        });
        return toZIP;
    }

    private Label createExportLabelButton() {
        Label export = new Label(i18n.export());
        export.setStyleName("n52_sensorweb_client_legendbuttonDisabled");
        export.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (exportMenu.isVisible()) {
                    exportMenu.hide();
                }
                else {
                    exportMenu.setLeft(exportButton.getAbsoluteLeft() - 2);
                    exportMenu.setWidth(exportButton.getWidth());
                    exportMenu.show();
                }
            }
        });
        return export;
    }

    private void createExportMenu() {
        exportMenu = new VLayout();
        exportMenu.setLeft(exportButton.getAbsoluteLeft());
        exportMenu.setTop(30);
        exportMenu.setStyleName("n52_sensorweb_client_interactionmenu");
        exportMenu.setAutoHeight();
        exportMenu.setZIndex(1000000);
        exportMenu.addMember(createPDFLabel());
        exportMenu.addMember(createCSVLabel());
        exportMenu.addMember(createZIPLabel());
        exportMenu.setVisible(false);
    }

    private Label createAddTimeSeriesLabelButton() {
        Label addTS = new Label(i18n.picker());
        addTS.setStyleName("n52_sensorweb_client_legendbuttonPrimary");
        addTS.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                StationSelector.getInst().show();
            }
        });
        return addTS;
    }

    private Label createSESTabLabelButton() {
        Label sesTabLabelButton = new Label(i18n.editProfile());
        sesTabLabelButton.setStyleName("n52_sensorweb_client_legendbutton");
        sesTabLabelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Legend.this.showProfileWindow();
            }
        });
        sesTabLabelButton.setVisible(true);
        return sesTabLabelButton;
    }
    
    private void showProfileWindow() {
        if (profileWindow == null) {
            profileWindow = new ProfileWindow();
        }
        profileWindow.show();
    }

    public void switchToDiagramTab() {
        DataPanel dataPanel = View.getView().getDataPanel();
        DataPanelTab diagramTab = View.getView().getDiagramTab();
        dataPanel.getPanel().selectTab(diagramTab);
        dataPanel.setCurrentTab(diagramTab);
        dataPanel.update();
    }

    public VStack getLegendStack() {
        return this.legend;
    }

    public LegendElement[] getEntries() {
        LegendElement[] elements = new LegendElement[legendEntries.size()];
        return legendEntries.toArray(elements);
    }

    public void fill(ArrayList<LegendElement> elementsToFillIn) {

        // remove old elements
        Iterator<LegendElement> iterator = legendEntries.iterator();
        while (iterator.hasNext()) {
            LegendElement currentElement = iterator.next();
            if ( !elementsToFillIn.contains(currentElement)) {
                iterator.remove(); // remove current
            }
        }

        // add new elements
        for (LegendElement legendElement : elementsToFillIn) {
            if ( !legendEntries.contains(legendElement)) {
                addLegendElement(legendElement);
                if ( !legendElement.equals(selectedElement)) {
                    legendElement.hideFooter();
                }
            }
        }
        reorderAlong(elementsToFillIn);
    }

    /**
     * reorders legend entries along the order given by the passed <code>elements</code>
     */
    private void reorderAlong(ArrayList<LegendElement> elements) {
        if (legend.getMembers().length > 0) {
            legend.removeMembers(legend.getMembers());
            legendEntries.clear();
            for (int i = 0; i < elements.size(); i++) {
                legend.addMember(elements.get(i).getLayout());
                legendEntries.add(i, elements.get(i));
            }
        }
    }

    public String getId() {
        return this.elemID;
    }

    void setSelectedElement(LegendElement element) {
        this.selectedElement = element;
    }

    public void stopExportLoadingSpinner() {
        exportLoadingSpinner.hide();
        exportButton.show();
    }

    public void setExportButtonActiv(boolean activ) {
        if (activ) {
            exportButton.setDisabled(false);
            exportButton.setStyleName("n52_sensorweb_client_legendbutton");
        }
        else {
            exportButton.setDisabled(true);
            exportButton.setStyleName("n52_sensorweb_client_legendbuttonDisabled");
        }
    }

    public void startExportLoadingSpinner() {
        exportLoadingSpinner.show();
        exportButton.hide();
    }
}