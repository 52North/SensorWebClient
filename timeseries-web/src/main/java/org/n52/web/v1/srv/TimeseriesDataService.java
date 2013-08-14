package org.n52.web.v1.srv;

import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.UndesignedParameterSet;

public interface TimeseriesDataService {

	TimeseriesDataCollection getTimeseries(UndesignedParameterSet parameters);

}
