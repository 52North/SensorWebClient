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
import org.n52.client.sos.event.data.handler.StoreFeatureEventHandler;
import org.n52.shared.serializable.pojos.sos.Feature;

public class StoreFeatureEvent extends FilteredDispatchGwtEvent<StoreFeatureEventHandler> {
	
	public static Type<StoreFeatureEventHandler> TYPE = new Type<StoreFeatureEventHandler>();
	
	private Feature feature;
	
	private String serviceURL;
	
	public StoreFeatureEvent(String serviceURL, Feature feature, StoreFeatureEventHandler... blockHandlers) {
		super(blockHandlers);
		this.serviceURL = serviceURL;
		this.feature = feature;
	}
	
	@Override
	protected void onDispatch(StoreFeatureEventHandler handler) {
		handler.onStore(this);
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<StoreFeatureEventHandler> getAssociatedType() {
		return TYPE;
	}

	public Feature getFeature() {
		return feature;
	}

	public String getServiceURL() {
		return serviceURL;
	}

}
