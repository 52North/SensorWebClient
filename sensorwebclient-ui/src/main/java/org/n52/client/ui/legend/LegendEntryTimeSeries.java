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

import static org.n52.client.ctrl.PropertiesManager.getPropertiesManager;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.eesgmbh.gimv.client.event.LoadImageDataEvent;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.PropertiesManager;
import org.n52.client.ctrl.TimeManager;
import org.n52.client.ses.ui.LoginWindow;
import org.n52.client.ses.ui.subscribe.EventSubscriptionWindow;
import org.n52.client.sos.ctrl.SOSController;
import org.n52.client.sos.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.event.ChangeTimeSeriesStyleEvent;
import org.n52.client.sos.event.DatesChangedEvent;
import org.n52.client.sos.event.LegendElementSelectedEvent;
import org.n52.client.sos.event.TimeSeriesChangedEvent;
import org.n52.client.sos.event.UpdateScaleEvent;
import org.n52.client.sos.event.data.DeleteTimeSeriesEvent;
import org.n52.client.sos.event.data.ExportEvent;
import org.n52.client.sos.event.data.FirstValueOfTimeSeriesEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesLastValueEvent;
import org.n52.client.sos.event.data.StoreTimeSeriesPropsEvent;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;
import org.n52.client.sos.event.data.TimeSeriesHasDataEvent;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesFirstValueEventHandler;
import org.n52.client.sos.event.data.handler.StoreTimeSeriesLastValueEventHandler;
import org.n52.client.sos.event.data.handler.SwitchAutoscaleEventHandler;
import org.n52.client.sos.event.data.handler.TimeSeriesHasDataEventHandler;
import org.n52.client.sos.event.handler.LegendElementSelectedEventHandler;
import org.n52.client.sos.event.handler.TimeSeriesChangedEventHandler;
import org.n52.client.sos.event.handler.UpdateScaleEventHandler;
import org.n52.client.sos.legend.TimeSeries;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.View;
import org.n52.client.ui.btn.ImageButton;
import org.n52.client.ui.btn.SmallButton;
import org.n52.client.util.ClientUtils;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.DragStopEvent;
import com.smartgwt.client.widgets.events.DragStopHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.ColorPickerItem;
import com.smartgwt.client.widgets.form.fields.ComboBoxItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SliderItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class LegendEntryTimeSeries extends Layout implements LegendElement {

	private static final String LINE_STYLE_LINE_DOTS = "5";

	private static final String LINE_STYLE_DASHED = "4";

	private static final String LINE_STYLE_DOTS = "3";

	private static final String LINE_STYLE_AREA = "2";

	private static final String LINE_STYLE_LINE = "1";

	protected String timeseriesID;

	protected HLayout legendEntryHead;

	private VLayout legendEntry;

	private SmallButton titleCol;

	private Label titleLabel;

	private String width;

	private String height;

	protected boolean isSelected = false;

	protected Img noDataSign;

	protected Img loadingSpinner;

	private VLayout legendEntryFoot;

	private VLayout legendInfo;

	private Label phenonmenonLabel;

	protected Label firstValueInterval;

	protected Label lastValueInterval;

	private Label stationLabel;

	private VLayout refvalLayout;

	protected SliderItem slider;

	protected ComboBoxItem seriesType;

	protected ComboBoxItem lineStyles;
	
	protected ComboBoxItem lineWidth;

	protected RadioGroupItem scale;

	private DynamicForm cpForm;

	protected ColorPickerItem colors;

	protected Window styleChanger;

	private LegendEntryTimeSeriesEventBroker eventBroker;

	private SmallButton sesComButton;

	private SmallButton infoButton;
	
	private Window informationWindow;

	private SmallButton deleteButton;

	private boolean didUpdateRefVals = false;

	protected boolean wasDragged = false;

	private DateTimeFormat formatter = DateTimeFormat.getFormat("dd.MM.yyyy HH:mm");

    private LoginWindow subsriptionWindow;

	public LegendEntryTimeSeries(TimeSeries ts, String width, String height) {
		this.width = width;
		this.height = height;
		this.timeseriesID = ts.getId();
		this.eventBroker = new LegendEntryTimeSeriesEventBroker();

		init();
	}

	public LegendEntryTimeSeriesEventBroker getEventBroker() {
		return this.eventBroker;
	}

	@Override
	protected void onDraw() {
		super.onDraw();
		if (this.wasDragged) {

			// members are updated earlier than children!!
			Canvas[] members = View.getView().getLegend().getLegendStack()
					.getMembers();
			for (int entryIndex = 0; entryIndex < members.length; entryIndex++) {
				Canvas canvas = members[entryIndex];
				if (canvas instanceof LegendElement) {
					LegendElement element = (LegendElement) canvas;
					element.setOrdering(entryIndex);
					DataStoreTimeSeriesImpl dataStore = DataStoreTimeSeriesImpl
							.getInst();
					dataStore.getDataItem(element.getElemId()).setOrdering(
							entryIndex);
				}
			}
			this.wasDragged = false;
			EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
		}
	}

	private void init() {

		setAutoHeight();
		setStyleName("n52_sensorweb_client_legendEntryLayout");
		setCanDrag(true);
		setCanDrop(true);
		setKeepInParentRect(true);
		setWidth("100%");

		addDragStopHandler(new DragStopHandler() {
			public void onDragStop(DragStopEvent event) {
				LegendEntryTimeSeries.this.wasDragged = true;
			}
		});

		this.legendEntry = new VLayout();
		this.legendEntry.setMinWidth(220);
		this.legendEntry.setWidth("100%");

		// legend head
		this.legendEntryHead = new HLayout();
		this.legendEntryHead.setTabIndex(-1);
		this.legendEntryHead.setStyleName("n52_sensorweb_client_legendEntryHeaderNoData");
		this.legendEntryHead.setCursor(Cursor.POINTER);
		this.legendEntryHead.setHeight(this.height);

		this.noDataSign = new Img("../img/icons/exclamation.png");
		this.noDataSign.setTooltip(i18n.noDataAvailable());
		this.noDataSign.setWidth(16);
		this.noDataSign.setHeight(16);
		this.noDataSign.setPadding(6);

		this.loadingSpinner = new Img("../img/loader.gif");
		this.loadingSpinner.setWidth(16);
		this.loadingSpinner.setHeight(16);
		this.loadingSpinner.setPadding(6);

		this.titleLabel = new Label();
		this.titleLabel.setWidth100();
		this.titleLabel.setStyleName("n52_sensorweb_client_legendEntryTitle");
		this.titleLabel.setOverflow(Overflow.HIDDEN);
		this.titleLabel.setCursor(Cursor.POINTER);
		this.titleLabel.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (isFooterVisible()) {
					hideFooter();
				} else {
					showFooter();
				}
				EventBus.getMainEventBus().fireEvent(
						new LegendElementSelectedEvent(
								LegendEntryTimeSeries.this, false));
			}
		});
		
		this.legendEntryHead.addMember(this.loadingSpinner);
		this.legendEntryHead.addMember(this.noDataSign);
		this.legendEntryHead.addMember(this.titleLabel);
		this.legendEntryHead.addMember(createLegendTools());

		// legend foot
		this.legendEntryFoot = new VLayout();
		this.legendEntryFoot.setStyleName("n52_sensorweb_client_legendEntryFooter");
		HLayout hLegendInfos = new HLayout();
		
		HStack separator = new HStack();
		separator.setWidth(10);
		hLegendInfos.addMember(separator);
		hLegendInfos.addMember(createLegendInfo());
		this.legendEntryFoot.addMember(hLegendInfos);
		this.legendEntryFoot.addMember(createRefValueLayout());
		
		createStyleToolsWindow();

		this.legendEntryFoot.hide();

		this.legendEntry.addMember(this.legendEntryHead);
		this.legendEntry.addMember(this.legendEntryFoot);
		addMember(this.legendEntry);
	}

	private Canvas createLegendInfo() {

		this.legendInfo = new VLayout();
		this.legendInfo.setTabIndex(-1);
		// this.offeringLabel = new Label();
		// this.procedureLabel = new Label();
		this.phenonmenonLabel = new Label();
		this.stationLabel = new Label();
		this.firstValueInterval = new Label();
		this.lastValueInterval = new Label();
		
		this.phenonmenonLabel.setStyleName("n52_sensorweb_client_legendInfoRow");
		this.stationLabel.setStyleName("n52_sensorweb_client_legendInfoRow");

		// this.offeringLabel.setHeight(15);
		// this.procedureLabel.setHeight(15);
		this.phenonmenonLabel.setAutoHeight();
		this.stationLabel.setAutoHeight();

		// this.legendInfo.addMember(this.offeringLabel);
		// this.legendInfo.addMember(this.procedureLabel);
		this.legendInfo.addMember(this.phenonmenonLabel);
		this.legendInfo.addMember(this.stationLabel);
		this.legendInfo.addMember(createValueIntervalLabel());
		
		return this.legendInfo;
	}

	private Canvas createValueIntervalLabel() {
		Layout interval = new HLayout();
		interval.setAutoWidth();
		interval.setStyleName("n52_sensorweb_client_legendInfoRow");
		this.firstValueInterval.setAutoWidth();
		this.firstValueInterval.setWrap(false);
		this.firstValueInterval.setStyleName("n52_sensorweb_client_legendlink");
		this.lastValueInterval.setAutoWidth();
		this.lastValueInterval.setWrap(false);
		this.lastValueInterval.setStyleName("n52_sensorweb_client_legendlink");
		Label separator = new Label(i18n.to());
		separator.setAlign(Alignment.CENTER);
		separator.setWidth(20);
		interval.addMember(this.firstValueInterval);
		interval.addMember(separator);
		interval.addMember(this.lastValueInterval);
		return interval;
	}

	private Canvas createRefValueLayout() {
		this.refvalLayout = new VLayout();
		this.refvalLayout.setTabIndex(-1);
		this.refvalLayout.setHeight("*");
		this.refvalLayout.setOverflow(Overflow.VISIBLE);
		this.refvalLayout.setStyleName("n52_sensorweb_client_refValLayout");

		return this.refvalLayout;
	}

	protected TimeSeries getTimeSeries() {
		return ((TimeSeries) getDataWrapper());
	}

	private Canvas createLegendTools() {
		HLayout tools = new HLayout();
		
		createColorChangeButton();
		tools.addMember(this.titleCol);
		
		if (ClientUtils.isSesEnabled()) {
			createSesCommunicatorButton();
			tools.addMember(this.sesComButton);
		}
		
		createInformationButton();
		tools.addMember(this.infoButton);
		
		createDeleteLegendEntryButton();
		tools.addMember(this.deleteButton);
		
		return tools;
	}

	private ImageButton createJumpToLastValueButton() {
		final ImageButton jumpLast = new ImageButton("jumpLast",
				"../img/icons/control_end_blue.png",
				i18n.jumpToLast(),
				i18n.jumpToLastExtended());
		View.getView().registerTooltip(jumpLast);
		jumpLast.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				long date = LegendEntryTimeSeries.this.getTimeSeries()
						.getLastValueDate();
				if (date == 0) {
					Toaster.getToasterInstance().addMessage(
							i18n.errorSOS() + ": "
									+ i18n.jumpToLast());
					return;
				}
				long interval = TimeManager.getInst().getEnd() - date;

				long begin = TimeManager.getInst().getBegin() - interval;

				EventBus.getMainEventBus().fireEvent(
						new DatesChangedEvent(begin, date));

			}
		});
		return jumpLast;
	}

	private ImageButton createJumpToFirstValueButton() {
		ImageButton jumpFirst = new ImageButton("jumpFirst",
				"../img/icons/control_start_blue.png",
				i18n.jumpToFirst(),
				i18n.jumpToFirstExtended());
		View.getView().registerTooltip(jumpFirst);
		jumpFirst.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				long date = LegendEntryTimeSeries.this.getTimeSeries()
						.getFirstValueDate();

				if (date == 0) {
					Toaster.getToasterInstance().addMessage(
							i18n.errorSOS() + ": "
									+ i18n.jumpToFirst());
					return;
				}

				long interval = TimeManager.getInst().getBegin() - date;

				long end = TimeManager.getInst().getEnd() - interval;

				EventBus.getMainEventBus().fireEvent(
						new DatesChangedEvent(date, end));

			}
		});
		return jumpFirst;
	}

	private ImageButton createPDFExportButton() {
		ImageButton exportPDF = new ImageButton("exportPDF",
				"../img/icons/page_white_acrobat.png",
				i18n.exportPDF(), i18n.exportPDFExtended());
		View.getView().registerTooltip(exportPDF);
		exportPDF.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				ArrayList<TimeSeries> series = new ArrayList<TimeSeries>();
				series.add(getTimeSeries());
				EventBus.getMainEventBus().fireEvent(
						new ExportEvent(series, ExportEvent.ExportType.PDF));

			}
		});
		return exportPDF;
	}

	private ImageButton createXLSExportButton() {
		ImageButton exportXLS = new ImageButton(
				"exportXLS", "../img/icons/page_white_excel.png", //$NON-NLS-2$
				i18n.exportXLS(), i18n.exportXLSExtended());
		View.getView().registerTooltip(exportXLS);
		exportXLS.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				List<TimeSeries> series = new ArrayList<TimeSeries>();
				series.add(getTimeSeries());
				EventBus.getMainEventBus().fireEvent(
						new ExportEvent(series, ExportEvent.ExportType.XLS));

			}
		});
		return exportXLS;
	}

	private ImageButton createCSVExportButton() {
		ImageButton exportCSV = new ImageButton(
				"exportCSV", "../img/icons/table.png", //$NON-NLS-2$
				i18n.exportCSV(), i18n.exportCSVExtended());
		View.getView().registerTooltip(exportCSV);
		exportCSV.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				List<TimeSeries> series = new ArrayList<TimeSeries>();
				series.add(getTimeSeries());
				EventBus.getMainEventBus().fireEvent(
						new ExportEvent(series, ExportEvent.ExportType.CSV));

			}
		});
		return exportCSV;
	}

	private void createDeleteLegendEntryButton() {
		this.deleteButton = new SmallButton(new Img("../img/icons/del.png"),
				i18n.delete(), i18n.deleteExtended());
		this.deleteButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent evt) {
				if (SOSController.isDeletingTS) {
					Toaster.getToasterInstance().addMessage(i18n.deleteTimeSeriesActiv());
				} else {
					SOSController.isDeletingTS = true;
					LegendEntryTimeSeries.this.getEventBroker().unregister();
					EventBus.getMainEventBus().fireEvent(
							new DeleteTimeSeriesEvent(LegendEntryTimeSeries.this
									.getElemId()));	
				}
			}
		});
	}

	private void createInformationButton() {
		this.infoButton = new SmallButton(new Img("../img/icons/info.png"),
				i18n.infos(), i18n.infoExtended());
		this.infoButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (informationWindow == null) {
					createInformationWindow();
				}
				informationWindow.show();
			}
		});
	}
	
	private void createInformationWindow() {
		informationWindow = new Window();
		informationWindow.setTitle(LegendEntryTimeSeries.this.getTimeSeries().getTimeSeriesLabel());
		informationWindow.setWidth(450);
		informationWindow.setHeight(500);
		informationWindow.setShowMinimizeButton(false);
		informationWindow.centerInPage();
		HTMLPane htmlPane = new HTMLPane();
		htmlPane.setContentsURL(LegendEntryTimeSeries.this
				.getTimeSeries().getMetadataUrl()); 
		htmlPane.setContentsType(ContentsType.PAGE);
		informationWindow.addItem(htmlPane);
	}

	private void createSesCommunicatorButton() {
		this.sesComButton = new SmallButton(
				new Img("../img/icons/event.png"), i18n.sesCommunicatorButton(), i18n.sesCommunicatorButtonExtend());
		this.sesComButton.hide();
		this.sesComButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			    TimeSeries dataItem = DataStoreTimeSeriesImpl.getInst().getDataItem(timeseriesID);
			    LegendEntryTimeSeries.this.showSubscriptionWindow(dataItem);
			}
		});
	}

	private void showSubscriptionWindow(TimeSeries dataItem) {
	    if (subsriptionWindow == null) {
            subsriptionWindow = new EventSubscriptionWindow(dataItem);
        }
        subsriptionWindow.show();
    }

    private void createColorChangeButton() {
		this.titleCol = new SmallButton(new Label(), i18n.changeColor(),
				i18n.changeColorExtended());
		this.titleCol.hide();
		this.titleCol.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LegendEntryTimeSeries.this.styleChanger.show();
			}
		});
	}

	public void update() {

		TimeSeries dw = (TimeSeries) getDataWrapper();
		
		String phenomenon = this.getTimeSeries().getProperties()
				.getPhenomenon().getLabel();

		this.titleLabel.setContents("<span>" + phenomenon + "@"
				+ getStationName(dw) + "</span>");
		this.titleCol.getCanvas().setBackgroundColor(dw.getColor());
		this.styleChanger.setTitle(dw.getPhenomenonId() + "@" + getStationName(dw));
		if (dw.hasData()) {
			this.noDataSign.hide();
			if (this.isSelected) {
				this.legendEntryHead.setStyleName("n52_sensorweb_client_legendEntryHeaderSelected");
			} else {
				this.legendEntryHead.setStyleName("n52_sensorweb_client_legendEntryHeader");
			}
		} else {
			this.noDataSign.show();
			if (this.isSelected) {
				this.legendEntryHead
						.setStyleName("n52_sensorweb_client_legendEntryHeaderSelectedNoData");
			} else {
				this.legendEntryHead.setStyleName("n52_sensorweb_client_legendEntryHeaderNoData");
			}

		}

		this.seriesType.setValue(getTimeSeries().getGraphStyle());
		this.lineStyles.setValue(getTimeSeries().getLineStyle());
		this.lineWidth.setValue(getTimeSeries().getLineWidth());
		if (getTimeSeries().isAutoScale()) {
			this.scale.setDefaultValue(i18n.autoScale());
		} else {
			this.scale.setDefaultValue(i18n.zeroScale());
		}
		this.slider.setValue(getTimeSeries().getOpacity());
		this.colors.setValue(getTimeSeries().getColor());

		// String offering =
		// this.getTimeSeries().getProperties().getOffering().getTitle();
		// comment out, for eventually later use
		// if (offering.contains("/")) {
		// offering = offering.substring(offering.lastIndexOf("/") + 1);
		// }
		// this.offeringLabel.setContents("<span style='font-weight:bold;'>" +
		// I18N.sosClient.offeringLabel()
		// + "</span>: " + offering);

		// String procedure =
		// this.getTimeSeries().getProperties().getProcedure().getDescription();
		// if (procedure.contains("/")) {
		// procedure = procedure.substring(procedure.lastIndexOf("/") + 1);
		// }
		// this.procedureLabel.setContents("<span style='font-weight:bold;'>" +
		// I18N.sosClient.procedureLabel()
		// + "</span>: " + procedure);

		String uom = this.getTimeSeries().getProperties().getUnitOfMeasure();
		// if (phenomenon.contains("/")) {
		// phenomenon = phenomenon.substring(phenomenon.lastIndexOf("/") + 1);
		// }
		StringBuilder phenomenonHtmlContent = new StringBuilder();
		phenomenonHtmlContent.append("<span>");
		phenomenonHtmlContent.append(i18n.phenomenonLabel());
		phenomenonHtmlContent.append(":</span> ");
		phenomenonHtmlContent.append(phenomenon);
		if (uom != null && !uom.isEmpty()) {
		    phenomenonHtmlContent.append(" [");
		    phenomenonHtmlContent.append(uom);
		    phenomenonHtmlContent.append("]");
        }
        this.phenonmenonLabel.setContents(phenomenonHtmlContent.toString());
		setFirstValueInterval();
		setLastValueInterval();

		// if (station.contains("/")) {
		// station = station.substring(station.lastIndexOf("/") + 1);
		// }
		this.stationLabel.setContents("<span>"
				+ i18n.foiLabel() + ":</span> "
				+ getStationName(this.getTimeSeries()));

		Set<String> values = this.getTimeSeries().getProperties()
				.getReferenceValues();

		if (!this.didUpdateRefVals) {
			if (values.size() != 0) {
				for (final String value : values) {
					DynamicForm refValForm = new DynamicForm();
					refValForm.setWidth(15);
					HLayout refValRow = new HLayout();
					refValRow.setTabIndex(-1);

					final CheckboxItem check = new CheckboxItem();
					// check.setTitle("");
					check.setShowTitle(false);
					check.setWidth(15);
					// check.setTitleStyle("width=80px;");
					// check.setTitleOrientation(TitleOrientation.RIGHT);
					check.setShowLabel(false);
					check.setLabelAsTitle(false);
					check.setValue(this.getTimeSeries().getProperties()
							.getRefValue(value).show());

					check.addChangedHandler(new ChangedHandler() {

						public void onChanged(ChangedEvent event) {
							getTimeSeries().getProperties().getRefValue(value)
									.setShow(check.getValueAsBoolean());

							EventBus.getMainEventBus().fireEvent(
									new StoreTimeSeriesPropsEvent(
											getTimeSeries().getId(),
											getTimeSeries().getProperties()));
							EventBus.getMainEventBus().fireEvent(
									new LoadImageDataEvent());
						}
					});

					refValForm.setFields(check);

					Label label = new Label(value
							+ ": "
							+ this.getTimeSeries().getProperties()
									.getRefValue(value).getValue());
					label.setAutoHeight();
					label.setWidth100();
					label.setMargin(5);

					Layout colorLayout = new Layout();
					colorLayout.setHeight(20);
					colorLayout.setWidth(20);
					String color = getTimeSeries().getProperties().getRefValue(value).getColor();
					colorLayout.setStyleName("n52_sensorweb_client_legendEntryTitleColorIndicatorSmall");
					colorLayout.setBackgroundColor(color);

					refValRow.addMember(refValForm);
					refValRow.addMember(label);
					refValRow.addMember(colorLayout);

					this.refvalLayout.addMember(refValRow);
					this.didUpdateRefVals = true;
				}
			} else {
				this.refvalLayout.hide();
			}

		}
		this.loadingSpinner.hide();
	}

	private String getStationName(TimeSeries ts) {
		// TODO perhaps use regular expressions
		String station = ts.getStationName();
		// remove phenomenon identifier
		station = station.replace(ts.getPhenomenonId(), "");
		// remove first '-'
		if (station.startsWith("-")) {
			station = station.replaceFirst("-", "");
		}
		// replace '_' with ' '
		station = station.replace("_", " ");
		return station;
	}

	protected void setFirstValueInterval() {
		if (this.getTimeSeries().getFirstValueDate() != 0) {
			this.firstValueInterval.setContents(this.formatter.format(new Date(
					this.getTimeSeries().getFirstValueDate()))
					+ " ("
					+ this.getTimeSeries().getFirstValue()
					+ " "
					+ this.getTimeSeries().getUnitOfMeasure() + ")");
			this.firstValueInterval.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					long date = LegendEntryTimeSeries.this.getTimeSeries()
							.getFirstValueDate();
					if (date == 0) {
						Toaster.getToasterInstance().addMessage(
								i18n.errorSOS() + ": "
										+ i18n.jumpToFirst());
						return;
					}
					long interval = TimeManager.getInst().getBegin() - date;
					long end = TimeManager.getInst().getEnd() - interval;
					EventBus.getMainEventBus().fireEvent(
							new DatesChangedEvent(date, end));
				}
			});
		} else {
			this.firstValueInterval.setContents(i18n.noData());
		}
	}

	protected void setLastValueInterval() {
		if (this.getTimeSeries().getLastValueDate() != 0) {
			this.lastValueInterval.setContents(this.formatter.format(new Date(
					this.getTimeSeries().getLastValueDate()))
					+ " ("
					+ this.getTimeSeries().getLastValue()
					+ " "
					+ this.getTimeSeries().getUnitOfMeasure() + ")");
			this.lastValueInterval.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					long date = LegendEntryTimeSeries.this.getTimeSeries()
							.getLastValueDate();
					if (date == 0) {
						Toaster.getToasterInstance().addMessage(
								i18n.errorSOS() + ": "
										+ i18n.jumpToLast());
						return;
					}
					long interval = TimeManager.getInst().getEnd() - date;
					long begin = TimeManager.getInst().getBegin() - interval;
					EventBus.getMainEventBus().fireEvent(
							new DatesChangedEvent(begin, date));
				}
			});
		} else {
			this.lastValueInterval.setContents(i18n.noData());
		}
	}

	private Canvas createStyleToolsWindow() {
		this.styleChanger = new Window();
		this.styleChanger.setShowModalMask(true);
		this.styleChanger.setWidth(250);
		this.styleChanger.setHeight(280);
		this.styleChanger.setIsModal(true);
		this.styleChanger.centerInPage();
		this.styleChanger.setCanDragResize(true);
		this.styleChanger.setShowCloseButton(true);
		
		this.setCanDrag(true);
		// opacity-slider
		this.slider = new SliderItem();
		this.slider.setTitle(i18n.Opacity());
		this.slider.setWidth(120);
		this.slider.setHeight(30);
		this.slider.setMinValue(0f);
		this.slider.setMaxValue(100f);
		this.slider.setValue(this.slider.getMaxValue());

		this.seriesType = new ComboBoxItem();
		String levelLine = i18n.levelLine();
		String sumLine = i18n.sumLine();
		LinkedHashMap<String, String> levelstyle = new LinkedHashMap<String, String>();
		levelstyle.put(TimeSeries.GRAPH_STYLE_GAUGELINE, levelLine);
		levelstyle.put(TimeSeries.GRAPH_STYLE_SUMLINE, sumLine);
		this.seriesType.setValueMap(levelstyle);
		this.seriesType.setWidth(85);
		this.seriesType.setTitle(i18n.seriesType());

		this.seriesType.addChangedHandler(new ChangedHandler() {

			public void onChanged(ChangedEvent event) {
				PropertiesManager propertiesManager = getPropertiesManager();
				String selectedSeriesType = LegendEntryTimeSeries.this.seriesType
						.getValue().toString();

				if (selectedSeriesType.equals(TimeSeries.GRAPH_STYLE_GAUGELINE)) {
					String defaultHydrographStyle = propertiesManager
							.getParameterAsString("defaultHydrographStyle");
					LegendEntryTimeSeries.this.lineStyles
							.setValue(defaultHydrographStyle);
				} else if (selectedSeriesType
						.equals(TimeSeries.GRAPH_STYLE_SUMLINE)) {
					String defaultSumlineStyle = propertiesManager
							.getParameterAsString("defaultSumLineStyle");
					LegendEntryTimeSeries.this.lineStyles
							.setValue(defaultSumlineStyle);
				}

			}
		});

		LinkedHashMap<String, String> styles = new LinkedHashMap<String, String>();
		styles.put(LINE_STYLE_LINE, i18n.lineStyle());
		styles.put(LINE_STYLE_DOTS, i18n.dottedStyle());
		styles.put(LINE_STYLE_AREA, i18n.areaStyle());
		styles.put(LINE_STYLE_DASHED, i18n.dashedStyle());
		styles.put(LINE_STYLE_LINE_DOTS, i18n.lineDotsStyle());

		this.lineStyles = new ComboBoxItem();
		this.lineStyles.setTitle(i18n.linestyle());
		this.lineStyles.setShowTitle(true);
		this.lineStyles.setValueMap(styles);
		this.lineStyles.setWidth(85);
		
		this.lineWidth = new ComboBoxItem();
		this.lineWidth.setTitle(i18n.lineWidth());
		this.lineWidth.setShowTitle(true);
		this.lineWidth.setWidth(85);
		this.lineWidth.setValueMap("1","2","3","4","5","6","7","8","9","10");

		this.scale = new RadioGroupItem();
		this.scale.setTitle(i18n.scale());
		this.scale.setValueMap(i18n.zeroScale(),
				i18n.autoScale());

		this.cpForm = new DynamicForm();
		this.cpForm.setNumCols(1);
		this.cpForm.setAlign(Alignment.LEFT);
		this.cpForm.setAutoWidth();

		this.colors = new ColorPickerItem();
		this.colors.setShowTitle(true);
		this.colors.setTitle(i18n.color());
		this.colors.setWidth(85);
		this.cpForm.setShowComplexFields(false);
		this.cpForm.setFields(this.scale, this.seriesType, this.lineStyles, 
				this.lineWidth, this.colors, this.slider);
		this.cpForm.setSaveOnEnter(true);

		SmallButton conf = new SmallButton(
				new Img("../img/icons/acc.png"),
				i18n.setAndRefresh(),
				i18n.setAndRefreshExt());
		conf.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				LegendEntryTimeSeries.this.styleChanger.hide();

				TimeSeries timeseries = DataStoreTimeSeriesImpl.getInst()
						.getDataItem(LegendEntryTimeSeries.this.timeseriesID);
				timeseries.setLineStyle(LegendEntryTimeSeries.this.lineStyles
						.getValue().toString());
				timeseries.setSeriesType(LegendEntryTimeSeries.this.seriesType
						.getValue().toString());
				timeseries.setLineWidth(Integer
						.valueOf(LegendEntryTimeSeries.this.lineWidth
								.getValueAsString()));
				boolean scaleToNullCheck = false;
				boolean autoScaleCheck = false;
				if (LegendEntryTimeSeries.this.scale.getValueAsString().equals(
						i18n.zeroScale())) {
					scaleToNullCheck = true;
				} else {
					autoScaleCheck = true;
				}
				EventBus.getMainEventBus().fireEvent(
						new UpdateScaleEvent(LegendEntryTimeSeries.this
								.getTimeSeries().getPhenomenonId(), scaleToNullCheck,
								autoScaleCheck));
				EventBus.getMainEventBus()
						.fireEvent(
								new ChangeTimeSeriesStyleEvent(
										LegendEntryTimeSeries.this
												.getTimeSeries().getId(),
										LegendEntryTimeSeries.this.colors
												.getValue().toString(),
										new Double(
												LegendEntryTimeSeries.this.slider
														.getValue().toString()),
										scaleToNullCheck,
										LegendEntryTimeSeries.this.lineStyles
												.getValue().toString(),
										autoScaleCheck));
				EventBus.getMainEventBus().fireEvent(
						new StoreTimeSeriesPropsEvent(getTimeSeries().getId(),
								getTimeSeries().getProperties()));
			}
		});
		SmallButton cancelColor = new SmallButton(new Img(
				"../img/icons/del.png"), i18n.cancel(),
				i18n.cancel());
		cancelColor.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				LegendEntryTimeSeries.this.styleChanger.hide();
			}
		});

		HLayout buttonsStyle = new HLayout();
		buttonsStyle.setAutoHeight();
		buttonsStyle.setAlign(Alignment.RIGHT);
		buttonsStyle.addMember(conf);
		buttonsStyle.addMember(cancelColor);

		HLayout hlayout = new HLayout();
		hlayout.setAlign(Alignment.CENTER);
		hlayout.addMember(this.cpForm);
