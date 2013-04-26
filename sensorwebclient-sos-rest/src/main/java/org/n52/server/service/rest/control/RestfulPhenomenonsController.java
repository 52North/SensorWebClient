
package org.n52.server.service.rest.control;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulPhenomenonsController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + PATH_PHENOMENONS, method = RequestMethod.GET)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance,
                                          @RequestParam(value = KVP_SHOW, required = false) String details,
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset,
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Phenomenon[] phenomenons = (Phenomenon[]) result.getResults();

        if (offset != null) {
            return pageResults(phenomenons, offset.intValue(), size.intValue());
        }

        ModelAndView mav = new ModelAndView("phenomenons");
        return mav.addObject(phenomenons);
    }

    @RequestMapping(value = "/{instance}/" + PATH_PHENOMENONS + "/{id}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String phenomenon) throws Exception {
        ModelAndView mav = new ModelAndView("phenomenons");
        QueryParameters parameters = new QueryParameters().setPhenomenon(phenomenon);
        QueryResponse< ? > result = performQuery(instance, parameters);

        Phenomenon[] phenomenons = (Phenomenon[]) result.getResults();
        if (phenomenons.length == 0) {
            throw new ResourceNotFoundException();
        }
        else {
            mav.addObject(phenomenons[0]);
        }
        return mav;
    }

    private ModelAndView pageResults(Phenomenon[] phenomenons, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("phenomenons");
        return mavPage.createPagedModelAndViewFrom(phenomenons, offset, size);
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredPhenomenonQuery(parameters);
        return doQuery(query);
    }

}
