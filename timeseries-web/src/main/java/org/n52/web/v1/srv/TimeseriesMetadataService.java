package org.n52.web.v1.srv;

import org.n52.io.v1.data.TimeseriesMetadataOutput;

public interface TimeseriesMetadataService {

	TimeseriesMetadataOutput getMetadata(String timeseriesId);

}
