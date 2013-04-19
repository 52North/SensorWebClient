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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/services", produces = {"text/html", "application/*"})
public class RestfulMetadataController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulMetadataController.class);
    
    private GetMetadataService metadataService;
    
    // phenomenon handler methods

	@RequestMapping(value = "/{instance}/phenomenons", method = RequestMethod.POST)
    public ModelAndView getPhenomenon(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView("phenomenons");
            PhenomenonQueryResponse phenomenons = (PhenomenonQueryResponse) metadataService.getPhenomenons(querySet, instance);
            mav.addObject(phenomenons.getPhenomenons());
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
	@RequestMapping(value = "/{instance}/phenomenons", method = RequestMethod.GET)
    public ModelAndView getPhenomenonByGET(
    		@PathVariable("instance") String instance, 
    		@RequestParam(value="phenomenon", required=false) String phenomenon) {
		QuerySet query = createQuerySet(null,phenomenon,null,null);
    	ModelAndView mav = getPhenomenon(query, instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/phenomenons/{id}")
    public ModelAndView getPhenomenonByID(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = createQuerySet(null, id, null, null);
    	ModelAndView mav = getPhenomenon(query, instance);
    	return mav;
    }
    
    // procedure handler methods

    @RequestMapping(value = "/{instance}/procedures", method = RequestMethod.POST)
    public ModelAndView getProcedure(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView("procedures");
            ProcedureQueryResponse procedures = (ProcedureQueryResponse) metadataService.getProcedures(querySet, instance);
            mav.addObject(procedures.getProcedure());
            mav.addObject("test", "check");
            return mav;
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
    @RequestMapping(value = "/{instance}/procedures")
    public ModelAndView getProcedureByGET(
    		@PathVariable("instance") String instance,
    		@RequestParam(value="procedure", required=false) String procedure) {
    	QuerySet query = createQuerySet(null, null, null, procedure);
    	ModelAndView mav = getProcedure(query, instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/procedures/{id}")
    public ModelAndView getProcedureByID(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = createQuerySet(null, null, null, id);
    	ModelAndView mav = getProcedure(query, instance);
    	return mav;
    }
    
    // offering handler methods
    
    @RequestMapping(value = "/{instance}/offerings", method = RequestMethod.POST)
    public ModelAndView getOffering(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView("offerings");
            OfferingQueryResponse offerings = (OfferingQueryResponse) metadataService.getOfferings(querySet, instance);
            return mav.addObject(offerings.getOfferings());
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    @RequestMapping(value = "/{instance}/offerings")
    public ModelAndView getOfferingByGET(
    		@PathVariable("instance") String instance,
    		@RequestParam(value="offering", required=false) String offering) {
    	QuerySet query = createQuerySet(offering, null, null, null);
    	ModelAndView mav = getOffering(query, instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/offerings/{id}")
    public ModelAndView getOfferingByID(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = createQuerySet(id, null, null, null);
    	ModelAndView mav = getOffering(query, instance);
    	return mav;
    }

    // feature handler methods
    
    @RequestMapping(value = "/{instance}/features", method = RequestMethod.POST) 
    public ModelAndView getFeature(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView("features");
            FeatureQueryResponse features = (FeatureQueryResponse) metadataService.getFeatures(querySet, instance);
            return mav.addObject(features.getFeatures());
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }
    
    @RequestMapping(value = "/{instance}/features")
    public ModelAndView getFeatureByGET(
    		@PathVariable("instance") String instance,
    		@RequestParam(value="feature", required=false) String feature) {
    	QuerySet query = createQuerySet(null, null, feature, null);
    	ModelAndView mav = getFeature(query, instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/features/{id}")
    public ModelAndView getFeatureByID(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = createQuerySet(null, null, id, null);
    	ModelAndView mav = getFeature(query, instance);
    	return mav;
    }

    // station handler methods
    
    @RequestMapping(value = "/{instance}/stations", method = RequestMethod.POST)
    public ModelAndView getStation(@RequestBody QuerySet querySet, @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView("stations");
            StationQueryResponse stations = (StationQueryResponse) metadataService.getStations(querySet, instance);
            return mav.addObject(stations.getStations());
        } catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    @RequestMapping(value = "/{instance}/stations")
    public ModelAndView getStationByGET(
    		@PathVariable("instance") String instance,
    		@RequestParam(value="offering", required=false) String offering,
    		@RequestParam(value="procedure", required=false) String procedure,
    		@RequestParam(value="phenomenon", required=false) String phenomenon,
    		@RequestParam(value="feature", required=false) String feature) {
    	QuerySet query = createQuerySet(offering, phenomenon, feature, procedure);
    	ModelAndView mav = getStation(query, instance);
    	return mav;
    }
    
    @RequestMapping(value = "/{instance}/stations/{id}")
    public ModelAndView getStationByID(@PathVariable("instance") String instance, @PathVariable("id") String id) {
    	QuerySet query = createQuerySet(null, null, id, null);
    	ArrayList<String> stationFilter = new ArrayList<String>();
    	stationFilter.add(id);
    	query.addAllFeatureOfInterests(stationFilter);
    	ModelAndView mav = getStation(query, instance);
    	return mav;
    }
    
	public GetMetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(GetMetadataService metadataService) {
		this.metadataService = metadataService;
	}

	private Map<String, ?> createServices(Collection<SOSMetadata> instances) {
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

	private QuerySet createQuerySet(String offering, String phenomenon,
			String feature, String procedure) {
		QuerySet query = new QuerySet();
		
		if (offering != null) {
			ArrayList<String> offeringFilter = new ArrayList<String>();
	    	offeringFilter.add(offering);
	    	query.addAllOfferings(offeringFilter);
		}
		
		if (phenomenon != null) {
			ArrayList<String> phenomenonFilter = new ArrayList<String>();
	    	phenomenonFilter.add(phenomenon);
	    	query.addAllPhenomenons(phenomenonFilter);
		}
		
		if (feature != null) {
			ArrayList<String> featureFilter = new ArrayList<String>();
	    	featureFilter.add(feature);
	    	query.addAllFeatureOfInterests(featureFilter);
		}
		
		if (procedure != null){
			ArrayList<String> procedureFilter = new ArrayList<String>();
	    	procedureFilter.add(procedure);
	    	query.addAllProcedures(procedureFilter);
		}
	
		return query;
	}

}
