package org.n52.api.v1.srv;

import org.n52.io.v1.data.in.UndesignedParameterSet;
import org.n52.io.v1.data.out.TimeseriesDataCollection;
import org.n52.io.v1.data.out.TimeseriesMetadata;
import org.n52.server.mgmt.ConfigurationContext;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;

public class RestTimeseriesAPIAdapter implements TimeseriesDataService, TimeseriesMetadataService {

    private GetDataService dataService;

	@Override
	public TimeseriesDataCollection getTimeseries(UndesignedParameterSet parameters) {
		
		return dataService.getTimeSeriesFromParameterSet(parameters);
	}

	@Override
	public TimeseriesMetadata getMetadata(String timeseriesId) {
		for (SOSMetadata metadata : ConfigurationContext.getServiceMetadatas().values()) {
			Station station = metadata.getStationByTimeSeriesId(timeseriesId);
			if (station != null) {
				
				// TODO create TimeseriesMetadta
				
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
