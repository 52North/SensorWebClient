package org.n52.server.service.rest.control;

import java.util.Collection;

import org.n52.server.service.ServiceInstancesService;
import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.server.service.rest.model.ServiceInstance;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(produces = {"text/html", "application/*"})
public class RestfulServiceInstancesController implements RestfulKvp {
    
    private ServiceInstancesService serviceInstancesService;
    
    @RequestMapping(value = "/services/{id}", method=RequestMethod.GET)
    public ModelAndView getInstancesByGET(@PathVariable(value="id") String id, 
                                          @RequestParam(value=KVP_SHOW, required=false) String filter) {
        ServiceInstance serviceInstance = serviceInstancesService.getServiceInstance(id);
        if (serviceInstance == null) {
            throw new ResourceNotFoundException();
        }
        ModelAndView mav = new ModelAndView("services");
        mav.addObject(serviceInstance);
        return mav;
    }
    
    @RequestMapping(value = "/services", method=RequestMethod.GET)
    public ModelAndView getInstancesByGET(@RequestParam(value=KVP_SHOW, required=false) String details,
                                          @RequestParam(value=KVP_OFFSET, required=false) Integer offset,
                                          @RequestParam(value=KVP_SIZE, required=false, defaultValue=KVP_DEFAULT_SIZE) Integer size) {
        
        // TODO condense output depending on 'show' parameter
        
        Collection<ServiceInstance> instances = serviceInstancesService.getServiceInstances();

        if (offset != null) {
            return pageResults(instances, offset.intValue(), size.intValue());
        }
        
        ModelAndView mav = new ModelAndView("services");
        return mav.addObject(instances);
    }

    private ModelAndView pageResults(Collection<ServiceInstance> services, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("services");
        return mavPage.createPagedModelAndViewFrom(services, offset, size);
    }

    public ServiceInstancesService getServiceInstancesService() {
        return serviceInstancesService;
    }

    public void setServiceInstancesService(ServiceInstancesService serviceInstancesService) {
        this.serviceInstancesService = serviceInstancesService;
    }

}
