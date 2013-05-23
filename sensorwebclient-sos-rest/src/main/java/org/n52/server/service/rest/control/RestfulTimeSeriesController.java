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

import static org.n52.server.oxf.util.ConfigurationContext.getServiceMetadata;
import static org.n52.shared.requests.query.QueryParameters.createEmptyFilterQuery;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.n52.server.service.GetDataService;
import org.n52.server.service.GetImageService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterSet;
import org.n52.server.service.rest.model.ModelAndViewPager;
import org.n52.server.service.rest.model.TimeseriesData;
import org.n52.server.service.rest.model.TimeseriesDataCollection;
import org.n52.shared.requests.query.QueryFactory;
import org.n52.shared.requests.query.QueryParameters;
import org.n52.shared.requests.query.queries.QueryRequest;
import org.n52.shared.requests.query.responses.QueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
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
    
    @RequestMapping(value = "/{instance}/timeseries", produces = "application/json")
    public ModelAndView getTimeseriesData(@PathVariable String instance,
                                          @RequestParam(value = KVP_SHOW, required = false) String details,
                                          @RequestParam(value = KVP_OFFSET, required = false) Integer offset,
                                          @RequestParam(value = KVP_SIZE, required = false, defaultValue = KVP_DEFAULT_SIZE) Integer size) throws Exception {

        List<Object> allTimeseries = new ArrayList<Object>();
        for (Station station : getAllStations(instance)) {
            if (details != null && "complete".equalsIgnoreCase(details)) {
                createCompleteOutput(allTimeseries, station);
            } else {
                createSimpleOutput(allTimeseries, station);
            }
        }
        
        if (offset != null) {
            return pageResults(allTimeseries, offset.intValue(), size.intValue());
        }
        
        ModelAndView mav = new ModelAndView("timeseries");
        return mav.addObject("multipleTimeseries", allTimeseries);
    }

    private void createCompleteOutput(List<Object> allTimeseries, Station station) {
        for (SosTimeseries timeseries : station.getObservingTimeseries()) {
            allTimeseries.add(timeseries);
        }
    }

    private void createSimpleOutput(List<Object> allTimeseries, Station station) {
        for (SosTimeseries timeseries : station.getObservingTimeseries()) {
            allTimeseries.add(timeseries.getTimeseriesId());
        }
    }
    
    private ModelAndView pageResults(List<Object> timeseries, int offset, int size) {
        ModelAndViewPager mavPage = new ModelAndViewPager("stations");
        return mavPage.createPagedModelAndViewFrom(timeseries, offset, size);
    }

    @RequestMapping(value = "/{instance}/timeseries/{timeseriesId}", produces = "application/json")
    public ModelAndView getTimeseriesData(@PathVariable String instance,
                                          @PathVariable String timeseriesId,
                                          @RequestParam(required = false) String timespan) throws Exception {

        TimeseriesDataCollection results = new TimeseriesDataCollection();
        for (Station station : getAllStations(instance)) {
            if (station.contains(timeseriesId)) {
                SOSMetadata metadata = findServiceMetadataForItemName(instance);
                ParameterSet parameterSet = createParameterSet(timespan);
                parameterSet.addTimeseries(station.getTimeseriesById(timeseriesId));
                results.addAll(dataService.getTimeSeriesFromParameterSet(parameterSet, metadata));
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

    @RequestMapping(value = "/{instance}/timeseries/{timeseriesId}", produces = "image/png")
    public void getTimeseriesGraph(HttpServletResponse response,
                                   @PathVariable String instance,
                                   @PathVariable String timeseriesId,
                                   @RequestParam(required = false) String timespan,
                                   @RequestParam(required = false) String size) throws Exception {
        try {
            for (Station station : getAllStations(instance)) {
                if (station.contains(timeseriesId)) {
                    response.setContentType("image/png");
                    ParameterSet parameterSet = createParameterSet(timespan, size);
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
    
    @RequestMapping(value = "/timeseries/", produces = "image/png")
    public void getTimeseriesGraph(HttpServletResponse response,
                                   @PathVariable String instance,
                                   @RequestBody ParameterSet parameterSet) throws Exception {
        try {
            for (String reference : parameterSet.getTimeseriesReferences()) {
                SosTimeseries timeseries = parameterSet.getTimeseriesByReference(reference);
                SOSMetadata metadata = getServiceMetadata(timeseries.getServiceUrl());
                Station station = metadata.getStationByTimeSeries(timeseries);
                if (station == null) {
                    LOGGER.info("No station associated for timeseries {}", timeseries);
                    throw new ResourceNotFoundException();
                } 
                response.setContentType("image/png");
                parameterSet.addTimeseries(timeseries);
                imageService.writeTimeSeriesChart(parameterSet, instance, response.getOutputStream());
                break;
            }
        }
        finally {
            response.getOutputStream().close();
        }
    }

    private ParameterSet createParameterSet(String timespan) {
        return createParameterSet(timespan, null);
    }

    private ParameterSet createParameterSet(String timespan, String size) {
        try {
            ParameterSet parameterSet = new ParameterSet();
            parameterSet.setTimespan(timespan);
            parameterSet.setSize(size);
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

    
    
    
    
    
    
    
    
    
    
    
//    @Deprecated
//    @RequestMapping(value = "/{instance}/timeseries", method = RequestMethod.POST)
//    public ModelAndView getData(@RequestBody ParameterSet parameterSet,
//                                @PathVariable("instance") String instance) {
//        try {
//            ModelAndView mav = new ModelAndView();
//            mav.addAllObjects(dataService.getTimeSeriesFromParameterSet(parameterSet, instance));
//            return mav;
//        }
//        catch (Exception e) {
//            LOGGER.error("Could not create response.", e);
//            throw new InternalServiceException();
//        }
//    }

    @Deprecated
    @RequestMapping(value = "/timeseries", produces = "image/png", method = RequestMethod.POST)
    public ModelAndView getImage(@RequestBody ParameterSet parameterSet) {
        try {
            ModelAndView mav = new ModelAndView();
//            mav.addObject(imageService.createTimeSeriesChart(parameterSet, instance));
            return mav;
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

//    @Deprecated
//    @RequestMapping(value = "/{instance}/image", method = RequestMethod.GET)
//    public void getImagebyGET(HttpServletResponse response,
//                              @PathVariable("instance") String instance,
//                              @RequestParam(value = "offering", required = true) String offering,
//                              @RequestParam(value = "procedure", required = true) String procedure,
//                              @RequestParam(value = "feature", required = true) String feature,
//                              @RequestParam(value = "phenomenon", required = true) String phenomenon,
//                              @RequestParam(value = "timespan", required = false) String timespan,
//                              @RequestParam(value = "size", required = false) String size) {
//        try {
//            SosTimeseries timeseries = new SosTimeseries();
//            timeseries.setOffering(offering);
//            timeseries.setProcedure(procedure);
//            timeseries.setPhenomenon(phenomenon);
//            timeseries.setFeature(feature);
//
//            ParameterSet parameterSet = new ParameterSet();
//            parameterSet.addTimeseries(timeseries);
//
//            parameterSet.setTimespan(timespan);
//            parameterSet.setSize(size);
//
//            response.setContentType("image/png");
//
//            imageService.writeTimeSeriesChart(parameterSet, instance, response.getOutputStream());
//            response.getOutputStream().close();
//        }
//        catch (Exception e) {
//            LOGGER.error("Could not create response.", e);
//            throw new InternalServiceException();
//        }
//    }

}
