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
package org.n52.client.sos.event.data;

import java.util.Collection;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.StorePhenomenaEventHandler;
import org.n52.shared.serializable.pojos.sos.Phenomenon;

/**
 * The Class StorePhenomenaEvent.
 * 
 * @author <a href="mailto:f.bache@52north.de">Felix Bache</a>
 */
public class StorePhenomenaEvent extends FilteredDispatchGwtEvent<StorePhenomenaEventHandler> {

    /** The TYPE. */
    public static Type<StorePhenomenaEventHandler> TYPE = new Type<StorePhenomenaEventHandler>();

    /** The sos url. */
    private String sosURL;

    /** The off parameterId. */
    private String offID;

    /** The phenomena. */
    private Collection<Phenomenon> phenomenons;

    /**
     * Instantiates a new store phenomena event.
     * 
     * @param sosURL
     *            the sos url
     * @param offID
     *            the off parameterId
     * @param phenomena
     *            the phenomena
     * @param blockedhandlers
     *            the blockedhandlers
     */
    public StorePhenomenaEvent(String sosURL, String offID, Collection<Phenomenon> phenomenons,
            StorePhenomenaEventHandler... blockedhandlers) {
        super(blockedhandlers);
        this.sosURL = sosURL;
        this.offID = offID;
        this.phenomenons = phenomenons;
    }

    /**
     * Gets the sos url.
     * 
     * @return the sos url
     */
    public String getSosURL() {
        return this.sosURL;
    }

    /**
     * Gets the off parameterId.
     * 
     * @return the off parameterId
     */
    public String getOffID() {
        return this.offID;
    }

    /**
     * Gets the phenomena.
     * 
     * @return the phenomena
     */
    public Collection<Phenomenon> getPhenomenons() {
        return this.phenomenons;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent#onDispatch(com
     * .google.gwt.event.shared.EventHandler)
     */
    @Override
    protected void onDispatch(StorePhenomenaEventHandler handler) {
        handler.onStore(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
     */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<StorePhenomenaEventHandler> getAssociatedType() {
        return TYPE;
    }

}
