package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.FtryTraining;
import com.massil.dto.TrainingVideos;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.*;
import com.massil.repository.FtryTrainingRepo;
import com.massil.repository.RoleMappingRepo;
import com.massil.services.AppraiseVehicleService;
import com.massil.services.FtryTrainingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FtryTrainingServiceImpl implements FtryTrainingService {

    @Value("${video_folder_path}")
    private String videoFolderPath;

    @Autowired
    private AppraiseVehicleService appraiseVehicleService;
    @Autowired
    private RoleMappingRepo roleRepo;

    @Autowired
    private FtryTrainingRepo ftryTrainingRepo;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;
    @Value("${file_size}")
    private Long fileSize;

    @Override
    public Response trainingUpload(FtryTraining ftryTraining,  UUID userId) throws AppraisalException, IOException {
        Response response=new Response();
        ERoleMapping userById = roleRepo.findByUserId(userId);
        if (null!=userById && userById.getRole().getRoleGroup().equals("FA")) {
            EFtryTraining eFtryTraining = appraisalVehicleMapper.ftryTrainToEFtryTrain(ftryTraining);
            eFtryTraining.setUser(userById.getUser());
            ftryTrainingRepo.save(eFtryTraining);
            response.setStatus(Boolean.TRUE);
            response.setMessage("Training video uploaded successfully ");
            response.setCode(HttpStatus.OK.value());
        }else throw new AppraisalException("Role not found");
        return response;
    }

    @Override
    public TrainingVideos trainingDownload(UUID userId)  {
        List<EFtryTraining> all=null;
        ERoleMapping roleMapping = roleRepo.findByUserId(userId);
        if(roleMapping.getRole().getRoleGroup().equals(AppraisalConstants.VIDEO_FOR_D)) {
            all = ftryTrainingRepo.findAllVideoForD();
        }else if(roleMapping.getRole().getRole().equals(AppraisalConstants.P1)) {
            all = ftryTrainingRepo.findAllVideoForPU();
        }
        else all=ftryTrainingRepo.findAllByValid();
        List<FtryTraining> ftryTrainings = appraisalVehicleMapper.eFtryTrainToFtryTrain(all);
        Map<String, List<FtryTraining>> collected = ftryTrainings.stream().collect(Collectors.groupingBy(FtryTraining::getCategory));
        TrainingVideos trainingVideos=new TrainingVideos();
        trainingVideos.setCategoryWiseList(collected.entrySet());
        trainingVideos.setCode(HttpStatus.OK.value());
        trainingVideos.setStatus(true);
        return trainingVideos;
    }

    @Override
    public Response deleteTrainVideo(UUID userId, Long factTrainId) throws AppraisalException {
        Response response=new Response();
        ERoleMapping userById = roleRepo.findByUserId(userId);
        EFtryTraining trainingId = ftryTrainingRepo.findById(factTrainId).orElse(null);
        if (null!=userById && userById.getRole().getRole().equals("A1")) {
            if(null !=trainingId && trainingId.getValid() ){
                trainingId.setValid(false);
                ftryTrainingRepo.save(trainingId);
                response.setMessage("Deleted Successfully");
                response.setCode(HttpStatus.OK.value());
                response.setStatus(true);
            }else
                throw new AppraisalException("Did not find factoryTrainingId of  - " + factTrainId);
        }else
            throw new AppraisalException("Only Admin can delete videos  - " + userId);

        return response;
    }


}
