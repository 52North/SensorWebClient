package org.n52.web.v1.srv;

import org.n52.io.v1.data.ServiceOutput;

public interface ServiceParameterService extends ParameterService<ServiceOutput> {

    public boolean isKnownTimeseries(String timeseriesId);
}
