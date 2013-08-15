package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.api.v1.io.TimeseriesConverter;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;

public class TimeseriesOutputAdapter implements TimeseriesDataService, TimeseriesMetadataService {

    private GetDataService dataService;

	@Override
	public TimeseriesDataCollection getTimeseriesData(UndesignedParameterSet parameters) {
		return dataService.getTimeSeriesFromParameterSet(parameters);
	}


    @Override
    public TimeseriesMetadataOutput[] getExpandedParameters(int offset, int size) {
        List<TimeseriesMetadataOutput> allProcedures = new ArrayList<TimeseriesMetadataOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            for (Station station : metadata.getStations()) {
                SosTimeseries[] timeseriesAsArray = getTimeseriesAsArray(station);
                allProcedures.addAll(converter.convertExpanded(timeseriesAsArray));
            }
        }
        return allProcedures.toArray(new TimeseriesMetadataOutput[0]);
    }


    @Override
    public TimeseriesMetadataOutput[] getCondensedParameters(int offset, int size) {
        List<TimeseriesMetadataOutput> allProcedures = new ArrayList<TimeseriesMetadataOutput>();
        for (SOSMetadata metadata : getSOSMetadatas()) {
            TimeseriesConverter converter = new TimeseriesConverter(metadata);
            for (Station station : metadata.getStations()) {
                SosTimeseries[] timeseriesAsArray = getTimeseriesAsArray(station);
                allProcedures.addAll(converter.convertCondensed(timeseriesAsArray));
            }
        }
        return allProcedures.toArray(new TimeseriesMetadataOutput[0]);
    }

    private SosTimeseries[] getTimeseriesAsArray(Station station) {
        ArrayList<SosTimeseries> timeseries = station.getObservedTimeseries();
        return timeseries.toArray(new SosTimeseries[0]);
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String timeseriesId) {
        for (SOSMetadata metadata : getSOSMetadatas()) {
            Station station = metadata.getStationByTimeSeriesId(timeseriesId);
            if (station != null) {
                TimeseriesConverter converter = new TimeseriesConverter(metadata);
                SosTimeseries timeseries = station.getTimeseriesById(timeseriesId);
                return converter.convertExpanded(timeseries);
            }
        }
        return null;
    }


    public GetDataService getDataService() {
        return dataService;
    }

    public void setDataService(GetDataService dataService) {
        this.dataService = dataService;
    }

}
