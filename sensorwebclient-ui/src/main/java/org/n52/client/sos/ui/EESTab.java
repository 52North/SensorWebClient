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

import org.eesgmbh.gimv.client.controls.KeystrokeControl;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEvent;
import org.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler;
import org.eesgmbh.gimv.client.event.SetViewportPixelBoundsEvent;
import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.presenter.ImagePresenter;
import org.eesgmbh.gimv.client.presenter.MousePointerPresenter;
import org.eesgmbh.gimv.client.presenter.TooltipPresenter;
import org.eesgmbh.gimv.client.view.GenericWidgetView;
import org.eesgmbh.gimv.client.view.GenericWidgetViewImpl;
import org.eesgmbh.gimv.client.view.ImageViewImpl;
import org.eesgmbh.gimv.client.widgets.Viewport;
import org.eesgmbh.gimv.shared.util.Bound;
import org.eesgmbh.gimv.shared.util.Bounds;
import org.eesgmbh.gimv.shared.util.Direction;
import org.n52.client.bus.EventBus;
import org.n52.client.ctrl.Application;
import org.n52.client.model.data.DataStoreTimeSeriesImpl;
import org.n52.client.sos.ctrl.EESTabController;
import org.n52.client.util.exceptions.ExceptionHandler;
import org.n52.client.view.gui.elements.DataPanelTab;
import org.n52.client.view.gui.elements.ctrl.DataControls;
import org.n52.shared.Constants;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VStack;

public class EESTab extends DataPanelTab {

    private static EESTabController controller;

    /*
     * TODO: monitor impact of setting this public
     */
    public static Layout layout;

    private Viewport mainChartViewport;

    private HTML verticalMousePointerLine;

    private Viewport overviewChartViewport;

    private EventBus overviewEventBus = EventBus.getOverviewChartEventBus();

    private EventBus mainChartEventBus = EventBus.getMainEventBus();

    protected TooltipPresenter tooltipPresenter;

    private HorizontalPanel horizontalSlider; // TODO use a flowpanel instead

    private int lastSliderPosition;

    private HTML leftHandleWidget;

    private HTML rightHandleWidget;

    private HTML mainHandleWidget;
    
    private Img mainChartLoadingSpinner;
    

    public EESTab(String ID, String title) {
        super("DiagramTab");
    	layout = new Layout();
    	
        MousePointerDomainBoundsHandler listener = new MousePointerDomainBoundsHandler();
        this.mainChartEventBus.addHandler(SetDomainBoundsEvent.TYPE, listener);

        controller = new EESTabController(this);

        setID(ID);
        setTitle(title);
        setIcon("../img/icons/chart_curve.png");
    }

    public static int getPanelHeight() {
        // - 100 overview height - 5 margin correction
        int height = layout.getParentElement().getHeight() - 100 - 5;
        if (Application.isHasStarted() && controller.getControls().isVisible()) {
            height -= controller.getControls().getHeight();
        }
        return height;
    }

    public static int getPanelWidth() {
    	int width = layout.getParentElement().getWidth() - 5;
		return width;
    }

    public void init() {
        try {
            setPane(layout);
            
            EESTab.layout.setVertical(true);

            this.mainChartViewport = getMainChartViewport();
            this.overviewChartViewport = getOverviewChartViewport();
            
            EESTab.layout.addMember(this.mainChartViewport);
            EESTab.layout.addMember(this.overviewChartViewport);
            initKeyControls();
            initZooming();
            initTooltips();
            
            this.mainChartLoadingSpinner = new Img("../img/loader.gif");
            this.mainChartLoadingSpinner.setWidth(32);
            this.mainChartLoadingSpinner.setHeight(32);
            this.mainChartLoadingSpinner.setLeft(getPanelWidth()/2);
            this.mainChartLoadingSpinner.setTop(getPanelHeight()/2);
            this.mainChartLoadingSpinner.hide();
            this.mainChartViewport.add(this.mainChartLoadingSpinner);
            
            this.mainChartViewport.setHandlerManager(this.mainChartEventBus);
            this.overviewChartViewport.setHandlerManager(this.overviewEventBus);
            this.overviewEventBus.fireEvent(StateChangeEvent.createMove());
            this.mainChartEventBus.fireEvent(StateChangeEvent.createMove());
            this.mainChartEventBus.fireEvent(StateChangeEvent.createZoom());

            int mainOffsetWidth = this.mainChartViewport.getOffsetWidth();
            int mainOffsetHeight = this.mainChartViewport.getOffsetHeight();
            Bounds mainBounds = new Bounds(mainOffsetWidth, 0, mainOffsetHeight, 0);
            int overviewOffsetWidth = this.overviewChartViewport.getOffsetWidth();
            int overviewOffsetHeight = this.overviewChartViewport.getOffsetHeight();
            Bounds overviewBounds = new Bounds(overviewOffsetWidth, 0, overviewOffsetHeight, 0);
            this.mainChartEventBus.fireEvent(new SetViewportPixelBoundsEvent(mainBounds));
            this.overviewEventBus.fireEvent(new SetViewportPixelBoundsEvent(overviewBounds));
        }
        catch (Exception e) {
            ExceptionHandler.handleUnexpectedException(e);
        }
    }

