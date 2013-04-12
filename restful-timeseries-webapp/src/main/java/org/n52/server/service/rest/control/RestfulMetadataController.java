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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.n52.server.service.GetMetadataService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.QuerySet;
import org.n52.shared.requests.query.responses.FeatureQueryResponse;
import org.n52.shared.requests.query.responses.OfferingQueryResponse;
import org.n52.shared.requests.query.responses.PhenomenonQueryResponse;
import org.n52.shared.requests.query.responses.ProcedureQueryResponse;
import org.n52.shared.requests.query.responses.StationQueryResponse;
import org.n52.shared.serializable.pojos.sos.SOSMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/service")
public class RestfulMetadataController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulMetadataController.class);
    
    private GetMetadataService metadataService;
    
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ModelAndView getInstances(){
    	ModelAndView mav = new ModelAndView();
    	Collection<SOSMetadata> instances = metadataService.getInstances();
    	mav.addAllObjects(createMav(instances));
    	return mav;
    }
    
    // phenomenon handler methods

    private Map<String, ?> createMav(Collection<SOSMetadata> instances) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
    	Collection<Object> services = new ArrayList<Object>();
    	for (SOSMetadata sosMetadata : instances) {
    		HashMap<String, String> sos = new HashMap<String, String>();
    		sos.put("id", sosMetadata.getConfiguredItemName());
    		sos.put("url", sosMetadata.getServiceUrl());
    		services.add(sos);
		}
    	map.put("services", services);
		return map;
	}

	@RequestMapping(value = "/{instance}/phenomenon", method = RequestMethod.POST)
    public ModelAndView getPhenomenon(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            PhenomenonQueryResponse phenomenons = (PhenomenonQueryResponse) metadataService.getPhenomenons(querySet, instance);
            mav.addAllObjects(createMav(phenomenons));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
	private Map<String, ?> createMav(PhenomenonQueryResponse phenomenons) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("phenomenons", phenomenons.getPhenomenons());
		map.put("pagingEndReached", phenomenons.isPagingEnd());
		map.put("pagingEndIndex", phenomenons.getPagingEndIndex());
		return map;
	}

	@RequestMapping(value = "/{instance}/phenomenon")
    public ModelAndView getPhenomenonByGET(@PathVariable("instance") String instance) {
    	ModelAndView mav = getPhenomenon(new QuerySet(), instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/phenomenon/{id}")
    public ModelAndView getPhenomenonByGET(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = new QuerySet();
    	ArrayList<String> phenomenonFilter = new ArrayList<String>();
    	phenomenonFilter.add(id);
    	query.setPhenomenonFilter(phenomenonFilter);
    	ModelAndView mav = getPhenomenon(query, instance);
    	return mav;
    }
    
    // procedure handler methods

    @RequestMapping(value = "/{instance}/procedure", method = RequestMethod.POST)
    public ModelAndView getProcedure(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            ProcedureQueryResponse procedures = (ProcedureQueryResponse) metadataService.getProcedures(querySet, instance);
            mav.addAllObjects(createMav(procedures));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
    private Map<String, ?> createMav(ProcedureQueryResponse procedures) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("procedures", procedures.getProcedure());
		map.put("pagingEndReached", procedures.isPagingEnd());
		map.put("pagingEndIndex", procedures.getPagingEndIndex());
		return map;
	}

	@RequestMapping(value = "/{instance}/procedure")
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance) {
    	ModelAndView mav = getProcedure(new QuerySet(), instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/procedure/{id}")
    public ModelAndView getProcedureByGET(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = new QuerySet();
    	ArrayList<String> procedureFilter = new ArrayList<String>();
    	procedureFilter.add(id);
    	query.setProcedureFilter(procedureFilter);
    	ModelAndView mav = getProcedure(query, instance);
    	return mav;
    }
    
    // offering handler methods
    
    @RequestMapping(value = "/{instance}/offering", method = RequestMethod.POST)
    public ModelAndView getOffering(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            OfferingQueryResponse offerings = (OfferingQueryResponse) metadataService.getOfferings(querySet, instance);
            mav.addAllObjects(createMav(offerings));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    private Map<String, ?> createMav(OfferingQueryResponse offerings) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("offerings", offerings.getOffering());
		map.put("pagingEndReached", offerings.isPagingEnd());
		map.put("pagingEndIndex", offerings.getPagingEndIndex());
		return map;
	}

	@RequestMapping(value = "/{instance}/offering")
    public ModelAndView getOfferingByGET(@PathVariable("instance") String instance) {
    	ModelAndView mav = getOffering(new QuerySet(), instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/offering/{id}")
    public ModelAndView getOfferingByGET(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = new QuerySet();
    	ArrayList<String> offeringFilter = new ArrayList<String>();
    	offeringFilter.add(id);
    	query.setOfferingFilter(offeringFilter);
    	ModelAndView mav = getOffering(query, instance);
    	return mav;
    }

    // feature handler methods
    
    @RequestMapping(value = "/{instance}/feature", method = RequestMethod.POST) 
    public ModelAndView getFeature(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            FeatureQueryResponse features = (FeatureQueryResponse) metadataService.getFeatures(querySet, instance);
            mav.addAllObjects(createMav(features));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
    private Map<String, ?> createMav(FeatureQueryResponse features) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("features", features.getFeature());
		map.put("pagingEndReached", features.isPagingEnd());
		map.put("pagingEndIndex", features.getPagingEndIndex());
		return map;
	}

	@RequestMapping(value = "/{instance}/feature")
    public ModelAndView getFeatureByGET(@PathVariable("instance") String instance) {
    	ModelAndView mav = getFeature(new QuerySet(), instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/feature/{id}")
    public ModelAndView getFeatureByGET(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = new QuerySet();
    	ArrayList<String> featureFilter = new ArrayList<String>();
    	featureFilter.add(id);
    	query.setFeatureOfInterestFilter(featureFilter);
    	ModelAndView mav = getFeature(query, instance);
    	return mav;
    }

    // station handler methods
    
    @RequestMapping(value = "/{instance}/station", method = RequestMethod.POST)
    public ModelAndView getStation(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            StationQueryResponse stations = (StationQueryResponse) metadataService.getStations(querySet, instance);
            mav.addAllObjects(createMav(stations));
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    private Map<String, ?> createMav(StationQueryResponse stations) {
    	HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("stations", stations.getStations());
		map.put("pagingEndReached", stations.isPagingEnd());
		map.put("pagingEndIndex", stations.getPagingEndIndex());
		return map;
	}

	@RequestMapping(value = "/{instance}/station")
    public ModelAndView getStationByGET(@PathVariable("instance") String instance) {
    	ModelAndView mav = getStation(new QuerySet(), instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/station/{id}")
    public ModelAndView getStationByGET(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = new QuerySet();
    	ArrayList<String> stationFilter = new ArrayList<String>();
    	stationFilter.add(id);
    	query.setFeatureOfInterestFilter(stationFilter);
    	ModelAndView mav = getStation(query, instance);
    	return mav;
    }
    
	public GetMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(GetMetadataService metadataService) {
		this.metadataService = metadataService;
	}

}
