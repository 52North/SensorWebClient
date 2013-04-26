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

import org.n52.server.service.GetDataService;
import org.n52.server.service.GetImageService;
import org.n52.server.service.rest.InternalServiceException;
import org.n52.server.service.rest.ParameterConstellation;
import org.n52.server.service.rest.ParameterSet;
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
@RequestMapping(value = "/services/")
public class RestfulTimeSeriesController implements RestfulKvp, RestfulUrls {

    private static final Logger LOGGER = LoggerFactory.getLogger(RestfulTimeSeriesController.class);

    private GetDataService dataService;

    private GetImageService imageService;
    

    @RequestMapping(value = "{instance}" + PATH_TIMESERIES, method = RequestMethod.GET)
    public ModelAndView getTimeseriesMetadatasByGET(@PathVariable("instance") String instance) {
        
        // TODO

        return new ModelAndView();
    }

    @RequestMapping(value = "{instance}/timeseries", method = RequestMethod.POST)
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

    @RequestMapping(value = "{instance}/timseries/image")
    public ModelAndView getImage(@RequestBody ParameterSet parameterSet, 
                                 @PathVariable("instance") String instance) {
        try {
            ModelAndView mav = new ModelAndView();
            mav.addObject(imageService.getTimeSeriesChart(parameterSet, instance));
            return mav;
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
        }
    }

    @RequestMapping(value = "{instance}/image", method = RequestMethod.GET)
    public ModelAndView getImagebyGET(@PathVariable("instance") String instance,
                                      @RequestParam(value = "offering", required = true) String offering,
                                      @RequestParam(value = "procedure", required = true) String procedure,
                                      @RequestParam(value = "feature", required = true) String feature,
                                      @RequestParam(value = "phenomenon", required = true) String phenomenon) {
        try {
            ParameterConstellation paramConst = new ParameterConstellation();
            paramConst.setOffering(offering);
            paramConst.setProcedure(procedure);
            paramConst.setPhenomenon(phenomenon);
            paramConst.setFeatureOfInterest(feature);
            ModelAndView mav = new ModelAndView();
            // mav.addObject(imageService.getTimeSeriesChart(paramConst, instance));
            return mav;
        }
        catch (Exception e) {
            LOGGER.error("Could not create response.", e);
            throw new InternalServiceException();
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

}
