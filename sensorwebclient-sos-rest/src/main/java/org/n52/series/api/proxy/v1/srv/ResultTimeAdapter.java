/**
 * Copyright (C) 2012-2016 52°North Initiative for Geospatial Open Source
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
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.n52.series.api.proxy.v1.srv;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.apache.xmlbeans.XmlObject;
import org.n52.io.request.IoParameters;
import org.n52.oxf.OXFException;
import org.n52.oxf.adapter.ParameterContainer;
import org.n52.oxf.adapter.OperationResult;
import org.n52.oxf.ows.capabilities.Operation;
import org.n52.sensorweb.spi.ResultTimeService;
import org.n52.server.da.AccessorThreadPool;
import org.n52.server.da.oxf.OperationAccessor;
import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;
import org.n52.server.util.SosAdapterFactory;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.slf4j.LoggerFactory;

public class ResultTimeAdapter implements ResultTimeService {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(ResultTimeService.class);

    @Override
    public ArrayList<String> getResultTimeList(IoParameters parameters, String timeseriesId) {
        String start = parameters.getOther("start");
        String end = parameters.getOther("end");
        if (start == null || end == null) {
            LOGGER.error("Missing start or end parameter.");
        }
        for (SOSMetadata metadata : getSOSMetadatas()) {
            try {
                Station station = metadata.getStationByTimeSeriesId(timeseriesId);
                SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
                FutureTask<OperationResult> futureTask = new FutureTask<OperationResult>(createGDA(timeseries, metadata, start, end));
                AccessorThreadPool.execute(futureTask);
                OperationResult result = futureTask.get(metadata.getTimeout(), TimeUnit.MILLISECONDS);
                if (result == null) {
                    LOGGER.error("Get no result for GDA request");
                }
                XmlObject result_xb = XmlObject.Factory.parse(result.getIncomingResultAsStream());
                return getResultTimes(result_xb);
            } catch (Exception ex) {
                LOGGER.error("Get no result for GDA request", ex);
            }
        }
        return null;
    }

    private Callable<OperationResult> createGDA(SosTimeseries timeseries, SOSMetadata metadata, String start, String end) throws OXFException {
        ParameterContainer container = new ParameterContainer();
        container.addParameterShell("procedure", timeseries.getProcedure().getProcedureId());
        container.addParameterShell("observedProperty", timeseries.getPhenomenon().getPhenomenonId());
        container.addParameterShell("featureOfInterest", timeseries.getFeature().getFeatureId());
        container.addParameterShell("version", metadata.getVersion());
        String[] array = new String[2];
        array[0] = start;
        array[1] = end;
        container.addParameterShell("phenomenonTime", array);
        Operation operation = new Operation("GetDataAvailability", metadata.getServiceUrl(), metadata.getServiceUrl());
        return new OperationAccessor(SosAdapterFactory.createSosAdapter(metadata), operation, container);
    }

    public ArrayList<String> getResultTimes(XmlObject result_xb) {
        ArrayList<String> timestamps = new ArrayList<String>();
        String queryExpression = "declare namespace gda='http://www.opengis.net/sosgda/1.0'; $this/gda:GetDataAvailabilityResponse/gda:dataAvailabilityMember";
        XmlObject[] response = result_xb.selectPath(queryExpression);
        if (response == null || response.length == 0) {
            queryExpression = "declare namespace gda='http://www.opengis.net/sos/2.0'; $this/gda:GetDataAvailabilityResponse/gda:dataAvailabilityMember";
            response = result_xb.selectPath(queryExpression);
        }
        for (XmlObject xmlObject : response) {
            try {
                XmlObject[] extension = xmlObject.selectChildren("http://www.opengis.net/sosgda/1.0", "extension");
                XmlObject[] dataRecord = extension[0].selectChildren("http://www.opengis.net/swe/2.0", "DataRecord");
                XmlObject[] fields = dataRecord[0].selectChildren("http://www.opengis.net/swe/2.0", "field");
                for (XmlObject field : fields) {
                    XmlObject[] time = field.selectChildren("http://www.opengis.net/swe/2.0", "Time");
                    XmlObject[] value = time[0].selectChildren("http://www.opengis.net/swe/2.0", "value");
                    timestamps.add(value[0].newCursor().getTextValue());
                }
            } catch (Exception e) {
                LOGGER.warn("Find no result times and return with an empty list.", e);
            }
        }
        return timestamps;
    }

}
