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
package org.n52.client.eventBus.events.sos;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.sos.handler.GetStationsEventHandler;
import org.n52.shared.serializable.pojos.BoundingBox;

/**
 * The Class GetProcedurePositionsEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class GetStationsEvent extends
        FilteredDispatchGwtEvent<GetStationsEventHandler> {

    /** The TYPE. */
    public static Type<GetStationsEventHandler> TYPE =
            new Type<GetStationsEventHandler>();

    /** The sos url. */
    private String sosURL;

    /** The bbox. */
    private BoundingBox bbox;

    /**
     * Instantiates a new gets the procedure positions event.
     * 
     * @param sosURL
     *            the sos url
     * @param bbox
     *            the bbox
     * @param blockedHandlers
     *            the blocked handlers
     */
    public GetStationsEvent(String sosURL, BoundingBox bbox,
            GetStationsEventHandler... blockedHandlers) {
        super(blockedHandlers);
        this.sosURL = sosURL;
        this.bbox = bbox;
    }

    /**
     * Gets the sOSURL.
     * 
     * @return the sOSURL
     */
    public String getSOSURL() {
        return this.sosURL;
    }

    /**
     * Gets the b box.
     * 
     * @return the b box
     */
    public BoundingBox getBBox() {
        return this.bbox;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(GetStationsEventHandler handler) {
        handler.onGetProcedurePositions(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<GetStationsEventHandler> getAssociatedType() {
        return TYPE;
    }

}
