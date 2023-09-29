package com.massil.responseHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.TreeMap;


public class ApiResponseHandler {
    String message;
    /**
     * This method is used to generate response
     * @param status this is the status
     * @param message
     * @author YogeshKumarV
     * @return  Object
     */
    public static ResponseEntity<Object> generateResponse( HttpStatus status,String message) {
        Map<String, Object> map = new TreeMap<>();
        map.put("status", status.value());
        map.put("message", message);
        return new ResponseEntity<>(map,status);
    }
}
