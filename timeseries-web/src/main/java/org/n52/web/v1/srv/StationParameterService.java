package org.n52.web.v1.srv;

import org.n52.io.v1.data.StationOutput;

public interface StationParameterService {

    StationOutput[] getStation(int offset, int size);

    StationOutput getStation(String stationId);

}
