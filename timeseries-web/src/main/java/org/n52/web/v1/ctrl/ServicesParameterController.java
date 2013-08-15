
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_SERVICES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.n52.web.v1.ctrl.Stopwatch.startStopwatch;

import org.n52.io.v1.data.ServiceOutput;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_SERVICES, produces = {"application/json"})
public class ServicesParameterController extends ParameterController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ServicesParameterController.class);

    public ModelAndView getCollection(@RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);
        int offset = map.getOffset();
        int size = map.getSize();
        
        if (map.isExpanded()) {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = serviceParameterService.getExpandedParameters(offset, size);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        } else {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = serviceParameterService.getCondensedParameters(offset, size);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        }
    }

    public ModelAndView getItem(@PathVariable("item") String serviceId, @RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);

        // TODO check parameters and throw BAD_REQUEST if invalid

        Stopwatch stopwatch = startStopwatch();
        ServiceOutput service = serviceParameterService.getParameter(serviceId);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        if (service == null) {
            throw new ResourceNotFoundException("Found no service with given id.");
        }

        return new ModelAndView().addObject(service);
    }

}
