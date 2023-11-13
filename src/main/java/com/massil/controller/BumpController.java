package com.massil.controller;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.ApprCreaPage;
import com.massil.services.AutoBidService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/bump")
@Tag(name = "Ctrl_bump",description = "Bump Module")
public class BumpController {
    @Autowired
    private AutoBidService autoBidService;
    @PostMapping("/addBumps")
    public ResponseEntity<String> addBumps(){
        String added = autoBidService.addBumpsAndRange();
        return new ResponseEntity<>(added,HttpStatus.OK);
    }
    @PostMapping("/updateBumps")
    public ResponseEntity<String > updateBumps(){
        autoBidService.updateBumpsAndRange();
        return new ResponseEntity<>("Updated successfully",HttpStatus.OK);
    }
}
