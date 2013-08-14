
package org.n52.web.v1.ctrl;

import static org.n52.io.MimeType.IMAGE_PNG;
import static org.n52.io.v1.data.DesignedParameterSet.createContextForSingleTimeseries;
import static org.n52.io.v1.data.UndesignedParameterSet.createForSingleTimeseries;
import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_TIMESERIES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.n52.io.IOFactory;
import org.n52.io.render.ChartRenderer;
import org.n52.io.render.RenderingContext;
import org.n52.io.v1.data.StyleProperties;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ServiceParameterService;
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
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_TIMESERIES, produces = {"application/json"})
public class TimeseriesDataController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TimeseriesDataController.class);

    private ServiceParameterService serviceParameterService;
    
    private TimeseriesMetadataService timeseriesMetadataService;

    private TimeseriesDataService timeseriesDataService;
    
    @RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_TIMESERIES + "/data", produces = "application/json", method = GET)
    public ModelAndView getTimeseriesData(@PathVariable String timeseriesId,
                                          @RequestParam(required = false) String timespan) {

        // TODO check parameters and throw BAD_REQUEST if invalid

        if ( !serviceParameterService.isKnownTimeseries(timeseriesId)) {
            throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
        }

        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, timespan);
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseries(parameters);

        // TODO add paging

        return new ModelAndView().addObject(timeseriesData);
    }

    @RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_TIMESERIES + "/data", produces = "image/png", method = GET)
    public void getTimeseriesCollection(HttpServletResponse response,
                                       @PathVariable String timeseriesId,
                                       @RequestParam(required = false) String timespan,
                                       @RequestParam(required = false) StyleProperties style) {

        if ( !serviceParameterService.isKnownTimeseries(timeseriesId)) {
            throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
        }

        response.setContentType(IMAGE_PNG.getMimeType());
        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
        RenderingContext context = createContextForSingleTimeseries(metadata, style);
        ChartRenderer renderer = IOFactory.create().createChartRenderer(context);
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, timespan);
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseries(parameters);
        try {
            renderer.writeToOutputStream(timeseriesData, response.getOutputStream());
        }
        catch (IOException e) {
            LOGGER.error("Error writing to output stream.");
        }
        finally {
            try {
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
            catch (IOException e) {
                LOGGER.debug("OutputStream already flushed and closed.");
            }
        }
    }

    public ServiceParameterService getServiceParameterService() {
        return serviceParameterService;
    }

    public void setServiceParameterService(ServiceParameterService serviceParameterService) {
        this.serviceParameterService = serviceParameterService;
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
