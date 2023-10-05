package com.massil.controller;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import com.massil.services.FilterSpecificationService;
import com.massil.services.UserRegistrationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/user")
@Tag(name = "User Registration", description = "User Module")
public class UserRegControllerController {

    Logger log = LoggerFactory.getLogger(UserRegControllerController.class);

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private FilterSpecificationService specificationService;




    /**
     * This method saves the new user by calling createUser() method of UserRegistrationService
     * @param userRegistration This is the object coming from ui
     * @param dealerId This is the dealer id coming from ui in header
     * @return
     */
    @Operation(summary = "Add users in database")
    @PostMapping("/registerUser")
    public ResponseEntity<Response> userCreation(@RequestBody @Validated UserRegistration userRegistration,
                                                 @RequestHeader(name="dealerId",required = false)Long dealerId) throws AppraisalException {
        log.info("User Creation method is triggered");
        Response response=userRegistrationService.createUser(userRegistration,dealerId);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }
    /**
     * This method updates the EUserRegistration entity by calling updateUser() method of UserRegistrationService
     * @param userRegistration This is the object of UserRegistration dto
     * @param userId This is primary key of EUserRegistration entity
     * @author YogeshKumarV
     * @return  Response
     */
    @Operation(summary = "Edit users")
    @PostMapping("/userUpdate")
    public ResponseEntity<Response> modifyUser(@RequestBody @Validated UserRegistration userRegistration, @RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("User Upadte method is Triggered **Controller**");
        Response response=userRegistrationService.updateUser(userRegistration,userId);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }
    /**
     * This method will show dealers based on userId by calling showInEditPage() method of UserRegistrationService
     * @param userId This is primary key of EUserRegistration entity
     * @author YogeshKumarV
     * @return  UserRegistration
     */
    @Operation(summary = "show users")
    @PostMapping("/showUser")
    public ResponseEntity<UserRegistration> showUserInEditPage(@RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("Showing user in edit page is triggered **Controller**");
        UserRegistration userRegistration = userRegistrationService.showInEditPage(userId);
        log.debug("OBJECT FOR SHOWING USER IN EDIT PAGE",userRegistration);
        return new ResponseEntity<>(userRegistration,HttpStatus.OK);
    }

    /**
     * This method returns user information
     * @param userName this is username
     * @param password this is password
     * @return UserRegistration
     */
    @Operation(summary = "find users by userName and Password")
    @PostMapping("/findUser")
    public ResponseEntity<UserRegistration> findUser(@RequestHeader("userName") String userName,@RequestHeader("password") String password) throws AppraisalException {
        log.info("Find user by userName and Password is triggered **Controller**");
        UserRegistration userRegistration = userRegistrationService.findUser(userName,password);
        return new ResponseEntity<>(userRegistration,HttpStatus.OK);
    }

    /**
     * This method is used to delete user from the database
     * @param userId
     * @return
     * @throws GlobalException
     */
    @Operation(summary="This method is used to delete user")
    @PostMapping("/deleteuser")
    public ResponseEntity<Response> removeUser(@RequestHeader("userId") UUID userId) throws GlobalException {
        return new ResponseEntity<>(userRegistrationService.deleteUser(userId),HttpStatus.OK);
    }

    /**
     * This method will return corporate dealer List
     * @param userId
     * @return
     * @throws AppraisalException
     */
    @PostMapping("/corDlrList")
    public ResponseEntity<Response> crpDlrList(@RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("Getting corporate DealerList **Controller**");
        return new ResponseEntity<>(userRegistrationService.corpDlr(userId),HttpStatus.OK);
    }

    /**
     * This method will return dealer list  under Corporate Admin
     * @param userId
     * @return
     * @throws AppraisalException
     */
    @PostMapping("/dlrList")
    public ResponseEntity<Response> dealerList(@RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("Getting DealerList under Corporate Admin **Controller**");
        return new ResponseEntity<>(userRegistrationService.dealerList(userId),HttpStatus.OK);
    }


