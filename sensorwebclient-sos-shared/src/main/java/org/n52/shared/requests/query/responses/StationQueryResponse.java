package org.n52.shared.requests.query.responses;

import java.util.ArrayList;
import java.util.List;

import org.n52.shared.serializable.pojos.sos.Station;

public class StationQueryResponse extends QueryResponse {
	
	private static final long serialVersionUID = -2689753333997419445L;

	private List<Station> stations;
	
	public StationQueryResponse() {
		// for serialization
	}

	public List<Station> getStations() {
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public void addStation(Station station) {
		if (stations == null) {
			stations = new ArrayList<Station>();
		}
		stations.add(station);		
	}

}
