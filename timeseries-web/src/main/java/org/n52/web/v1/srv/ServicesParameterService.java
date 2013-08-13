package org.n52.web.v1.srv;

import org.n52.io.v1.data.ServiceOutput;

public interface ServicesParameterService {

	public boolean isKnownTimeseries(String id);

	public ServiceOutput[] getServices(int offset, int size);

	public ServiceOutput getService(String item);

}
