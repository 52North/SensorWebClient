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
package org.n52.client.view.gui.elements.controlsImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.n52.client.control.ClientUtils;
import org.n52.client.control.PropertiesManager;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.EventCallback;
import org.n52.client.i18n.I18N;
import org.n52.client.model.communication.LoaderManager;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
import org.n52.client.model.data.dataManagers.TimeManager;
import org.n52.client.model.data.representations.TimeSeries;
import org.n52.client.sos.event.DatesChangedEvent;
import org.n52.client.sos.event.SwitchGridEvent;
import org.n52.client.sos.event.data.ExportEvent;
import org.n52.client.sos.event.data.FirstValueOfTimeSeriesEvent;
import org.n52.client.sos.event.data.OverviewIntervalChangedEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesLastValueEvent;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;
import org.n52.client.sos.event.data.OverviewIntervalChangedEvent.IntervalType;
import org.n52.client.sos.event.data.handler.OverviewIntervalChangedEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesFirstValueEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesLastValueEventHandler;
import org.n52.client.sos.event.handler.DatesChangedEventHandler;
import org.n52.client.view.View;
import org.n52.client.view.gui.elements.tabImpl.EESTab;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.client.view.gui.widgets.buttons.ImageButton;
import org.n52.client.view.gui.widgets.windows.InteractionWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.DateDisplayFormat;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.Positioning;
import com.smartgwt.client.types.TimeDisplayFormat;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.DateItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.TimeItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.validator.DateRangeValidator;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.LayoutSpacer;
import com.smartgwt.client.widgets.layout.VLayout;

public abstract class DataControlsTimeSeries extends DataControls {

    private VLayout innerLayout;

    private HLayout topLayout;

    private HLayout bottomLayout;

    private long from;

    private long to;

    private final DateItem fromDateItem = new DateItem();

    private final DateItem toDateItem = new DateItem();

    private final TimeItem fromTimeItem = new TimeItem();

    private final TimeItem toTimeItem = new TimeItem();

    private boolean isDiagram = true;

    protected ImageButton exportZipPDF;

    protected ImageButton exportZipCSV;

    protected ImageButton exportZipXLS;

    private ImageButton diagForwardDay;

    private ImageButton diagBackWeek;

    private ImageButton diagForwardWeek;

    private ImageButton diagBackMonth;

    private ImageButton diagBackYear;

    private ImageButton diagForwardMonth;

    private ImageButton diagForwardYear;

    private ImageButton diagBackDay;

    private ImageButton refresh;

    private String elemID;

    protected ImageButton jumpToday;

    protected TextItem overviewInterval;

    protected ComboBoxItem overviewIntervalType;

    private long currentLast = Long.MIN_VALUE;

    private long currentFirst = Long.MAX_VALUE;

    protected IntervalType currentIntervalType;

    protected ImageButton exportPDFallInOne;

    protected long currentInterval;

    protected Window expertsWindow;

    protected VLayout expertsLayout;

    protected HLayout controlButtons;

	private HLayout buttonLayout;
	
	private InteractionWindow diagramInteractionMenu;

	private Label gridButton;

	private boolean gridShown;

	private Label autoScaleButton;

    private final static int DAY_IN_MILLISECONDS = 86400000;

    public long getCurrentLast() {
        return this.currentLast;
    }

    public long getCurrentFirst() {
        return this.currentFirst;
    }

    public void setCurrentLast(long currentLast) {
        this.currentLast = currentLast;
        DateRangeValidator vali = new DateRangeValidator();
        vali.setMax(new Date(this.currentLast + DAY_IN_MILLISECONDS));
        vali.setMin(new Date(this.currentFirst - DAY_IN_MILLISECONDS));
        this.fromDateItem.setValidators(vali);
        this.toDateItem.setValidators(vali);
    }

    public void setCurrentFirst(long currentFirst) {
        this.currentFirst = currentFirst;
        DateRangeValidator vali = new DateRangeValidator();
        vali.setMax(new Date(this.currentLast + DAY_IN_MILLISECONDS));
        vali.setMin(new Date(this.currentFirst - DAY_IN_MILLISECONDS));
        this.fromDateItem.setValidators(vali);
        this.toDateItem.setValidators(vali);
    }

    public DataControlsTimeSeries() {

        Date d = new Date();
        this.elemID = "controls_" + d.getTime();

        new DataControlsTimeServiesEventBroker();

        generateControls();

    }

    public boolean isDiagram() {
        return this.isDiagram;
    }

    public void setDiagram(boolean isDiagram) {
        this.isDiagram = isDiagram;
    }

