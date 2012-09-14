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
package org.n52.client.view.gui.widgets.mapping;

import org.gwtopenmaps.openlayers.client.control.Control;
import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 * The Class BBoxControl.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class BBoxControl extends Control {

    /**
     * Instantiates a new b box control.
     * 
     * @param element
     *            the element
     */
    protected BBoxControl(JSObject element) {
        super(element);
    }

    /**
     * Instantiates a new b box control.
     */
    public BBoxControl() {
        this(BBoxControlImpl.create());
    }

    /**
     * Instantiates a new b box control.
     * 
     * @param options
     *            the options
     */
    public BBoxControl(BBoxControlOptions options) {
        this(BBoxControlImpl.create(options.getJSObject()));
    }

}
