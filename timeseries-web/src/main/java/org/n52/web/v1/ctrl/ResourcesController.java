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

package org.n52.web.v1.ctrl;

import static org.n52.web.v1.ctrl.ResourcesController.Resource.createResource;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import java.util.ArrayList;
import java.util.List;

import org.n52.web.v1.srv.MetadataService;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH, produces = {"application/json"})
public class ResourcesController {
	
	private MetadataService metadataService;

    @RequestMapping("/")
    public ModelAndView getResources(@RequestParam(required=false) MultiValueMap<String, String> query) {
    	QueryMap map = QueryMap.createFromQuery(query);
    	
    	return new ModelAndView().addObject(getResourcesExpanded(map.shallExpand()));
    }
    
    public Resource[] getResourcesExpanded(boolean expanded) {
    	List<Resource> resources = new ArrayList<Resource>();
    	Resource services = createResource("services").withLabel("Service Provider").withDescription("A service provider offers timeseries data.");
    	Resource stations = createResource("stations").withLabel("Station").withDescription("A station is the place where measurement takes place.");
    	Resource timeseries = createResource("timeseries").withLabel("Timeseries").withDescription("Represents a sequence of data values measured over time.");
    	Resource categories = createResource("categories").withLabel("Category").withDescription("A category group available timeseries.");
    	Resource offerings = createResource("offerings").withLabel("Offering").withDescription("An organizing unit to filter resources.");
    	Resource features = createResource("features").withLabel("Feature").withDescription("An organizing unit to filter resources.");
    	Resource procedures = createResource("procedures").withLabel("Procedure").withDescription("An organizing unit to filter resources.");
    	Resource phenomena = createResource("phenomena").withLabel("Phenomenon").withDescription("An organizing unit to filter resources.");
    	if (expanded) {
    		services.setCount(metadataService.getServiceCount());
    		stations.setCount(metadataService.getStationsCount());
    		timeseries.setCount(metadataService.getTimeseriesCount());
    		categories.setCount(metadataService.getCategoriesCount());
    		offerings.setCount(metadataService.getOfferingsCount());
    		features.setCount(metadataService.getFeaturesCount());
    		procedures.setCount(metadataService.getProceduresCount());
    		phenomena.setCount(metadataService.getPhenomenaCount());
		}
        resources.add(services);
        resources.add(stations);
        resources.add(timeseries);
        resources.add(categories);
        resources.add(offerings);
        resources.add(features);
        resources.add(procedures);
        resources.add(phenomena);
        return resources.toArray(new Resource[0]);
    }
    
	public MetadataService getMetadataService() {
		return metadataService;
	}

	public void setMetadataService(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	static class Resource {

        private String id;
        private String label;
        private String description;
        private Integer count;

        private Resource(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getCount() {
			return count;
		}

		public void setCount(Integer count) {
			this.count = count;
		}

		public Resource withLabel(String label) {
            this.label = label;
            return this;
        }

        public Resource withDescription(String description) {
            this.description = description;
            return this;
        }
        
        public Resource withCount(Integer count) {
        	this.count = count;
        	return this;
        }

        public static Resource createResource(String id) {
            return new Resource(id);
        }
    }
}
