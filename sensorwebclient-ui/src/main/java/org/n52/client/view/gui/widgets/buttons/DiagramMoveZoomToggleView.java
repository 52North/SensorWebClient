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


/**
 * The Class DiagramMoveZoomToggleView.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class DiagramMoveZoomToggleView implements DiagramMoveZoomPresenter.View {

    /** The move. */
    private ImageButton move;

    /** The zoom. */
    private ImageButton zoom;
    
    /**
     * Instantiates a new diagram move zoom toggle view.
     * 
     * @param move
     *            the move
     * @param zoom
     *            the zoom
     */
    public DiagramMoveZoomToggleView(ImageButton move, ImageButton zoom) {
        this.move = move;
        this.zoom = zoom;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.widgets.buttons.DiagramMoveZoomPresenter.View
     * #addMoveClickHandler(com.smartgwt.client.widgets.events.ClickHandler)
     */
    public void addMoveClickHandler(
            com.smartgwt.client.widgets.events.ClickHandler clickHandler) {
        this.move.addClickHandler(clickHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.widgets.buttons.DiagramMoveZoomPresenter.View
     * #addZoomClickHandler(com.smartgwt.client.widgets.events.ClickHandler)
     */
    public void addZoomClickHandler(
            com.smartgwt.client.widgets.events.ClickHandler clickHandler) {
        this.zoom.addClickHandler(clickHandler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.widgets.buttons.DiagramMoveZoomPresenter.View
     * #toggleMove()
     */
    public void toggleMove() {
        this.move.setIcon("../img/icons/dragger_sel.png"); 
        this.zoom.setIcon("../img/icons/zoom_in.png"); 
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.n52.client.view.gui.widgets.buttons.DiagramMoveZoomPresenter.View
     * #toggleZoom()
     */
    public void toggleZoom() {
        this.move.setIcon("../img/icons/dragger.png");
        this.zoom.setIcon("../img/icons/zoom_in_sel.png");
    }

    /**
     * Gets the move button.
     * 
     * @return the move button
     */
    public ImageButton getMoveButton() {
        return this.move;
    }

    /**
     * Gets the zoom button.
     * 
     * @return the zoom button
     */
    public ImageButton getZoomButton() {
        return this.zoom;
    }

}
