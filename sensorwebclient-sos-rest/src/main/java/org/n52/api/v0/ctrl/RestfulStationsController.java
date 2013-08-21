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
import static org.n52.api.v0.out.StationOutput.createCompleteStationOutput;
import static org.n52.api.v0.out.StationOutput.createSimpleStationOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.n52.api.v0.io.Vicinity;
import org.n52.api.v0.out.ModelAndViewPager;
import org.n52.api.v0.out.StationOutput;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.ResourceNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class RestfulStationsController extends QueryController implements RestfulKvp, RestfulUrls {

    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS, method = RequestMethod.GET)
    public ModelAndView getStationsByGET(@PathVariable("instance") String instance,
                                         @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                         @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size,
                                         @RequestParam(value = KVP_SHOW, defaultValue = KVP_DEFAULT_SHOW) String details,
                                         @RequestParam(value = KVP_WITHIN, required = false) String near,
                                         @RequestParam(value = KVP_FEATURE, required = false) String feature,
                                         @RequestParam(value = KVP_PHENOMENON, required = false) String phenomenon,
                                         @RequestParam(value = KVP_PROCEDURE, required = false) String procedure,
                                         @RequestParam(value = KVP_OFFERING, required = false) String offering) throws Exception {

        QueryParameters parameters = new QueryParameters()
                .setPhenomenon(phenomenon)
                .setProcedure(procedure)
                .setOffering(offering)
                .setFeature(feature);

        if (near != null) {
            ObjectMapper mapper = new ObjectMapper();
            Vicinity vicinity = mapper.readValue(near, Vicinity.class);
            parameters.setSpatialFilter(vicinity.calculateBounds());
        }

        QueryResponse< ? > result = performQuery(instance, parameters);

        Station[] stations = (Station[]) result.getResults();
        List<StationOutput> output = new ArrayList<StationOutput>();
        if (shallShowCompleteResults(details)) {
            Collections.addAll(output, createCompleteStationOutput(stations));
        }
        else {
            Collections.addAll(output, createSimpleStationOutput(stations));
        }

        if (offset < 0) {
            return new ModelAndView("stations").addObject("stations", output);
        } else {
            return pageResults(output, offset, size);
        }
    }

    private ModelAndView pageResults(List<StationOutput> stations, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("stations");
        return mavPage.createPagedModelAndViewFrom(stations, offset, size);
    }

    // this mapping handles identifier URLs
    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS + "/**", method = RequestMethod.GET)
    public ModelAndView getProcedureByID(@PathVariable(value = "instance") String instance,
                                         HttpServletRequest request) throws Exception {
        String station = getDecodedIndividuumIdentifierFor(COLLECTION_STATIONS, request);
        return createResponseView(instance, station);
    }

    @RequestMapping(value = "/{instance}/" + COLLECTION_STATIONS + "/{id:.+}", method = RequestMethod.GET)
    public ModelAndView getStationByID(@PathVariable(value = "instance") String instance,
                                       @PathVariable(value = "id") String station) throws Exception {
        return createResponseView(instance, decode(station));
    }

    private ModelAndView createResponseView(String instance, String station) throws Exception {
        station = stripKnownFileExtensionFrom(station);
        QueryParameters parameters = new QueryParameters().setStation(station);
        QueryResponse< ? > result = performQuery(instance, parameters);
        Station[] stations = (Station[]) result.getResults();
        if (stations.length == 0) {
            throw new ResourceNotFoundException();
        }
        ModelAndView mav = new ModelAndView("stations");
        return mav.addObject("station", createCompleteStationOutput(stations[0]));
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredStationQuery(parameters);
        return doQuery(query);
    }

}