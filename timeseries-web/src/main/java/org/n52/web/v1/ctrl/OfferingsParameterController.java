package org.n52.web.v1.ctrl;


import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_OFFERINGS;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.out.Offering;
import org.n52.web.v1.srv.OfferingsParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_OFFERINGS, produces = {"application/json"})
public class OfferingsParameterController extends ParameterController implements RestfulKvp, RestfulUrls {
	
	private OfferingsParameterService service;

	public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		Offering[] allOfferings = service.getOfferings(offset, size);
		
		// TODO add paging
		
		// TODO add expand check
		
		return new ModelAndView().addObject(allOfferings);
	}
	
	public ModelAndView getItem(@PathVariable("item") String item) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		// TODO add expand check
		
		Offering offering = service.getOffering(item);
		
		return new ModelAndView().addObject(offering);
	}

	public OfferingsParameterService getService() {
		return service;
	}

	public void setService(OfferingsParameterService service) {
		this.service = service;
	}
	
}

