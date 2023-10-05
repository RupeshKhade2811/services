package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.UUID;

public interface UserRegistrationService {
    /**
     *This method will create user
     * Method taking UserRegistration as argument and returning a message
     * @param userRegistration This is the object  of UserRegistration
     * @return message
     */

    Response createUser(UserRegistration userRegistration, Long dealerId) throws AppraisalException;


    /**
     * This method will show user based on userId
     * @param userId This is the primary key of EUserRegistration entity
     * @author YogeshKumarV
     * @return  UserRegistration
     */
    UserRegistration showInEditPage(UUID userId) throws AppraisalException;

    /**
     * This method updates the EUserRegistration based on userId
     * @param userRegistration This is the object of UserRegistration dto
     * @param userId This is the primary key of EUserRegistration entity
     * @author YogeshKumarV
     * @return  Response
     */
    Response updateUser(UserRegistration userRegistration, UUID userId) throws AppraisalException;

    /**
     * This method finds the user by userName and Password and returns User information
     * @param UserName This is username
     * @param password This is password
     * @return UserRegistration
     */
    UserRegistration findUser(String UserName,String password) throws AppraisalException;

    /**
     * This method is used to delete user
     * @param userId
     * @return
     */
    Response deleteUser(UUID userId) throws GlobalException;

    UsrDlrList corpDlr(UUID userId) throws AppraisalException;

     UsrDlrList dealerList(UUID userId) throws AppraisalException;

    Response assignFactoryUser(UUID factoryUser,UUID userId) throws AppraisalException;
    Response makeUserSubscribed(UUID userId, Boolean isSubscribed, Double amt);

    public UserExistResponse checkUser(String userName);

    public UserRegistration getUserinfo(UUID userId) throws AppraisalException;

    public String updateUserInIS(UpdateUserIS updateUserIS,UUID dealerD2UserId);
    String profilePicUpload(MultipartFile file, UUID userId) throws AppraisalException, IOException;
     VideoAndImageResponse downloadImageFromFileSystem(String imageName) throws IOException, NoSuchFileException;

     Response otpGenerator(String email) throws AppraisalException;
     Response sendMailForOtp(String email) throws AppraisalException, MessagingException;

     Response sendMailForPassowrdSuccess(DealerRegistration dealer,UUID d2UserId,String email) throws AppraisalException, IOException;

     Response validateOtp(String email,String otp) throws ParseException, AppraisalException;

}
