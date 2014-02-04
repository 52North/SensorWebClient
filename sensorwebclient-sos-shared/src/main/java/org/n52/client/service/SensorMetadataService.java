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
package org.n52.client.service;

import org.n52.shared.responses.GetProcedureDetailsUrlResponse;
import org.n52.shared.responses.SOSMetadataResponse;
import org.n52.shared.responses.SensorMetadataResponse;
import org.n52.shared.serializable.pojos.TimeseriesProperties;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public interface SensorMetadataService {

    /**
     * Assembles metadata for given timeseries' properties object. Metadata has to be set within the passed
     * object which is set and returned via {@link SensorMetadataResponse}. This is needed to keep reference
     * infos set by the client.
     * 
     * @param properties
     *        the timeseries' properties to fill. Holds reference information important for the client later
     *        on.
     * @return the sensor metadata response containing the assembled metadata of the timeseries.
     * @throws Exception
     *         if assembling metdata fails for some reason.
     */
    public SensorMetadataResponse getSensorMetadata(final TimeseriesProperties properties) throws Exception;

    public GetProcedureDetailsUrlResponse getProcedureDetailsUrl(final SosTimeseries timeseries) throws Exception;

    public SOSMetadataResponse getUpdatedSOSMetadata();

}
