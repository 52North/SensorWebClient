package org.n52.web.v1.ctrl;


import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_SERVICES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.out.Service;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ServicesParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_SERVICES, produces = {"application/json"})
public class ServicesParameterController extends ParameterController {
	
	private ServicesParameterService servicesParameterService;

	public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid

		Service[] allServices = servicesParameterService.getServices(offset, size);
		
		// TODO add paging
		
		return new ModelAndView().addObject(allServices);
	}
	
	public ModelAndView getItem(@PathVariable("item") String item) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		Service service = servicesParameterService.getService(item);
		
		if (service == null) {
			throw new ResourceNotFoundException("Found no service with given id.");
		}
		
		return new ModelAndView().addObject(service);
	}

	public ServicesParameterService getServicesParameterService() {
		return servicesParameterService;
	}

	public void setServicesParameterService(ServicesParameterService servicesParameterService) {
		this.servicesParameterService = servicesParameterService;
	}
	
}

