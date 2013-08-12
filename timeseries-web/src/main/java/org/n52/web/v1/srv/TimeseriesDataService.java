package org.n52.web.v1.srv;

import org.n52.io.v1.data.in.UndesignedParameterSet;
import org.n52.io.v1.data.out.TimeseriesDataCollection;

public interface TimeseriesDataService {

	TimeseriesDataCollection getTimeseries(UndesignedParameterSet parameters);

}