    /**
     * This method will assign factory salesman or factory manager to user
     * @param factoryUser
     * @param userId
     * @return
     * @throws AppraisalException
     */
    @PostMapping("/assignFactoryUser")
    public ResponseEntity<Response> assignFactoryUser(@RequestParam ("factoryUserId") UUID factoryUser,@RequestParam("userId") UUID userId) throws AppraisalException {
        Response response = userRegistrationService.assignFactoryUser(factoryUser, userId);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    /**
     *
     * @param userId
     * @param isSubscribed
     * @param amt
     * @return
     */
    @PostMapping("/subscribeUser")
    public ResponseEntity<Response> makeUserSubscribed(@RequestParam("userId") UUID userId, @RequestParam("isSubscribed") Boolean isSubscribed, @RequestParam("amt") Double amt){
        Response response = userRegistrationService.makeUserSubscribed(userId, isSubscribed, amt);
        return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
    }

    @PostMapping("/checkUserName")
    public ResponseEntity<UserExistResponse> searchUserName(@RequestHeader("userName")String userName) throws AppraisalException {
        UserExistResponse userExistResponse = userRegistrationService.checkUser(userName);
        return new ResponseEntity<>(userExistResponse,HttpStatus.ACCEPTED);
    }
    @PostMapping("/corporateAdminList")
    public ResponseEntity<CorporateAdminList> getCorporateAdminList(@RequestBody SellingDealer filter) throws AppraisalException {
        CorporateAdminList corporateAdminList = specificationService.sendCorporateList(filter);
        return new ResponseEntity<>(corporateAdminList,HttpStatus.ACCEPTED);

    }


    @Operation(summary = "find users by userId")
    @PostMapping("/getUser")
    public ResponseEntity<UserRegistration> getUser(@RequestHeader("userId") UUID userId) throws AppraisalException {
        log.info("Find user by userName and Password is triggered **Controller**");
        UserRegistration userRegistration = userRegistrationService.getUserinfo(userId);
        return new ResponseEntity<>(userRegistration,HttpStatus.OK);
    }
    @PostMapping("/userList")
    public List<UserRegistration> searchUserList(@RequestParam ("roleGroup") String roleGroup, @RequestParam("name") String name){
        List<UserRegistration> userList = specificationService.getUserList(roleGroup, name);
        return userList;
    }

    /**
     * This method saves the profile image coming from ui and returns the file name
     * @param file MultipartFile data coming from ui
     * @return file name in Response class
     */
    @Operation(summary = "Upload Image and Returns image name")
    @PostMapping("/uploadProfilePic")
    public ResponseEntity<Response> uploadImage(@RequestBody MultipartFile file, @RequestHeader("userId") UUID userId) throws AppraisalException, IOException {
        log.info("RUNNING THIS METHOD FOR UPLOADING THE PROFILE PICTURE");
        if (null != file && null!=userId) {
            String fileName = userRegistrationService.profilePicUpload(file,userId);
            Response response = new Response();
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Image uploaded Successfully");
            response.setFileName(fileName);
            response.setStatus(true);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else throw new AppraisalException("image cann't be empty");
    }
    /**
     * This method sends the image of a car base on image name
     * @param pic1 This is the name of image send by ui
     * @return byte[]
     */
    @Operation(summary = "get Image by image name ")
    @GetMapping(value = "/getProfilePic",produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> downloadImageFromFileSystem(@RequestParam String pic1) throws IOException , NoSuchFileException {
        log.info("DOWNLOADING IMAGE FROM FILE");
        VideoAndImageResponse response = userRegistrationService.downloadImageFromFileSystem(pic1);
        return new ResponseEntity<>(response.getImageBytes(), HttpStatus.OK);
    }

    @PostMapping("/otpMail")
    public ResponseEntity<Response> generateOtp(@RequestHeader ("email") String email) throws AppraisalException, MessagingException {
        Response response = userRegistrationService.sendMailForOtp(email);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }
    @PostMapping("/passwordUpdated")
    public ResponseEntity<Response> passwordUpdationMail(
            @RequestBody DealerRegistration dealer,
            @RequestHeader ("d2UserId") UUID d2UserId,
            @RequestHeader ("email") String email
    ) throws AppraisalException, IOException {
        Response response = userRegistrationService.sendMailForPassowrdSuccess(dealer,d2UserId,email);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/otpValidation")
    public ResponseEntity<Response> otpValidation(@RequestHeader ("email") String email,@RequestHeader ("otp") String otp) throws ParseException, AppraisalException {
        Response response = userRegistrationService.validateOtp(email, otp);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }



}
