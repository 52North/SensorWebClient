package org.n52.api.v1.io;

import java.util.Map;

import org.n52.io.v1.data.FeatureOutput;
import org.n52.io.v1.data.OfferingOutput;
import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.io.v1.data.ProcedureOutput;
import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.TimeseriesOutput;
import org.n52.shared.serializable.pojos.ReferenceValue;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;

public class TimeseriesConverter extends OutputConverter<SosTimeseries, TimeseriesMetadataOutput> {

    public TimeseriesConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public TimeseriesMetadataOutput convertExpanded(SosTimeseries timeseries) {
        TimeseriesMetadataOutput convertedTimeseries = convertCondensed(timeseries);
        Procedure procedure = getLookup().getProcedure(timeseries.getProcedureId());
        Map<String, ReferenceValue> refValues = procedure.getReferenceValues();
        for (String refValueId : refValues.keySet()) {
            ReferenceValue refValue = refValues.get(refValueId);
            
            // TODO create single TimeseriesValue for each ReferencePoint
            
        }
        // TODO first and last value
//        convertedTimeseries.setFirstValue(firstValue);
//        convertedTimeseries.setLastValue(lastValue);
        
        // TODO Auto-generated method stub
        
        return convertedTimeseries;
        
    }

    @Override
    public TimeseriesMetadataOutput convertCondensed(SosTimeseries timeseries) {
        TimeseriesMetadataOutput convertedTimeseries = new TimeseriesMetadataOutput();
        convertedTimeseries.setParameters(getCondensedParameters(timeseries));
        convertedTimeseries.setStation(getCondensedStation(timeseries));
        convertedTimeseries.setId(timeseries.getTimeseriesId());
        return convertedTimeseries;
    }

    private TimeseriesOutput getCondensedParameters(SosTimeseries timeseries) {
        TimeseriesOutput timeseriesOutput = new TimeseriesOutput();
        timeseriesOutput.setFeature(getCondensedFeature(timeseries));
        timeseriesOutput.setOffering(getCondensedOffering(timeseries));
        timeseriesOutput.setProcedure(getCondensedProcedure(timeseries));
        timeseriesOutput.setPhenomenon(getCondensedPhenomenon(timeseries));
        return timeseriesOutput;
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
