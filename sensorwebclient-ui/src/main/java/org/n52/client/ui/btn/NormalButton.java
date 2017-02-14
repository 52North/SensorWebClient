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

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;

/**
 * Wrapperclass for a normal button.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class NormalButton extends Button {

    /** The width. */
    private int width = 120;

    /** The icon. */
    private String icon;

    /** The title. */
    private String title;

    /** The show roll over. */
    private boolean showRollOver = false;

    /** The show down. */
    private boolean showDown = true;

    /** The icon alignment. */
    private String iconAlignment = Alignment.LEFT.getValue();

    /**
     * Instantiates a new normal button.
     * 
     * @param icon
     *            the icon
     * @param title
     *            the title
     */
    public NormalButton(String icon, String title) {

        this.icon = icon;
        this.title = title;

        this.setTitle(this.title);
        this.setIcon(this.icon);
        this.setIconAlign(this.iconAlignment);
        this.setShowRollOver(this.showRollOver);
        this.setShowDown(this.showDown);
        this.setWidth(this.width);

    }

}