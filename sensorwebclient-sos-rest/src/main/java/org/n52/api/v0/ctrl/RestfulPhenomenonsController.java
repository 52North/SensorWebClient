/**
 * ﻿Copyright (C) 2012
 * by 52 North Initiative for Geospatial Open Source Software GmbH
 *
 * Contact: Andreas Wytzisk
 * 52 North Initiative for Geospatial Open Source Software GmbH
 * Martin-Luther-King-Weg 24
 * 48155 Muenster, Germany
 * info@52north.org
 *
 * This program is free software; you can redistribute and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation.
 *
 * This program is distributed WITHOUT ANY WARRANTY; even without the implied
 * WARRANTY OF MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program (see gnu-gpl v2.txt). If not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA or
 * visit the Free Software Foundation web page, http://www.fsf.org.
 */

package org.n52.api.v0.ctrl;

import static org.n52.api.v0.ctrl.RestfulUrls.DEFAULT_PATH;

import javax.servlet.http.HttpServletRequest;

import org.n52.api.v0.out.ModelAndViewPager;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Phenomenon;
import org.n52.web.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class RestfulPhenomenonsController extends QueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_PHENOMENONS, method = RequestMethod.GET)
    public ModelAndView getPhenomenonsByGET(@PathVariable("instance") String instance,
                                            @RequestParam(value = KVP_SHOW, required = false) String details,
                                            @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                            @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size) throws Exception {

        QueryParameters parameters = QueryParameters.createEmptyFilterQuery();
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
        String phenomenon = getDecodedIndividuumIdentifierFor(COLLECTION_PHENOMENONS, request);
        return createResponseView(instance, phenomenon);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_PHENOMENONS + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         @PathVariable(value = "id") String phenomenon) throws Exception {
        return createResponseView(instance, decode(phenomenon));
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
