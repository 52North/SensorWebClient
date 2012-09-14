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

package org.n52.client.view.gui.widgets.stationPicker;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.control.I18N;
import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.events.dataEvents.sos.NewTimeSeriesEvent;
import org.n52.client.view.gui.widgets.LoadingSpinner;
import org.n52.client.view.gui.widgets.Toaster;
import org.n52.client.view.gui.widgets.buttons.SmallButton;
import org.n52.client.view.gui.widgets.mapping.InfoMarker;
import org.n52.client.view.gui.widgets.windows.InteractionWindow;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.n52.shared.serializable.pojos.sos.Offering;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
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
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.RadioGroupItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

public class StationPicker extends Window {
	
    private static final String COMPONENT_ID = "stationPicker";
    
    private static int WIDTH = 950;
    
    private static int HEIGHT = 550;

    private static StationPicker instance;

    private static StationPickerController controller;
    
	private Layout guiContent;

	private Map<String, RadioGroupItem> phenomenonsSelectors;
	
	private Label phenomenonInfoLabel;
	
	private Label stationInfoLabel;

	private HTMLPane procedureDetailsHTMLPane;

	private SmallButton confirmSelectionButton;
	
	private Label showSelectionMenuButton;
	
	private InteractionWindow selectionMenu;
	
	private InteractionWindow infoWindow;
	
	private ListGrid listGrid;
	
	private Img stationLoadingSpinner;
	
	private Canvas informationFieldSpinner;
	
    public static StationPicker getInst() {
        if (instance == null) {
        	controller = new StationPickerController();
            instance = new StationPicker(controller);
        }
        return instance;
    }

    private StationPicker(StationPickerController controller) {
    	phenomenonsSelectors = new HashMap<String, RadioGroupItem>();
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
        setShowModalMask(true);
        setTitle(I18N.sosClient.pickStation());
        setWidth(WIDTH);
        setHeight(HEIGHT);
        centerInPage();
        setIsModal(true);
        setCanDragResize(true);
        setShowMaximizeButton(true);
        setShowMinimizeButton(false);
        setMargin(0);
        addResizedHandler(new ResizedHandler() {
			@Override
			public void onResized(ResizedEvent event) {
				WIDTH = StationPicker.this.getWidth();
				HEIGHT = StationPicker.this.getHeight();
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
    	showSelectionMenuButton = new Label(I18N.sosClient.choosePhenomenon());
    	showSelectionMenuButton.setStyleName("sensorweb_client_legendbuttonPrimary");
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
		selectionMenu.setWindowTitle(I18N.sosClient.dataprovider());
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
		informationFieldSpinner = createLoadingSpinner();
		buttons.addMember(createLoadingSpinner());
		buttons.addMember(createAddTimeSeriesButton());
		buttons.addMember(createCancelButton());
		layout.addMember(buttons);
		infoWindow = new InteractionWindow(layout);
		infoWindow.setZIndex(1000000);
		infoWindow.setWidth(300);
		infoWindow.setHeight(300);
		setInfoWindowPosition();
		infoWindow.hide();
		return infoWindow;
	}
	
	private void setInfoWindowPosition() {
		infoWindow.setTop(HEIGHT - infoWindow.getHeight() - 35);
		infoWindow.setLeft(2);
	}

	SelectionChangedHandler getSOSSelectionHandler() {
		return new SOSSelectionChangedHandler(controller);
	}

	private MapWidget createMapContent() {
    	return controller.createMap();
    }
    
    FormItem getPhenomenonsSelectionGroup(String serviceURL) {
		if (phenomenonsSelectors.containsKey(serviceURL)) {
			RadioGroupItem selector = phenomenonsSelectors.get(serviceURL);
			return selector;
		}
		RadioGroupItem radioGroup = new RadioGroupItem(serviceURL);
		radioGroup.setShowTitle(false);
		radioGroup.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				Object value = event.getValue();
				if (value != null) {
					hideInfoWindow();
					controller.setSelectedPhenomenon(value.toString());
					controller.updateContentUponPhenomenonSelection();
				}
			}
		});

		phenomenonsSelectors.put(serviceURL, radioGroup);
		return radioGroup;
	}

	private Canvas createInformationFieldForSelectedProcedure() {
		VLayout layout = new VLayout();
		procedureDetailsHTMLPane = new HTMLPane();
		phenomenonInfoLabel = new Label();
		phenomenonInfoLabel.setAutoHeight();
		stationInfoLabel = new Label();
		stationInfoLabel.setAutoHeight();
		layout.addMember(phenomenonInfoLabel);
		layout.addMember(stationInfoLabel);
		layout.addMember(procedureDetailsHTMLPane);
		return layout;
	}

