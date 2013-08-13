package org.n52.web.v1.ctrl;


import static org.n52.web.v1.ctrl.RestfulUrls.COLLECTION_FEATURES;
import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.FeatureOutput;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.FeaturesParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + COLLECTION_FEATURES, produces = {"application/json"})
public class FeaturesParameterController extends ParameterController {
	
	private FeaturesParameterService featuresParameterService;

	public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid

		FeatureOutput[] allFeatures = featuresParameterService.getFeatures(offset, size);
		
		// TODO add paging
		
		return new ModelAndView().addObject(allFeatures);
	}
	
	public ModelAndView getItem(@PathVariable("item") String item) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		FeatureOutput feature = featuresParameterService.getFeature(item);
		
		if (feature == null) {
			throw new ResourceNotFoundException("Found no feature with given id.");
		}
		
		return new ModelAndView().addObject(feature);
	}

	public FeaturesParameterService getFeaturesParameterService() {
		return featuresParameterService;
	}

	public void setFeaturesParameterService(
			FeaturesParameterService featuresParameterService) {
		this.featuresParameterService = featuresParameterService;
	}

}