    private void generateControls() {

        setAlign(Alignment.LEFT);
        setHeight(DataControls.CONTROL_HEIGHT);
        setOverflow(Overflow.AUTO);
        // setAlign(Alignment.CENTER);
        setStyleName("sensorweb_client_dataControls");

        this.innerLayout = new VLayout();
        this.innerLayout.setTabIndex(-1);
        this.topLayout = new HLayout();
        this.topLayout.setTabIndex(-1);
        this.bottomLayout = new HLayout();
        this.bottomLayout.setTabIndex(-1);

        this.expertsWindow = new Window();
        this.expertsWindow.setTitle(I18N.sosClient.expertsMenu());
        this.expertsWindow.setIsModal(true);
        // this.expertsWindow.setAutoSize(true);
        this.expertsWindow.setWidth(290);
        this.expertsWindow.setHeight(130);
        this.expertsWindow.setCanDragResize(true);
        this.expertsWindow.setShowModalMask(true);

        this.expertsLayout = new VLayout();
        this.expertsLayout.setAlign(Alignment.RIGHT);
        this.expertsLayout.setWidth100();
        this.expertsWindow.addItem(this.expertsLayout);

        ImageButton experts =
                new ImageButton("experts", "../img/icons/clock.png", I18N.sosClient.expertsMenuButton(),
                        I18N.sosClient.expertsMenuButton());
        experts.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.expertsWindow.centerInPage();
                DataControlsTimeSeries.this.expertsWindow.show();

            }
        });

        // top layout
        DynamicForm intervalForm = new DynamicForm();

        // date and time pickers
        this.fromDateItem.setTitle(I18N.sosClient.from());
        this.fromDateItem.setValidateOnChange(true);
        this.fromDateItem.setValidateOnExit(true);
        this.fromDateItem.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
        // this.fromDateItem.setErrorFormatter(new FormItemErrorFormatter() {
        //        
        // public String getErrorHTML(String[] errors) {
        // return i18nManager.constants.errorChooseBiggerDate() + " "+
        // errors[0];
        // }
        // });
        // fromDateItem.setUseTextField(true);
        this.fromTimeItem.setTitle(I18N.sosClient.time());
        this.fromTimeItem.setTooltip(I18N.sosClient.time());
        this.fromTimeItem.setShowHint(false);
        this.fromTimeItem.setShowTitle(false);
        //this.fromTimeItem.setTimeFormatter(TimeDisplayFormat.TOSHORTPADDED24HOURTIME);
        this.fromTimeItem.setTimeFormatter(TimeDisplayFormat.TOSHORTPADDED24HOURTIME);

        long today = TimeManager.getInst().getEnd();
        this.to = today;

        String d = DateTimeFormat.getFormat("HH:mm").format(new Date(today));

        this.from = TimeManager.getInst().getBegin();

        this.toDateItem.setTitle(I18N.sosClient.to());
        this.toDateItem.setValidateOnChange(true);
        this.toDateItem.setValidateOnExit(true);
        this.toDateItem.setDateFormatter(DateDisplayFormat.TOEUROPEANSHORTDATE);
        // this.toDateItem.setErrorFormatter(new FormItemErrorFormatter() {
        //        
        // public String getErrorHTML(String[] errors) {
        // Toaster.getInstance().addErrorMessage(i18nManager.constants.errorChooseSmallerDate()
        // + " "
        // +TimeManager.getInst().ge);
        // return "";
        // }
        //         
        // });
        // this.toDateItem.setErrorIconSrc("../img/icons/exclamation.png");
        // this.toDateItem.setErrorIconHeight(16);
        // this.toDateItem.setErrorIconWidth(16);
        // this.toDateItem.setErrorOrientation(FormErrorOrientation.LEFT);
        // toDateItem.setUseTextField(true);
        this.toTimeItem.setTitle(I18N.sosClient.time());
        this.toTimeItem.setTooltip(I18N.sosClient.time());
        this.toTimeItem.setShowHint(false);
        this.toTimeItem.setShowTitle(false);
        //this.fromTimeItem.setTimeFormatter(TimeDisplayFormat.TOSHORTPADDED24HOURTIME);
        this.fromTimeItem.setTimeFormatter(TimeDisplayFormat.TOSHORTPADDED24HOURTIME);

        this.fromDateItem.setValue(this.from);
        this.toDateItem.setValue(this.to);

        this.toTimeItem.setDefaultValue(d);
        this.fromTimeItem.setDefaultValue(d);

        this.toTimeItem.setValidateOnChange(true);
        this.fromTimeItem.setValidateOnChange(true);

        this.fromTimeItem.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getKeyName().equalsIgnoreCase("enter")) {
                    DataControlsTimeSeries.this.getFromTimeItem().blurItem();
                }
            }
        });

        this.toTimeItem.addKeyPressHandler(new KeyPressHandler() {

            public void onKeyPress(KeyPressEvent event) {
                if (event.getKeyName().equalsIgnoreCase("enter")) {
                    DataControlsTimeSeries.this.getToTimeItem().blurItem();
                }
            }
        });

        this.toTimeItem.setWidth(50);
        this.fromTimeItem.setWidth(50);

        EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(this.from, this.to, true));

        LayoutSpacer spacer = new LayoutSpacer();
        spacer.setWidth(10);

        this.setRefresh(new ImageButton("diagRefresh", "../img/icons/arrow_refresh.png", I18N.sosClient
                .refresh(), I18N.sosClient.refreshExtended()));
        View.getInstance().registerTooltip(this.getRefresh());
        intervalForm.setNumCols(3);

        this.overviewInterval = new TextItem();
        this.overviewInterval.setTitle(I18N.sosClient.overviewInterval());
        this.overviewInterval.setWidth(40);
        this.overviewInterval.setDefaultValue(PropertiesManager.getInstance().getParameterAsString(
                "defaultOverviewInterval"));

        this.overviewIntervalType = new ComboBoxItem();
        this.overviewIntervalType.setTitle(I18N.sosClient.in());
        this.overviewIntervalType.setShowTitle(false);
        this.overviewIntervalType.setWidth(70);
        String[] types = I18N.sosClient.intervalTypes().split(",");
        LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
        for (int i = 0; i < types.length; i += 2) {

            values.put(types[i], types[i + 1]);

        }
        this.overviewIntervalType.setValueMap(values);
        this.overviewIntervalType.setDefaultValue("DAY");
        this.currentIntervalType = IntervalType.DAY;
        this.overviewInterval.addChangedHandler(new ChangedHandler() {

            public void onChanged(ChangedEvent event) {

                try {

                    long inter = 0;
                    switch (DataControlsTimeSeries.this.currentIntervalType) {
                    case HOUR:
                        inter =
                                new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 60 * 60 * 1000;
                        break;
                    case DAY:
                        inter =
                                new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 24 * 60
                                        * 60 * 1000;
                        break;
                    case MONTH:
                        inter =
                                new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 30 * 24
                                        * 60 * 60 * 1000;
                        break;
                    case YEAR:
                        inter =
                                new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 365
                                        * 24 * 60 * 60 * 1000;
                        break;
                    default:
                        inter =
                                new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 24 * 60
                                        * 60 * 1000;
                        break;
                    }
                    DataControlsTimeSeries.this.currentInterval = inter;
                } catch (Exception e) {
                    // just for empty textfield
                    if (!GWT.isProdMode()) {
                        GWT.log("", e);
                    }
                }

            }
        });
        this.overviewIntervalType.addChangedHandler(new ChangedHandler() {

            public void onChanged(ChangedEvent event) {
                long inter = 0;
                IntervalType newIntervalType = IntervalType.valueOf(event.getValue().toString());
                switch (newIntervalType) {
                case HOUR:
                    inter =
                            new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 60 * 60 * 1000;
                    DataControlsTimeSeries.this.currentIntervalType = IntervalType.HOUR;
                    break;
                case DAY:
                    inter =
                            new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 24 * 60
                                    * 60 * 1000;
                    DataControlsTimeSeries.this.currentIntervalType = IntervalType.DAY;
                    break;
                case MONTH:
                    inter =
                            new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 30 * 24
                                    * 60 * 60 * 1000;
                    DataControlsTimeSeries.this.currentIntervalType = IntervalType.MONTH;
                    break;
                case YEAR:
                    inter =
                            new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 365 * 24
                                    * 60 * 60 * 1000;
                    DataControlsTimeSeries.this.currentIntervalType = IntervalType.YEAR;
                    break;
                default:
                    inter =
                            new Long(DataControlsTimeSeries.this.overviewInterval.getValue().toString()) * 24 * 60
                                    * 60 * 1000;
                    break;
                }
                DataControlsTimeSeries.this.currentInterval = inter;
                DataControlsTimeSeries.this.currentIntervalType = newIntervalType;
            }
        });

        intervalForm.setFields(this.fromDateItem, this.fromTimeItem, this.toDateItem, this.toTimeItem);

        DynamicForm oIntervalForm = new DynamicForm();
        oIntervalForm.setNumCols(6);
        oIntervalForm.setFields(this.overviewInterval, this.overviewIntervalType);
        this.bottomLayout.addMember(oIntervalForm);

        this.expertsLayout.addMember(intervalForm);
        this.expertsLayout.addMember(oIntervalForm);

        ImageButton confExp =
                new ImageButton("confExp", "../img/icons/accept.png", I18N.sosClient.OK(),
                        I18N.sosClient.OK());
        confExp.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                Date begin = (Date) DataControlsTimeSeries.this.getFromDateItem().getValue();
                Date end = (Date) DataControlsTimeSeries.this.getToDateItem().getValue();

                begin = createDate(begin, DataControlsTimeSeries.this.getFromTimeItem().getDisplayValue());
                end = createDate(end, DataControlsTimeSeries.this.getToTimeItem().getDisplayValue());
                
                if (datesAreValid(begin.getTime(), end.getTime())) {
                    DataControlsTimeSeries.this.getRefresh().setClicked();
                    DataControlsTimeSeries.this.getFromDateItem().blurItem();
                    DataControlsTimeSeries.this.getFromTimeItem().blurItem();
                    DataControlsTimeSeries.this.getToDateItem().blurItem();
                    DataControlsTimeSeries.this.getToTimeItem().blurItem();

                    long interval = end.getTime() - begin.getTime();
                    if (interval > DataControlsTimeSeries.this.currentInterval) {
                        Toaster.getInstance().addMessage(I18N.sosClient.errorOverviewInterval());
                        DataControlsTimeSeries.this.getRefresh().setNotClicked();
                        LoaderManager.getInstance().stopLoadingAnimations();
                        return;

                    }
                    
                    // WORKAROUND to have a maximum zoom level in the time frame Bug 508
                    begin = createDate(begin, DataControlsTimeSeries.this.getFromTimeItem().getDisplayValue());
                    end = createDate(end, DataControlsTimeSeries.this.getToTimeItem().getDisplayValue());
                    if (ClientUtils.isValidTimeFrameForZoomIn(begin.getTime(), end.getTime())) {
                        EventBus.getMainEventBus().fireEvent(
                                new OverviewIntervalChangedEvent(DataControlsTimeSeries.this.currentInterval,
                                        DataControlsTimeSeries.this.currentIntervalType));
                        DataControlsTimeSeries.this.expertsWindow.hide();
                        fireDateChangedEvent();
                    }
                    
                    
                } else {
                    DataControlsTimeSeries.this.getRefresh().setNotClicked();
                    LoaderManager.getInstance().stopLoadingAnimations();
                    DataControlsTimeSeries.this.expertsWindow.hide();
                }
            }
        });
        ImageButton cancelExp =
                new ImageButton("cancelExp", "../img/icons/cancel.png", I18N.sosClient.cancel(),
                        I18N.sosClient.cancel());
        cancelExp.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                DataControlsTimeSeries.this.resetDatePicker();
                DataControlsTimeSeries.this.expertsWindow.hide();

            }
        });

        HLayout buttons = new HLayout();
        buttons.setWidth100();
        buttons.setAlign(Alignment.RIGHT);
        buttons.addMember(confExp);
        buttons.addMember(cancelExp);

        this.controlButtons = new HLayout();
        this.expertsLayout.addMember(this.controlButtons);
        this.expertsLayout.addMember(buttons);

        /*** EXPERTS END ***/
        /************************************************/

