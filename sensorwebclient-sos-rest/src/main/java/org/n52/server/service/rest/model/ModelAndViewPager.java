package org.n52.server.service.rest.model;

import org.n52.shared.requests.query.ResultPage;
import org.springframework.web.servlet.ModelAndView;

public class ModelAndViewPager {
    
    private ModelAndView mav;
    
    public ModelAndViewPager(String viewName) {
        this.mav = new ModelAndView(viewName);
    }

    public void setPage(ResultPage<?> pageResult) {
        mav.addObject(pageResult);
    }
    
    public ModelAndView getPagedModelAndView() {
        return mav;
    }

}
