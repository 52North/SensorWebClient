
package org.n52.server.service.rest.control;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import javax.servlet.http.HttpServletRequest;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Feature;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulFeaturesController extends TimeseriesParameterQueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES, method = RequestMethod.GET)
    public ModelAndView getFeaturesByGET(@PathVariable("instance") String instance,
                                         @RequestParam(value = KVP_SHOW, required = false) String details,
                                         @RequestParam(value = KVP_OFFSET, required = false) Integer offset,
                                         @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Feature[] features = (Feature[]) result.getResults();

        if (offset != null) {
            return pageResults(features, offset.intValue(), size.intValue());
        }

        ModelAndView mav = new ModelAndView("features");
        return mav.addObject("features", features);
    }

    private ModelAndView pageResults(Feature[] features, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("features");
        return mavPage.createPagedModelAndViewFrom(features, offset, size);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES + "/**", method = RequestMethod.GET)
    public ModelAndView getFeatureByGET(@PathVariable(value = "instance") String instance,
                                        HttpServletRequest request) throws Exception {
        String feature = getIndididuumIdentifierFor(COLLECTION_FEATURES, request);
        return createResponseView(instance, feature);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_FEATURES + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getFeatureByGET(@PathVariable(value = "instance") String instance,
                                        @PathVariable(value = "id") String feature) throws Exception {
        return createResponseView(instance, feature);
    }

    private ModelAndView createResponseView(String instance, String feature) throws Exception {
        ModelAndView mav = new ModelAndView("features");
        feature = stripKnownFileExtensionFrom(feature);
        QueryParameters parameters = new QueryParameters().setFeature(feature);
        QueryResponse< ? > result = performQuery(instance, parameters);

        if (result.getResults().length == 0) {
            throw new ResourceNotFoundException();
        }
        
        mav.addObject("feature", result.getResults()[0]);
        return mav;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredFeaturesQuery(parameters);
        return doQuery(query);
    }

}
