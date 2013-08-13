package org.n52.api.v1.srv;

import static org.n52.server.mgmt.ConfigurationContext.getSOSMetadatas;

import java.util.ArrayList;
import java.util.List;

import org.n52.io.v1.data.StationOutput;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.web.v1.srv.StationParameterService;

public class StationOutputAdapter implements StationParameterService {

	@Override
	public StationOutput[] getStation(int offset, int size) {
		List<StationOutput> allStations = new ArrayList<StationOutput>();
		for (SOSMetadata metadata : getSOSMetadatas()) {
		    
		    // TODO get station
		}
		return allStations.toArray(new StationOutput[0]);
	}

	@Override
	public StationOutput getStation(String stationId) {
		for (SOSMetadata metadata : getSOSMetadatas()) {

		    // TODO get station
		    
		}
		return null;
	}

}