    private void initZooming() {
        HTML zoomBox = new HTML();
        DOM.setStyleAttribute(zoomBox.getElement(), "opacity", "0.15");
        DOM.setStyleAttribute(zoomBox.getElement(), "mozOpacity", "0.15");
        DOM.setStyleAttribute(zoomBox.getElement(), "msFilter", "\"progid:DXImageTransform.Microsoft.Alpha(Opacity=15)\"");
        DOM.setStyleAttribute(zoomBox.getElement(), "filter", "alpha(opacity=15)");
        
        DOM.setStyleAttribute(zoomBox.getElement(), "outline", "black dashed 1px");
        DOM.setStyleAttribute(zoomBox.getElement(), "backgroundColor", "blue");
        DOM.setStyleAttribute(zoomBox.getElement(), "visibility", "hidden");
        this.mainChartViewport.add(zoomBox);

        GenericWidgetView zoomBoxView = new GenericWidgetViewImpl(zoomBox);
        new ZoomBoxPresenter(this.mainChartEventBus, zoomBoxView);
    }

    private Viewport getMainChartViewport() {
        Image mainChartImage = new Image("img/blank.gif");

        Viewport mainchart = new Viewport("100%", "100%");
        mainchart.setEnableZoomWhenShiftkeyPressed(true);
        mainchart.add(mainChartImage);

        // as it is focusable, we do not want to see an outline
        DOM.setStyleAttribute(mainchart.getElement(), "outline", "none");
        ImageViewImpl imageView = new ImageViewImpl(mainChartImage);

        new ImagePresenter(this.mainChartEventBus, imageView);
        new DragImageControl(this.mainChartEventBus);
        new MouseWheelControl(this.mainChartEventBus);

        return mainchart;
    }

    private Viewport getOverviewChartViewport() {
        Image overviewChartImage = new Image("img/blank.gif");
        Viewport overview = new Viewport("100%", "100px");
        overview.add(overviewChartImage);

        DOM.setStyleAttribute(overview.getElement(), "outline", "none");
        this.horizontalSlider = createOverviewSlider();
        overview.add(this.horizontalSlider);

        ImageViewImpl imageView = new ImageViewImpl(overviewChartImage);
        new ImagePresenter(this.overviewEventBus, imageView);
        return overview;
    }

    public void setVisibleSlider(boolean isVisible) {
        if (this.horizontalSlider != null) {
            this.horizontalSlider.setVisible(isVisible);
        }
    }

    public void addSlider() {
        if (this.horizontalSlider == null && this.overviewChartViewport != null) {
            this.horizontalSlider = createOverviewSlider();
            this.overviewChartViewport.add(this.horizontalSlider, this.lastSliderPosition, 0);
        }
    }

    public void removeSlider() {
        if (this.horizontalSlider != null) {
            this.lastSliderPosition = this.overviewChartViewport.getWidgetLeft(this.horizontalSlider);
//            int left = this.leftHandleWidget.getAbsoluteLeft() + this.leftHandleWidget.getOffsetWidth();
//            int right = this.rightHandleWidget.getAbsoluteLeft();
//            this.lastMainHandleWidth = right - left;
            this.overviewChartViewport.remove(this.horizontalSlider);
            this.horizontalSlider = null;
        }
    }

