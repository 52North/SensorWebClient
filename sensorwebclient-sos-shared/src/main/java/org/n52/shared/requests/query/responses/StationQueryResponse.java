package org.n52.shared.requests.query.responses;

import org.n52.shared.serializable.pojos.sos.Station;

public class StationQueryResponse extends QueryResponse<Station> {
	
	private static final long serialVersionUID = -2689753333997419445L;

	private Station[] stations = new Station[0];
	
	public StationQueryResponse() {
		// for serialization
	}

	public Station[] getStations() {
		return stations;
	}

	public void setStations(Station[] stations) {
		this.stations = stations;
	}

}
