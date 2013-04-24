package org.n52.server.service.rest.model;

import org.n52.shared.requests.query.PageResult;
import org.springframework.web.servlet.ModelAndView;

public class ModelAndViewPager {
    
    private ModelAndView mav;
    
    public ModelAndViewPager(String viewName) {
        this.mav = new ModelAndView(viewName);
    }

    public void setPage(PageResult<?> pageResult) {
        mav.addObject(pageResult);
    }
    
    public ModelAndView getPagedModelAndView() {
        return mav;
    }

}
