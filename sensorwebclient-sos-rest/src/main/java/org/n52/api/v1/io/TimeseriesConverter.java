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
package org.n52.api.v1.io;

import java.util.Set;

import org.n52.io.v1.data.CategoryOutput;
import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.ServiceOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesOutput;
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
        Procedure procedure = getLookup().getProcedure(timeseries.getProcedureId());
        convertedTimeseries.setParameters(getCondensedParameters(timeseries));
        convertedTimeseries.setRefValues(getReferenceValues(procedure));
        return convertedTimeseries;
    }

    @Override
    public TimeseriesMetadataOutput convertCondensed(SosTimeseries timeseries) {
        TimeseriesMetadataOutput convertedTimeseries = new TimeseriesMetadataOutput();
        convertedTimeseries.setLabel(timeseries.getLabel());
        convertedTimeseries.setStation(getCondensedStation(timeseries));
        Phenomenon phenomenon = getLookup().getPhenomenon(timeseries.getPhenomenonId());
        convertedTimeseries.setUom(phenomenon.getUnitOfMeasure());
        convertedTimeseries.setId(timeseries.getTimeseriesId());
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

    private String[] getReferenceValues(Procedure procedure) {
        Set<String> referenceValues = procedure.getReferenceValues().keySet();
        return !referenceValues.isEmpty()
                ? referenceValues.toArray(new String[0])
                : null; // will not be listed in output
    }

    private CategoryOutput getCondensedCategory(SosTimeseries timeseries) {
        
        // TODO do we want a category to be an identifiable object or is a string just fine?
        
        return null;
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
