package org.n52.web.v1.srv;

import org.n52.io.v1.data.out.TimeseriesMetadata;

public interface TimeseriesMetadataService {

	TimeseriesMetadata getMetadata(String timeseriesId);

}
