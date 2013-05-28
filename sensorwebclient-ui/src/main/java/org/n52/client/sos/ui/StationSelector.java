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

import static com.smartgwt.client.types.Overflow.HIDDEN;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.bus.EventBus;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.ui.ApplyCancelButtonLayout;
import org.n52.client.ui.InteractionWindow;
import org.n52.client.ui.LoadingSpinner;
import org.n52.client.ui.Toaster;
import org.n52.client.ui.map.InfoMarker;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
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
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
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

	private HTMLPane procedureDetailsHTMLPane;

	private Label showSelectionMenuButton;
	
	private InteractionWindow selectionMenu;
	
	private InteractionWindow infoWindow;
	
	private ListGrid listGrid;
	
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
		listGrid = SelectionMenuModel.createListGrid(this);
		Layout layout = new Layout();
		layout.addMember(listGrid);
		selectionMenu = new InteractionWindow(layout);
		selectionMenu.setZIndex(1000000);
		selectionMenu.setWidth(250);
		selectionMenu.setHeight(290);
		selectionMenu.setWindowTitle(i18n.dataprovider());
		setSelectionMenuWindowPosition();
		return selectionMenu;
	}

	private void setSelectionMenuWindowPosition() {
		selectionMenu.setTop(34);
		selectionMenu.setLeft(WIDTH - selectionMenu.getWidth() - 25);
	}

	private Canvas createInfoWindow() {
		VLayout layout = new VLayout();
		layout.addMember(createInformationFieldForSelectedProcedure());
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

    private Canvas createInformationFieldForSelectedProcedure() {
        VLayout layout = new VLayout();
        procedureDetailsHTMLPane = new HTMLPane();
        phenomenonBox = new SelectItem(i18n.phenomenonLabel());
        phenomenonBox.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String category = (String) event.getItem().getValue();
				controller.loadParameterConstellationByCategory(category);
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
        layout.addMember(procedureDetailsHTMLPane);
        return layout;
    }

    private Canvas createLoadingSpinner() {
        String imgURL = "../img/loader_wide.gif";
        LoadingSpinner loader = new LoadingSpinner(imgURL, 43, 11);
        loader.setPadding(7);
        return loader;
    }

	private void setInfoWindowPosition() {
		infoWindow.setTop(HEIGHT - infoWindow.getHeight() - 35);
		infoWindow.setLeft(2);
	}

	SelectionChangedHandler getSOSSelectionHandler() {
		return new SOSSelectionChangedHandler(controller);
	}

	public ClickHandler getSOSClickedHandler() {
		return new SOSClickedHandler(controller);
	}

	private MapWidget createMapContent() {
    	return controller.createMap();
    }
    
    FormItem createFilterCategorySelectionGroup(String serviceUrl) {
		if (stationFilterGroups.containsKey(serviceUrl)) {
			RadioGroupItem selector = stationFilterGroups.get(serviceUrl);
			return selector;
		}
		//RadioGroupItem radioGroup = new RadioGroupItem(serviceUrl);
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
        final String selectedServiceURL = controller.getSelectedServiceURL();
		final Station station = controller.getSelectedStation();
		SosTimeseries timeseries = controller.getSelectedTimeseries();
		
		NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder(selectedServiceURL)
				.addStation(station)
				.addTimeseries(timeseries)
				.build();
		EventBus.getMainEventBus().fireEvent(event);
	}

	protected void closeStationpicker() {
		hide();
	    hideInfoWindow();
	    StationSelector.controller.clearMarkerSelection();
	}

	public void updateProcedureDetailsURL(String url) {
		procedureDetailsHTMLPane.setContentsURL(url);
		procedureDetailsHTMLPane.show();
		applyCancel.finishLoading();
	}
	
	public void clearProcedureDetails() {
		procedureDetailsHTMLPane.hide();
	}
	
	public void updateStationFilters(final SOSMetadata currentMetadata) {
		hideInfoWindow();
		Map<String, String> sortedCategories = getAlphabeticallySortedMap();
		for (Station station : currentMetadata.getStations()) {
			Set<String> categories = getStationCategories(station);
			for (String category : categories) {
				sortedCategories.put(category, category);
			}
		}
		String serviceUrl = currentMetadata.getId();
		RadioGroupItem selector = stationFilterGroups.get(serviceUrl);
		LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>(sortedCategories);
		selector.setValueMap(categories);
	}
	
	private Set<String> getStationCategories(Station station) {
		Set<String> categories = new HashSet<String>();
		for (SosTimeseries timeseries : station.getObservingTimeseries()) {
			categories.add(timeseries.getCategory());
		}
		return categories;
	}

	public void setSelectedFilter(String serviceURL, String filter) {
		RadioGroupItem selector = stationFilterGroups.get(serviceURL);
		if (selector == null) {
			// debug message .. should not happen anyway
			Toaster.getToasterInstance().addErrorMessage("Missing expansion component for " + serviceURL);
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
		String[] array = getStationCategories(infoMarker.getStation()).toArray(new String[0]);
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
		String phenDesc = null;
		if (controller.getSelectedPhenomenon() != null) {
			phenDesc = controller.getSelectedPhenomenon().getLabel();
		}
		String foiDesc = null;
//		if (controller.getSelectedFeature() != null) {
//			foiDesc = controller.getSelectedFeature().getLabel();
//		}
		if (phenDesc != null && !phenDesc.isEmpty()) {
			phenomenonBox.setValue(phenDesc);
//			phenomenonInfoLabel.setContents(i18n.phenomenonLabel() + ": " + phenDesc);
//			phenomenonInfoLabel.show();
		} else {
//			phenomenonInfoLabel.hide();
		}
		if (foiDesc != null && !foiDesc.isEmpty()) {
			stationInfoLabel.setContents(i18n.foiLabel() + ": " + foiDesc);
			stationInfoLabel.show();
		} else {
			stationInfoLabel.hide();
		}
	}
}