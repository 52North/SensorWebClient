
package org.n52.server.service.rest.control;

import java.util.HashMap;
import java.util.Map;

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
public class RestfulProceduresController extends TimeseriesParameterQueryController implements RestfulKvp, RestfulUrls {

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
        return mav.addObject(mapProcedures(procedures));
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
            mav.addObject(mapProcedure(procedures[0]));
        }
        return mav;
    }
    
    // fix mapping with ref values, currently the repsonse object file just shows a list of the reference description and not of the value
    private Object mapProcedure(Procedure procedure) {
		Map<String, Object> object = new HashMap<String, Object>();
		object.put("id", procedure.getId());
		object.put("label", procedure.getLabel());
		Map<String, Object> refValues = new HashMap<String, Object>();
		for (String key : procedure.getrefValues()) {
			refValues.put(key, procedure.getRefValue(key).getValue());
		}
		object.put("refValues", refValues);
		return object;
	}

	private Object mapProcedures(Procedure[] procedures) {
		Object[] objects = new Object[procedures.length];
		for (int i = 0; i < procedures.length; i++) {
			objects[i] = mapProcedure(procedures[i]);
		}
		return objects;
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
