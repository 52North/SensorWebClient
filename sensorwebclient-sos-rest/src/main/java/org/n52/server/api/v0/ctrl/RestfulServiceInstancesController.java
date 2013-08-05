
package org.n52.server.api.v0.ctrl;

import static org.n52.server.api.v0.ctrl.RestfulServiceInstancesController.DEFAULT_PATH;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;

import org.n52.server.api.v0.output.ModelAndViewPager;
import org.n52.server.api.v0.output.ServiceInstance;
import org.n52.server.service.ServiceInstancesService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = DEFAULT_PATH, produces = { "application/json*" })
public class RestfulServiceInstancesController implements RestfulKvp {
    
    
    static final String DEFAULT_PATH = "/v0/services";
    
    private ServiceInstancesService serviceInstancesService;
    
    @RequestMapping(value = "/")
    public String forwardToServicesSite() throws IOException {
        return "redirect:" + DEFAULT_PATH;
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getInstancesByGET(@RequestParam(value = KVP_SHOW, required = false) String details,
                                          @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                          @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size) {

        // TODO condense output depending on 'show' parameter

        Collection<ServiceInstance> instances = serviceInstancesService.getServiceInstances();

        if (offset < 0) {
            return new ModelAndView("services").addObject("services", instances);
        } else {
            return pageResults(instances, offset, size);
        }
    }

    private ModelAndView pageResults(Collection<ServiceInstance> services, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("services");
        return mavPage.createPagedModelAndViewFrom(services, offset, size);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModelAndView getInstancesByGET(@PathVariable(value = "id") String service,
                                          @RequestParam(value = KVP_SHOW, required = false) String filter) throws Exception {
        ModelAndView mav = new ModelAndView("services");
        String decodedServiceId = doubleDecode(service);
        ServiceInstance serviceInstance = serviceInstancesService.getServiceInstance(decodedServiceId);
        mav.addObject("service", serviceInstance);
        return mav;
    }

    private String doubleDecode(String service) throws UnsupportedEncodingException {
        return URLDecoder.decode(service, "utf-8");
    }

    public ServiceInstancesService getServiceInstancesService() {
        return serviceInstancesService;
    }

    public void setServiceInstancesService(ServiceInstancesService serviceInstancesService) {
        this.serviceInstancesService = serviceInstancesService;
    }

}
