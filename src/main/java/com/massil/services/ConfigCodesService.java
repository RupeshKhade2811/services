package com.massil.services;
/**
 *
 */


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.AppraisalConfigs;
import com.massil.dto.ConfigDropDown;
import com.massil.dto.FilterDropdowns;
import com.massil.dto.FilterParameters;

import java.util.List;
import java.util.UUID;


public interface ConfigCodesService {
    /**
     * This method inserts configCodes into the database
     * @param configCodes This is the object of List<ConfigCodes>
     * @return message
     */
    public String addConfigCode(List<ConfigDropDown> configCodes);

    /**
     *This method send the AppraisalConfigs dto to ui base on userId
     * @param userId This is the User id
     * @return AppraisalConfig
     */
    AppraisalConfigs getAppraisalConfigs(UUID userId) throws AppraisalException;

    /**
     * This method is used to update configCodes
     * @param configCodes
     * @param codeId
     * @return
     */
    Response updateConfig(ConfigDropDown configCodes,Long codeId) throws GlobalException;

    /**
     * This method is used ti delete the configCodes
     * @param codeId
     * @return
     */
    Response deleteConfig(Long codeId) throws GlobalException;

    FilterDropdowns sendFilterParams(FilterParameters filter, UUID userId, String module) throws AppraisalException;




}
