package org.n52.web.v1.ctrl;


import static org.n52.web.v1.ctrl.RestfulUrls.DEFAULT_PATH;

import org.n52.io.v1.data.out.Procedure;
import org.n52.web.ResourceNotFoundException;
import org.n52.web.v1.srv.ProceduresParameterService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = DEFAULT_PATH + "/" + RestfulUrls.COLLECTION_PROCEDURES, produces = {"application/json"})
public class ProceduresParameterController extends ParameterController {
	
	private ProceduresParameterService proceduresParameterService;

	public ModelAndView getCollection(@RequestParam(defaultValue = KVP_DEFAULT_OFFSET) int offset, @RequestParam(defaultValue = KVP_DEFAULT_SIZE) int size) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid

		Procedure[] allProcedures = proceduresParameterService.getProcedures(offset, size);
		
		// TODO add paging
		
		return new ModelAndView().addObject(allProcedures);
	}
	
	public ModelAndView getItem(@PathVariable("item") String item) {
		
		// TODO check parameters and throw BAD_REQUEST if invalid
		
		Procedure procedure = proceduresParameterService.getProcedure(item);
		
		if (procedure == null) {
			throw new ResourceNotFoundException("Found no procedure with given id.");
		}
		
		return new ModelAndView().addObject(procedure);
	}

	public ProceduresParameterService getProceduresParameterService() {
		return proceduresParameterService;
	}

	public void setProceduresParameterService(
			ProceduresParameterService proceduresParameterService) {
		this.proceduresParameterService = proceduresParameterService;
	}

}

