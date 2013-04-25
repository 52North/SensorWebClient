
package org.n52.server.service.rest.control;

import static org.n52.server.service.rest.control.RestfulUrls.PATH_INSTANCES;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.QueryParameters;
import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.server.service.rest.model.ServiceInstance;
import org.n52.shared.requests.query.PageResult;
import org.n52.shared.requests.query.ProcedureQuery;
import org.n52.shared.requests.query.responses.ProcedureQueryResponse;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Procedure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulProceduresController extends TimeseriesParameterController implements RestfulKvp, RestfulUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulProceduresController.class);

//    @RequestMapping(value = "/{instance}/procedures", method = RequestMethod.POST)
//    public ModelAndView getProceduresByPOST(@PathVariable("instance") String instance, 
//                                            @RequestBody QueryParameters querySet) {
//        try {
//            ModelAndView mav = new ModelAndView("procedures");
//            
//            ProcedureQueryResponse procedures = (ProcedureQueryResponse) getProcedures(querySet, instance);
//            mav.addObject(procedures.getProcedure());
//            mav.addObject("test", "check");
//            return mav;
//        }
//        catch (Exception e) {
//            LOGGER.error("Could not create response.", e);
//            throw new InternalServiceException();
//        }
//    }

    @RequestMapping(value = "/{instance}/" + PATH_PROCEDURES)
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance, 
                                          @RequestParam(value = KVP_SHOW, required = false) String details, 
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset, 
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        // TODO condense output depending on 'show' parameter

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
        QueryResponse< ? > result = doQuery(createProcedureQuery(parameters, instance));
        Procedure[] procedures = (Procedure[]) result.getResultSubset().getResults();

        if (offset == null) {
            ModelAndView mav = new ModelAndView("procedures");
            return mav.addObject(procedures);
        }
        else {
            ModelAndViewPager mavPage = createResultPage(offset.intValue(), size.intValue(), procedures);
            return mavPage.getPagedModelAndView();
        }
    }
    
    private ModelAndViewPager createResultPage(int offset, int size, Procedure[] procedures) {
        ModelAndViewPager mavPage = new ModelAndViewPager("procedures");
        if (offset <= procedures.length) {
            Procedure[] subset = Arrays.copyOfRange(procedures, offset, size);
            mavPage.setPage(new PageResult<Procedure>(offset, procedures.length, subset));
        }
        return mavPage;
    }

    @RequestMapping(value = "/{instance}/" + PATH_PROCEDURES + "/{id}")
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance, 
                                         @PathVariable(value = "id") String procedure) throws Exception {
        ModelAndView mav = new ModelAndView("procedures");
        QueryParameters parameters = new QueryParameters().addProcedure(procedure);
        QueryResponse< ? > result = doQuery(createProcedureQuery(parameters, instance));
        Procedure[] procedures = (Procedure[]) result.getResultSubset().getResults();
        if (procedures.length == 0) {
            throw new ResourceNotFoundException();
        } else {
            mav.addObject(procedures[0]);
        }
        return mav;
    }

    private QueryParameters createQueryParameters(String offering, String phenomenon, String feature, String procedure) {
        QueryParameters query = new QueryParameters();

        if (offering != null) {
            query.addOffering(offering);
        }

        if (phenomenon != null) {
            query.addPhenomenon(phenomenon);
        }

        if (feature != null) {
            query.addFeatureOfInterest(feature);
        }

        if (procedure != null) {
            query.addProcedure(procedure);
        }
        
        return query;
    }
}
