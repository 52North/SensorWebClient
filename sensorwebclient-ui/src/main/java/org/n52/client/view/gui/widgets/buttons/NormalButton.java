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