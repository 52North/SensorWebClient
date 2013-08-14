package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getServiceMetadatas;

import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;

public class TimeseriesOutputAdapter implements TimeseriesDataService, TimeseriesMetadataService {

    private GetDataService dataService;

	@Override
	public TimeseriesDataCollection getTimeseries(UndesignedParameterSet parameters) {
		return dataService.getTimeSeriesFromParameterSet(parameters);
	}


    @Override
    public TimeseriesMetadataOutput[] getExpandedParameters(int offset, int size) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput[] getCondensedParameters(int offset, int size) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public TimeseriesMetadataOutput getParameter(String timeseriesId) {
        for (SOSMetadata metadata : getServiceMetadatas().values()) {
            Station station = metadata.getStationByTimeSeriesId(timeseriesId);
            if (station != null) {
                
                // TODO create TimeseriesMetadata
                
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
