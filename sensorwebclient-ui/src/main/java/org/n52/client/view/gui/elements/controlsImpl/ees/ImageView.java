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
//package org.n52.client.view.gui.elements.controlsImpl.ees;
//
//import org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View;
//
//import com.google.gwt.event.dom.client.LoadHandler;
//import com.google.gwt.event.shared.HandlerRegistration;
//import com.google.gwt.user.client.DOM;
//import com.google.gwt.user.client.ui.Image;
//import com.google.gwt.user.client.ui.Widget;
//
///**
// * The Class ImageView.
// * 
// * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
// */
//public class ImageView implements View {
//
//    /** The image. */
//    private Image image;
//
//    /**
//     * Instantiates a new image view.
//     * 
//     * @param image
//     *            the image
//     */
//    public ImageView(Image image) {
//        this.image = image;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#asWidget
//     * ()
//     */
//    public Widget asWidget() {
//        return this.image;
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see
//     * org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#setUrl
//     * (java.lang.String)
//     */
//    public void setUrl(String url) {
//        this.image.setUrl(url);
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#
//     * changePosition(int, int)
//     */
//    public void changePosition(int offsetX, int offsetY) {
//        setPosition(DOM.getIntStyleAttribute(this.image.getElement(), "left") + offsetX, //$NON-NLS-1$
//                DOM.getIntStyleAttribute(this.image.getElement(), "top") + offsetY); //$NON-NLS-1$
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#
//     * setPosition(int, int)
//     */
//    public void setPosition(int x, int y) {
//        DOM.setStyleAttribute(this.image.getElement(), "left", x + "px"); //$NON-NLS-1$ //$NON-NLS-2$
//        DOM.setStyleAttribute(this.image.getElement(), "top", y + "px"); //$NON-NLS-1$ //$NON-NLS-2$
//    }
//
//    /*
//     * (non-Javadoc)
//     * 
//     * @see org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#
//     * addLoadHandler(com.google.gwt.event.dom.client.LoadHandler)
//     */
//    public HandlerRegistration addLoadHandler(LoadHandler loadHandler) {
//        return this.image.addLoadHandler(loadHandler);
//    }
//
//    /* (non-Javadoc)
//     * @see org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#changeDimensions(int, int)
//     */
//    public void changeDimensions(int offsetWidth, int offsetHeight) {
//        setDimensions(this.image.getWidth() + offsetWidth, this.
//                image.getHeight() + offsetHeight);
//    }
//
//    /* (non-Javadoc)
//     * @see org.n52.client.view.gui.elements.controlsImpl.ees.EESPresenter.View#setDimensions(int, int)
//     */
//    public void setDimensions(int width, int height) {
//        this.image.setWidth(width + "px");
//        this.image.setHeight(height + "px");
//    }
//
// }
