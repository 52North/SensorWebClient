
package org.n52.web.v1.ctrl;

import static org.n52.io.MimeType.APPLICATION_PDF;
import static org.n52.io.v1.data.DesignedParameterSet.createContextForSingleTimeseries;
import static org.n52.io.v1.data.UndesignedParameterSet.createForSingleTimeseries;
import static org.n52.web.v1.ctrl.QueryMap.createFromQuery;
import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_TIMESERIES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.n52.web.v1.ctrl.Stopwatch.startStopwatch;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.n52.io.IOFactory;
import org.n52.io.IOHandler;
import org.n52.io.TimeseriesIOException;
import org.n52.io.img.RenderingContext;
import org.n52.io.v1.data.DesignedParameterSet;
import org.n52.io.v1.data.TimeseriesDataCollection;
import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.io.v1.data.UndesignedParameterSet;
import org.n52.web.BaseController;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ServiceParameterService;
import org.n52.web.v1.srv.TimeseriesDataService;
import org.n52.web.v1.srv.TimeseriesMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_TIMESERIES, produces = {"application/json"})
public class TimeseriesDataController extends BaseController {

    private final static Logger LOGGER = LoggerFactory.getLogger(TimeseriesDataController.class);

    private ServiceParameterService serviceParameterService;

    private TimeseriesMetadataService timeseriesMetadataService;

    private TimeseriesDataService timeseriesDataService;

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"application/json"}, method = GET)
    public ModelAndView getTimeseriesData(HttpServletResponse response,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required = false) MultiValueMap<String, String> query) {

        checkIfUnknownTimeseries(timeseriesId);

        QueryMap map = createFromQuery(query);
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, map.getTimespan());

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        // TODO add paging

        return new ModelAndView().addObject(timeseriesData.getAllTimeseries());
    }

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"application/pdf"}, method = GET)
    public void getTimeseriesReport(HttpServletResponse response,
                                    @PathVariable String timeseriesId,
                                    @RequestParam(required = false) MultiValueMap<String, String> query) throws Exception {

        checkIfUnknownTimeseries(timeseriesId);

        /*
         * The following code is copied code from #getTimeseriesCollection ... do refactor
         */
        QueryMap map = createFromQuery(query);
        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
        RenderingContext context = createContextForSingleTimeseries(metadata, map.getStyle());
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, map.getTimespan());
        IOHandler renderer = IOFactory.create()
                .forMimeType(APPLICATION_PDF)
                .inLanguage(map.getLanguage())
                .createIOHandler(context);

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());
        // end of copied code

        try {
            renderer.generateOutput(timeseriesData);
            renderer.encodeAndWriteTo(response.getOutputStream());
        }
        catch (IOException e) {
            LOGGER.error("Error handling output stream.");
            throw e; // handled by BaseController
        }
        catch (TimeseriesIOException e) {
            LOGGER.error("Could not write report to stream.");
            throw e; // handled by BaseController
        }
    }

    private void checkIfUnknownTimeseries(String timeseriesId) {
        if ( !serviceParameterService.isKnownTimeseries(timeseriesId)) {
            throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
        }
    }

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"image/png"}, method = GET)
    public void getTimeseriesChart(HttpServletResponse response,
                                   @PathVariable String timeseriesId,
                                   @RequestParam(required = false) MultiValueMap<String, String> query) throws Exception {

        /*
         * If anything goes wrong or is invalid, no appropriate exception view can be resolved because we are
         * writing on the output stream directly.
         * 
         * TODO check how BaseController is able to resolve json view when exceptions occur
         */

        checkIfUnknownTimeseries(timeseriesId);

        QueryMap map = createFromQuery(query);
        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
        RenderingContext context = createContextForSingleTimeseries(metadata, map.getStyle());
        setChartDimension(context.getChartStyleDefinitions(), map);
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, map.getTimespan());
        IOHandler renderer = IOFactory.create().inLanguage(map.getLanguage()).createIOHandler(context);

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        try {
            renderer.generateOutput(timeseriesData);
            renderer.encodeAndWriteTo(response.getOutputStream());
        }
        catch (IOException e) {
            LOGGER.error("Error handling output stream.");
            throw e; // handled by BaseController
        }
        catch (TimeseriesIOException e) {
            LOGGER.error("Could not write chart image to stream.");
            throw e; // handled by BaseController
        }
    }

    private void setChartDimension(DesignedParameterSet chartStyleDefinitions, QueryMap map) {
        chartStyleDefinitions.setWidth(map.getWidth());
        chartStyleDefinitions.setHeight(map.getHeight());
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