//        this.jumpToday =
//                new ImageButton("jumpToToday", "../img/icons/date_go.png", I18N.sosClient.jumpToday(),
//                        I18N.sosClient.jumpTodayExt());
//        View.getInstance().registerTooltip(this.jumpToday);

//        jumpTodayLabel.setStyleName("label");
//        jumpTodayLabel.setWidth(60);
        
        
        

        // EventBus.getInst().fireEvent(
        // new OverviewIntervalChangedEvent(new
        // Long(PropertiesManager.getInstance()
        // .getAttributeByName("defaultOverviewInterval")) * 24 * 60 * 60 *
        // 1000,
        // IntervalType.DAY));

        // this.topLayout.setHeight(20);
        // this.topLayout.addMember(spacer);

        // topForm.draw();

        // bottom layout
        // day
        this.setDiagBackDay(new ImageButton("diagBackDay", "../img/icons/control_left.png", I18N.sosClient
                .back(), I18N.sosClient.backExtended()));
        // this.getDiagBackDay().setShowRollOver(true);
//        this.topLayout.addMember(this.getDiagBackDay());
        View.getInstance().registerTooltip(this.getDiagBackDay());

        Label day = new Label(I18N.sosClient.day());
        day.setWidth(35);
        day.setAlign(Alignment.CENTER);
        day.setPosition(Positioning.ABSOLUTE);
        day.setStyleName("controlLabel");
