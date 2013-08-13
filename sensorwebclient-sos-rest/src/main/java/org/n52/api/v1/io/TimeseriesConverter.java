package org.n52.api.v1.io;

import java.util.Map;

import org.n52.io.v1.data.StationOutput;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
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
        
        // TODO Auto-generated method stub
        
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput convertCondensed(SosTimeseries timeseries) {
        TimeseriesMetadataOutput convertedTimeseries = new TimeseriesMetadataOutput();
        convertedTimeseries.setStation(getCondensedStation(timeseries));
        convertedTimeseries.setId(timeseries.getTimeseriesId());
        
        Procedure procedure = getLookup().getProcedure(timeseries.getProcedureId());
        Map<String, ReferenceValue> refValues = procedure.getReferenceValues();
        for (String refValueId : refValues.keySet()) {
            ReferenceValue refValue = refValues.get(refValueId);
            
            // TODO create single TimeseriesValue for each ReferencePoint
            
        }
        // TODO first and last value
//        convertedTimeseries.setFirstValue(firstValue);
//        convertedTimeseries.setLastValue(lastValue);
        
        return null;
    }

    private StationOutput getCondensedStation(SosTimeseries timeseries) {
        Station station = getMetadata().getStationByTimeSeries(timeseries);
        StationConverter stationConverter = new StationConverter(getMetadata());
        StationOutput convertedStation = stationConverter.convertCondensed(station);
        return convertedStation;
    }

}
