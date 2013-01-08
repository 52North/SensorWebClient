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
package org.n52.client.sos.ctrl;

import static org.n52.client.sos.i18n.SosStringsAccessor.i18n;

import org.n52.client.eventBus.EventBus;
import org.n52.client.eventBus.EventCallback;
import org.n52.client.sos.event.SwitchGridEvent;
import org.n52.client.sos.event.data.RequestDataEvent;
import org.n52.client.sos.event.data.SwitchAutoscaleEvent;
import org.n52.client.sos.event.data.UndoEvent;
import org.n52.client.sos.event.handler.SwitchGridEventHandler;
import org.n52.client.sos.ui.DiagramMoveZoomPresenter;
import org.n52.client.sos.ui.DiagramMoveZoomToggleView;
import org.n52.client.ui.View;
import org.n52.client.view.gui.widgets.buttons.ImageButton;

import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

public class DataControlsEES extends DataControlsTimeSeries implements SwitchGridEventHandler {

    private ImageButton bbox;

    private ImageButton move;

    private ImageButton undo;

    private ImageButton autoScale;

    private ImageButton grid;
    
    private Button sesTabButton;

    private boolean isGrid = true;
    
    public DataControlsEES() {
        EventBus.getMainEventBus().addHandler(SwitchGridEvent.TYPE, this);

        this.bbox = new ImageButton("diagBBox", "../img/icons/zoom_in.png",
                i18n.bboxZoom(), i18n.bboxZoomExt());
        View.getInstance().registerTooltip(this.bbox);

        this.move = new ImageButton("diagMove", "../img/icons/dragger.png",
                i18n.diagMove(), i18n.diagMoveExt());
        View.getInstance().registerTooltip(this.move);

        this.undo =
                new ImageButton("undo", "../img/icons/arrow_undo.png", i18n.undo(),
                        i18n.undoExt());
        View.getInstance().registerTooltip(this.undo);        
        
        sesTabButton = new Button( i18n.sesTabButton());
        
        sesTabButton.setIcon("../img/icons/email_go.png");
        
        sesTabButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
//				View.getInstance().getDataPanel().setWidth100();
//                View.getInstance().getDataPanel().setHeight100();
//
//                org.n52.client.eventBus.events.TabSelectedEvent customEvent;
//                customEvent = new org.n52.client.eventBus.events.TabSelectedEvent(View.getInstance().getSesTab());
//                EventBus.getMainEventBus().fireEvent(customEvent);
				
				View.getInstance().getDataPanel().getPanel().selectTab(View.getInstance().getSesTab());
				
//				View.getInstance().getDataPanel().setCurrentTab(View.getInstance().getSesTab());
//				View.getInstance().getDataPanel().update();
			}
		});
        
        this.undo.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new UndoEvent(), new EventCallback() {

                    public void onEventFired() {
                        EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
                    }
                });
            }
        });
        this.autoScale =
                new ImageButton("autoScale", "../img/icons/arrow_up_down.png",
                        i18n.autoScaleButton(), i18n.autoScaleButtonExtra());
        View.getInstance().registerTooltip(this.autoScale);
        this.autoScale.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new SwitchAutoscaleEvent(true), new EventCallback() {

                    public void onEventFired() {
                        EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
                    }
                });
            }
        });

        this.grid = new ImageButton("grid", "../img/icons/grid_on.png", "grid", "grid");
        View.getInstance().registerTooltip(this.grid);
        this.grid.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                EventBus.getMainEventBus().fireEvent(new SwitchGridEvent(), new EventCallback() {

                    public void onEventFired() {
                        EventBus.getMainEventBus().fireEvent(new RequestDataEvent());
                    }
                });

            }
        });

        this.controlButtons.addMember(this.grid);

        DiagramMoveZoomToggleView moveZoom = new DiagramMoveZoomToggleView(this.move, this.bbox);
		DiagramMoveZoomPresenter moveZoomPresenter = new DiagramMoveZoomPresenter(moveZoom, EventBus.getMainEventBus());
//        moveZoomPresenter.getHandlerManager().fireEvent(
//                StateChangeEvent.createMove());
//        moveZoomPresenter.getHandlerManager().fireEvent(
//                StateChangeEvent.createMove());
        
//        getButtonLayout().addMember(this.grid, 15);
//        getButtonLayout().addMember(this.autoScale, 15);
        
//        getButtonLayout().addMember(moveZoom.getMoveButton(), 15);
        
        
//        getTopLayout().addMember(this.grid, 15);
//        getTopLayout().addMember(this.autoScale, 15);
//        getTopLayout().addMember(this.undo, 15);
//        getTopLayout().addMember(this.sesTabButton, 15);
//        getTopLayout().addMember(moveZoom.getZoomButton(), 15);
//        getTopLayout().addMember(moveZoom.getMoveButton(), 15);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.eventBus.events.handler.SwitchGridEventHandler#onSwitch()
     */
    public void onSwitch() {
        if (this.isGrid) {
            this.grid.setSrc("../img/icons/grid_off.png");
        } else {
            this.grid.setSrc("../img/icons/grid_on.png");
        }
        this.isGrid = !this.isGrid;

    }
}
