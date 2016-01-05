/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui;

import org.n52.client.ctrl.LoaderManager;

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
