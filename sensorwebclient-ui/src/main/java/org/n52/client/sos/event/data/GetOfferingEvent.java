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

import org.eesgmbh.gimv.client.event.FilteredDispatchGwtEvent;
import org.n52.client.sos.event.data.handler.GetOfferingEventHandler;

public class GetOfferingEvent extends FilteredDispatchGwtEvent<GetOfferingEventHandler>{
	
	public static Type<GetOfferingEventHandler> TYPE = new Type<GetOfferingEventHandler>();
	
	private String serviceURL;
	
	private String offeringID;
	
	public GetOfferingEvent(String serviceURL, String offeringID) {
		this.serviceURL = serviceURL;
		this.offeringID = offeringID;
	}

	@Override
	protected void onDispatch(GetOfferingEventHandler handler) {
		handler.onGetOffering(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<GetOfferingEventHandler> getAssociatedType() {
		return TYPE;
	}

	public String getOfferingID() {
		return offeringID;
	}

	public String getServiceURL() {
		return serviceURL;
	}
	
}