//        this.topLayout.addMember(day);

        this.setDiagForwardDay(new ImageButton("diagForwardDay", "../img/icons/control_right.png",
                I18N.sosClient.forth(), I18N.sosClient.forthExtended()));
//        this.topLayout.addMember(this.getDiagForwardDay());
        View.getInstance().registerTooltip(this.getDiagForwardDay());
        // this.getDiagForwardDay().setShowRollOver(true);

        // week
        this.setDiagBackWeek(new ImageButton("diagBackWeek", "../img/icons/control_left.png", I18N.sosClient
                .back(), I18N.sosClient.backExtended()));
//        this.topLayout.addMember(this.getDiagBackWeek());
        View.getInstance().registerTooltip(this.getDiagBackWeek());
        // this.getDiagBackWeek().setShowRollOver(true);

        Label week = new Label(I18N.sosClient.week());
        week.setWidth(35);
        week.setAlign(Alignment.CENTER);
        week.setPosition(Positioning.ABSOLUTE);
        week.setStyleName("controlLabel");
//        this.topLayout.addMember(week);

        this.setDiagForwardWeek(new ImageButton("diagForwardWeek", "../img/icons/control_right.png",
                I18N.sosClient.forth(), I18N.sosClient.forthExtended()));
//        this.topLayout.addMember(this.getDiagForwardWeek());
        View.getInstance().registerTooltip(this.getDiagForwardWeek());
        // this.getDiagForwardWeek().setShowRollOver(true);

        // month
        this.setDiagBackMonth(new ImageButton("diagBackMonth", "../img/icons/control_left.png", I18N.sosClient
                .back(), I18N.sosClient.backExtended()));
//        this.topLayout.addMember(this.getDiagBackMonth());
        View.getInstance().registerTooltip(this.getDiagBackMonth());
        // this.getDiagBackMonth().setShowRollOver(true);

        Label month = new Label(I18N.sosClient.month());
        month.setWidth(35);
        month.setAlign(Alignment.CENTER);
        month.setPosition(Positioning.ABSOLUTE);
        month.setStyleName("controlLabel");
//        this.topLayout.addMember(month);

        this.setDiagForwardMonth(new ImageButton("diagForwardMonth", "../img/icons/control_right.png",
                I18N.sosClient.forth(), I18N.sosClient.forthExtended()));
//        this.topLayout.addMember(this.getDiagForwardMonth());
        View.getInstance().registerTooltip(this.getDiagForwardMonth());
        // this.getDiagForwardMonth().setShowRollOver(true);

        // year
        this.setDiagBackYear(new ImageButton("diagBackYear", "../img/icons/control_left.png", I18N.sosClient
                .back(), I18N.sosClient.backExtended()));
//        this.topLayout.addMember(this.getDiagBackYear());
        View.getInstance().registerTooltip(this.getDiagBackYear());
        // this.getDiagBackYear().setShowRollOver(true);

        Label year = new Label(I18N.sosClient.year());
        year.setWidth(35);
        year.setAlign(Alignment.CENTER);
        year.setPosition(Positioning.ABSOLUTE);
        year.setStyleName("controlLabel");
//        this.topLayout.addMember(year);

        this.setDiagForwardYear(new ImageButton("diagdiagForwardYear", "../img/icons/control_right.png",
                I18N.sosClient.forth(), I18N.sosClient.forthExtended()));
        View.getInstance().registerTooltip(this.getDiagForwardYear());
