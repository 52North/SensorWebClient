/**
 * ﻿Copyright (C) 2012-2014 52°North Initiative for Geospatial Open Source
 * Software GmbH
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as publishedby the Free
 * Software Foundation.
 *
 * If the program is linked with libraries which are licensed under one of the
 * following licenses, the combination of the program with the linked library is
 * not considered a "derivative work" of the program:
 *
 *     - Apache License, version 2.0
 *     - Apache Software License, version 1.0
 *     - GNU Lesser General Public License, version 3
 *     - Mozilla Public License, versions 1.0, 1.1 and 2.0
 *     - Common Development and Distribution License (CDDL), version 1.0
 *
 * Therefore the distribution of the program linked with libraries licensed under
 * the aforementioned licenses, is permitted by the copyright holders if the
 * distribution is compliant with both the GNU General Public License version 2
 * and the aforementioned licenses.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */
package org.n52.api.v0.ctrl;

import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;
import static org.n52.shared.serializable.pojos.TimeseriesRenderingOptions.createDefaultRenderingOptions;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.n52.api.v0.io.DesignedParameterSet;
import org.n52.api.v0.io.UndesignedParameterSet;
import org.n52.api.v0.out.ModelAndViewPager;
import org.n52.api.v0.out.TimeseriesData;
import org.n52.api.v0.out.TimeseriesDataCollection;
import org.n52.api.v0.srv.GetDataService;
import org.n52.api.v0.srv.GetImageService;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.n52.shared.serializable.pojos.sos.SosTimeseries;
import org.n52.shared.serializable.pojos.sos.Station;
import org.n52.web.BadRequestException;
import org.n52.web.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class RestfulTimeSeriesController extends QueryController implements RestfulKvp, RestfulUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulTimeSeriesController.class);
    
    private GetDataService dataService;

    private GetImageService imageService;

    @RequestMapping(value = "/v0/timeseries", produces = "image/png", method = POST)
    public void getTimeseriesGraphForMultipleTimeseries(HttpServletResponse response,
                                                        @RequestBody DesignedParameterSet parameterSet) throws Exception {
        try {
            response.setContentType("image/png");
            imageService.writeTimeSeriesChart(parameterSet, response.getOutputStream());
        }
        finally {
            response.getOutputStream().close();
        }
    }

    @RequestMapping(value = "/v0/timeseries", produces = "application/json", method = POST)
    public ModelAndView getTimeseriesDataForMultipleTimeseries(@RequestBody UndesignedParameterSet parameterSet) throws Exception {
        TimeseriesDataCollection results = new TimeseriesDataCollection();
        results.addAll(dataService.getTimeSeriesFromParameterSet(parameterSet));
        ModelAndView mav = new ModelAndView("multipleTimeseries");
        return mav.addObject("multipleTimeseries", results.getAllTimeseries());
    }

    @RequestMapping(value = "/v0/services/{instance}/timeseries", produces = "application/json", method = GET)
    public ModelAndView getAllTimeseries(@PathVariable String instance,
                                         @RequestParam(value = KVP_SHOW, required = false) String details,
                                         @RequestParam(value = KVP_OFFSET, defaultValue = KVP_DEFAULT_OFFSET) int offset,
                                         @RequestParam(value = KVP_SIZE, defaultValue = KVP_DEFAULT_SIZE) int size,
                                         @RequestParam(value = KVP_FEATURE, required = false) String feature,
                                         @RequestParam(value = KVP_PHENOMENON, required = false) String phenomenon,
                                         @RequestParam(value = KVP_PROCEDURE, required = false) String procedure,
                                         @RequestParam(value = KVP_OFFERING, required = false) String offering) throws Exception {

        QueryParameters parameters = new QueryParameters()
                .setFeature(feature)
                .setOffering(offering)
                .setPhenomenon(phenomenon)
                .setProcedure(procedure);
        QueryResponse< ? > result = performQuery(instance, parameters);
        Station[] stations = (Station[]) result.getResults();
        
        List<Object> allTimeseries = new ArrayList<Object>();
        for (Station station : stations) {
            if (shallShowCompleteResults(details)) {
                allTimeseries.addAll(createCompleteOutput(station));
            }
            else {
                allTimeseries.addAll(createSimpleOutput(station));
            }
        }

        if (offset < 0) {
            return new ModelAndView("timeseries").addObject("multipleTimeseries", allTimeseries);
        }
        else {
            return pageResults(allTimeseries, offset, size);
        }
    }

    @RequestMapping(value = "/v0/services/{instance}/timeseries/{timeseriesId}", produces = "application/json", method = GET)
    public ModelAndView getTimeseriesData(@PathVariable String instance,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required = false) String timespan) throws Exception {

        TimeseriesDataCollection results = new TimeseriesDataCollection();
        for (Station station : getAllStations(instance)) {
            if (station.contains(timeseriesId)) {
                UndesignedParameterSet parameterSet = createUndesignedParameterSet(timespan);
                parameterSet.setTimeseries(new String[] { timeseriesId });
                results.addAll(dataService.getTimeSeriesFromParameterSet(parameterSet));
                break;
            }
        }

        TimeseriesData timeseries = results.getTimeseries(timeseriesId);
        if (timeseries == null) {
            throw new ResourceNotFoundException();
        }

        ModelAndView mav = new ModelAndView("timeseries");
        return mav.addObject("singleTimeseries", timeseries);

    }

    @RequestMapping(value = "/v0/services/{instance}/timeseries/{timeseriesId}", produces = "image/png", method = GET)
    public void getTimeseriesGraph(HttpServletResponse response,
                                   @PathVariable String instance,
                                   @PathVariable String timeseriesId,
                                   @RequestParam(required = false) String timespan,
                                   @RequestParam(defaultValue = "-1") int width,
                                   @RequestParam(defaultValue = "-1") int height) throws Exception {
        try {
            if (!containsServiceInstance(instance)) {
                LOGGER.info("SOS instance {} is not available.", instance);
                throw new ResourceNotFoundException();
            }
            SOSMetadata metadata = findServiceMetadataForItemName(instance);
            if ( !metadata.containsStationWithTimeseriesId(timeseriesId)) {
                LOGGER.info("Timeseries {} is not available.", timeseriesId);
                throw new ResourceNotFoundException();
            }

            for (Station station : getAllStations(instance)) {
                if (station.contains(timeseriesId)) {
                    response.setContentType("image/png");
                    DesignedParameterSet parameterSet = createDesignedParameterSet(timespan, width, height);
                    parameterSet.addTimeseriesWithRenderingOptions(timeseriesId, createDefaultRenderingOptions());
                    imageService.writeTimeSeriesChart(parameterSet, response.getOutputStream());
                    break;
                }
            }
        }
        finally {
            response.getOutputStream().close();
        }
    }

    private List<Object> createCompleteOutput(Station station) {
        List<Object> allTimeseries = new ArrayList<Object>();
        allTimeseries.addAll(station.getObservedTimeseries());
        return allTimeseries;
    }

    private List<Object> createSimpleOutput(Station station) {
        List<Object> allTimeseries = new ArrayList<Object>();
        for (SosTimeseries timeseries : station.getObservedTimeseries()) {
            allTimeseries.add(timeseries.getTimeseriesId());
        }
        return allTimeseries;
    }

    private ModelAndView pageResults(List<Object> timeseries, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("stations");
        return mavPage.createPagedModelAndViewFrom(timeseries, offset, size);
    }

    private UndesignedParameterSet createUndesignedParameterSet(String timespan) {
        try {
            UndesignedParameterSet parameterSet = new UndesignedParameterSet();
            parameterSet.setTimespan(timespan);
            return parameterSet;
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    private DesignedParameterSet createDesignedParameterSet(String timespan, int width, int height) {
        try {
            DesignedParameterSet parameterSet = new DesignedParameterSet();
            parameterSet.setTimespan(timespan);
            parameterSet.setWidth(width);
            parameterSet.setHeight(height);
            return parameterSet;
        }
        catch (IllegalArgumentException e) {
            throw new BadRequestException();
        }
    }

    private Station[] getAllStations(String instance) throws Exception {
        QueryResponse< ? > results = performQuery(instance, createEmptyFilterQuery());
        Station[] stations = (Station[]) results.getResults();
        if (stations.length == 0) {
            throw new ResourceNotFoundException();
        }
        return stations;
    }

    @Override
    protected QueryResponse< ? > performQuery(String instance, QueryParameters parameters) throws Exception {
        QueryFactory factory = getQueryFactoryFor(instance);
        QueryRequest query = factory.createFilteredStationQuery(parameters);
        return doQuery(query);
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

}
