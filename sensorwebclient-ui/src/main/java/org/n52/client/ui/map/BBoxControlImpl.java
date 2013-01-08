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
