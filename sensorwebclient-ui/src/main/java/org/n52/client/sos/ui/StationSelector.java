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
import static org.n52.client.bus.EventBus.getMainEventBus;
import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;
import static org.n52.client.sos.ui.SelectionMenuModel.createListGrid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

import org.gwtopenmaps.openlayers.client.MapWidget;
import org.n52.client.sos.event.data.NewTimeSeriesEvent;
import org.n52.client.ui.ApplyCancelButtonLayout;
import org.n52.client.ui.InteractionWindow;
import org.n52.client.ui.legend.LegendEntryTimeSeries;
import org.n52.client.ui.map.InfoMarker;
import org.n52.shared.serializable.pojos.sos.ObservationParameter.DecodeType;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.AnimationEffect;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.types.TreeModelType;
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
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;

public class StationSelector extends Window {
	
    private static final String COMPONENT_ID = "stationSelector";
    
    private static int WIDTH = 960;
    
    private static int HEIGHT = 552;

    private static StationSelector instance;

    private static StationSelectorController controller;
    
    private static final String SERVICE_URL_ZDM = "http://localhost/SOS/sos";
    
	private Layout guiContent;

	private Map<String, DynamicForm> stationFilterGroups;
	
	private Label stationInfoLabel;

	private HTMLPane timeseriesInfoHTMLPane;

	private Label showSelectionMenuButton;
	
	private InteractionWindow selectionMenu;
	
	private InteractionWindow infoWindow;
	
	private Img stationLoadingSpinner;
	
    private ApplyCancelButtonLayout applyCancel;

	private SelectItem phenomenonBox;
	
	private HashSet<String> phenomenonBoxValueMap = new HashSet<String>();
		
    public static StationSelector getInst() {
        if (instance == null) {
        	controller = new StationSelectorController();
            instance = new StationSelector(controller);
        }
        return instance;
    }

    private StationSelector(StationSelectorController controller) {
    	stationFilterGroups = new HashMap<String, DynamicForm>();
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
		selectionMenu.setWidth(300);
		selectionMenu.setHeight(301);
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
		infoWindow.setWidth(500);
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
            applyCancel.disableApplyButton();
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
		Station station = controller.getSelectedStation();
		if (station!=null) {
	        String stationName = station.getId();
			String stationId = stationName;
			if (stationName.contains("/")) {
				String[] nameSplitted = stationName.split("/");
				stationId = nameSplitted[nameSplitted.length-1];
			}
			
			String stationUrl = LegendEntryTimeSeries.STATION_DESCRIPTION_URL+stationId;
			timeseriesInfoHTMLPane.setContentsURL(stationUrl);
			timeseriesInfoHTMLPane.setContentsType(ContentsType.PAGE);
			timeseriesInfoHTMLPane.show();

		}
        
        phenomenonBox = new SelectItem(i18n.phenomenonLabel());
        phenomenonBox.addChangedHandler(new ChangedHandler() {
			@Override
			public void onChanged(ChangedEvent event) {
				String category = (String) event.getItem().getValue();
				applyCancel.switchApplyButton(category != null && !category.isEmpty());
				controller.loadTimeseriesByCategory(category);
			}
		});
        DynamicForm form = new DynamicForm();
        form.setItems(phenomenonBox);
        stationInfoLabel = new Label();
        stationInfoLabel.setAutoHeight();
        layout.addMember(form);
        layout.addMember(stationInfoLabel);
        layout.addMember(timeseriesInfoHTMLPane);
        return layout;
    }

	private void setInfoWindowPosition() {
		infoWindow.setTop(HEIGHT - infoWindow.getHeight() - 35);
		infoWindow.setLeft(2);
	}

	private MapWidget createMapContent() {
    	return controller.createMap();
    }
    
	DynamicForm createFilterCategorySelectionGroup(String serviceUrl) {
		if (stationFilterGroups.containsKey(serviceUrl)) {
			DynamicForm selector = stationFilterGroups.get(serviceUrl);
			return selector;
		}
		DynamicForm dynamicForm = new DynamicForm();
		TreeGrid treeGrid = getNewTreeGrid();
		
		Tree tree = new Tree();

		treeGrid.setData(tree);
		
		for( Canvas child : dynamicForm.getChildren() ){
			dynamicForm.removeChild(child);
		}
		dynamicForm.addChild(treeGrid);
		

		stationFilterGroups.put(serviceUrl, dynamicForm);
		return dynamicForm;
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
		if(url != null && url.contains("www.portal-tideelbe.de")){
			final Station station = controller.getSelectedStation();
			
			String stationName = station.getId();
			String stationId = stationName;
			if (stationName.contains("/")) {
				String[] nameSplitted = stationName.split("/");
				stationId = nameSplitted[nameSplitted.length-1];
			}
					
			String stationUrl = LegendEntryTimeSeries.STATION_DESCRIPTION_URL+stationId;
			timeseriesInfoHTMLPane.setContentsURL(stationUrl);
			timeseriesInfoHTMLPane.setContentsType(ContentsType.PAGE);
		} else {
			timeseriesInfoHTMLPane.setContentsURL(url);
			timeseriesInfoHTMLPane.setContentsType(ContentsType.FRAGMENT);
		}
		timeseriesInfoHTMLPane.show();
		applyCancel.finishLoading();
	}
	
