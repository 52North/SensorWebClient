
package org.n52.web;

import static org.n52.io.MimeType.APPLICATION_JSON;
import static org.n52.io.MimeType.APPLICATION_PDF;
import static org.n52.io.MimeType.IMAGE_PNG;
import static org.n52.web.ExceptionResponse.createExceptionResponse;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.n52.web.v1.ctrl.ResourcesController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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

    @ExceptionHandler(value = BadRequestException.class)
    public void handle400(Exception e, HttpServletRequest request, HttpServletResponse response) {
        writeExceptionResponse((WebException) e, response, BAD_REQUEST);
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    public void handle404(Exception e, HttpServletRequest request, HttpServletResponse response) {
        writeExceptionResponse((WebException) e, response, NOT_FOUND);
    }

    @ExceptionHandler(value = InternalServiceException.class)
    public void handle500(Exception e, HttpServletRequest request, HttpServletResponse response) {
        writeExceptionResponse((WebException) e, response, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = {RuntimeException.class, Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        WebException wrappedException = new InternalServiceException("Unexpected Exception occured.", e);
        writeExceptionResponse(wrappedException, response, INTERNAL_SERVER_ERROR);
    }

    private void writeExceptionResponse(WebException e, HttpServletResponse response, HttpStatus status) {
        LOGGER.error("An exception occured.", e);

        // TODO consider using a 'suppress_response_codes=true' parameter and always return 200 OK

        response.setStatus(status.value());
        response.setContentType(APPLICATION_JSON.getMimeType());
        ObjectWriter writer = new ObjectMapper().writerWithType(ExceptionResponse.class);
        ExceptionResponse exceptionResponse = createExceptionResponse(e, INTERNAL_SERVER_ERROR);
        try {
            writer.writeValue(response.getOutputStream(), exceptionResponse);
        }
        catch (IOException ioe) {
            LOGGER.error("Could not process error message.", e);
        }
    }

}
