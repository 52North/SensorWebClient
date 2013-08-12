package org.n52.web.v1.ctrl;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

public abstract class ParameterController implements RestfulKvp, RestfulUrls{
	
	@RequestMapping(method=RequestMethod.GET)
	public abstract ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size);
	
	@RequestMapping(value="/{item}", method=RequestMethod.GET)
	public abstract ModelAndView getItem(@PathVariable("item") String item);

}
