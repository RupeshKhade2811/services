package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.FtryTraining;
import com.massil.dto.TrainingVideos;

import java.io.IOException;
import java.util.UUID;

public interface FtryTrainingService {

    /**
     * This method uploads the training videos and description
     * @return
     */
    Response trainingUpload(FtryTraining ftryTraining,  UUID userId) throws AppraisalException, IOException;


    /**
     * This method downloads the training videos and description
     * @return
     */
    TrainingVideos trainingDownload(UUID userId) ;

    Response deleteTrainVideo(UUID userId,Long factTrainId) throws AppraisalException;


}