    /**
     * Creates the Slider the user can interact with to change the shown time intervals of the given
     * timeseries'.
     * 
     * @return the TimeSlider as a whole
     */
    private HorizontalPanel createOverviewSlider() {
        HorizontalPanel horizontalSlider = new HorizontalPanel();
        DOM.setStyleAttribute(horizontalSlider.getElement(), "marginTop", "6px");
        horizontalSlider.setHeight("75px");

        this.leftHandleWidget = buildSliderPart("8px", "75px", "w-resize", "#6585d0", 0.5);
        this.rightHandleWidget = buildSliderPart("8px", "75px", "e-resize", "#6585d0", 0.5);
        this.mainHandleWidget = buildSliderPart("100%", "75px", "move", "#aaa", 0.5);

        horizontalSlider.add(this.leftHandleWidget);
        horizontalSlider.setCellWidth(this.leftHandleWidget, "15px");
        horizontalSlider.add(this.mainHandleWidget);
        horizontalSlider.setCellWidth(this.mainHandleWidget, "100%");
        horizontalSlider.add(this.rightHandleWidget);
        horizontalSlider.setCellWidth(this.rightHandleWidget, "15px");
        DOM.setStyleAttribute(horizontalSlider.getElement(), "visibility", "hidden");
        
        GenericWidgetViewImpl view = new GenericWidgetViewImpl(horizontalSlider);
        OverviewPresenter overviewPresenter = new OverviewPresenter(view, this.overviewEventBus, this.mainChartEventBus);

        // Define handles for overview control
        GenericWidgetView leftHandle = new GenericWidgetViewImpl(this.leftHandleWidget);
        GenericWidgetView mainHandle = new GenericWidgetViewImpl(this.mainHandleWidget);
        GenericWidgetView rightHandle = new GenericWidgetViewImpl(this.rightHandleWidget);

        overviewPresenter.addHandle(leftHandle, Bound.LEFT);
        overviewPresenter.addHandle(mainHandle, Bound.RIGHT, Bound.LEFT);
        overviewPresenter.addHandle(rightHandle, Bound.RIGHT);
        overviewPresenter.setMinClippingWidth(40); // min width
        overviewPresenter.setVerticallyLocked(true); // drag horizontally only

        return horizontalSlider;
    }

    private void initTooltips() {

        Element mousePointerElement = getMousePointerLineElement();
        DOM.setStyleAttribute(mousePointerElement, "backgroundColor", "blue");
        DOM.setStyleAttribute(mousePointerElement, "width", "0px");
        DOM.setStyleAttribute(mousePointerElement, "height", "0px");
        DOM.setStyleAttribute(mousePointerElement, "visibility", "hidden");
        DOM.setStyleAttribute(mousePointerElement, "marginTop", "6px");
        this.mainChartViewport.add(this.verticalMousePointerLine);

        this.tooltipPresenter = new TooltipPresenter(this.mainChartEventBus);

        this.tooltipPresenter.configureHoverMatch(true, false, false);
        this.tooltipPresenter.setTooltipZIndex(Constants.Z_INDEX_ON_TOP);

        GenericWidgetViewImpl widget = new GenericWidgetViewImpl(this.verticalMousePointerLine);
        MousePointerPresenter mpp = new MousePointerPresenter(this.mainChartEventBus, widget);
        mpp.configure(true, false);
    }

    protected Element getMousePointerLineElement() {
        if (this.verticalMousePointerLine == null) {
            this.verticalMousePointerLine = new HTML();
        }
        return this.verticalMousePointerLine.getElement();
    }

    private void initKeyControls() {
        KeystrokeControl kCtrl = new KeystrokeControl(this.mainChartEventBus);
        kCtrl.addTargetElement(this.mainChartViewport.getElement());
        kCtrl.addTargetElement(this.overviewChartViewport.getElement());
        kCtrl.addDocumentAndBodyAsTarget();

        // 10px offset each
        kCtrl.registerKey(KeyCodes.KEY_LEFT, Direction.EAST, 10);
        kCtrl.registerKey(KeyCodes.KEY_UP, Direction.SOUTH, 10);
        kCtrl.registerKey(KeyCodes.KEY_RIGHT, Direction.WEST, 10);
        kCtrl.registerKey(KeyCodes.KEY_DOWN, Direction.NORTH, 10);

        // 30px offset if ctrl is pressed
        kCtrl.registerKey(KeyCodes.KEY_LEFT, true, false, false, false, Direction.EAST, 30);
        kCtrl.registerKey(KeyCodes.KEY_UP, true, false, false, false, Direction.NORTH, 30);
        kCtrl.registerKey(KeyCodes.KEY_RIGHT, true, false, false, false, Direction.WEST, 30);
        kCtrl.registerKey(KeyCodes.KEY_DOWN, true, false, false, false, Direction.SOUTH, 30);
    }