	/**
	 * @deprecated Use hideProcedureDetails instead
	 */
	public void clearProcedureDetails() {
		hideProcedureDetails();
	}
	
	public void hideProcedureDetails() {
		timeseriesInfoHTMLPane.hide();
	}
	
	public void updateStationFilters(final SOSMetadata currentMetadata) {
		hideInfoWindow();
		/** categoryTree contains all Main nodes (String) and corresponding subnodes (HashMap) */
		HashMap<String, HashMap<String, SosTimeseries>> categoryTree = new HashMap<String, HashMap<String, SosTimeseries>>();
		for (Station station : currentMetadata.getStations()) {
			ArrayList<SosTimeseries> categories = station.getObservedTimeseries();
			for (SosTimeseries category : categories) {
				/** parentMap contains all subnodes */
				HashMap<String, SosTimeseries> parentMap;
				String categoryName;
				if(category.getServiceUrl() != null && category.getServiceUrl().contains(SERVICE_URL_ZDM)){
					categoryName = category.getParentName();
				} else {
					categoryName = SosTimeseries.PARENT_NAME_DEFAULT;
				}
				if(categoryTree.containsKey(categoryName)){
					parentMap = categoryTree.get(categoryName);
				} else {
					parentMap = new HashMap<String, SosTimeseries>();
					categoryTree.put(categoryName, parentMap);
				}
				if( !parentMap.containsKey(category.getCategory())){
					parentMap.put(category.getCategory(), category);
				}
			}
		}

		Vector<TreeNode> parentNodes = new Vector<TreeNode>();
		for(String parentName : categoryTree.keySet()){
			TreeNode parent = new TreeNode();
			parent.setName(SosTimeseries.decodeSpecialCharacters(parentName));
			parent.setAttribute("nativeName", parentName);
			
			HashMap<String, SosTimeseries> children = categoryTree.get(parentName);
			Vector<TreeNode> childNodes = new Vector<TreeNode>();
			
			for( SosTimeseries category : children.values()){
				TreeNode newTreeNode = new TreeNode();
				newTreeNode.setName(category.getCategory(DecodeType.NATURAL));
				newTreeNode.setAttribute("nativeName", category.getCategory(DecodeType.ASCII));
				newTreeNode.setAttribute("type", category.getType().toString());
				if( SosTimeseries.PARENT_NAME_DEFAULT.equals(parentName)){
					parentNodes.add(newTreeNode);
				} else {
					childNodes.add(newTreeNode);
				}
			}
			
			if( !SosTimeseries.PARENT_NAME_DEFAULT.equals(parentName)){
				parent.setChildren(childNodes.toArray(new TreeNode[childNodes.size()]));
				parentNodes.add(parent);
			}
		}
		
		
		Tree tree = new Tree();
		tree.setNameProperty("name");
		tree.setModelType(TreeModelType.CHILDREN);
		tree.setData(parentNodes.toArray(new TreeNode[parentNodes.size()]));
		
		TreeGrid treeGrid = getNewTreeGrid();
		treeGrid.setData(tree);
		treeGrid.sort();
		treeGrid.setHeight(230);
		treeGrid.setWidth(250);

		String serviceUrl = currentMetadata.getId();
		DynamicForm selector = stationFilterGroups.get(serviceUrl);
		for( Canvas child : selector.getChildren() ){
			selector.removeChild(child);
		}
		selector.addChild(treeGrid);
	}
	
	private Set<String> getStationCategories(Station station) {
		Set<String> categories = new HashSet<String>();
		for (SosTimeseries timeseries : station.getObservedTimeseries()) {
			categories.add(timeseries.getCategory());
		}
		return categories;
	}

	public void setSelectedFilter(String serviceURL, String filter) {
		// not needed any more
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
		String[] array = getStationCategories(infoMarker.getStation()).toArray(new String[0]);
		phenomenonBox.setValueMap(array);
		phenomenonBoxValueMap = new HashSet<String>(Arrays.asList(array));
		phenomenonBox.clearValue();
		infoWindow.setWindowTitle(header);
		infoWindow.show();
		updateInfoLabels();
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
		if (selectedPhenomenon != null && !selectedPhenomenon.isEmpty() && phenomenonBoxValueMap.contains(selectedPhenomenon)) {
			phenomenonBox.setValue(selectedPhenomenon);
			applyCancel.enableApplyButton();
			stationInfoLabel.hide();
		} else {
			hideProcedureDetails();
			applyCancel.disableApplyButton();
		}
	}
	
	private TreeGrid getNewTreeGrid(){
		TreeGrid treeGrid = new TreeGrid();
		treeGrid.setShowHeader(false);
		treeGrid.setShowAllRecords(true);

		treeGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
			@Override
			public void onSelectionChanged(SelectionEvent event) {
				Object value = event.getRecord();
				if (value != null) {
					if( value instanceof Record){
						hideInfoWindow();
						Record castedValue = (Record) value;
						if( !castedValue.getAttributeAsBoolean("isFolder")){
							controller.setStationFilter( castedValue.getAttribute("nativeName") + "|" + castedValue.getAttribute("type"));
							controller.updateContentUponStationFilter();
						}
					}
				}
			}
		});

		TreeGridField field = new TreeGridField("name");
		treeGrid.setFields(field);
		
		return treeGrid;
	}
}