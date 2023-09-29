package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.Status;

import java.util.List;

public interface StatusService {

    /**
     * this method creates bidding status in table
     * @param statuses this is the object of List<Status>
     * @author yudhister
     * @return message
     */
    public String addStatus(List<Status> statuses);

    /**
     * this method update status in table
     * @param status this is the object of Status dto
     * @param stsId this is primary key of EStatus entity
     * @author yudhister
     * @return response
     */
    Response updateStatus(Status status, Long stsId) throws AppraisalException;

    /**
     * this method delete status in table
     * @param stsId this is primary key of EStatus entity
     * @author yudhister
     * @return response
     */
    Response deleteStatus(Long stsId) throws AppraisalException;
}
