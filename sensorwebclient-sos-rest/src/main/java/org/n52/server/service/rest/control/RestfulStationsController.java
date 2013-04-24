package org.n52.server.service.rest.control;

import org.n52.server.service.GetMetadataService;
import org.n52.server.service.rest.InternalServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public class RestfulStationsController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulStationsController.class);
    
    private static final String KVP_FILTER = "filter";
    
    private GetMetadataService metadataService;
    
    @RequestMapping(value = "/{instance}/stations", method = RequestMethod.GET)
    public ModelAndView getStation(@PathVariable(value="instance") String instance, 
                                   @RequestParam(value=KVP_FILTER, required=false) String filter,
                                   @RequestParam(value="offset", required=false) int offset,
                                   @RequestParam(value="size", required=false) int size) {
        try {
            ModelAndView mav = new ModelAndView("stations");
            if (filter != null) {
                if ("full".equalsIgnoreCase(filter)) {
//                    metadataService.getStations(query, instance);
                } 
            }
//            StationQueryResponse stations = (StationQueryResponse) metadataService.getStations(querySet, instance);
//            mav.addAllObjects(createStations(stations));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    public GetMetadataService getMetadataService() {
        return metadataService;
    }

    public void setMetadataService(GetMetadataService metadataService) {
        this.metadataService = metadataService;
    }

}
