
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_TIMESERIES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.TimeseriesMetadataOutput;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.TimeseriesMetadataService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_TIMESERIES, produces = {"application/json"})
public class TimeseriesMetadataController extends ParameterController {

    private TimeseriesMetadataService timeseriesMetadataService;

    @Override
    public ModelAndView getCollection(@RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);
        int offset = map.getOffset();
        int size = map.getSize();
        
        if (map.isExpanded()) {
            Object[] result = timeseriesMetadataService.getExpandedParameters(offset, size);

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        } else {
            Object[] result = timeseriesMetadataService.getCondensedParameters(offset, size);

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        }
    }

    @Override
    public ModelAndView getItem(@PathVariable("item") String timeseriesId, @RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);

        // TODO check parameters and throw BAD_REQUEST if invalid

        TimeseriesMetadataOutput metadata = timeseriesMetadataService.getParameter(timeseriesId);

        if (metadata == null) {
            throw new ResourceNotFoundException("The timeseries with id '" + timeseriesId + "' was not found.");
        }

        return new ModelAndView().addObject(metadata);
    }

    public TimeseriesMetadataService getTmeseriesMetadataService() {
        return timeseriesMetadataService;
    }

    public void setTimeseriesMetadataService(TimeseriesMetadataService timeseriesMetadataService) {
        this.timeseriesMetadataService = timeseriesMetadataService;
    }

}
