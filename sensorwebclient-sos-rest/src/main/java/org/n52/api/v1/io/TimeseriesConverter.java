package org.n52.api.v1.io;

import org.n52.io.v1.data.TimeseriesOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;

public class TimeseriesConverter extends OutputConverter<SosTimeseries, TimeseriesOutput> {

    public TimeseriesConverter(SOSMetadata metadata) {
        super(metadata);
    }

    @Override
    public TimeseriesOutput convertExpanded(SosTimeseries timeseries) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesOutput convertCondensed(SosTimeseries timeseries) {
        // TODO Auto-generated method stub
        return null;
        
    }

}
