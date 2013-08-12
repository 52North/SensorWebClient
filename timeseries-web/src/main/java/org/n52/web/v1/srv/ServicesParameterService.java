package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.Service;

public interface ServicesParameterService {

	public boolean isKnownTimeseries(String id);

	public Service[] getServices(int offset, int size);

	public Service getService(String item);

}
