
package org.n52.server.api.v0.ctrl;

import java.io.IOException;
import java.util.Collection;

import org.n52.server.api.v0.output.ModelAndViewPager;
import org.n52.server.api.v0.output.ServiceInstance;
import org.n52.server.service.ServiceInstancesService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@RequestMapping(value = "/v0/services", produces = {"text/html", "application/*"})
public class RestfulServiceInstancesController implements RestfulKvp {

    private ServiceInstancesService serviceInstancesService;
    
    @RequestMapping(value = "/")
    public String forwardToServicesSite() throws IOException {
        return "redirect:/api/v0/services";
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
                                          @RequestParam(value = KVP_SHOW, required = false) String filter) {
        ModelAndView mav = new ModelAndView("services");
        ServiceInstance serviceInstance = serviceInstancesService.getServiceInstance(service);
        mav.addObject("service", serviceInstance);
        return mav;
    }

    public ServiceInstancesService getServiceInstancesService() {
        return serviceInstancesService;
    }

    public void setServiceInstancesService(ServiceInstancesService serviceInstancesService) {
        this.serviceInstancesService = serviceInstancesService;
    }

}
