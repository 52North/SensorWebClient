/**
 * Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
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
package org.n52.series.api.proxy.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.HashSet;
import java.util.Set;

import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.serializable.pojos.sos.Category;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.sensorweb.v1.spi.CountingMetadataService;

public class MetadataAdapter implements CountingMetadataService {

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
			Set<Category> categorieSet = new HashSet<Category>();
			SosTimeseries[] timeseries = metadata.getMatchingTimeseries(QueryParameters.createEmptyFilterQuery());
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
