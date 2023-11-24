package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import com.massil.persistence.model.EDealerRegistration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;


public interface DealerRegistrationService {

    /**
     *This Method will create dealer taking DealerRegistration Object as argument
     * and returning a message
     * @param dealerRegistration This is the object of DealerRegistration
     * @return message
     */

    String createDealer(DealerRegistration dealerRegistration) throws AppraisalException;



    /**
     * This method will show dealer based on dealerId
 //    * @param dealerId This is the primary key of EDealerRegistration entity
     * @author YogeshKumarV
     * @return  DealerRegistration
     */
    DealerRegistration showInDealerEditPage(UUID d2UserId) throws AppraisalException;
    /**
     * This method updates the EDealerRegistration based on userId
     * @param dealerRegistration This is the object of DealerRegistration dto
     * @param d2UserId This is the primary key of EDealerRegistration entity
     * @author YogeshKumarV
     * @return  Response
     */
    Response updateDealer(DealerRegistration dealerRegistration, UUID d2UserId) throws AppraisalException, IOException;

    /**
     * This method is used to delete dealer from the database
     * @param dealerId
     * @return
     */
    Response deleteDealer(Long dealerId) throws GlobalException;

    DlrList getDlrList(Integer pageNo, Integer pageSize) throws GlobalException,AppraisalException;

    /**
     * This method set the company id to dealer
//     * @param dealerId
     * @return
     */
 //   Response setApprovalForDealer(Long dealerId);
    Response dealerD1Approval(DealerManagersAssign managersAssign) throws AppraisalException;


    Response chkDlrShpName(String dealerShipName);

    Response pendingDlr(Integer pageNo, Integer pageSize) throws AppraisalException;

    String pdfUpload(MultipartFile file) throws AppraisalException, IOException;


    List<UserRegistration> dlrListToDlrAdmin(UUID dealerAdminId, Integer pageNo, Integer pageSize) throws AppraisalException;
    public UpdateUserIS forUserUpdateIS(DealerRegistration newDealer, EDealerRegistration oldDealer);


}
