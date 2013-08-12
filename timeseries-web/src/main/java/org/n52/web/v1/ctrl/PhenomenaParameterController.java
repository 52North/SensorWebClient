package org.n52.web.v1.ctrl;


import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_OBSERVED_PROPERTIES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.out.Phenomenon;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.PhenomenaParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_OBSERVED_PROPERTIES, produces = {"application/json"})
public class PhenomenaParameterController extends ParameterController {
	
	private PhenomenaParameterService phenomenaParameterService;

	public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid

		Phenomenon[] allPhenomena = phenomenaParameterService.getPhenomena(offset, size);
		
		// TODO add paging
		
		return new ModelAndView().addObject(allPhenomena);
	}
	
	public ModelAndView getItem(@PathVariable("item") String item) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		Phenomenon phenomenon = phenomenaParameterService.getPhenomenon(item);
		
		if (phenomenon == null) {
			throw new ResourceNotFoundException("Found no feature with given id.");
		}
		
		return new ModelAndView().addObject(phenomenon);
	}

	public PhenomenaParameterService getPhenomenaParameterService() {
		return phenomenaParameterService;
	}

	public void setPhenomenaParameterService(
			PhenomenaParameterService phenomenaParameterService) {
		this.phenomenaParameterService = phenomenaParameterService;
	}

}

