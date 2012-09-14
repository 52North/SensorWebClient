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
package org.n52.client.eventBus.events.dataEvents.sos;

import java.util.Arrays;

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.eventBus.events.dataEvents.sos.handler.GetPhenomenonsEventHandler;

public class GetPhenomenonsEvent extends FilteredDispatchGwtEvent<GetPhenomenonsEventHandler> {

    public static Type<GetPhenomenonsEventHandler> TYPE = new Type<GetPhenomenonsEventHandler>();

    private String sosURL;

    public static class Builder {
		private String serviceURL; // required
		private GetPhenomenonsEventHandler[] blockedHandlers = new GetPhenomenonsEventHandler[0];
    	
    	public Builder(String sosURL) {
    		this.serviceURL = sosURL;
    	}

    	public Builder addBlockedHandlers(GetPhenomenonsEventHandler... handlers) {
    		this.blockedHandlers = handlers;
    		return this;
    	}
    	public GetPhenomenonsEvent build() {
    		return new GetPhenomenonsEvent(this);
    	}
    	
    	String getServiceURL() {
    		return this.serviceURL;
    	}
    	
    	GetPhenomenonsEventHandler[] getBlockedHandlers() {
    		return blockedHandlers;
    	}
    	
    }

    private GetPhenomenonsEvent(Builder builder) {
		this.sosURL = builder.getServiceURL();
		GetPhenomenonsEventHandler[] handlers = builder.getBlockedHandlers();
		getBlockedHandlers().addAll(Arrays.asList(handlers));
	}

    public String getSosURL() {
        return this.sosURL;
    }

    @Override
    protected void onDispatch(GetPhenomenonsEventHandler handler) {
        handler.onGetPhenomena(this);
    }

    @Override
    public Type<GetPhenomenonsEventHandler> getAssociatedType() {
        return TYPE;
    }

}
