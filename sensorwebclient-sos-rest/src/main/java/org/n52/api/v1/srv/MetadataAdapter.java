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
package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.HashSet;
import java.util.Set;

import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.MetadataService;

public class MetadataAdapter implements MetadataService {

	@Override
	public int getServiceCount() {
		return getSOSMetadatas().size();
	}

	@Override
	public int getStationsCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getStations().size();
		}
		return count;
	}

	@Override
	public int getTimeseriesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			for (Station station : metadata.getStations()) {
				count += station.getObservedTimeseries().size();
			}
		}
		return count;
	}

	@Override
	public int getOfferingsCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getOfferings().size();
		}
		return count;
	}

	@Override
	public int getCategoriesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			Set<String> categorieSet = new HashSet<String>();
			SosTimeseries[] timeseries = metadata.getTimeseriesRelatedWith(QueryParameters.createEmptyFilterQuery());
			for (SosTimeseries timeserie : timeseries) {
				categorieSet.add(timeserie.getCategory());
			}
			count += categorieSet.size();
		}
		return count;
	}

	@Override
	public int getFeaturesCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getFeatures().size();
		}
		return count;
	}

	@Override
	public int getProceduresCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getProcedures().size();
		}
		return count;
	}

	@Override
	public int getPhenomenaCount() {
		int count = 0;
		for (SOSMetadata metadata : getSOSMetadatas()) {
			count += metadata.getTimeseriesParametersLookup().getPhenomenons().size();
		}
		return count;
	}

}
