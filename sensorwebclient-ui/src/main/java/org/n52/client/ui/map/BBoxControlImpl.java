/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.client.ui.map;

import org.gwtopenmaps.openlayers.client.Bounds;
import org.gwtopenmaps.openlayers.client.util.JSObject;

/**
 * The Class BBoxControlImpl.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class BBoxControlImpl {

    /** The bbox. */
    private Bounds bbox;

    /**
     * Creates the.
     * 
     * @return the jS object
     */
    public static native JSObject create()/*-{
                                          var control = new $wnd.OpenLayers.Control.ZoomIn();
                                          $wnd.OpenLayers.Util.extend(control, { 
                                  				draw: function () {
                                          			this.box = new $wnd.OpenLayers.Handler.Box( control, 
                                          														{"done": this.notice},
                                  	       														{keyMask: $wnd.OpenLayers.Handler.MOD_SHIFT});
                                          														this.box.activate();
                                      		}
                                          }); 
                                          return control;
                                          }-*/;

    /**
     * Creates the.
     * 
     * @param options
     *            the options
     * @return the jS object
     */
    public static native JSObject create(JSObject options)/*-{
                                                          return new $wnd.OpenLayers.Control.ZoomBox(options);
                                                          }-*/;

}
