package org.n52.web.v1.ctrl;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class RestController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);

    @ExceptionHandler(value = {RuntimeException.class, Exception.class })
    public void handleException(Exception e, HttpServletRequest request) {
        LOGGER.error("Could not handle request.", e);
    }
}
