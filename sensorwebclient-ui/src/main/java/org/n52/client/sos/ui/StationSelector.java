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

import static com.smartgwt.client.types.Overflow.HIDDEN;
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.client.sos.ui.SelectionMenuModel.createListGrid;
import static org.n52.client.ui.Toaster.getToasterInstance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.ui.ApplyCancelButtonLayout;
import org.n52.client.ui.InteractionWindow;
import org.n52.client.ui.LoadingSpinner;
import org.n52.client.ui.map.InfoMarker;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickEvent;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class StationSelector extends Window {

    private static final String COMPONENT_ID = "stationSelector";

    private static int WIDTH = 960;

    private static int HEIGHT = 552;

    private static StationSelector instance;

    private static StationSelectorController controller;

	private Layout guiContent;

	private Map<String, RadioGroupItem> stationFilterGroups;

	private Label phenomenonInfoLabel;

	private Label stationInfoLabel;

	private HTMLPane timeseriesInfoHTMLPane;

	private Label showSelectionMenuButton;

	private InteractionWindow selectionMenu;

	private InteractionWindow infoWindow;

	private Img stationLoadingSpinner;

    private ApplyCancelButtonLayout applyCancel;

	private SelectItem phenomenonBox;

    public static StationSelector getInst() {
        if (instance == null) {
        	controller = new StationSelectorController();
            instance = new StationSelector(controller);
        }
        return instance;
    }

    private StationSelector(StationSelectorController controller) {
    	stationFilterGroups = new HashMap<String, RadioGroupItem>();
    	controller.setStationPicker(this);
        initializeWindow();
        initializeContent();
        addCloseClickHandler(new CloseClickHandler() {
            public void onCloseClick(CloseClickEvent event) {
            	closeStationpicker();
            }
        });
    }

	private void initializeWindow() {
        setID(COMPONENT_ID);
        setOverflow(HIDDEN);
        setShowModalMask(true);
        setIsModal(true);
        setTitle(i18n.pickStation());
        setWidth(WIDTH);
        setHeight(HEIGHT);
        centerInPage();
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowMinimizeButton(false);
        addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				WIDTH = StationSelector.this.getWidth();
				HEIGHT = StationSelector.this.getHeight();
				setSelectionMenuButtonPosition();
				setSelectionMenuWindowPosition();
				setStationLoadingSpinnerPosition();
				setInfoWindowPosition();
			}
		});
    }

    private void initializeContent() {
    	if (guiContent == null) {
    		guiContent = new HLayout();
    		guiContent.addMember(createMapContent());
    		guiContent.addChild(createSelectionMenuButton());
    		guiContent.addChild(createSelectionMenuWindow());
    		guiContent.addChild(createStationLoadingSpinner());
    		guiContent.addChild(createInfoWindow());
	    	addItem(guiContent);
		}
	}

    private Canvas createStationLoadingSpinner() {
		stationLoadingSpinner = new Img("../img/loader.gif");
		stationLoadingSpinner.setWidth(32);
		stationLoadingSpinner.setHeight(32);
		setStationLoadingSpinnerPosition();
		return stationLoadingSpinner;
	}

	private void setStationLoadingSpinnerPosition() {
		stationLoadingSpinner.setTop((HEIGHT - stationLoadingSpinner.getHeight())/2);
		stationLoadingSpinner.setLeft((WIDTH - stationLoadingSpinner.getWidth())/2);
	}

	private Canvas createSelectionMenuButton() {
    	showSelectionMenuButton = new Label(i18n.chooseDataSource());
    	showSelectionMenuButton.setStyleName("n52_sensorweb_client_legendbuttonPrimary");
    	showSelectionMenuButton.setZIndex(1000000);
    	showSelectionMenuButton.setAutoHeight();
    	showSelectionMenuButton.setAutoWidth();
    	showSelectionMenuButton.setWrap(false);
    	showSelectionMenuButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (selectionMenu.isVisible()) {
					selectionMenu.animateHide(AnimationEffect.SLIDE);
				} else {
					selectionMenu.animateShow(AnimationEffect.SLIDE);
				}
			}
		});
    	setSelectionMenuButtonPosition();
		return showSelectionMenuButton;
	}

	private void setSelectionMenuButtonPosition() {
		int width = 150;
		showSelectionMenuButton.setWidth(width);
		showSelectionMenuButton.setTop(3);
    	showSelectionMenuButton.setLeft(WIDTH - width - 25);
	}

	private Canvas createSelectionMenuWindow() {
		Layout layout = new Layout();
        layout.addMember(createExpandableSelectionGrid());
		selectionMenu = new InteractionWindow(layout);
		selectionMenu.setZIndex(1000000);
		selectionMenu.setWidth(250);
		selectionMenu.setHeight(290);
		selectionMenu.setWindowTitle(i18n.dataprovider());
		setSelectionMenuWindowPosition();
		return selectionMenu;
	}

    private ListGrid createExpandableSelectionGrid() {
        ListGrid listGrid = createListGrid(this);
        listGrid.addSelectionChangedHandler(new SOSSelectionChangedHandler(controller));
        listGrid.addClickHandler(new SOSClickedHandler(controller));
        listGrid.setSelectionType(SelectionStyle.SINGLE);
        listGrid.setCanExpandMultipleRecords(false);
        return listGrid;
    }

	private void setSelectionMenuWindowPosition() {
		selectionMenu.setTop(34);
		selectionMenu.setLeft(WIDTH - selectionMenu.getWidth() - 25);
	}

	private Canvas createInfoWindow() {
		VLayout layout = new VLayout();
		layout.addMember(createInformationFieldForSelectedStation());
		HLayout buttons = new HLayout();
		buttons.setAutoHeight();
		buttons.setAlign(Alignment.RIGHT);
        buttons.addMember(createApplyCancelCanvas());
        layout.addMember(buttons);

		infoWindow = new InteractionWindow(layout);
		infoWindow.setZIndex(1000000);
		infoWindow.setWidth(300);
		infoWindow.setHeight(300);
		setInfoWindowPosition();
		infoWindow.hide();
		return infoWindow;
	}

	private ApplyCancelButtonLayout createApplyCancelCanvas() {
        if (applyCancel == null) {
            applyCancel = new ApplyCancelButtonLayout();
            applyCancel.createApplyButton(i18n.addNewTimeseries(), i18n.addNewTimeseriesExt(), createApplyHandler());
            applyCancel.createCancelButton(i18n.cancel(), i18n.cancel(), createCancelHandler());
            applyCancel.setAlign(Alignment.RIGHT);
        }
        return applyCancel;
    }

	private ClickHandler createApplyHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                StationSelector.this.loadTimeSeries();
                closeStationpicker();
            }
        };
    }

    private ClickHandler createCancelHandler() {
        return new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                controller.clearMarkerSelection();
                clearProcedureDetails();
                hideInfoWindow();
            }
        };
    }

    private Canvas createInformationFieldForSelectedStation() {
        VLayout layout = new VLayout();
        timeseriesInfoHTMLPane = new HTMLPane();
        phenomenonBox = new SelectItem(i18n.phenomenonLabel());
        phenomenonBox.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String category = (String) event.getItem().getValue();
				controller.loadTimeseriesByCategory(category);
			}
		});
        DynamicForm form = new DynamicForm();
        form.setItems(phenomenonBox);