//        this.topLayout.addMember(this.getDiagForwardYear());
        // this.getDiagForwardYear().setShowRollOver(true);

//        this.topLayout.addMember(jumpTodayLabel);
//        this.topLayout.addMember(jumpToForm);

//        this.topLayout.addMember(experts);

        // this.bottomLayout.addMember(this.getRefresh());

        // zip-export pdf
        this.exportZipPDF =
                new ImageButton("diagExportZipPDF", "../img/icons/folder_acrobat.png", I18N.sosClient
                        .exportZipPDF(), I18N.sosClient.exportZipPDFExtended());
        View.getInstance().registerTooltip(this.exportZipPDF);

        // zip-export CSV
        this.exportZipCSV =
                new ImageButton("diagExportZipCSV", "../img/icons/folder_csv.png", I18N.sosClient
                        .exportZipCSV(), I18N.sosClient.exportZipCSVExtended());
        View.getInstance().registerTooltip(this.exportZipCSV);

        // zip-export XLS
        this.exportZipXLS =
                new ImageButton("diagExportZipXLS", "../img/icons/folder_excel.png", I18N.sosClient
                        .exportZipXLS(), I18N.sosClient.exportZipXLSExtended());
        View.getInstance().registerTooltip(this.exportZipXLS);

        // zip-export XLS
        this.exportPDFallInOne =
                new ImageButton("diagExportPDFallIneOne", "../img/icons/page_white_acrobat_add.png",
                        I18N.sosClient.exportPDFallInOne(), I18N.sosClient.exportPDFallInOneExtended());
        View.getInstance().registerTooltip(this.exportPDFallInOne);

        LayoutSpacer s = new LayoutSpacer();
        s.setWidth("*");

        this.topLayout.addMember(s);

//        this.topLayout.addMember(this.exportZipCSV);
//        this.topLayout.addMember(this.exportZipXLS);
//        this.topLayout.addMember(this.exportZipPDF);
//        this.topLayout.addMember(this.exportPDFallInOne);

