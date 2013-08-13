package org.n52.web.v1.ctrl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.n52.io.v1.data.TimeseriesMetadata;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ServicesParameterService;
import org.n52.web.v1.srv.TimeseriesMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public class TimeseriesMetadataController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TimeseriesMetadataController.class);
	
	private ServicesParameterService serviceInstancesService; 

	private TimeseriesMetadataService timeseriesMetadataService;
	
	@RequestMapping(value = "/api/v1/timeseries/{timeseriesId}", produces = "application/json", method = GET)
	public ModelAndView getTimeseriesData(@PathVariable String timeseriesId,  @RequestParam(required = false) String timespan) {

		// TODO check parameters and throw BAD_REQUEST if invalid
		
		if (!serviceInstancesService.isKnownTimeseries(timeseriesId)) {
			throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
		}
		
		TimeseriesMetadata timeseriesMetaData = timeseriesMetadataService.getMetadata(timeseriesId);
		
		// TODO add paging

		return new ModelAndView().addObject(timeseriesMetaData);
	}

	public TimeseriesMetadataService getService() {
		return timeseriesMetadataService;
	}

	public void setService(TimeseriesMetadataService service) {
		this.timeseriesMetadataService = service;
	}

}
