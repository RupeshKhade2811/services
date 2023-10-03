package com.massil.config;


import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestControllerExceptionHandler {


    Logger log = LoggerFactory.getLogger(RestControllerExceptionHandler.class);
    @ExceptionHandler(value = { GlobalException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> badRequest(GlobalException ex)
    {
        log.info("exception " + ex);
        return new ResponseEntity(new Response(500, ex.getMessage(),false), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> exception(Exception ex)
    {
        log.info("exception " + ex);
        return new ResponseEntity(new Response(400, ex.getMessage(),false), HttpStatus.BAD_REQUEST);
    }


}
