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
package org.n52.client.view.gui.widgets.buttons;

import org.eesgmbh.gimv.client.event.StateChangeEvent;
import org.eesgmbh.gimv.client.event.StateChangeEventHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

/**
 * The Class DiagramMoveZoomPresenter.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class DiagramMoveZoomPresenter {

    /** The view. */
    private View view;

    /** The handler manager. */
    private HandlerManager handlerManager;

    /** The state change event handler. */
    private StateChangeEventHandlerImpl stateChangeEventHandler;

    /**
     * The Interface View.
     */
    public interface View {

        /**
         * Toggle move.
         */
        void toggleMove();

        /**
         * Toggle zoom.
         */
        void toggleZoom();

        /**
         * Adds the move click handler.
         * 
         * @param clickHandler
         *            the click handler
         */
        void addMoveClickHandler(ClickHandler clickHandler);

        /**
         * Adds the zoom click handler.
         * 
         * @param clickHandler
         *            the click handler
         */
        void addZoomClickHandler(ClickHandler clickHandler);
    }

    /**
     * Instantiates a new diagram move zoom presenter.
     * 
     * @param view
     *            the view
     * @param handlerManager
     *            the handler manager
     */
    public DiagramMoveZoomPresenter(View view, HandlerManager handlerManager) {
        this.setView(view);
        this.setHandlerManager(handlerManager);

        this.stateChangeEventHandler = new StateChangeEventHandlerImpl();
        this.getHandlerManager().addHandler(StateChangeEvent.TYPE,
                this.stateChangeEventHandler);

        this.getView().addMoveClickHandler(new MoveClickHandler());
        this.getView().addZoomClickHandler(new ZoomClickHandler());

    }

    /**
     * Sets the view.
     * 
     * @param view
     *            the view to set
     */
    public void setView(View view) {
        this.view = view;
    }

    /**
     * Gets the view.
     * 
     * @return the view
     */
    public View getView() {
        return this.view;
    }

    /**
     * Sets the handler manager.
     * 
     * @param handlerManager
     *            the handlerManager to set
     */
    public void setHandlerManager(HandlerManager handlerManager) {
        this.handlerManager = handlerManager;
    }

    /**
     * Gets the handler manager.
     * 
     * @return the handlerManager
     */
    public HandlerManager getHandlerManager() {
        return this.handlerManager;
    }

    /**
     * The Class StateChangeEventHandlerImpl.
     */
    private class StateChangeEventHandlerImpl implements StateChangeEventHandler {

        /**
         * Instantiates a new state change event handler impl.
         */
        public StateChangeEventHandlerImpl() {
            // nothin
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.eesgmbh.gimv.client.event.StateChangeEventHandler#onStateChange
         * (org.eesgmbh.gimv.client.event.StateChangeEvent)
         */
        public void onStateChange(StateChangeEvent event) {
            if (event.isMove()) {
                DiagramMoveZoomPresenter.this.getView().toggleMove();
            } else if (event.isZoom()) {
                DiagramMoveZoomPresenter.this.getView().toggleZoom();
            }
        }
    }

    /**
     * The Class MoveClickHandler.
     */
    private class MoveClickHandler implements ClickHandler {

        /**
         * Instantiates a new move click handler.
         */
        
        public MoveClickHandler() {
            // do nothin
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.smartgwt.client.widgets.events.ClickHandler#onClick(com.smartgwt
         * .client.widgets.events.ClickEvent)
         */
        public void onClick(ClickEvent event) {
            DiagramMoveZoomPresenter.this.getHandlerManager().fireEvent(
                    StateChangeEvent.createMove());
            // view.toggleMove();
        }
    }

    /**
     * The Class ZoomClickHandler.
     */
    private class ZoomClickHandler implements ClickHandler {

        /**
         * Instantiates a new zoom click handler.
         */
        public ZoomClickHandler() {
            // nothin
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.smartgwt.client.widgets.events.ClickHandler#onClick(com.smartgwt
         * .client.widgets.events.ClickEvent)
         */
        public void onClick(ClickEvent event) {
            DiagramMoveZoomPresenter.this.getHandlerManager().fireEvent(
                    StateChangeEvent.createZoom());
            // view.toggleZoom();
        }
    }
}
