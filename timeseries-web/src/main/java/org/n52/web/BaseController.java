package org.n52.web;

import static org.n52.io.MimeType.APPLICATION_JSON;
import static org.n52.io.MimeType.APPLICATION_PDF;
import static org.n52.io.MimeType.IMAGE_PNG;
import static org.n52.web.ExceptionResponse.createExceptionResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.web.v1.ctrl.ResourcesController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Controller
public abstract class BaseController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ResourcesController.class);

    protected boolean isRequestingJsonData(HttpServletRequest request) {
        return APPLICATION_JSON.getMimeType().equals(request.getHeader("Accept"));
    }
    
    protected boolean isRequestingPdfData(HttpServletRequest request) {
        return APPLICATION_PDF.getMimeType().equals(request.getHeader("Accept"));
    }
    
    protected boolean isRequestingPngData(HttpServletRequest request) {
        return IMAGE_PNG.getMimeType().equals(request.getHeader("Accept"));
    }
    
    // TODO make stack tracing configurable
    
    @ExceptionHandler(value = BadRequestException.class)
    public ModelAndView handle400(Exception e, HttpServletRequest request, HttpServletResponse response) {
        return createResponse(e, response, BAD_REQUEST);
    }
    
    @ExceptionHandler(value = ResourceNotFoundException.class)
    public ModelAndView handle404(Exception e, HttpServletRequest request, HttpServletResponse response) {
        return createResponse(e, response, NOT_FOUND);
    }

    @ExceptionHandler(value = InternalServiceException.class)
    public ModelAndView handle500(Exception e, HttpServletRequest request, HttpServletResponse response) {
        return createResponse(e, response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RuntimeException.class, Exception.class })
    public ModelAndView handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        LOGGER.error("Unexpected exception occured. Please contact service provider.", e);
        return createUnexpectedInternalServerError(e, response);
    }
    
    private ModelAndView createResponse(Exception e, HttpServletResponse response, HttpStatus status) {
        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON.getMimeType());
        return new ModelAndView().addObject(createExceptionResponse((WebException) e, status));
    }

    private ModelAndView createUnexpectedInternalServerError(Exception e, HttpServletResponse response) {
        response.setStatus(INTERNAL_SERVER_ERROR.value());
        response.setContentType(APPLICATION_JSON.getMimeType());
        return new ModelAndView().addObject(createExceptionResponse(e, INTERNAL_SERVER_ERROR));
    }
}
