package org.n52.web.v1.srv;

import static org.n52.io.generalize.DouglasPeuckerGeneralizer.createNonConfigGeneralizer;

import org.n52.io.generalize.GeneralizerException;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneralizingTimeseriesDataService implements TimeseriesDataService {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GeneralizingTimeseriesDataService.class);
    
    private TimeseriesDataService composedService;

    public GeneralizingTimeseriesDataService(TimeseriesDataService toCompose) {
        this.composedService = toCompose;
    }

    @Override
    public TimeseriesDataCollection getTimeseriesData(UndesignedParameterSet parameters) {
        TimeseriesDataCollection ungeneralizedData = composedService.getTimeseriesData(parameters);
        try {
            return createNonConfigGeneralizer(ungeneralizedData).generalize();
        } catch (GeneralizerException e) {
            LOGGER.error("Could not generalize timeseries collection. Returning original data.", e);
            return ungeneralizedData;
        }
    }
    
    public static TimeseriesDataService composeDataService(TimeseriesDataService toCompose) {
        return new GeneralizingTimeseriesDataService(toCompose);
    }

}
