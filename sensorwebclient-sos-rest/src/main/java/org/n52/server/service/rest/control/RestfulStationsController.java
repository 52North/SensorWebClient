package org.n52.server.service.rest.control;

import org.n52.server.service.rest.QueryParameters;
import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.ResultPage;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Station;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulStationsController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {
    
    @RequestMapping(value = "/{instance}/" + PATH_STATIONS)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance, 
                                          @RequestParam(value = KVP_SHOW, required = false) String details, 
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset, 
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
        QueryResponse< ? > result = doQuery(createProcedureQuery(parameters, instance));
        Station[] stations = (Station[]) result.getResults();

        if (offset == null) {
            ModelAndView mav = new ModelAndView("stations");
            return mav.addObject(stations);
        }
        else {
            ModelAndViewPager mavPage = createResultPage(offset.intValue(), size.intValue(), stations);
            return mavPage.getPagedModelAndView();
        }
    }
    
    private ModelAndViewPager createResultPage(int offset, int size, Station[] procedures) {
        ModelAndViewPager mavPage = new ModelAndViewPager("stations");
        mavPage.setPage(ResultPage.createPageFrom(procedures, offset, size));
        return mavPage;
    }

    @RequestMapping(value = "/{instance}/" + PATH_STATIONS + "/{id}")
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance, 
                                         @PathVariable(value = "id") String procedure) throws Exception {
        ModelAndView mav = new ModelAndView("procedures");
        QueryParameters parameters = new QueryParameters().addProcedure(procedure);
        QueryResponse< ? > result = doQuery(createProcedureQuery(parameters, instance));
        Station[] stations = (Station[]) result.getResults();
        if (stations == null || stations.length == 0) {
            throw new ResourceNotFoundException();
        }
        mav.addObject(stations[0]);
        return mav;
    }

}