//        phenomenonInfoLabel = new Label();
//        phenomenonInfoLabel.setAutoHeight();
        stationInfoLabel = new Label();
        stationInfoLabel.setAutoHeight();
//        layout.addMember(phenomenonInfoLabel);
        layout.addMember(form);
        layout.addMember(stationInfoLabel);
        layout.addMember(timeseriesInfoHTMLPane);
        return layout;
    }

    private Canvas createLoadingSpinner() {
        String imgURL = "../img/mini_loader_bright.gif";
        LoadingSpinner loader = new LoadingSpinner(imgURL, 43, 11);
        loader.setPadding(7);
        return loader;
    }

	private void setInfoWindowPosition() {
		infoWindow.setTop(HEIGHT - infoWindow.getHeight() - 35);
		infoWindow.setLeft(2);
	}

	private MapWidget createMapContent() {
    	return controller.createMap();
    }

    FormItem createFilterCategorySelectionGroup(String serviceUrl) {
		if (stationFilterGroups.containsKey(serviceUrl)) {
			RadioGroupItem selector = stationFilterGroups.get(serviceUrl);
			return selector;
		}
		RadioGroupItem radioGroup = new RadioGroupItem("sosDataSource");
		radioGroup.setShowTitle(false);
		radioGroup.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Object value = event.getValue();
				if (value != null) {
					hideInfoWindow();
					controller.setStationFilter(value.toString());
					controller.updateContentUponStationFilter();
				}
			}
		});

		stationFilterGroups.put(serviceUrl, radioGroup);
		return radioGroup;
	}

	private void loadTimeSeries() {
		final Station station = controller.getSelectedStation();
		SosTimeseries timeseries = controller.getSelectedTimeseries();
		NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder()
				.addStation(station)
				.addTimeseries(timeseries)
				.build();
		getMainEventBus().fireEvent(event);
	}

	protected void closeStationpicker() {
		hide();
	    hideInfoWindow();
	    StationSelector.controller.clearMarkerSelection();
	}

	public void updateProcedureDetailsURL(String url) {
		timeseriesInfoHTMLPane.setContentsURL(url);
		timeseriesInfoHTMLPane.show();
		applyCancel.finishLoading();
	}

	public void clearProcedureDetails() {
	    timeseriesInfoHTMLPane.clear();
		timeseriesInfoHTMLPane.hide();
	}

	public void updateStationFilters(final SOSMetadata currentMetadata) {
		hideInfoWindow();
		Map<String, String> sortedCategories = getAlphabeticallySortedMap();
		for (Station station : currentMetadata.getStations()) {
			Set<Category> categories = getStationCategories(station);
			for (Category category : categories) {
				sortedCategories.put(category.getLabel(), category.getLabel());
			}
		}
		String serviceUrl = currentMetadata.getServiceUrl();
		RadioGroupItem selector = stationFilterGroups.get(serviceUrl);
		LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>(sortedCategories);
		selector.setValueMap(categories);
	}

	private Set<Category> getStationCategories(Station station) {
		Set<Category> categories = new HashSet<Category>();
		for (SosTimeseries timeseries : station.getObservedTimeseries()) {
			categories.add(timeseries.getCategory());
		}
		return categories;
	}

	private String[] getStationCategoryLabels(Station station) {
		Set<String> labels = new HashSet<String>();
		for (SosTimeseries timeseries : station.getObservedTimeseries()) {
			labels.add(timeseries.getCategory().getLabel());
		}
		return labels.toArray(new String[0]);
	}

	public void setSelectedFilter(String serviceURL, String filter) {
		RadioGroupItem selector = stationFilterGroups.get(serviceURL);
		if (selector == null) {
			// debug message .. should not happen anyway
			getToasterInstance().addErrorMessage("Missing expansion component for " + serviceURL);
		} else {
			selector.setValue(filter);
		}
	}

	protected SortedMap<String, String> getAlphabeticallySortedMap() {
        return new TreeMap<String, String>(new Comparator<String>() {
            public int compare(String word1, String word2) {
                return word1.compareToIgnoreCase(word2);
            }
        });
    }

    public String getId() {
        return COMPONENT_ID;
    }

	public void showInfoWindow(InfoMarker infoMarker, String header) {
		updateInfoLabels();
		String[] array = getStationCategoryLabels(infoMarker.getStation());
		phenomenonBox.setValueMap(array);
		phenomenonBox.clearValue();
		infoWindow.setWindowTitle(header);
		infoWindow.show();
		applyCancel.setLoading();
	}

	public void hideInfoWindow() {
		infoWindow.hide();
	}

	public void showStationLoadingSpinner(boolean show) {
		if (show) {
			stationLoadingSpinner.show();
		} else {
			stationLoadingSpinner.hide();
		}
	}

	public void updateInfoLabels() {
		String selectedPhenomenon = null;
		if (controller.getSelectedPhenomenon() != null) {
			selectedPhenomenon = controller.getSelectedPhenomenon().getLabel();
		}
		if (selectedPhenomenon != null && !selectedPhenomenon.isEmpty()) {
			phenomenonBox.setValue(selectedPhenomenon);
			stationInfoLabel.hide();
		}
	}
}