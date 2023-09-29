package com.massil.ExceptionHandle;
//@Author: Rupesh Khade

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.NoSuchFileException;

/**
 * This class is used for Handling Exceptions
 * This will only Handle the Exceptions in the controller
 */

//@RestControllerAdvice

public class ApplicationExceptionHandler  {

    Logger log = LoggerFactory.getLogger(ApplicationExceptionHandler.class);


    /**
     * This method will handle the exception raised if validation is failed
     * @param ex This is the object of MethodArgumentNotValidException
     * @return field and default message
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleMethodArgumentException(MethodArgumentNotValidException ex){
        log.info("***VALIDATION FAILED***");


        StringBuilder e = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            e.append(error.getField() + "-" + error.getDefaultMessage()+", ");
        }

        Response response=new Response();
        response.setCode(HttpStatus.BAD_REQUEST.value());
        response.setStatus(false);
        log.debug("Validated Fields",response);
        response.setMessage(e.toString());

        return ResponseEntity.badRequest().body(response);
    }
    /**
     * This method will handle the exception raised if number format exception is raised
     * @param ex This is the object of NumberFormatException
     * @return errorResponse
     */
    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<Response
             > handleNumberFormatException(NumberFormatException ex){
        log.info("NUMBER FORMAT EXCEPTION");
        String message = "please Enter Integers ";
        Response errorResponse = new Response();
        errorResponse.setStatus(false);
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(message);
        log.debug(ex.getMessage(),errorResponse);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method will handle the exception for type mismatch exception
     * @param ex This is the object of MethodArgumentTypeMismatchException
     * @return errorResponse
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<Response> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex){
        String message = "please Enter Integers.. ";
        Response errorResponse = new Response();
        errorResponse.setStatus(false);
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(message);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NoSuchFileException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<Response> handleNoSuchFileException(NoSuchFileException ex){
        String message = "File Not Found ";
        Response errorResponse = new Response();
        errorResponse.setStatus(false);
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(message);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(AppraisalException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public  ResponseEntity<Response> handleAppraisalException(AppraisalException ex){
        Response errorResponse = new Response();
        errorResponse.setStatus(false);
        errorResponse.setCode(HttpStatus.BAD_REQUEST.value());
        errorResponse.setMessage(ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }




}