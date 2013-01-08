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
package org.n52.client.ui;

import org.n52.client.ui.btn.ImageButton;
import org.n52.client.ui.legend.LegendEntryTimeSeries;

/**
 * Wrapperclass for the Ajax-Loader-Images.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class LoaderImage extends Loader {

    /** The ID. */
    private String ID;

    /** The url. */
    private String url;

    /** The parent image button. */
    private ImageButton parentImageButton = null;

    /** The parent legend entry. */
    private LegendEntryTimeSeries parentLegendEntry = null;

    /** The is loading. */
    private boolean isLoading = true;

    /**
     * Instantiates a new loader image.
     * 
     * @param ID
     *            the iD
     * @param imageUrl
     *            the image url
     */
    public LoaderImage(String ID, String imageUrl, ImageButton parent) {
        super();
        this.ID = ID;
        this.url = imageUrl;
        this.parentImageButton = parent;
    }

    /**
     * Sets the parent legend entry.
     * 
     * @param entry
     *            Sets the parent as a LegendEntry
     */
    public void setParentLegendEntry(LegendEntryTimeSeries entry) {
        this.parentLegendEntry = entry;
    }

    /**
     * Gets the url.
     * 
     * @return the url
     */
    public String getUrl() {
        return this.url;
    }

    /**
     * Checks if is loading.
     * 
     * @return true, if is loading
     */
    public boolean isLoading() {
        return this.isLoading;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.smartgwt.client.widgets.BaseWidget#getID()
     */
    @Override
    public String getID() {
        return this.ID;
    }

    /**
     * Turns the loader off and performs special parent methods.
     */
    @Override
    public void stopAnimation() {
        this.isLoading = false;
        if (this.parentImageButton != null) {
            this.parentImageButton.turnOFF();
        } else if (this.parentLegendEntry != null) {
            Toaster.getInstance().addMessage("turnOFF");
        }
    }

    /**
     * Turns the loader on and performs special parent methods.
     */
    @Override
    public void startAnimation() {
        this.isLoading = true;
        setSrc(this.url);
        if (this.parentImageButton != null) {
            this.parentImageButton.turnON();
        } else if (this.parentLegendEntry != null) {
//            this.parentLegendEntry.setHeaderLoading();
            Toaster.getInstance().addMessage("turnON");
        }
    }

}