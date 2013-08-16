
package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.web.v1.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class ResourcesController {

    @RequestMapping("/")
    public ModelAndView getResources() {
        return new ModelAndView().addObject(Resources.get());
    }
}
