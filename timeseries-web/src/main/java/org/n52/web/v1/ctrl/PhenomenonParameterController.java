
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_OBSERVED_PROPERTIES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.PhenomenonOutput;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.PhenomenaParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_OBSERVED_PROPERTIES, produces = {"application/json"})
public class PhenomenonParameterController extends ParameterController {

    private PhenomenaParameterService phenomenonParameterService;

    public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                      @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {

        // TODO check parameters and throw BAD_REQUEST if invalid

        PhenomenonOutput[] allPhenomena = phenomenonParameterService.getPhenomena(offset, size);

        // TODO add paging

        return new ModelAndView().addObject(allPhenomena);
    }

    public ModelAndView getItem(@PathVariable("item") String item) {

        // TODO check parameters and throw BAD_REQUEST if invalid

        PhenomenonOutput phenomenon = phenomenonParameterService.getPhenomenon(item);

        if (phenomenon == null) {
            throw new ResourceNotFoundException("Found no feature with given id.");
        }

        return new ModelAndView().addObject(phenomenon);
    }

    public PhenomenaParameterService getPhenomenonParameterService() {
        return phenomenonParameterService;
    }

    public void setPhenomenonParameterService(PhenomenaParameterService phenomenonParameterService) {
        this.phenomenonParameterService = phenomenonParameterService;
    }

}
