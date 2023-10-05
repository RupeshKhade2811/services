package com.massil.controller;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import com.massil.persistence.model.D2DlrList;
import com.massil.services.DealerRegistrationService;
import com.massil.services.EmailService;
import com.massil.services.FilterSpecificationService;
import freemarker.template.TemplateException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.Operation;


import java.io.IOException;
import java.util.List;
import java.util.UUID;


@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/dealer")
@Tag(name = "Dealer registration", description = "Dealer Module")
public class DealerRegistrationController {
    Logger log = LoggerFactory.getLogger(DealerRegistrationController.class);

    @Autowired
    private DealerRegistrationService dealerRegistrationService;
    @Autowired
    private FilterSpecificationService specService;

    @Autowired
    private EmailService emailService;

    /**
     * This method saves the new dealer by calling createDealer() method of DealerRegistrationService
     * @param dealerRegistration This is the object coming from ui
     * @return
     */
    @Operation(summary = "Add dealer in database")
    @PostMapping("/savedealer")
    public ResponseEntity<Response> dealerCreation(@RequestBody @Validated DealerRegistration dealerRegistration) throws AppraisalException {

        log.info("Dealer Creation method is triggered");
        String message=dealerRegistrationService.createDealer(dealerRegistration);
        Response response= new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage(message);
        response.setStatus(true);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }
    /**
     * This method updates the EDealerRegistration entity by calling updateDealer() method of DealerRegistrationService
     * @param dealerRegistration This is the object of DealerRegistration dto
     * @param d2UserId This is the object of EUserRegistration entity
     * @author YogeshKumarV
     * @return  Response
     */
    @PostMapping("/dealerUpdate")
    public ResponseEntity<Response> modifyDealer(@RequestBody @Validated DealerRegistration dealerRegistration, @RequestHeader("d2UserId") UUID d2UserId) throws AppraisalException, IOException {

        log.info("Dealer update method is triggered **Controller**");
        Response response = dealerRegistrationService.updateDealer(dealerRegistration, d2UserId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    /**
     * This method will show dealers based on dealerId by calling showInEditPage() method of DealerRegistrationService
     * @param d2UserId This is primary key of EUserRegistration entity
     * @author YogeshKumarV
     * @return  DealerRegistration
     */
    @PostMapping("/showDealer")
    public ResponseEntity<DealerRegistration> showDealerInEditPage(@RequestHeader("d2UserId")UUID d2UserId) throws AppraisalException {
        log.info("Showing Dealer in Edit page is triggered **Controller**");
        DealerRegistration dealerRegistration = dealerRegistrationService.showInDealerEditPage(d2UserId);
        log.debug("OBJECT FOR SHOWING DEALER IN EDIT PAGE **Controller");
        return new ResponseEntity<>(dealerRegistration,HttpStatus.OK);
    }

    /**
     * This method is used to delete dealer
     * @param dealerId
     * @return
     * @throws GlobalException
     */
    @Operation(summary = "This method is used to delete dealer")
    @PostMapping("/deletedealer")
    public ResponseEntity<Response> removeDealer(@RequestHeader("dealerId") Long dealerId) throws GlobalException {
        return new ResponseEntity<>(dealerRegistrationService.deleteDealer(dealerId),HttpStatus.OK);
    }


    @Operation(summary = "show Dealer List")
    @PostMapping("/showDealerList")
    public ResponseEntity<DlrList> showDlrDetails(@RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException, GlobalException {
        DlrList dlrList = dealerRegistrationService.getDlrList(pageNo,pageSize);
        return new ResponseEntity<>(dlrList,HttpStatus.OK);
    }
    @PostMapping("/searchDlrList")
    public ResponseEntity<DlrList> searchAlDlrList(@RequestBody DealerRegistration filter) throws AppraisalException {
        DlrList dlrList = specService.srchByDlrList(filter);
        return new ResponseEntity<>(dlrList,HttpStatus.OK);
    }
/*    @PostMapping("setCompIdInDealer")
    public ResponseEntity<Response> setCompIdInDealer(@RequestHeader("dealerId") Long dealerId,@RequestHeader("compId") Long compId){
        Response response = dealerRegistrationService.setCompIdToDealer(dealerId, compId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }*/
    @PostMapping("/searchAllDlrList")
    public ResponseEntity<DlrList> searchAllDlrList(@RequestBody DealerRegistration filter) throws AppraisalException {
        DlrList dlrList = specService.searchDealers(filter);
        return new ResponseEntity<>(dlrList,HttpStatus.OK);
    }
    @PostMapping("/dlrD1Approval")
    public ResponseEntity<Response> dealerApproval(@RequestBody DealerManagersAssign managersAssign) throws AppraisalException {
        Response response = dealerRegistrationService.dealerD1Approval(managersAssign);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @Operation(summary = "Checking dealership name in db ")
    @PostMapping("/chkDealershipname")
    public ResponseEntity<Response> chkDlrShpName(@RequestHeader("dealershipName") String dealerShipName) throws AppraisalException {

        log.info("DealerShp checking method is triggered");
        if(null!= dealerShipName){
            Response response=dealerRegistrationService.chkDlrShpName(dealerShipName);
            log.info(response.getMessage());
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }else throw  new AppraisalException("invalid input");

    }
    @PostMapping("/dlrCreationMail")
    public ResponseEntity<Response> dlrCreationEmail(@RequestBody DealerRegistration dealerRegistration) throws AppraisalException, TemplateException, MessagingException, IOException, MessagingException {
        Response response = emailService.sendToDealer(dealerRegistration);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @PostMapping("/pendingDlrList")
    public ResponseEntity<Response> pendingDlrList(@RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException {
        Response response = dealerRegistrationService.pendingDlr(pageNo,pageSize);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

/*    @PostMapping("/dealerApproval")
    public ResponseEntity<Response> setApprovalForDealer(@RequestHeader("dealerId") Long dealerId){
        Response response = dealerRegistrationService.setApprovalForDealer(dealerId);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }*/

    @Operation(summary = "Upload pdf and Returns pdf name")
    @PostMapping("/uploadPdf")
    public ResponseEntity<Response> uploadPdf(@RequestBody MultipartFile file) throws AppraisalException, IOException {
        log.info("RUNNING THIS METHOD FOR UPLOADING THE PDF");
        if (null != file) {
            String map = dealerRegistrationService.pdfUpload(file);
            Response response = new Response();
            response.setCode(HttpStatus.OK.value());
            response.setFileName(map);
            response.setMessage("File Uploaded Successfully");
            response.setStatus(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else throw new AppraisalException("pdf cann't be empty");



    }
    @Operation(summary = "show Dealer List to dealer admin")
    @PostMapping("/dlrListToDlrAdmin")
    public List<UserRegistration> dlrListToDlAdmin(@RequestHeader("dealerId") UUID dealerUserId,@RequestParam Integer pageNo, @RequestParam Integer pageSize) throws AppraisalException {
        return dealerRegistrationService.dlrListToDlrAdmin(dealerUserId, pageNo, pageSize);
    }

    @PostMapping("/searchDlrD2")
    public ResponseEntity< List<D2DlrList>> getDlrD2(@RequestBody SellingDealer filter) throws AppraisalException {
        List<D2DlrList> dlrD2Lists = specService.sendDlrD2(filter);
        return new ResponseEntity<>(dlrD2Lists,HttpStatus.ACCEPTED);

    }





}