	private SmallButton createAddTimeSeriesButton() {
        Img img = new Img("../img/icons/acc.png");
        String normalTooltip = I18N.sosClient.addNewTimeseries();
        String extendedTooltip = I18N.sosClient.addNewTimeseriesExt();
        confirmSelectionButton = new SmallButton(img, normalTooltip, extendedTooltip);
        confirmSelectionButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent evt) {
                StationPicker.this.loadTimeSeries();
                closeStationpicker();
            }
        });
        return confirmSelectionButton;
	}
	
	private SmallButton createCancelButton() {
		Img img = new Img("../img/icons/del.png");
        String normalTooltip = I18N.sosClient.cancel();
        String extendedTooltip = I18N.sosClient.cancel();
        SmallButton cancelSelectionButton = new SmallButton(img, normalTooltip, extendedTooltip);
        cancelSelectionButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
            	controller.clearMarkerSelection();
            	clearProcedureDetails();
            	hideInfoWindow();
            }
        });
        return cancelSelectionButton;
    }

	private Canvas createLoadingSpinner() {
		String imgURL = "../img/loader_wide.gif";
		LoadingSpinner loader = new LoadingSpinner(imgURL, 43, 11);
        loader.setPadding(7);
        return loader;
	}

	private void loadTimeSeries() {
		final SOSMetadata metadata = controller.getCurrentMetadata();
		final String offeringId = controller.getSelectedOfferingId();
		final String featureId = controller.getSelectedFeatureId();
		final String procedureId = controller.getSelectedProcedureId();
		final String phenomenonId = controller.getSelectedPhenomenonId();
		final String selectedServiceURL = controller.getSelectedServiceURL();
		
		Offering offering = metadata.getOffering(offeringId);
		FeatureOfInterest feature = metadata.getFeature(featureId);
		Phenomenon phenomenon = metadata.getPhenomenon(phenomenonId);
		Procedure procedure = metadata.getProcedure(procedureId);
		Station station = controller.getSelectedStation();
		
		NewTimeSeriesEvent event = new NewTimeSeriesEvent.Builder(selectedServiceURL)
				.addStation(station)
				.addOffering(offering)
				.addFOI(feature)
				.addProcedure(procedure)
				.addPhenomenon(phenomenon)
				.build();
		EventBus.getMainEventBus().fireEvent(event);
	}

	protected void closeStationpicker() {
		hide();
	    hideInfoWindow();
	    StationPicker.controller.clearMarkerSelection();
	}

	public void updateProcedureDetailsURL(String url) {
		procedureDetailsHTMLPane.setContentsURL(url);
		procedureDetailsHTMLPane.show();
		informationFieldSpinner.hide();
	}
	
	public void clearProcedureDetails() {
		procedureDetailsHTMLPane.hide();
	}
	
	public void updatePhenomenonSelector(final SOSMetadata currentMetadata) {
		hideInfoWindow();
		Map<String, String> sortedPhenomenons = getAlphabeticallySortedMap();
		for (Phenomenon phenomenon : currentMetadata.getPhenomenons()) {
			sortedPhenomenons.put(phenomenon.getId(), phenomenon.getLabel());
		}
		String serviceURL = currentMetadata.getId();
		RadioGroupItem selector = phenomenonsSelectors.get(serviceURL);
		LinkedHashMap<String, String> phenomenons = new LinkedHashMap<String, String>(sortedPhenomenons);
		selector.setValueMap(phenomenons);
	}
	
	public void setSelectedPhenomenon(String serviceURL, String phenomenonName) {
		RadioGroupItem selector = phenomenonsSelectors.get(serviceURL);
		if (selector == null) {
			// debug message .. should not happen anyway
			Toaster.getInstance().addErrorMessage("Missing expansion component for " + serviceURL);
		} else {
			selector.setValue(phenomenonName);
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

	public void showInfoWindow(InfoMarker infoMarker) {
		updateInfoLabels();
		infoWindow.setWindowTitle(infoMarker.getStation().getProcedure());
		infoWindow.show();
		informationFieldSpinner.show();
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
		if (controller.getSelectedFeature() != null) {
			foiDesc = controller.getSelectedFeature().getLabel();
		}
		if (phenDesc != null && !phenDesc.isEmpty()) {
			phenomenonInfoLabel.setContents(I18N.sosClient.phenomenonLabel() + ": " + phenDesc);
			phenomenonInfoLabel.show();
		} else {
			phenomenonInfoLabel.hide();
		}
		if (foiDesc != null && !foiDesc.isEmpty()) {
			stationInfoLabel.setContents(I18N.sosClient.foiLabel() + ": " + foiDesc);
			stationInfoLabel.show();
		} else {
			stationInfoLabel.hide();
		}
	}
}