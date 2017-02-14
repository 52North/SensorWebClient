/**
 * Copyright (C) 2012-2017 52°North Initiative for Geospatial Open Source
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
package org.n52.server.service;

import org.n52.client.service.TimeSeriesDataService;
import org.n52.server.io.TimeseriesDataGenerator;
import org.n52.shared.requests.TimeSeriesDataRequest;
import org.n52.shared.responses.TimeSeriesDataResponse;
import org.n52.shared.serializable.pojos.DesignOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesDataServiceImpl implements TimeSeriesDataService {

    private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesDataServiceImpl.class);
    
    @Override
    public TimeSeriesDataResponse getTimeSeriesData(TimeSeriesDataRequest request) throws Exception {
        try {
            LOG.debug("Performing TimeSeries data request.");
            DesignOptions options = request.getOptions();
            TimeseriesDataGenerator generator = new TimeseriesDataGenerator();
            TimeSeriesDataResponse resp = (TimeSeriesDataResponse) generator.producePresentation(options);
            LOG.debug(resp.toDebugString());
            return resp;
        } catch (Exception e) {
            LOG.error("Exception occured on server side.", e);
            throw e; // last chance to log on server side
        }
    }

}
