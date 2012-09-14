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
package org.n52.client.view.gui.widgets;

import org.n52.client.model.communication.LoaderManager;

/**
 * The Class LoadingSpinner.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class LoadingSpinner extends Loader {

    /** The loader url. */
    private String loaderUrl = "../img/mini_loader_bright.gif"; //$NON-NLS-1$

    /** The blank url. */
    private String blankUrl = "../img/blank.gif"; //$NON-NLS-1$

    /** The width. */
    private int width = 16;

    /** The height. */
    private int height = 16;

    /**
     * Instantiates a new loading spinner.
     */
    public LoadingSpinner() {

        init();

    }

    /**
     * Instantiates a new loading spinner.
     * 
     * @param url
     *            the url
     * @param width
     *            the width
     * @param height
     *            the height
     */
    public LoadingSpinner(String url, int width, int height) {

        this.loaderUrl = url;
        this.width = width;
        this.height = height;
        init();
    }

    /**
     * Inits the.
     */
    private void init() {

        setSrc(this.blankUrl);
        setWidth(this.width);
        setHeight(this.height);

        LoaderManager.getInstance().registerLoader(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.view.gui.passive.Loader#turnOFF()
     */
    @Override
    public void stopAnimation() {
        setSrc(this.blankUrl);

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.n52.client.view.gui.passive.Loader#turnON()
     */
    @Override
    public void startAnimation() {
        setSrc(this.loaderUrl);

    }

}
