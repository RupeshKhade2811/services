package com.massil.controller;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.Status;
import com.massil.services.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/status")
@Tag(name = "Status", description = "operation on Status")
public class StatusController {

    Logger log = LoggerFactory.getLogger(ConfigCodesController.class);

    @Autowired
    private StatusService stsService;


    /**
     * this method creates bidding status in table
     * @param statuses this is the object of List<Status>
     * @author yudhister
     * @return message
     */
    @Operation(summary = "Add statuses to Database")
    @PostMapping("/addStatus")
    public ResponseEntity<Response> addStatus(@RequestBody List<Status> statuses){
        log.info("This method adds statuses in the DB table");
        String s = stsService.addStatus(statuses);
        Response response =new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(s);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * this method update status in table
     * @param status this is the object of Status dto
     * @param statusId this is primary key of EStatus entity
     * @author yudhister
     * @return response
     */
    @Operation(summary = "update statuses to Database")
    @PostMapping("/statusUpdate")
    public ResponseEntity<Response> modifyStatus(@RequestBody Status status, @RequestHeader("statusId") Long statusId) throws AppraisalException {
        log.info("Status Upadte method is Triggered **Controller**");
        Response response=stsService.updateStatus(status,statusId);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    /**
     * this method delete status in table
     * @param statusId this is primary key of EStatus entity
     * @author yudhister
     * @return response
     */
    @Operation(summary = "Delete Status  by StatusId")
    @PostMapping("/deleteStatus")
    public ResponseEntity<Response> deleteStatus(@RequestParam Long statusId) throws AppraisalException {
        log.info("DELETING APPRAISAL");
        return new ResponseEntity<>(stsService.deleteStatus(statusId),HttpStatus.OK);
    }


}
