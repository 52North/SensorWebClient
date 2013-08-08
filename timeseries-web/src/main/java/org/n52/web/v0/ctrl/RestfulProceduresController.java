
package org.n52.web.v0.ctrl;

import static org.n52.web.v0.ctrl.RestfulUrls.DEFAULT_PATH;
import static org.n52.io.v0.output.ProcedureOutput.createCompleteProcedureOutput;
import static org.n52.io.v0.output.ProcedureOutput.createSimpleProcedureOutput;

import javax.servlet.http.HttpServletRequest;

import org.n52.io.v0.output.ModelAndViewPager;
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
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class RestfulProceduresController extends QueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_PROCEDURES, method = RequestMethod.GET)
    public ModelAndView getProceduresByGET(@PathVariable("instance") String instance,
                                           @RequestParam(value = KVP_SHOW, required = false) String details,
                                           @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                           @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size) throws Exception {

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
        QueryResponse< ? > result = performQuery(instance, parameters);
        Procedure[] procedures = (Procedure[]) result.getResults();

        if (offset < 0) {
            return new ModelAndView("procedures").addObject("procedures", createSimpleProcedureOutput(procedures));
        } else {
            return pageResults(procedures, offset, size);
        }
    }

    private ModelAndView pageResults(Procedure[] procedures, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("procedures");
        return mavPage.createPagedModelAndViewFrom(procedures, offset, size);
    }

    // this mapping handles identifier URLs
    @RequestMapping(value = "/{instance}/" + COLLECTION_PROCEDURES + "/**", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         HttpServletRequest request) throws Exception {
        String procedure = getDecodedIndividuumIdentifierFor(COLLECTION_PROCEDURES, request);
        return createResponseView(instance, procedure);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_PROCEDURES + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String procedure) throws Exception {
        return createResponseView(instance, decode(procedure));
    }

    private ModelAndView createResponseView(String instance, String procedure) throws Exception {
        ModelAndView mav = new ModelAndView("procedures");
        procedure = stripKnownFileExtensionFrom(procedure);
        QueryParameters parameters = new QueryParameters().setProcedure(procedure);
        QueryResponse< ? > result = performQuery(instance, parameters);

        if (result.getResults().length == 0) {
            throw new ResourceNotFoundException();
        }

        Procedure[] procedures = (Procedure[]) result.getResults();
        mav.addObject("procedure", createCompleteProcedureOutput(procedures[0]));
        return mav;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredProcedureQuery(parameters);
        return doQuery(query);
    }

}
