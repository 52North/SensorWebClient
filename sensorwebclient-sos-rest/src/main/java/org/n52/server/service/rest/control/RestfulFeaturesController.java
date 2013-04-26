
package org.n52.server.service.rest.control;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.FeatureOfInterest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulFeaturesController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + PATH_FEATURES)
    public ModelAndView getFeaturesByGET(@PathVariable("instance") String instance, 
                                          @RequestParam(value = KVP_SHOW, required = false) String details, 
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset, 
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        FeatureOfInterest[] features = (FeatureOfInterest[]) result.getResults();

        if (offset != null) {
            return pageResults(features, offset.intValue(), size.intValue());
        }
        
        ModelAndView mav = new ModelAndView("features");
        return mav.addObject(features);
    }
    
    @RequestMapping(value = "/{instance}/" + PATH_FEATURES + "/{id}")
    public ModelAndView getFeatureByGET(@PathVariable(value = "instance") String instance, 
                                         @PathVariable(value = "id") String feature) throws Exception {
        ModelAndView mav = new ModelAndView("features");
        QueryParameters parameters = new QueryParameters().setFeature(feature);
        QueryResponse< ? > result = performQuery(instance, parameters);
        
        FeatureOfInterest[] features = (FeatureOfInterest[]) result.getResults();
        if (features.length == 0) {
            throw new ResourceNotFoundException();
        } else {
            mav.addObject(features[0]);
        }
        return mav;
    }

    private ModelAndView pageResults(FeatureOfInterest[] features, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("features");
        return mavPage.createPagedModelAndViewFrom(features, offset, size);
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredFeaturesQuery(parameters);
        return doQuery(query);
    }

}
