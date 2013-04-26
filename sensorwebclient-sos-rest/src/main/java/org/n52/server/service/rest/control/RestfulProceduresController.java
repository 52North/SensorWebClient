
package org.n52.server.service.rest.control;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulProceduresController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + PATH_PROCEDURES, method = RequestMethod.GET)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance,
                                          @RequestParam(value = KVP_SHOW, required = false) String details,
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset,
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Procedure[] procedures = (Procedure[]) result.getResults();

        if (offset != null) {
            return pageResults(procedures, offset.intValue(), size.intValue());
        }

        ModelAndView mav = new ModelAndView("procedures");
        return mav.addObject(procedures);
    }

    @RequestMapping(value = "/{instance}/" + PATH_PROCEDURES + "/{id}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String procedure) throws Exception {
        ModelAndView mav = new ModelAndView("procedures");
        QueryParameters parameters = new QueryParameters().setProcedure(procedure);
        QueryResponse< ? > result = performQuery(instance, parameters);
        Procedure[] procedures = (Procedure[]) result.getResults();
        if (procedures.length == 0) {
            throw new ResourceNotFoundException();
        }
        else {
            mav.addObject(procedures[0]);
        }
        return mav;
    }

    private ModelAndView pageResults(Procedure[] procedures, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("procedures");
        return mavPage.createPagedModelAndViewFrom(procedures, offset, size);
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredProcedureQuery(parameters);
        return doQuery(query);
    }

}
