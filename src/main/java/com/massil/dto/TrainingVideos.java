package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class TrainingVideos extends Response {
    private Set<Map.Entry<String, List<FtryTraining>>> categoryWiseList;
}
