
package org.n52.server.api.v0.ctrl;

import javax.servlet.http.HttpServletRequest;

import org.n52.server.api.v0.output.ModelAndViewPager;
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
@RequestMapping(value = "/v0/services", produces = {"text/html", "application/*"})
public class RestfulPhenomenonsController extends QueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_PHENOMENONS, method = RequestMethod.GET)
    public ModelAndView getPhenomenonsByGET(@PathVariable("instance") String instance,
                                            @RequestParam(value = KVP_SHOW, required = false) String details,
                                            @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                            @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size,
                                            @RequestParam(value = KVP_FEATURE, required = false) String feature,
                                            @RequestParam(value = KVP_PROCEDURE, required = false) String procedure,
                                            @RequestParam(value = KVP_OFFERING, required = false) String offering) throws Exception {

        QueryParameters parameters = new QueryParameters()
                .setProcedure(procedure)
                .setOffering(offering)
                .setFeature(feature);
        
        QueryResponse< ? > result = performQuery(instance, parameters);
        Phenomenon[] phenomenons = (Phenomenon[]) result.getResults();

        if (offset < 0) {
            return new ModelAndView("phenomenons").addObject("phenomenons", phenomenons);
        } else {
            return pageResults(phenomenons, offset, size);
        }
    }

    private ModelAndView pageResults(Phenomenon[] phenomenons, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("phenomenons");
        return mavPage.createPagedModelAndViewFrom(phenomenons, offset, size);
    }

    // this mapping handles identifier URLs
    @RequestMapping(value = "/{instance}/" + COLLECTION_PHENOMENONS + "/**", method = RequestMethod.GET)
    public ModelAndView getPhenomenonByID(@PathVariable(value = "instance") String instance,
                                          HttpServletRequest request) throws Exception {
        String phenomenon = getIndididuumIdentifierFor(COLLECTION_PHENOMENONS, request);
        return createResponseView(instance, phenomenon);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_PHENOMENONS + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String phenomenon) throws Exception {
        return createResponseView(instance, phenomenon);
    }

    private ModelAndView createResponseView(String instance, String phenomenon) throws Exception {
        ModelAndView mav = new ModelAndView("phenomenons");
        phenomenon = stripKnownFileExtensionFrom(phenomenon);
        QueryParameters parameters = new QueryParameters().setPhenomenon(phenomenon);
        QueryResponse< ? > result = performQuery(instance, parameters);

        if (result.getResults().length == 0) {
            throw new ResourceNotFoundException();
        }

        mav.addObject("phenomenon", result.getResults()[0]);
        return mav;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredPhenomenonQuery(parameters);
        return doQuery(query);
    }

}
