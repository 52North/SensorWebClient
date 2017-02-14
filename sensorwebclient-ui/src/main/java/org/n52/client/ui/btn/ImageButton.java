/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui.btn;

import org.n52.client.ctrl.LoaderManager;
import org.n52.client.ui.LoaderImage;
import org.n52.client.ui.View;

import com.google.gwt.user.client.Random;
import com.smartgwt.client.types.Cursor;
import com.smartgwt.client.widgets.Img;

/**
 * Wrapperclass for Imagebuttons with special configurations such as tooltips.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class ImageButton extends Button {

    private Img delegate; // TODO encapsulate UI elements
    
    /** The size. */
    private int size = 16;

    /** The show down. */
    private boolean showDown = false;

    /** The show roll over. */
    private boolean showRollOver = false;

    /** The parameterId. */
    private String id;

    /** The icon. */
    private String icon;

    /** The tool tip. */
    private String shortToolTip;

    /** The loader. */
    private LoaderImage loader;

    /** The was clicked. */
    private boolean wasClicked = false;

    /** The extended tooltip. */
    private String extendedTooltip;

    public ImageButton(String id, String icon, String shortTooltip, String extendedTooltip) {
        this.icon = icon;
        this.shortToolTip = shortTooltip;
        this.extendedTooltip = extendedTooltip;
        this.id = id + "_" + System.currentTimeMillis();
        init();
    }
    
    private void init() {
        setStyleName("n52_sensorweb_client_imagebutton");
//        int length = this.size + 2 * this.margin;
//        this.setWidth(length);
//        this.setHeight(length);

        String loaderId = "loader_" + (LoaderManager.getInstance().getCount() + Random.nextInt(10000));
        this.loader = new LoaderImage(loaderId, "../img/mini_loader_bright.gif", this);

        this.setID(this.id);
        this.setSrc(this.icon);
        this.setShowHover(true);
        this.setShowRollOver(this.showRollOver);
        this.setShowDownIcon(this.showDown);
        this.setShowFocusedAsOver(false);
        this.setCursor(Cursor.POINTER);
        
        if (View.getView().isShowExtendedTooltip()) {
            this.setTooltip(this.extendedTooltip);
        }
        else {
            this.setTooltip(this.shortToolTip);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.smartgwt.client.widgets.StatefulCanvas#setIcon(java.lang.String)
     */
    @Override
    public void setIcon(String icon) {
        this.icon = icon;
        this.setSrc(this.icon);
    }

    /**
     * Sets the clicked.
     */
    public void setClicked() {
        this.wasClicked = true;
    }

    /**
     * Sets the not clicked.
     */
    public void setNotClicked() {
        this.wasClicked = false;
    }

    /**
     * Turn off.
     */
    public void turnOFF() {
        this.setSrc(this.icon);
    }

    /**
     * Turn on.
     */
    public void turnON() {
        if (this.wasClicked) {
            this.setSrc(this.loader.getUrl());
            this.wasClicked = false;
        }
    }

    @Override
    public void setExtendedTooltip() {
        this.setTooltip(this.extendedTooltip);
    }

    @Override
    public void setShortTooltip() {
        this.setTooltip(this.shortToolTip);
    }

}