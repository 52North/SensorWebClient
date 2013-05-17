
package org.n52.server.service.rest.control;

import javax.servlet.http.HttpServletRequest;

import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Station;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulStationsController extends TimeseriesParameterQueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS, method = RequestMethod.GET)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance,
                                          @RequestParam(value = KVP_SHOW, required = false) String details,
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset,
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size,
                                          @RequestParam(value = KVP_FEATURE, required = false) String feature,
                                          @RequestParam(value = KVP_PHENOMENON, required = false) String phenomenon,
                                          @RequestParam(value = KVP_PROCEDURE, required = false) String procedure,
                                          @RequestParam(value = KVP_OFFERING, required = false) String offering) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = new QueryParameters()
                .setPhenomenon(phenomenon)
                .setProcedure(procedure)
                .setOffering(offering)
                .setFeature(feature);

        QueryResponse< ? > result = performQuery(instance, parameters);
        Station[] stations = (Station[]) result.getResults();

        if (offset != null) {
            return pageResults(stations, offset.intValue(), size.intValue());
        }

        ModelAndView mav = new ModelAndView("stations");
        return mav.addObject("stations", stations);
    }

    private ModelAndView pageResults(Station[] stations, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("stations");
        return mavPage.createPagedModelAndViewFrom(stations, offset, size);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS + "/**", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                        HttpServletRequest request) throws Exception {
        String station = getIndididuumIdentifierFor(COLLECTION_STATIONS, request);
        return createResponseView(instance, station);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String station) throws Exception {
        return createResponseView(instance, station);
    }

    private ModelAndView createResponseView(String instance, String station) throws Exception {
        ModelAndView mav = new ModelAndView("stations");
        station = stripKnownFileExtensionFrom(station);
        QueryParameters parameters = new QueryParameters().setStation(station);
        QueryResponse< ? > result = performQuery(instance, parameters);

        if (result.getResults().length == 0) {
            throw new ResourceNotFoundException();
        }
        
        mav.addObject("station", result.getResults()[0]);
        return mav;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredStationQuery(parameters);
        return doQuery(query);
    }

}