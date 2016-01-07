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
package org.n52.series.api.proxy.v1.io;

import static java.lang.System.currentTimeMillis;

import java.util.ArrayList;
import java.util.List;
import org.n52.io.response.v1.CategoryOutput;
import org.n52.io.response.v1.FeatureOutput;
import org.n52.io.response.v1.OfferingOutput;
import org.n52.io.response.v1.PhenomenonOutput;
import org.n52.io.response.v1.ProcedureOutput;
import org.n52.io.response.ReferenceValueOutput;
import org.n52.io.response.TimeseriesMetadataOutput;
import org.n52.io.response.v1.ServiceOutput;
import org.n52.io.response.v1.StationOutput;
import org.n52.io.response.v1.TimeseriesOutput;
import org.n52.io.response.TimeseriesValue;
import org.n52.io.response.v1.SeriesMetadataV1Output;

import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class TimeseriesConverter extends OutputConverter<SosTimeseries, TimeseriesMetadataOutput> {

//    private GetDataService dataService;

    public TimeseriesConverter(SOSMetadata metadata) {
        super(metadata);
//        this.dataService = dataService;
    }

    @Override
    public TimeseriesMetadataOutput convertExpanded(SosTimeseries timeseries) {
        TimeseriesMetadataOutput convertedTimeseries = convertCondensed(timeseries);
        convertedTimeseries.setParameters(getCondensedParameters(timeseries));
        convertedTimeseries.setReferenceValues(getReferenceValues(timeseries));
        convertedTimeseries.setRawFormats(getMetadata().getObservationFormats());
        return convertedTimeseries;
    }

    @Override
    public TimeseriesMetadataOutput convertCondensed(SosTimeseries timeseries) {
        SeriesMetadataV1Output convertedTimeseries = new SeriesMetadataV1Output();
        Phenomenon phenomenon = getLookup().getPhenomenon(timeseries.getPhenomenonId());
        convertedTimeseries.setStation(getCondensedStation(timeseries));
        convertedTimeseries.setUom(phenomenon.getUnitOfMeasure());
        convertedTimeseries.setId(timeseries.getTimeseriesId());
        convertedTimeseries.setLabel(timeseries.getLabel());
        return convertedTimeseries;
    }

    private TimeseriesOutput getCondensedParameters(SosTimeseries timeseries) {
        TimeseriesOutput timeseriesOutput = new TimeseriesOutput();
        timeseriesOutput.setFeature(getCondensedFeature(timeseries));
        timeseriesOutput.setOffering(getCondensedOffering(timeseries));
        timeseriesOutput.setProcedure(getCondensedProcedure(timeseries));
        timeseriesOutput.setPhenomenon(getCondensedPhenomenon(timeseries));
        timeseriesOutput.setCategory(getCondensedCategory(timeseries));
        timeseriesOutput.setService(getCondensedService(timeseries));
        return timeseriesOutput;
    }

    private ServiceOutput getCondensedService(SosTimeseries timeseries) {
        ServiceConverter converter = new ServiceConverter(getMetadata());
        return converter.convertCondensed(getMetadata());
    }

    private ReferenceValueOutput[] getReferenceValues(SosTimeseries timeseries) {
        Procedure procedure = getLookup().getProcedure(timeseries.getProcedureId());
        List<ReferenceValueOutput> referenceValues = new ArrayList<ReferenceValueOutput>();
        for (String refValueName : procedure.getReferenceValues().keySet()) {
            ReferenceValueOutput converted = new ReferenceValueOutput();
            ReferenceValue value = procedure.getRefValue(refValueName);
            String referenceValueId = value.getGeneratedGlobalId(timeseries.getTimeseriesId());
            converted.setReferenceValueId(referenceValueId);
            TimeseriesValue lastValue = value.getValues().length == 1
                    ? createEverValidReferenceValue(value.getLastValue())
                    : value.getLastValue();
            converted.setLastValue(lastValue);
            converted.setLabel(value.getId());
            referenceValues.add(converted);
        }
        return referenceValues.toArray(new ReferenceValueOutput[0]);
    }

    private TimeseriesValue createEverValidReferenceValue(TimeseriesValue lastValue) {
        return new TimeseriesValue(currentTimeMillis(), lastValue.getValue());
    }

    private CategoryOutput getCondensedCategory(SosTimeseries timeseries) {
        CategoryConverter converter = new CategoryConverter(getMetadata());
        return converter.convertCondensed(timeseries.getCategory());
    }

    private OfferingOutput getCondensedOffering(SosTimeseries timeseries) {
        OfferingConverter coverter = new OfferingConverter(getMetadata());
        return coverter.convertCondensed(timeseries.getOffering());
    }
    
    private ProcedureOutput getCondensedProcedure(SosTimeseries timeseries) {
        ProcedureConverter coverter = new ProcedureConverter(getMetadata());
        return coverter.convertCondensed(timeseries.getProcedure());
    }
    
    private PhenomenonOutput getCondensedPhenomenon(SosTimeseries timeseries) {
        PhenomenonConverter coverter = new PhenomenonConverter(getMetadata());
        return coverter.convertCondensed(timeseries.getPhenomenon());
    }

    private FeatureOutput getCondensedFeature(SosTimeseries timeseries) {
        FeatureConverter coverter = new FeatureConverter(getMetadata());
        return coverter.convertCondensed(timeseries.getFeature());
    }

    private StationOutput getCondensedStation(SosTimeseries timeseries) {
        Station station = getMetadata().getStationByTimeSeries(timeseries);
        StationConverter stationConverter = new StationConverter(getMetadata());
        return stationConverter.convertCondensed(station);
    }

}