//		hlayout.setBorder("1px solid red");
		VLayout vlayout = new VLayout();
		vlayout.setAlign(Alignment.CENTER);
//		vlayout.setBorder("1px solid blue");
		vlayout.addMember(hlayout);
		vlayout.addMember(buttonsStyle);
		
		this.styleChanger.setStyleName("n52_sensorweb_client_styleChangerForm");
		this.styleChanger.addItem(vlayout);

		this.styleChanger.hide();

		return this.styleChanger;
	}

	public String getElemId() {
		return this.timeseriesID;
	}

	public LegendData getDataWrapper() {
		return DataStoreTimeSeriesImpl.getInst().getDataItem(this.timeseriesID);
	}

	public void updateLayout() {
		// TODO not needed?
	}

	private HLayout createOrderEntryTools() {
		HLayout ordButt = new HLayout();
		ordButt.setTabIndex(-1);
		ordButt.setWidth(40);
		ordButt.setAlign(Alignment.RIGHT);

		ImageButton up = new ImageButton("up", "../img/icons/arrow_up.png",
				i18n.up(), i18n.upExt());
		ImageButton down = new ImageButton("down",
				"../img/icons/arrow_down.png", i18n.down(),
				i18n.downExt());
		up.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Canvas[] members = View.getView().getLegend()
						.getLegendStack().getMembers();

				for (int i = 0; i < members.length; i++) {

					if (members[i] instanceof LegendElement) {

						LegendElement le = (LegendElement) members[i];

						if (le.equals(LegendEntryTimeSeries.this)) {
							try {
								EventBus.getMainEventBus().fireEvent(
										new LegendElementSelectedEvent(le,
												false));
								LegendElement pred = (LegendElement) members[i - 1];
								int tmp = pred.getOrdering();
								pred.setOrdering(le.getOrdering());
								le.setOrdering(tmp);

								DataStoreTimeSeriesImpl.getInst()
										.getDataItem(le.getElemId())
										.setOrdering(le.getOrdering());
								DataStoreTimeSeriesImpl.getInst()
										.getDataItem(pred.getElemId())
										.setOrdering(pred.getOrdering());

							} catch (Exception e) {
								// was the first one, so do nothing
								return;
							}
						}
					}
				}

				EventBus.getMainEventBus().fireEvent(
						new TimeSeriesChangedEvent());
				EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
			}
		});

		down.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				Canvas[] members = View.getView().getLegend()
						.getLegendStack().getMembers();

				for (int i = 0; i < members.length; i++) {

					if (members[i] instanceof LegendElement) {

						LegendElement le = (LegendElement) members[i];

						if (le.equals(LegendEntryTimeSeries.this)) {
							try {
								EventBus.getMainEventBus().fireEvent(
										new LegendElementSelectedEvent(le,
												false));
								LegendElement succ = (LegendElement) members[i + 1];
								int tmp = succ.getOrdering();
								succ.setOrdering(le.getOrdering());
								le.setOrdering(tmp);

								DataStoreTimeSeriesImpl.getInst()
										.getDataItem(le.getElemId())
										.setOrdering(le.getOrdering());
								DataStoreTimeSeriesImpl.getInst()
										.getDataItem(succ.getElemId())
										.setOrdering(succ.getOrdering());

							} catch (Exception e) {
								// was the last one, so do nothing
								return;
							}
						}
					}
				}
				EventBus.getMainEventBus().fireEvent(
						new TimeSeriesChangedEvent());
				EventBus.getMainEventBus().fireEvent(new LoadImageDataEvent());
			}
		});

		ordButt.addMember(up);
		ordButt.addMember(down);
		return ordButt;
	}

	public int getOrdering() {
		return this.getTimeSeries().getOrdering();
	}

	public void setOrdering(int ordering) {
		this.getTimeSeries().setOrdering(ordering);
	}

	public void hideFooter() {
		this.infoButton.hide();
		this.deleteButton.hide();
		this.legendEntryFoot.hide();
		if (ClientUtils.isSesEnabled()) {
			this.sesComButton.hide();
		}
	}

	public void showFooter() {
		this.infoButton.show();
		this.deleteButton.show();
		this.legendEntryFoot.show();
		if (ClientUtils.isSesEnabled()) {
			this.sesComButton.show();
		}
	}

	public Canvas getClickTarget() {
		return this.legendEntryHead;
	}

	public void setTargetToDrag(Canvas c) {
		setDragTarget(c);
	}

	@Override
	public boolean isVisible() {
		// TODO check interface
		return false;
	}

	@Override
	public void setVisible(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setSelected(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setHasNoData(boolean b) {
		// TODO Auto-generated method stub

	}

	public void setFooterVisible(boolean b) {
		// TODO Auto-generated method stub

	}

	public boolean isFooterVisible() {
		return this.legendEntryFoot.isVisible();
	}

	public Layout getLayout() {
		return this;
	}

	public void setHasData(boolean b) {
		// XXX do nothing? check interface
	}

	public boolean getHasData() {
		// XXX always return false? check interface
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((timeseriesID == null) ? 0 : timeseriesID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		LegendEntryTimeSeries other = (LegendEntryTimeSeries) obj;
		if (timeseriesID == null) {
			if (other.timeseriesID != null)
				return false;
		} else if (!timeseriesID.equals(other.timeseriesID))
			return false;
		return true;
	}

	public class LegendEntryTimeSeriesEventBroker implements
			TimeSeriesChangedEventHandler,
			LegendElementSelectedEventHandler,
			TimeSeriesHasDataEventHandler,
			StoreTimeSeriesFirstValueEventHandler,
			StoreTimeSeriesLastValueEventHandler,
			UpdateScaleEventHandler,
			SwitchAutoscaleEventHandler {

		public LegendEntryTimeSeriesEventBroker() {
			EventBus.getMainEventBus().addHandler(TimeSeriesChangedEvent.TYPE,
					this);
			EventBus.getMainEventBus().addHandler(
					LegendElementSelectedEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(TimeSeriesHasDataEvent.TYPE,
					this);
			EventBus.getMainEventBus().addHandler(
					FirstValueOfTimeSeriesEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(
					StoreTimeSeriesLastValueEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(UpdateScaleEvent.TYPE, this);
			EventBus.getMainEventBus().addHandler(SwitchAutoscaleEvent.TYPE, this);
		}

		public void onTimeSeriesChanged(TimeSeriesChangedEvent evt) {
			LegendEntryTimeSeries.this.update();
		}

		public void unregister() {
			EventBus.getMainEventBus().removeHandler(
					TimeSeriesChangedEvent.TYPE, this);
			EventBus.getMainEventBus().removeHandler(
					LegendElementSelectedEvent.TYPE, this);
			EventBus.getMainEventBus().removeHandler(
					TimeSeriesHasDataEvent.TYPE, this);
			EventBus.getMainEventBus().removeHandler(
					FirstValueOfTimeSeriesEvent.TYPE, this);
			EventBus.getMainEventBus().removeHandler(
					StoreTimeSeriesLastValueEvent.TYPE, this);
			EventBus.getMainEventBus().removeHandler(UpdateScaleEvent.TYPE,
					this);
			EventBus.getMainEventBus().removeHandler(SwitchAutoscaleEvent.TYPE, this);
		}

		public void onSelected(LegendElementSelectedEvent evt) {
			if (evt.isNewAdded()) {
				LegendEntryTimeSeries.this.titleCol.show();
				LegendEntryTimeSeries.this.showFooter();
			}
			if (evt.getElement().equals(LegendEntryTimeSeries.this)) {
				LegendEntryTimeSeries.this.isSelected = true;
				if (LegendEntryTimeSeries.this.getTimeSeries().hasData()) {
					LegendEntryTimeSeries.this.noDataSign.hide();
					LegendEntryTimeSeries.this.legendEntryHead
							.setStyleName("n52_sensorweb_client_legendEntryHeaderSelected");
				} else {
					LegendEntryTimeSeries.this.noDataSign.show();
					LegendEntryTimeSeries.this.legendEntryHead
							.setStyleName("n52_sensorweb_client_legendEntryHeaderSelectedNoData");
				}
			} else {
				LegendEntryTimeSeries.this.isSelected = false;
				LegendEntryTimeSeries.this.hideFooter();
				if (LegendEntryTimeSeries.this.getTimeSeries().hasData()) {
					LegendEntryTimeSeries.this.noDataSign.hide();
					LegendEntryTimeSeries.this.legendEntryHead
							.setStyleName("n52_sensorweb_client_legendEntryHeader");
				} else {
					LegendEntryTimeSeries.this.noDataSign.show();
					LegendEntryTimeSeries.this.legendEntryHead
							.setStyleName("n52_sensorweb_client_legendEntryHeaderNoData");
				}
			}
		}

		public void onHasData(TimeSeriesHasDataEvent evt) {

			if (LegendEntryTimeSeries.this.getTimeSeries() != null
					&& evt.getTSID().equals(
							LegendEntryTimeSeries.this.getTimeSeries().getId())) {
				if (LegendEntryTimeSeries.this.isSelected) {
					if (evt.hasData()) {
						LegendEntryTimeSeries.this.noDataSign.hide();
						LegendEntryTimeSeries.this.legendEntryHead
								.setStyleName("n52_sensorweb_client_legendEntryHeaderSelected");
					} else {
						LegendEntryTimeSeries.this.noDataSign.show();
						LegendEntryTimeSeries.this.legendEntryHead
								.setStyleName("n52_sensorweb_client_legendEntryHeaderSelectedNoData");
						Toaster.getToasterInstance().addMessage(
								i18n.noDataAvailable()
										+ LegendEntryTimeSeries.this
												.getTimeSeries().getProcedureId());
					}
				} else {
					if (evt.hasData()) {
						LegendEntryTimeSeries.this.noDataSign.hide();
						LegendEntryTimeSeries.this.legendEntryHead
								.setStyleName("n52_sensorweb_client_legendEntryHeader");
					} else {
						LegendEntryTimeSeries.this.noDataSign.show();
						LegendEntryTimeSeries.this.legendEntryHead
								.setStyleName("n52_sensorweb_client_legendEntryHeaderNoData");
						Toaster.getToasterInstance().addMessage(
								i18n.noDataAvailable()
										+ LegendEntryTimeSeries.this
												.getTimeSeries().getProcedureId());
					}
				}
			}
		}

		public void onStore(FirstValueOfTimeSeriesEvent evt) {
			if (evt.getTsID().equals(LegendEntryTimeSeries.this.timeseriesID)) {
				setFirstValueInterval();
			}
		}

		public void onStore(StoreTimeSeriesLastValueEvent evt) {
			if (evt.getTsID().equals(LegendEntryTimeSeries.this.timeseriesID)) {
				setLastValueInterval();
			}
		}

		public void onUpdateScale(UpdateScaleEvent evt) {
			if (LegendEntryTimeSeries.this.getTimeSeries().getPhenomenonId()
					.equals(evt.getPhenomenonID())) {
				if (evt.isAutoScale()) {
					LegendEntryTimeSeries.this.getTimeSeries().setAutoScale(
							evt.isAutoScale());
					LegendEntryTimeSeries.this.scale.setValue(i18n
							.autoScale());
				}
				if (evt.isScaleToNull()) {
					LegendEntryTimeSeries.this.getTimeSeries().setScaleToZero(
							evt.isScaleToNull());
					LegendEntryTimeSeries.this.scale.setValue(i18n
							.zeroScale());
				}
			}
		}

		@Override
		public void onSwitch(SwitchAutoscaleEvent evt) {
			LegendEntryTimeSeries.this.getTimeSeries().setAutoScale(evt.getSwitch());
			LegendEntryTimeSeries.this.scale.setValue(i18n.autoScale());
			LegendEntryTimeSeries.this.getTimeSeries().setScaleToZero(false);
		}
	}

}