package com.massil.controller;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.FtryTraining;
import com.massil.dto.TrainingVideos;
import com.massil.responseHandler.ApiResponseHandler;
import com.massil.services.FtryTrainingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/trainingportal")
@Tag(name = "Factory Training", description = "Training Module")
public class FtryTrainingController {

    @Autowired
    private FtryTrainingService ftryTrainingService;


    Logger log = LoggerFactory.getLogger(FtryTrainingController.class);

    /**
     * This method saves the video in local folder and returns the file name
     * @return Response class
     */
    @Operation(summary = "Upload Video and Returns Video name")
    @PostMapping("/trainingUpload")
    public ResponseEntity<Response> trainingUpload(@RequestBody FtryTraining ftryTraining,  @RequestHeader("userId") UUID userId) throws AppraisalException, IOException {

            Response response = ftryTrainingService.trainingUpload(ftryTraining, userId);
            return new ResponseEntity<>(response,HttpStatus.OK);
    }



    @Operation(summary = "training download")
    @PostMapping(value = "/trainingDownload")
    public ResponseEntity<TrainingVideos> trainingDownload(@RequestHeader("userId")  UUID userId){

        log.info("GETTING LIST OF TRAINING SECTIONS");
        TrainingVideos eFtryTrainings = ftryTrainingService.trainingDownload(userId);
        return new ResponseEntity<>(eFtryTrainings, HttpStatus.OK);
    }

    @Operation(summary = "Delete Video in Factory Training Portal")
    @PostMapping("/deleteTrainingSection")
    public ResponseEntity<Response> deleteTrainVideo(@RequestHeader("userId") UUID userId,@RequestParam Long factTrainId) throws AppraisalException {
        log.info("DELETING APPRAISAL");
        return new ResponseEntity<>(ftryTrainingService.deleteTrainVideo(userId,factTrainId), HttpStatus.OK);
    }

}
