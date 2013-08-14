
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_PHENOMENA;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_PHENOMENA, produces = {"application/json"})
public class PhenomenonParameterController extends ParameterController {

    private ParameterService<PhenomenonOutput> phenomenonParameterService;

    public ModelAndView getCollection(@RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);
        int offset = map.getOffset();
        int size = map.getSize();
        
        if (map.isExpanded()) {
            Object[] result = phenomenonParameterService.getExpandedParameters(offset, size);

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        } else {
            Object[] result = phenomenonParameterService.getCondensedParameters(offset, size);

            // TODO add paging
            
            return new ModelAndView().addObject(result);
        }
    }

    public ModelAndView getItem(@PathVariable("item") String phenomenonId, @RequestParam(required=false) MultiValueMap<String, String> query) {
        QueryMap map = QueryMap.createFromQuery(query);

        // TODO check parameters and throw BAD_REQUEST if invalid

        PhenomenonOutput phenomenon = phenomenonParameterService.getParameter(phenomenonId);

        if (phenomenon == null) {
            throw new ResourceNotFoundException("Found no feature with given id.");
        }

        return new ModelAndView().addObject(phenomenon);
    }

    public ParameterService<PhenomenonOutput> getPhenomenonParameterService() {
        return phenomenonParameterService;
    }

    public void setPhenomenonParameterService(ParameterService<PhenomenonOutput> phenomenonParameterService) {
        this.phenomenonParameterService = phenomenonParameterService;
    }

}
