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

package org.n52.server.service.rest.control;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import javax.servlet.http.HttpServletResponse;

import org.n52.server.service.GetDataService;
import org.n52.server.service.GetImageService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterSet;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
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
@RequestMapping(value = "/services")
public class RestfulTimeSeriesController extends QueryController implements RestfulKvp, RestfulUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulTimeSeriesController.class);

    private GetDataService dataService;

    private GetImageService imageService;

    @RequestMapping(value = "/{instance}/timeseries/{timeseriesId}", consumes = "application/json")
    public ModelAndView getTimeseriesData(@PathVariable String instance,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required = false) String timespan) throws Exception {


        ParameterSet parameterSet = new ParameterSet();
        parameterSet.setTimespan(timespan);

        // TODO return raw data of timeseries

        return null;

    }

    @RequestMapping(value = "/{instance}/timeseries/{timeseriesId}", produces = "image/png")
    public void getTimeseriesGraph(HttpServletResponse response,
                                   @PathVariable String instance,
                                   @PathVariable String timeseriesId,
                                   @RequestParam(required = false) String timespan,
                                   @RequestParam(required = false) String size) throws Exception {
        try {
            QueryParameters parameters = createEmptyFilterQuery();
            QueryResponse< ? > results = performQuery(instance, parameters);
            Station[] stations = (Station[]) results.getResults();
            if (stations.length == 0) {
                throw new ResourceNotFoundException();
            }

            ParameterSet parameterSet = new ParameterSet();
            parameterSet.setTimespan(timespan);
            parameterSet.setSize(size);

            for (Station station : stations) {
                if (station.contains(timeseriesId)) {
                    response.setContentType("image/png");
                    parameterSet.addTimeseries(station.getTimeseriesById(timeseriesId));
                    imageService.writeTimeSeriesChart(parameterSet, instance, response.getOutputStream());
                    break;
                }
            }
        }
        finally {
            response.getOutputStream().close();
        }
    }

    public GetDataService getDataService() {
        return dataService;
    }

    public void setDataService(GetDataService dataService) {
        this.dataService = dataService;
    }

    public GetImageService getImageService() {
        return imageService;
    }

    public void setImageService(GetImageService imageService) {
        this.imageService = imageService;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredStationQuery(parameters);
        return doQuery(query);
    }

    @Deprecated
    @RequestMapping(value = "/{instance}/timeseries", method = RequestMethod.POST)
    public ModelAndView getData(@RequestBody ParameterSet parameterSet,
                                @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            mav.addAllObjects(dataService.getTimeSeriesFromParameterSet(parameterSet, instance));
            return mav;
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    @Deprecated
    @RequestMapping(value = "/{instance}/timeseries/image", method = RequestMethod.POST)
    public ModelAndView getImage(@RequestBody ParameterSet parameterSet,
                                 @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            mav.addObject(imageService.createTimeSeriesChart(parameterSet, instance));
            return mav;
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    @Deprecated
    @RequestMapping(value = "/{instance}/image", method = RequestMethod.GET)
    public void getImagebyGET(HttpServletResponse response,
                              @PathVariable("instance") String instance,
                              @RequestParam(value = "offering", required = true) String offering,
                              @RequestParam(value = "procedure", required = true) String procedure,
                              @RequestParam(value = "feature", required = true) String feature,
                              @RequestParam(value = "phenomenon", required = true) String phenomenon,
                              @RequestParam(value = "timespan", required = false) String timespan,
                              @RequestParam(value = "size", required = false) String size) {
        try {
            SosTimeseries timeseries = new SosTimeseries();
            timeseries.setOffering(offering);
            timeseries.setProcedure(procedure);
            timeseries.setPhenomenon(phenomenon);
            timeseries.setFeature(feature);

            ParameterSet parameterSet = new ParameterSet();
            parameterSet.addTimeseries(timeseries);

            parameterSet.setTimespan(timespan);
            parameterSet.setSize(size);

            response.setContentType("image/png");

            imageService.writeTimeSeriesChart(parameterSet, instance, response.getOutputStream());
            response.getOutputStream().close();
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

}
