
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_OFFERINGS;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.n52.web.v1.ctrl.Stopwatch.startStopwatch;

import org.n52.io.v1.data.OfferingOutput;
import org.n52.web.v1.srv.ParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_OFFERINGS, produces = {"application/json"})
public class OfferingsParameterController extends ParameterController implements RestfulUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(OfferingsParameterController.class);
    
    private ParameterService<OfferingOutput> offeringParameterService;

    public ModelAndView getCollection(@RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);
        int offset = map.getOffset();
        int size = map.getSize();
        
        if (map.isExpanded()) {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = offeringParameterService.getExpandedParameters(offset, size);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        } else {
            Stopwatch stopwatch = startStopwatch();
            Object[] result = offeringParameterService.getCondensedParameters(offset, size);
            LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        }
    }

    public ModelAndView getItem(@PathVariable("item") String offeringId, @RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);

        // TODO check parameters and throw BAD_REQUEST if invalid

        // TODO add expand check

        Stopwatch stopwatch = startStopwatch();
        OfferingOutput offering = offeringParameterService.getParameter(offeringId);
        LOGGER.debug("Processing request took {} seconds.", stopwatch.stopInSeconds());

        return new ModelAndView().addObject(offering);
    }

    public ParameterService<OfferingOutput> getOfferingParameterService() {
        return offeringParameterService;
    }

    public void setOfferingParameterService(ParameterService<OfferingOutput> offeringParameterService) {
        this.offeringParameterService = offeringParameterService;
    }

}