//        this.topLayout.addMember(link);

		ImageButton expandDiagramInteractionMenuButton = new ImageButton(
				"expander", "../img/icons/settings.png",
				I18N.sosClient.expandDiagramInteraction(),
				I18N.sosClient.expandDiagramInteractionTooltip());
		expandDiagramInteractionMenuButton.setAlign(Alignment.CENTER);
		expandDiagramInteractionMenuButton.setZIndex(1000001);
		// TODO extract size to css file!
		expandDiagramInteractionMenuButton.setWidth(32);
		expandDiagramInteractionMenuButton.setHeight(32);
        expandDiagramInteractionMenuButton.addStyleName("sensorweb_client_options_button");
		expandDiagramInteractionMenuButton.addStyleName("sensorweb_client_interactionbutton");
		expandDiagramInteractionMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (diagramInteractionMenu.isVisible()) {
					diagramInteractionMenu.animateHide(AnimationEffect.SLIDE);
				} else {
					diagramInteractionMenu.animateShow(AnimationEffect.SLIDE);
				}
			}
		});
		EESTab.layout.addChild(expandDiagramInteractionMenuButton);

        buttonLayout = new HLayout();
        buttonLayout.setStyleName("sensorweb_client_diagramInteractionMenu");
        buttonLayout.addMember(createJumpToTimeIntervalForm());
        buttonLayout.addMember(createGridToggleButton());
        buttonLayout.addMember(createAutoScaleButton());
        
        diagramInteractionMenu = new InteractionWindow(buttonLayout);
        diagramInteractionMenu.setZIndex(1000000);
        diagramInteractionMenu.setAutoHeight();
        diagramInteractionMenu.setAutoWidth();
        diagramInteractionMenu.setTop(34);
        diagramInteractionMenu.hide();
        EESTab.layout.addChild(diagramInteractionMenu);
        initEventHandler();
    }

	private Canvas createJumpToTimeIntervalForm() {
    	ComboBoxItem jumpToCombo = new ComboBoxItem();
        jumpToCombo.setTitle(I18N.sosClient.jumpTo());
    	jumpToCombo.setDefaultToFirstOption(true);
        jumpToCombo.setWrapTitle(false);
        jumpToCombo.setShowTitle(true);
        jumpToCombo.setWidth(100);

        String[] jumpToTypes = I18N.sosClient.jumpToTypes().split(",");
        LinkedHashMap<String, String> jumpToValues = new LinkedHashMap<String, String>();
        for (int i = 0; i < jumpToTypes.length; i += 2) {
            jumpToValues.put(jumpToTypes[i], jumpToTypes[i + 1]);
        }
        jumpToCombo.setValueMap(jumpToValues);
        jumpToCombo.addChangedHandler(new ChangedHandler() {
            public void onChanged(ChangedEvent event) {
                RecentTimeTerm type = RecentTimeTerm.valueOf((String) event.getValue());
                final Date now = new Date();
                long interval = 1000 * 60;
                // FIXME validierung mit overview
                switch (type) {
                case TODAY:
					long dayInMillis = 1000 * 60 * 60 * 24;
                	long lastmidnight = now.getTime() / dayInMillis * dayInMillis;
                	EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(lastmidnight, lastmidnight + (dayInMillis)));
					return;
                case LASTHOUR:
                    interval *= 60;
                    break;
                case LASTFIVEHOURS:
                    interval *= 60 * 5;
                    break;
                case LASTDAY:
                    interval *= 60 * 24;
                    break;
                case LASTMONTH:
                    interval *= 60 * 24 * 30;
                    break;
                case LASTWEEK:
                    interval *= 60 * 24 * 7;
                    break;
                default:
                    break;
                }
                final long start = now.getTime() - interval;
                final long end = now.getTime();

                EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(start,end));
            }
        });
        
    	DynamicForm jumpToForm = new DynamicForm();
    	jumpToForm.setStyleName("sensorweb_client_jumpToTimeIntervalForm");
        jumpToForm.setNumCols(2);
        jumpToForm.setFields(jumpToCombo);
        jumpToForm.setHeight("*");
        return jumpToForm;
	}

	private Canvas createGridToggleButton() {
		Layout layout = new Layout();
		layout.setStyleName("sensorweb_client_gridButtonLayout");
		gridShown = true;
		gridButton = new Label(I18N.sosClient.hideGrid());
		gridButton.setStyleName("sensorweb_client_gridButton");
		gridButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new SwitchGridEvent(), new EventCallback() {
                    public void onEventFired() {
                        EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
                        final boolean gridShown = DataControlsTimeSeries.this.gridShown;
                        if (gridShown) {
                        	String contents = "<html>" + I18N.sosClient.showGrid() + "</html>";
                            gridButton.setContents(contents);
                        } else {
                        	String contents = "<html>" + I18N.sosClient.hideGrid() + "</html>";
                            gridButton.setContents(contents);
                        }
                        DataControlsTimeSeries.this.gridShown = !gridShown;
                    }
                });
            }
        });
		gridButton.setWidth(80);
		gridButton.setWrap(false);
		layout.addMember(gridButton);
		return layout;
	}

	private Canvas createAutoScaleButton() {
		Layout layout = new Layout();
		layout.setStyleName("sensorweb_client_scaleButtonLayout");
		autoScaleButton = new Label(I18N.sosClient.resetScale());
		autoScaleButton.setStyleName("sensorweb_client_scaleButton");
	    autoScaleButton.addClickHandler(new ClickHandler() {
	        public void onClick(ClickEvent event) {
	            EventBus.getMainEventBus().fireEvent(new SwitchAutoscaleEvent(true), new EventCallback() {
	                public void onEventFired() {
	                    EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
	                }
	            });
	        }
	    });
	    autoScaleButton.setWidth(80);
	    autoScaleButton.setWrap(false);
	    return autoScaleButton;
	}

	
    private void initEventHandler() {

        this.getDiagBackDay().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagBackDay().setClicked();
                addToDates(-1);
            }
        });

        this.getDiagBackWeek().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagBackWeek().setClicked();
                addToDates(-7);
            }
        });

        this.getDiagBackMonth().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagBackMonth().setClicked();
                changeMonth(-1);
            }
        });

        this.getDiagBackYear().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagBackYear().setClicked();
                changeYear(-1);
            }
        });

        this.getDiagForwardDay().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagForwardDay().setClicked();
                addToDates(1);
            }
        });

        this.getDiagForwardWeek().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagForwardWeek().setClicked();
                addToDates(7);
            }
        });

        this.getDiagForwardMonth().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagForwardMonth().setClicked();
                changeMonth(1);
            }
        });

        this.getDiagForwardYear().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                DataControlsTimeSeries.this.getDiagForwardYear().setClicked();
                changeYear(1);
            }
        });

        this.getRefresh().addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {

                Date begin = (Date) DataControlsTimeSeries.this.getFromDateItem().getValue();
                Date end = (Date) DataControlsTimeSeries.this.getToDateItem().getValue();

                begin = createDate(begin, DataControlsTimeSeries.this.getFromTimeItem().getDisplayValue());
                end = createDate(end, DataControlsTimeSeries.this.getToTimeItem().getDisplayValue());

                if (datesAreValid(begin.getTime(), end.getTime())) {
                    DataControlsTimeSeries.this.getRefresh().setClicked();
                    DataControlsTimeSeries.this.getFromDateItem().blurItem();
                    DataControlsTimeSeries.this.getFromTimeItem().blurItem();
                    DataControlsTimeSeries.this.getToDateItem().blurItem();
                    DataControlsTimeSeries.this.getToTimeItem().blurItem();

                    long interval = end.getTime() - begin.getTime();
                    if (interval > DataControlsTimeSeries.this.currentInterval) {
                        Toaster.getInstance().addMessage(I18N.sosClient.errorOverviewInterval());
                        DataControlsTimeSeries.this.getRefresh().setNotClicked();
                        LoaderManager.getInstance().stopLoadingAnimations();
                        return;

                    }
                    EventBus.getMainEventBus().fireEvent(
                            new OverviewIntervalChangedEvent(DataControlsTimeSeries.this.currentInterval,
                                    DataControlsTimeSeries.this.currentIntervalType));

                    fireDateChangedEvent();
                } else {
                    DataControlsTimeSeries.this.getRefresh().setNotClicked();
                    LoaderManager.getInstance().stopLoadingAnimations();
                }
            }

        });

        this.exportZipPDF.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (!DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                    List<TimeSeries> series = new ArrayList<TimeSeries>();
                    series.addAll(DataStoreTimeSeriesImpl.getInst().getDataItems().values());
                    EventBus.getMainEventBus().fireEvent(new ExportEvent(series, ExportEvent.ExportType.PD_ZIP));
                }
            }

        });

        this.exportZipCSV.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (!DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                    List<TimeSeries> series = new ArrayList<TimeSeries>();
                    series.addAll(DataStoreTimeSeriesImpl.getInst().getDataItems().values());
                    EventBus.getMainEventBus().fireEvent(new ExportEvent(series, ExportEvent.ExportType.CSV_ZIP));
                }
            }

        });

        this.exportZipXLS.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (!DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                    List<TimeSeries> series = new ArrayList<TimeSeries>();
                    series.addAll(DataStoreTimeSeriesImpl.getInst().getDataItems().values());
                    EventBus.getMainEventBus().fireEvent(new ExportEvent(series, ExportEvent.ExportType.XLS_ZIP));
                }
            }

        });

        this.exportPDFallInOne.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (!DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                    List<TimeSeries> series = new ArrayList<TimeSeries>();
                    series.addAll(DataStoreTimeSeriesImpl.getInst().getDataItems().values());
                    EventBus.getMainEventBus().fireEvent(new ExportEvent(series, ExportEvent.ExportType.PDF_ALL_IN_ONE));
                }
            }
        });
    }

    /**
     * @return boolean
     * 
     *         Validates start and end dates for illegal values and puts the
     *         action on the stack
     */
    boolean datesAreValid(long begin, long end) {

        // check for date crossing
        if (begin > end) {
            // violated cross dates
            Toaster.getInstance().addErrorMessage(I18N.sosClient.errorCrossDates());
            resetDatePicker();
            return false;
        }

        // check for bigger interval than 5min
        if ((end - begin) < 5000) {
            Toaster.getInstance().addErrorMessage(I18N.sosClient.errorMinimalInterval());
            resetDatePicker();
            return false;
        }

        return true;
    }

    Date createDate(Date date, String time) {
        if (time.length() == 5) {
            date.setHours(new Integer(time.substring(0, 2)));
            date.setMinutes(new Integer(time.substring(3, 5)));
        } else if (time.length() == 4) {
            date.setHours(new Integer(time.substring(0, 1)));
            date.setMinutes(new Integer(time.substring(2, 4)));
        }
        return date;
    }

    void changeYear(int i) {

        Date begin = (Date) this.fromDateItem.getValue();
        Date end = (Date) this.toDateItem.getValue();

        begin.setYear(begin.getYear() + i);
        end.setYear(end.getYear() + i);

        if (datesAreValid(begin.getTime(), end.getTime())) {
            this.fromDateItem.setValue(begin);
            this.toDateItem.setValue(end);
            fireDateChangedEvent();
        } else {
            resetDatePicker();
        }

    }

    protected void resetDatePicker() {
        this.fromDateItem.setValue(new Date(TimeManager.getInst().getBegin()));
        this.toDateItem.setValue(new Date(TimeManager.getInst().getEnd()));

        String beg = DateTimeFormat.getFormat("HH:mm").format(new Date(TimeManager.getInst().getBegin()));
        String en = DateTimeFormat.getFormat("HH:mm").format(new Date(TimeManager.getInst().getEnd()));

        this.fromTimeItem.setValue(beg);
        this.toTimeItem.setValue(en);
    }

    void changeMonth(int i) {

        Date begin = (Date) this.fromDateItem.getValue();
        Date end = (Date) this.toDateItem.getValue();

        int bMonth = begin.getMonth();
        int eMonth = end.getMonth();

        if (bMonth + i < 0) {
            bMonth = 11;
            begin.setYear(begin.getYear() - 1);
            begin.setMonth(bMonth);
        } else if (bMonth + i > 11) {
            bMonth = 0;
            begin.setYear(begin.getYear() + 1);
            begin.setMonth(bMonth);
        } else {
            bMonth += i;
            begin.setMonth(bMonth);
        }

        if (eMonth + i < 0) {
            eMonth = 11;
            end.setYear(end.getYear() - 1);
            end.setMonth(eMonth);
        } else if (eMonth + i > 11) {
            eMonth = 0;
            end.setYear(end.getYear() + 1);
            end.setMonth(eMonth);
        } else {
            eMonth += i;
            end.setMonth(eMonth);
        }

        if (datesAreValid(begin.getTime(), end.getTime())) {
            this.fromDateItem.setValue(begin);
            this.toDateItem.setValue(end);
            fireDateChangedEvent();

        } else {
            resetDatePicker();
        }

    }

    void addToDates(int i) {
        try {

            Date begin = (Date) this.fromDateItem.getValue();
            Date end = (Date) this.toDateItem.getValue();

            long beginMillis = begin.getTime();
            long endMillis = end.getTime();

            beginMillis += (i * 24 * 60 * 60 * 1000);
            endMillis += (i * 24 * 60 * 60 * 1000);

            begin.setTime(beginMillis);
            end.setTime(endMillis);

            if (datesAreValid(begin.getTime(), end.getTime())) {
                this.fromDateItem.setValue(begin);
                this.toDateItem.setValue(end);
                fireDateChangedEvent();
            } else {
                resetDatePicker();
            }
        } catch (Exception e) {
            GWT.log("time exception", e);
        }

    }

    public HLayout getBootomLayout() {
        return this.bottomLayout;
    }

    public HLayout getTopLayout() {
        return this.topLayout;
    }

    /**
     * Fires the dateChangedEvent in the EventManager with an update.
     */
    void fireDateChangedEvent() {

        Date begin = (Date) this.fromDateItem.getValue();
        Date end = (Date) this.toDateItem.getValue();

        String timefrom = this.fromTimeItem.getDisplayValue();
        String timeto = this.toTimeItem.getDisplayValue();

        begin = createDate(begin, timefrom);
        end = createDate(end, timeto);

        EventBus.getMainEventBus().fireEvent(new DatesChangedEvent(begin.getTime(), end.getTime()));

    }

    public DateItem getFromDateItem() {
        return this.fromDateItem;
    }

    public DateItem getToDateItem() {
        return this.toDateItem;
    }

    public TimeItem getFromTimeItem() {
        return this.fromTimeItem;
    }

    public TimeItem getToTimeItem() {
        return this.toTimeItem;
    }

    public String getId() {
        return this.elemID;
    }

    @Override
    public Canvas getControls() {
        return this;
    }

    private class DataControlsTimeServiesEventBroker implements DatesChangedEventHandler, StoreTimeSeriesFirstValueEventHandler,
            StoreTimeSeriesLastValueEventHandler, OverviewIntervalChangedEventHandler {

        public DataControlsTimeServiesEventBroker() {
            EventBus.getMainEventBus().addHandler(DatesChangedEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(FirstValueOfTimeSeriesEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(StoreTimeSeriesLastValueEvent.TYPE, this);
            EventBus.getMainEventBus().addHandler(OverviewIntervalChangedEvent.TYPE, this);
        }

        public void onDatesChanged(DatesChangedEvent evt) {
            DataControlsTimeSeries.this.getFromDateItem().setValue(new Date(evt.getStart()));
            DataControlsTimeSeries.this.getToDateItem().setValue(new Date(evt.getEnd()));
            DataControlsTimeSeries.this.getFromTimeItem().setValue(new Date(evt.getStart()));
            DataControlsTimeSeries.this.getToTimeItem().setValue(new Date(evt.getEnd()));
        }

        public void onStore(StoreTimeSeriesLastValueEvent evt) {

            if (evt.getDate() > DataControlsTimeSeries.this.getCurrentLast()) {
                DataControlsTimeSeries.this.setCurrentLast(evt.getDate());
            }

        }

        public void onStore(FirstValueOfTimeSeriesEvent evt) {

            if (evt.getDate() < DataControlsTimeSeries.this.getCurrentFirst()) {
                DataControlsTimeSeries.this.setCurrentFirst(evt.getDate());
            }

        }

        public void onChanged(OverviewIntervalChangedEvent evt) {

            DataControlsTimeSeries.this.currentInterval = evt.getInterval();
            DataControlsTimeSeries.this.currentIntervalType = evt.getType();

            switch (DataControlsTimeSeries.this.currentIntervalType) {
            case HOUR:
                DataControlsTimeSeries.this.overviewInterval.setValue(evt.getInterval() / 60 / 60 / 1000);
                break;
            case DAY:
                DataControlsTimeSeries.this.overviewInterval.setValue(evt.getInterval() / 24 / 60 / 60 / 1000);
                break;
            case MONTH:
                DataControlsTimeSeries.this.overviewInterval.setValue(evt.getInterval() / 30 / 24 / 60 / 60 / 1000);
                break;
            case YEAR:
                DataControlsTimeSeries.this.overviewInterval.setValue(evt.getInterval() / 365 / 24 / 60 / 60 / 1000);
                break;
            default:
                DataControlsTimeSeries.this.overviewInterval.setValue(evt.getInterval() / 24 / 60 / 60 / 1000);
                break;
            }

            DataControlsTimeSeries.this.overviewIntervalType.setValue(evt.getType().toString());

        }

    }

    @Override
    public int getControlWidth() {
        return this.getWidth();
    }

    @Override
    public int getControlHeight() {
        return this.getHeight();
    }

    public void setDiagForwardYear(ImageButton diagForwardYear) {
        this.diagForwardYear = diagForwardYear;
    }

    public ImageButton getDiagForwardYear() {
        return this.diagForwardYear;
    }

    public void setDiagForwardMonth(ImageButton diagForwardMonth) {
        this.diagForwardMonth = diagForwardMonth;
    }

    public ImageButton getDiagForwardMonth() {
        return this.diagForwardMonth;
    }

    public void setDiagForwardWeek(ImageButton diagForwardWeek) {
        this.diagForwardWeek = diagForwardWeek;
    }

    public ImageButton getDiagForwardWeek() {
        return this.diagForwardWeek;
    }

    public void setDiagForwardDay(ImageButton diagForwardDay) {
        this.diagForwardDay = diagForwardDay;
    }

    public ImageButton getDiagForwardDay() {
        return this.diagForwardDay;
    }

    public void setDiagBackYear(ImageButton diagBackYear) {
        this.diagBackYear = diagBackYear;
    }

    public ImageButton getDiagBackYear() {
        return this.diagBackYear;
    }

    public void setDiagBackMonth(ImageButton diagBackMonth) {
        this.diagBackMonth = diagBackMonth;
    }

    public ImageButton getDiagBackMonth() {
        return this.diagBackMonth;
    }

    public void setDiagBackWeek(ImageButton diagBackWeek) {
        this.diagBackWeek = diagBackWeek;
    }

    public ImageButton getDiagBackWeek() {
        return this.diagBackWeek;
    }

    public void setDiagBackDay(ImageButton diagBackDay) {
        this.diagBackDay = diagBackDay;
    }

    public ImageButton getDiagBackDay() {
        return this.diagBackDay;
    }

    public void setRefresh(ImageButton refresh) {
        this.refresh = refresh;
    }

    public ImageButton getRefresh() {
        return this.refresh;
    }

	public HLayout getButtonLayout() {
		return buttonLayout;
	}
	
}