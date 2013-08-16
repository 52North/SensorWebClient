
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
import org.n52.io.render.ChartRenderer;
import org.n52.io.render.RenderingContext;
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

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"application/json", "application/pdf"}, method = GET)
    public ModelAndView getTimeseriesData(HttpServletResponse response,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required = false) String timespan) {

        checkIfUnknownTimeseries(timeseriesId);
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, timespan);

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        // TODO add paging

        return new ModelAndView().addObject(timeseriesData.getAllTimeseries());
    }

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"application/pdf"}, method = GET)
    public ModelAndView getTimeseriesData(HttpServletResponse response,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required=false) MultiValueMap<String, String> query) {

        QueryMap map = createFromQuery(query);
        checkIfUnknownTimeseries(timeseriesId);

        /*
         * The following code is copied code from #getTimeseriesCollection ... do refactor
         */
        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
        RenderingContext context = createContextForSingleTimeseries(metadata, map.getStyle());
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, map.getTimespan());
        ChartRenderer renderer = IOFactory.create()
                .forMimeType(APPLICATION_PDF)
                .createChartRenderer(context);

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());
        // end of copied code

        /*
         * TODO generate PDF report
         */

        return new ModelAndView().addObject(null);
    }

    private void checkIfUnknownTimeseries(String timeseriesId) {
        if ( !serviceParameterService.isKnownTimeseries(timeseriesId)) {
            throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
        }
    }

    @RequestMapping(value = "/{timeseriesId}/data", produces = {"image/png"}, method = GET)
    public void getTimeseriesCollection(HttpServletResponse response,
                                        @PathVariable String timeseriesId,
                                        @RequestParam(required=false) MultiValueMap<String, String> query) {

        checkIfUnknownTimeseries(timeseriesId);
        
        QueryMap map = createFromQuery(query);
        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);
        RenderingContext context = createContextForSingleTimeseries(metadata, map.getStyle());
        UndesignedParameterSet parameters = createForSingleTimeseries(timeseriesId, map.getTimespan());
        ChartRenderer renderer = IOFactory.create().createChartRenderer(context);

        Stopwatch stopwatch = startStopwatch();
        TimeseriesDataCollection timeseriesData = timeseriesDataService.getTimeseriesData(parameters);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        try {
            renderer.renderChart(timeseriesData);
            renderer.writeChartTo(response.getOutputStream());
        }
        catch (IOException e) {
            LOGGER.error("Error handling output stream.");
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