    private HTML buildSliderPart(String width, String height, String cursor, String color, double transparancy) {
        HTML container = new HTML();
        container.setWidth(width);
        container.setHeight(height);
        DOM.setStyleAttribute(container.getElement(), "cursor", cursor);
        DOM.setStyleAttribute(container.getElement(), "backgroundColor", color);
        
        // transparency styling (see also bug#449 and http://www.quirksmode.org/css/opacity.html)
        // note: since GWT complains, '-msFilter' has to be in plain camelCase (w/o '-')
        // ordering is important here
        DOM.setStyleAttribute(container.getElement(), "opacity", Double.toString(transparancy));
        DOM.setStyleAttribute(container.getElement(), "mozOpacity", Double.toString(transparancy));
        String opacity = "(opacity=" +Double.toString(transparancy*100) + ")";
        DOM.setStyleAttribute(container.getElement(), "msFilter", "\"progid:DXImageTransform.Microsoft.Alpha"+ opacity + "\"");
        DOM.setStyleAttribute(container.getElement(), "filter", "alpha" + opacity);
        return container;
    }

    @Override
    public DataControls getDataControls() {
        return controller.getControls();
    }

    public void redraw() {
        layout.markForRedraw();
    }

    public EventBus getOvervieweventBus() {
        return this.overviewEventBus;
    }

    protected class MousePointerDomainBoundsHandler implements SetDomainBoundsEventHandler {

        /*
         * (non-Javadoc)
         * 
         * @seeorg.eesgmbh.gimv.client.event.SetDomainBoundsEventHandler#
         * onSetDomainBounds(org.eesgmbh.gimv.client.event.SetDomainBoundsEvent)
         */
        public void onSetDomainBounds(SetDomainBoundsEvent event) {
            if ( !DataStoreTimeSeriesImpl.getInst().getDataItems().isEmpty()) {
                String[] widthHeight = getBoundValues(event);

                Element mousePointerElement = EESTab.this.getMousePointerLineElement();
                DOM.setStyleAttribute(mousePointerElement, "width", widthHeight[0]);
                DOM.setStyleAttribute(mousePointerElement, "height", widthHeight[1]);

                setTooltipsOnTop(event);
            }
        }

        /**
         * @return String array with width as 1st and height as 2nd element.
         */
        private String[] getBoundValues(SetDomainBoundsEvent event) {
            String absWidth = (isWidthWiderOne(event)) ? "1px" : "0px";
            String absHeight = Double.toString(event.getBounds().getAbsHeight()) + "px";
            return new String[] {absWidth, absHeight};
        }

        private void setTooltipsOnTop(SetDomainBoundsEvent event) {
            if (isWidthWiderOne(event)) {
                EESTab.this.tooltipPresenter.setTooltipZIndex(Constants.Z_INDEX_ON_TOP);
            }
            else {
                EESTab.this.tooltipPresenter.setTooltipZIndex(0);
            }
        }

        private boolean isWidthWiderOne(SetDomainBoundsEvent event) {
            return event.getBounds().getAbsWidth() > 1;
        }
    }

    /**
     * 
     */
    public void hideTooltips() {
        DOM.setStyleAttribute(EESTab.this.verticalMousePointerLine.getElement(), "width", "0px");
        this.tooltipPresenter.setTooltipZIndex(0);
    }

    
    class DraggableVStack extends VStack{
    	
    	public DraggableVStack(){
    		setSize("200", "30");
    		setCanDrag(true);
    		setBackgroundColor("#000000");
    	}
    	
    }  
    public static class DragLabel extends Label {  
        public DragLabel() {  
            setAlign(Alignment.CENTER);  
            setPadding(4);  
            setShowEdges(true);  
            setMinWidth(70);  
            setMinHeight(70);  
            setMaxWidth(300);  
            setMaxHeight(200);  
            setKeepInParentRect(true);  
            setCanDragReposition(true);  
            setDragAppearance(DragAppearance.TARGET);  
        }  
    }

	public void hideLoadingSpinner() {
		mainChartLoadingSpinner.hide();
	}

	public void showLoadingSpinner() {
		if (mainChartLoadingSpinner != null) {
			mainChartLoadingSpinner.show();			
		}
	}  

}
