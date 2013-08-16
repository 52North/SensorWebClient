
package org.n52.web.v1.ctrl;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import org.n52.web.v1.srv.ServiceParameterService;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

public abstract class ParameterController extends ResourcesController implements RestfulUrls {

    protected ServiceParameterService serviceParameterService;

    @RequestMapping(method = GET)
    public abstract ModelAndView getCollection(MultiValueMap<String, String> query);

    @RequestMapping(value = "/{item}", method = GET)
    public abstract ModelAndView getItem(String item, MultiValueMap<String, String> query);


    public ServiceParameterService getServiceParameterService() {
        return serviceParameterService;
    }

    public void setServiceParameterService(ServiceParameterService serviceParameterService) {
        this.serviceParameterService = serviceParameterService;
    }

}
