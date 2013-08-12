package org.n52.web.v1.ctrl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.n52.io.DataRenderer;
import org.n52.io.IOFactory;
import org.n52.io.v1.data.in.StyleOptions;
import org.n52.io.v1.data.in.UndesignedParameterSet;
import org.n52.io.v1.data.out.TimeseriesDataCollection;
import org.n52.io.v1.data.out.TimeseriesMetadata;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ServicesParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TimeseriesDataController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(TimeseriesDataController.class);
	
	private ServicesParameterService servicesParameterService; 

	private TimeseriesMetadataService timeseriesMetadataService;
	
	private TimeseriesDataService timeseriesDataService;
	
	private IOFactory ioFactory;
	
	@RequestMapping(value = "/v1/timeseries/{timeseriesId}/data", produces = "application/json", method = GET)
	public ModelAndView getTimeseriesData(@PathVariable String timeseriesId,  @RequestParam(required = false) String timespan) {

		// TODO check parameters and throw BAD_REQUEST if invalid
		
		if (!servicesParameterService.isKnownTimeseries(timeseriesId)) {
			throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
		}
		
		UndesignedParameterSet parameters = createUndesignedParamterSet(timeseriesId, timespan);
		TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseries(parameters);

		// TODO add paging

		return new ModelAndView().addObject(timeseriesData);
	}

	private UndesignedParameterSet createUndesignedParamterSet(String timeseriesId, String timespan) {
		UndesignedParameterSet parameters = new UndesignedParameterSet();
		parameters.setTimeseries(new String[] { timeseriesId });
		parameters.setTimespan(timespan);
		return parameters;
	}
	
	@RequestMapping(value = "/api/v1/timeseries/{timeseriesId}/data", produces = "image/png", method = GET)
	public void getOfferingsCollection(HttpServletResponse response, @PathVariable String timeseriesId,  @RequestParam(required = false) String timespan,
			@RequestParam(required = false) StyleOptions style) {
		
		if (!servicesParameterService.isKnownTimeseries(timeseriesId)) {
			throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
		}
		
		response.setContentType("image/png");
		UndesignedParameterSet parameters = createUndesignedParamterSet(timeseriesId, timespan);
		TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseries(parameters);
		DataRenderer renderer = ioFactory.createDataRenderer(timeseriesData, "image/png");
		try {
			TimeseriesMetadata metadata = timeseriesMetadataService.getMetadata(timeseriesId);
			renderer.renderToOutputStream(style, metadata, response.getOutputStream());
		} catch (IOException e) {
			LOGGER.error("Error writing to output stream.");
		} finally {
			try {
				response.getOutputStream().flush();
				response.getOutputStream().close();
			} catch (IOException e) {
				LOGGER.debug("OutputStream already flushed and closed.");
			}
		}
	}
	
	public IOFactory getIoFactory() {
		return ioFactory;
	}

	public void setIoFactory(IOFactory ioFactory) {
		this.ioFactory = ioFactory;
	}

	public ServicesParameterService getServicesParameterService() {
		return servicesParameterService;
	}

	public void setServicesParameterService(
			ServicesParameterService servicesParameterService) {
		this.servicesParameterService = servicesParameterService;
	}

	public TimeseriesMetadataService getTimeseriesMetadataService() {
		return timeseriesMetadataService;
	}

	public void setTimeseriesMetadataService(TimeseriesMetadataService timeseriesMetadataService) {
		this.timeseriesMetadataService = timeseriesMetadataService;
	}

	public TimeseriesDataService getTimeseriesDataService() {
		return timeseriesDataService;
	}

	public void setTimeseriesDataService(TimeseriesDataService timeseriesDataService) {
		this.timeseriesDataService = timeseriesDataService;
	}

}
