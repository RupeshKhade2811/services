package com.massil.services.impl;


/**
 * This class is implementing the methods of UserRegistrationService
 * this is the service class for user registration
 */

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.dto.UserRegistration;
import com.massil.dto.UsrDlrList;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.DealerRegistrationService;
import com.massil.services.UserRegistrationService;
import jakarta.mail.MessagingException;

import org.apache.commons.io.FilenameUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {

    private static final String OTP_CHARS = "0123456789";
    Logger log = LoggerFactory.getLogger(UserRegistrationServiceImpl.class);
    @Autowired
    UserRegistrationRepo userRegistrationRepo;
    @Value("${profile_pic_path}")
    private String profilePicPath;
    @Autowired
    DealerRegistrationRepo dealerRegistrationRepo;
    @Autowired
    RoleRepo roleRepo;
    @Autowired
    private RoleMappingRepo roleMappingRepo;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;
    @Autowired
    private CompanyRepo compayRepo;
    @Autowired
    private FactoryManagementRepo managementRepo;
    @Autowired
    private SubscriptionRepo subscriptionRepo;

    @Autowired
    private CreateOtpRepo createOtpRepo;

    @Value("${spring.mail.username}")
    private String fromMail;

    @Value("${identityServer_userCreation}")
    private String userCreation;

    @Value("${paramValue}")
    private String paramValue;

    @Autowired
    private JavaMailSender sender;

    @Autowired
    private DealerRegistrationService dealerRegistrationService;


    @Override
    @Transactional
    public Response createUser(UserRegistration userRegistration, Long dealerId) throws AppraisalException {
        log.info("User Creation method is triggered **Service IMPL**");
        EUserRegistration eUserRegistration=null;
        eUserRegistration = appraisalVehicleMapper.userRegisToEUserRegis(userRegistration);
        String value = callTheUrl(eUserRegistration);
        if(null!=value){
            if(null!=dealerId && (dealerId>0)) {
                EDealerRegistration eDealerRegistration = dealerRegistrationRepo.findDealerById(dealerId);
                if (null != eDealerRegistration) {
                    eUserRegistration.setDealer(eDealerRegistration);
                }else throw new AppraisalException("Invalid dealer_id");
            }
            eUserRegistration.setUserName(eUserRegistration.getUserName());
            eUserRegistration.setId(UUID.fromString(value));
            eUserRegistration.setValid(true);
            eUserRegistration.setCreatedOn(new Date());
            EUserRegistration save = userRegistrationRepo.save(eUserRegistration);
            ERoleMapping eRoleMapping = new ERoleMapping();
            EUserRegistration manager = userRegistrationRepo.findUserById(userRegistration.getManagerId());
            EUserRegistration factoryManager =  userRegistrationRepo.findUserById(userRegistration.getFactoryManager());
            EUserRegistration factorySalesman = userRegistrationRepo.findUserById(userRegistration.getFactorySalesman());
            ECompany companyId = compayRepo.findByCompanyId(userRegistration.getCompanyId());

            if (null!=userRegistration.getRoleId()) {
                ERole role = roleRepo.findByRole(userRegistration.getRoleId());

                eRoleMapping.setUser(save);
                eRoleMapping.setRole(role);
                eRoleMapping.setManager(manager);
                eRoleMapping.setCompany(companyId);
                eRoleMapping.setFactoryManager(factoryManager);
                eRoleMapping.setFactorySalesman(factorySalesman);
                eRoleMapping.setDealerAdmin(userRegistration.getDealerAdmin());
                roleMappingRepo.save(eRoleMapping);

            }else {
                ERole roleOfPublicUser = roleRepo.findRoleOfPublicUser(AppraisalConstants.P1);
                eRoleMapping.setUser(save);
                eRoleMapping.setRole(roleOfPublicUser);
                eRoleMapping.setManager(manager);
                eRoleMapping.setCompany(companyId);
                eRoleMapping.setFactoryManager(factoryManager);
                eRoleMapping.setFactorySalesman(factorySalesman);
                roleMappingRepo.save(eRoleMapping);
            }
        }
        Response response = new Response();
        response.setCode(HttpStatus.OK.value());
        response.setMessage("User Has Been saved Successfully");
        response.setStatus(true);
        return response;
    }

    public String callTheUrl(EUserRegistration eUserReg){
        Users users1 = appraisalVehicleMapper.EUserRegToUsers(eUserReg);
        List<UserEmail>emails= new ArrayList<>();
        EnterpriseExtension extension= new EnterpriseExtension();
        UserEmail email= new UserEmail();
        email.setValue(eUserReg.getEmail());
        email.setPrimary(true);
        emails.add(email);
        users1.setEmails(emails);
        users1.setEnterpriseExtension(extension);
        log.debug("Object sending to Identity Server{}",users1);
        SendingRole role = sendJsonResponse(users1);
        String s = sendingJsonPatchReq(role);
        return role.getOperations().get(0).getValue().get(0).getValue();

    }

    public SendingRole sendJsonResponse(Users users) {
        log.debug("User details sending to identity server **triggered**");
        SendingRole role = null;
        UserExist userExist=null;
        try{
        RestTemplate restTemplate = new RestTemplate();
        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept", "application/scim+json");
        headers.set("Content-Type", "application/scim+json");
        headers.set("Authorization", "Basic YWRtaW46YWRtaW4=");

        // Create the request entity with the JSON payload and headers
        HttpEntity<Users> requestEntity = new HttpEntity<>(users, headers);
        String url=userCreation+"Users";
        // Send the POST request to the third-party API
        ResponseEntity<UserResponse> response1 = restTemplate.postForEntity(url, requestEntity, UserResponse.class);
        UserResponse body = response1.getBody();
        List<UserValue> uservalues = new ArrayList<>();
        UserValue userValue = new UserValue();
        userValue.setValue(body.getId());
        userValue.setDisplay(body.getUserName());
        uservalues.add(userValue);

        Operation operation = new Operation();
        List<Operation> operations = new ArrayList<>();
        operation.setOp("add");
        operation.setPath("users");
        operation.setValue(uservalues);
        operations.add(operation);

        role = new SendingRole();
        role.setOperations(operations);

    }catch (HttpClientErrorException.Conflict ex){

           UserExistResponse userExistResponse = checkUser(users.getUserName());

            List<UserValue> uservalues = new ArrayList<>();
            UserValue userValue= new UserValue();
            userValue.setValue(userExistResponse.getResponse().get(0).getId());
            userValue.setDisplay(userExistResponse.getResponse().get(0).getUserName());
            uservalues.add(userValue);

            List<Operation> operations = new ArrayList<>();
            Operation operation= new Operation();
            operation.setOp("add");
            operation.setPath("users");
            operation.setValue(uservalues);
            operations.add(operation);

            role= new SendingRole();
            role.setOperations(operations);

        }
        return role;
    }

    @Override
    public UserExistResponse checkUser(String userName) {
        log.debug("UserName Searching checkUserImpl **triggered**");
        RestTemplate restTemplate = new RestTemplate();
        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept","application/scim+json");
        headers.set("Content-Type","application/scim+json");
        headers.set("Authorization","Basic YWRtaW46YWRtaW4=");

        UserExist userExist= new UserExist();
        userExist.setFilter("userName Eq "+userName);

        List<String> schemas= new ArrayList<>();
        String a= "urn:ietf:params:scim:api:messages:2.0:SearchRequest";
        schemas.add(a);
        userExist.setSchemas(schemas);

        String url=userCreation+"Users/.search";

        // Create the request entity with the JSON payload and headers
        HttpEntity<UserExist> requestEntity = new HttpEntity<>(userExist, headers);

        // Send the POST request to the third-party API
        log.debug("URL: " +url);
        log.debug("Filter: " + userExist.getFilter());
        ResponseEntity<SearchRes> response1 = restTemplate.postForEntity(url, requestEntity, SearchRes.class);

        UserExistResponse userExistResponse=null;
        if(0!=response1.getBody().getItemsPerPage() && 0!=response1.getBody().getTotalResults()){
            SearchRes body = response1.getBody();
            UserResponse response= new UserResponse();
            response.setId(body.getResources().get(0).getId());
            response.setUserName(body.getResources().get(0).getUserName());
            List<UserResponse>responseList=new ArrayList<>();
            responseList.add(response);
            userExistResponse= new UserExistResponse();
            userExistResponse.setResponse(responseList);
            userExistResponse.setMessage("This username already exist");
            userExistResponse.setCode(HttpStatus.CONFLICT.value());
            userExistResponse.setStatus(true);

        }else{
            userExistResponse= new UserExistResponse();
            userExistResponse.setMessage("username is available");
            userExistResponse.setCode(HttpStatus.OK.value());
        }
        return userExistResponse;

    }

    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
        return restTemplate;
    }

    private String sendingJsonPatchReq(SendingRole role) {
        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept","application/scim+json");
        headers.set("Content-Type","application/scim+json");
        headers.set("Authorization","Basic YWRtaW46YWRtaW4=");

        RestTemplate restTemplate = restTemplate();

        // Set the query parameters in the URI
        String url = userCreation +"Roles/" +paramValue;

        // Make the PATCH request with query parameters and headers
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(role, headers), String.class);
        return "success";
    }

    @Override
    public String updateUserInIS(UpdateUserIS updateUserIS,UUID dealerD2UserId) {
        // Set the request headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("accept","application/scim+json");
        headers.set("Content-Type","application/scim+json");
        headers.set("Authorization","Basic YWRtaW46YWRtaW4=");

        RestTemplate restTemplate = restTemplate();

        // Set the query parameters in the URI
        String url = userCreation +"Users/" +dealerD2UserId;

        // Make the PATCH request with query parameters and headers
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(updateUserIS, headers), String.class);

        return "success";
    }


    @Override
    public UserRegistration showInEditPage(UUID userId) throws AppraisalException {
        log.info("Showing user in edit page is triggered **Service IMPL**");

            EUserRegistration user = userRegistrationRepo.findUserById(userId);
            if (null!=user){
                UserRegistration userRegistration = appraisalVehicleMapper.eUserRegisToUserRegis(user);
                userRegistration.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(roleMappingRepo.findByUserId(userId).getRole()));
                if(null!=user.getDealer()) {
                    userRegistration.setDealershipNames(user.getDealer().getDealershipNames());
                }
                userRegistration.setPassword(null);
                userRegistration.setMessage("Successfully showing users in edit page");
                userRegistration.setCode(HttpStatus.OK.value());
                userRegistration.setStatus(true);
                log.debug("Object Showing in UI {}",userRegistration);
                return userRegistration;
            }
            else throw new AppraisalException("Invalid user Id");

    }

    @Override
    public Response updateUser(UserRegistration newUser, UUID userId) throws AppraisalException {
        log.info("User Update method is Triggered **Service IMPL**");
        EUserRegistration user = userRegistrationRepo.findUserById(userId);
        ERoleMapping roleMapping = roleMappingRepo.findByUserId(userId);
        EUserRegistration manager = userRegistrationRepo.findUserById(newUser.getManagerId());
        EUserRegistration factoryManager =  userRegistrationRepo.findUserById(newUser.getFactoryManager());
        EUserRegistration factorySalesman = userRegistrationRepo.findUserById(newUser.getFactorySalesman());
        ECompany companyId = compayRepo.findByCompanyId(newUser.getCompanyId());

        if (null!=user) {
                user = appraisalVehicleMapper.updateEUserRegisteration(newUser, user);
                log.debug("object coming after update {}",user);
                user.setModifiedOn(new Date());
                userRegistrationRepo.save(user);
            }else throw new AppraisalException("invalid userId");

        if(null!=manager){
            roleMapping.setManager(manager);
        }
        if(null!=factoryManager){
            roleMapping.setFactoryManager(factoryManager);
        }

        if(null!=factorySalesman){
            roleMapping.setFactorySalesman(factorySalesman);
        }
        if(null!=companyId){
            roleMapping.setCompany(companyId);
        }
        roleMappingRepo.save(roleMapping);
        Response response =new Response();
        response.setMessage("Updated Successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(true);
        return response;
    }

    @Override
    public UserRegistration findUser(String userName, String password) throws AppraisalException {
        log.info("finding user is available or not base on userName and Password");
            EUserRegistration user = userRegistrationRepo.findByUserNameAndPassword(userName,password);

            if (null!=user){
                UserRegistration userRegistration = appraisalVehicleMapper.eUserRegisToUserRegis(user);

                List<ERoleMapping> rolemap = roleMappingRepo.findByUserRoll(user.getId());
                for (ERoleMapping role:rolemap) {
                    userRegistration.setRoleId(role.getRole().getId());
                    appraisalVehicleMapper.eRoleToRole(role.getRole());
                    userRegistration.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(role.getRole()));
                }
                userRegistration.setUserName(user.getUserName());
                userRegistration.setMessage("User found successfully ");
                userRegistration.setCode(HttpStatus.OK.value());
                userRegistration.setStatus(true);
                log.debug("User found for userName {}:{}",userName,userRegistration);
                return userRegistration;
            }
            else throw new AppraisalException("Invalid username or password");

    }

    @Override
    public Response deleteUser(UUID userId) throws GlobalException {
        Response response=new Response();
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        if (null!=userById){
            userById.setValid(Boolean.FALSE);
            ERoleMapping byUserId = roleMappingRepo.findByUserId(userById.getId());
            byUserId.setValid(Boolean.FALSE);
            response.setStatus(Boolean.TRUE);
            response.setMessage("user deleted successfully");
            response.setCode(HttpStatus.OK.value());
            userRegistrationRepo.save(userById);
            roleMappingRepo.save(byUserId);
        }else throw new GlobalException("User Not found");
        return response;
    }
    @Override
    public UsrDlrList corpDlr(UUID userId) throws AppraisalException{
        UsrDlrList usrDlrList = new UsrDlrList();
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        if(null!=userById){
            UserRegistration userRegistration1 = appraisalVehicleMapper.eUserRegisToUserRegis(userById);
            userRegistration1.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(roleMappingRepo.findByUserId(userId).getRole()));
            List<ERoleMapping> byUserRoll = roleMappingRepo.findByManagerId(userId);
        List<UserRegistration> corporateDlr1=new ArrayList<>();
            for (ERoleMapping eRoleMapping : byUserRoll) {
                EUserRegistration user = eRoleMapping.getUser();
                UserRegistration userRegistration = appraisalVehicleMapper.eUserRegisToUserRegis(user);
                assert false;
                userRegistration.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(eRoleMapping.getRole()));
                corporateDlr1.add(userRegistration);
            }
        usrDlrList.setDetails(userRegistration1);
        usrDlrList.setUserList(corporateDlr1);
        usrDlrList.setCode(HttpStatus.OK.value());
        usrDlrList.setStatus(true);
        usrDlrList.setMessage("list found");
        }else throw new AppraisalException("invalid userId");
        return usrDlrList;
    }
    @Override
    public UsrDlrList dealerList(UUID userId) throws AppraisalException {
        ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);

        List<UserRegistration> listOfDealer=new ArrayList<>();
        UsrDlrList usrDlrList = new UsrDlrList();

        if(byUserId.getRole().getRoleGroup().equalsIgnoreCase("FA")){
            List<ERoleMapping> dealerList = roleMappingRepo.getDealerList();
            for (ERoleMapping list:dealerList){
                UserRegistration userRegistration = appraisalVehicleMapper.eUserRegisToUserRegis(list.getUser());
                userRegistration.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(list.getRole()));
                listOfDealer.add(userRegistration);
            }
            UserRegistration userReg = appraisalVehicleMapper.eUserRegisToUserRegis(byUserId.getUser());

            userReg.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(roleMappingRepo.findByUserId(userId).getRole()));
            usrDlrList.setDetails(userReg);
            usrDlrList.setUserList(listOfDealer);
        usrDlrList.setCode(HttpStatus.OK.value());
        usrDlrList.setStatus(true);
        usrDlrList.setMessage("list found");
        } else throw new AppraisalException("This user is not a Admin");
        return usrDlrList;
    }

    @Transactional
    @Override
    public Response assignFactoryUser(UUID factoryUser, UUID userId) throws AppraisalException {
        int recordAvailable = managementRepo.findRecordAvailable(factoryUser, userId);
        Response response = new Response();
        if(recordAvailable<=0) {
            EUserRegistration userById = userRegistrationRepo.findUserById(userId);
            EUserRegistration factoryUserById = userRegistrationRepo.findUserById(factoryUser);
            ERoleMapping roleMapping = roleMappingRepo.findByUserId(factoryUser);

            if (null != userById && null != factoryUserById && (roleMapping.getRole().getRoleGroup().equals("FS") || roleMapping.getRole().getRoleGroup().equals("FM"))) {
                EFactoryManagement management = new EFactoryManagement();
                management.setFactoryUser(factoryUserById);
                management.setUser(userById);
                management.setSignFrom(new Date());
                managementRepo.save(management);

                if(roleMapping.getRole().getRoleGroup().equals("FS")){
                    roleMapping.setFactorySalesman(factoryUserById);
                }
                if(roleMapping.getRole().getRoleGroup().equals("FM")){
                    roleMapping.setFactoryManager(factoryUserById);
                }
                roleMappingRepo.save(roleMapping);

                response.setMessage("factory user assigned successfully");
                response.setCode(HttpStatus.OK.value());
                response.setStatus(true);
            }
            return response;
        }throw new AppraisalException("Already user is assign to factoryUser");

    }

    @Override
    public Response makeUserSubscribed(UUID userId, Boolean isSubscribed,Double amt) {
        Response response = new Response();

        EFactorySubscription record1 = subscriptionRepo.findRecord(userId);
        if(null==record1 ) {
            if(Boolean.TRUE.equals(isSubscribed) && amt>0) {
                EUserRegistration userById = userRegistrationRepo.findUserById(userId);
                EFactorySubscription subscription = new EFactorySubscription();
                subscription.setUser(userById);
                subscription.setSubscribed(true);
                subscription.setSubscription(new Date());
                subscription.setAmount(amt);
                subscriptionRepo.save(subscription);
                response.setMessage("user is subscribed successfully");
            }else response.setMessage("user record not available or already unsubscribed");
        }
        else {
            if(Boolean.FALSE.equals(isSubscribed) && amt<=0) {
                record1.setSubscribed(false);
                record1.setUnSubscription(new Date());
                subscriptionRepo.save(record1);


                response.setMessage("user is unsubscribed successfully");
            }
            else response.setMessage("user is already subscribed");
        }
        response.setStatus(true);
        response.setCode(HttpStatus.OK.value());
        return response;
    }

    @Override
    public UserRegistration getUserinfo(UUID userId) throws AppraisalException {
        log.info("finding user is available or not base on userID");
        EUserRegistration byUserName = userRegistrationRepo.findByUserName(userId);
        if(null!=byUserName){
            UserRegistration userRegistration = appraisalVehicleMapper.eUserRegisToUserRegis(byUserName);
            List<ERoleMapping> rolemap = roleMappingRepo.findByUserRoll(userRegistration.getId());
            for (ERoleMapping role:rolemap) {
                userRegistration.setRoleId(role.getRole().getId());
                userRegistration.setRoleOfUser(appraisalVehicleMapper.eRoleToRole(role.getRole()));
            }
            if(null!=byUserName.getDealer()) {
                userRegistration.setDealershipNames(byUserName.getDealer().getDealershipNames());
            }
            userRegistration.setPassword(null);
            userRegistration.setCode(HttpStatus.OK.value());
            userRegistration.setMessage("user Found");
            userRegistration.setStatus(true);
            log.debug("User found for userId {}:{}",userId,userRegistration);
            return userRegistration;
        }
        else throw new AppraisalException("Invalid username");

    }
    @Override
    public String profilePicUpload(MultipartFile file, UUID userId) throws AppraisalException, IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (null != extension && (extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg"))) {
            String filename = UUID.randomUUID() + "." + extension;
            Path filePath = Paths.get(profilePicPath + filename);
            Files.write(filePath, file.getBytes());
            EUserRegistration userById = userRegistrationRepo.findUserById(userId);
            if(null!=userById){
                userById.setProfilePicture(filename);
                userRegistrationRepo.save(userById);
                return filename;
            }
            return "";
        }
        throw new AppraisalException("only .jpeg, .png,.jpg file types are allowed");

    }

    @Override
    public VideoAndImageResponse downloadImageFromFileSystem(String imageName) throws IOException, NoSuchFileException {
        VideoAndImageResponse responseDTO = new VideoAndImageResponse();
        byte[] images=null;
        String filePath = profilePicPath + imageName;
        images = Files.readAllBytes(new File(filePath).toPath());//Reading from folder
        responseDTO.setImageBytes(images);
        responseDTO.setCode(HttpStatus.OK.value());
        responseDTO.setStatus(true);
        responseDTO.setMessage("Image send successfully");
        return responseDTO;

    }

    @Override
    public Response otpGenerator(String email) throws AppraisalException {
        Response response = new Response();
        EUserRegistration userByEmail = userRegistrationRepo.findUserByEmailId(email);
        StringBuilder otp = new StringBuilder();
        if (null != userByEmail) {
            Random random = new Random();
            for (int i = 0; i < 6; i++) {
                otp.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
            }
            ECreateOtp createOtp = new ECreateOtp();
            createOtp.setUser(userByEmail);
            createOtp.setEmail(email);
            createOtp.setOtp(otp.toString());
            createOtp.setValid(Boolean.TRUE);
            createOtp.setCreatedOn(new Date());
            createOtpRepo.save(createOtp);
            response.setCode(HttpStatus.OK.value());
            response.setStatus(true);
            response.setMessage("OTP generated successfully");
            response.setFileName(otp.toString());
            response.setUserId(userByEmail.getId());
        } else throw new AppraisalException("Enter the valid email");
        return response;
    }

    @Override
    public Response sendMailForOtp(String email) throws AppraisalException, MessagingException {


        Response response = new Response();

        SimpleMailMessage message = new SimpleMailMessage();
        Response response1 = otpGenerator(email);

        String mailText = "Hi sir/ma'am,\n \tYour OTP for Reset Password is: \n\t" +
                "[" + response1.getFileName() + "]" + "\n\nThank You";
        message.setFrom(fromMail);
        message.setTo(email);
        message.setSubject("Password Reset *****OTP*****");
        message.setText(mailText);
        sender.send(message);

        response.setCode((HttpStatus.OK.value()));
        response.setMessage("mail send to : " + email);
        response.setStatus(Boolean.TRUE);
        response.setUserId(response1.getUserId());
        System.out.println(response1.getUserId());
        log.info("mail send to : {}", email);

        return response;
    }

    @Override
    public Response sendMailForPassowrdSuccess(DealerRegistration dealer,UUID d2UserId,String email) throws AppraisalException, IOException {
        Response response1 = dealerRegistrationService.updateDealer(dealer, d2UserId);
        if (response1.getCode()==200) {
            Response response = new Response();
            SimpleMailMessage helper = new SimpleMailMessage();
            String mailText = "Hi sir/ma'am,\n \tYour Password  has been updated successfully\n\nThank You";
            helper.setFrom(fromMail);

            helper.setTo(email);
            helper.setText(mailText);
            helper.setSubject("Password Updated..!");
            sender.send(helper);
            response.setCode((HttpStatus.OK.value()));
            response.setMessage("mail send to : " + email);
            response.setStatus(Boolean.TRUE);
            response.setFileName(response1.getMessage());
            log.info("mail send to : {}", email);
            return response;
        }else throw new AppraisalException("Password Updation failed");
    }

    @Override
    public Response validateOtp(String email, String otp) throws AppraisalException, ParseException {
        Response response = new Response();
        ECreateOtp createOtp = createOtpRepo.gettingLatestOtp(email);
        if (null != createOtp) {
            System.out.println(createOtp.getCreatedOn());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
            Date parsedGivenTime = dateFormat.parse(createOtp.getCreatedOn().toString());
            Date currentTime = new Date();
            long differenceInMillis = currentTime.getTime() - parsedGivenTime.getTime();
            long differenceInMinutes = differenceInMillis / (60 * 1000);
            System.out.println(differenceInMinutes);
            if (otp.equals(createOtp.getOtp())) {
                if (differenceInMinutes <= 15) {
                    response.setStatus(Boolean.TRUE);
                    response.setMessage("Success");
                    response.setCode(HttpStatus.OK.value());
                    System.out.println(differenceInMinutes);

                } else throw new AppraisalException("The OTP has expired");
            } else throw new AppraisalException("Enter the Valid OTP...!");
        } else throw new AppraisalException("Enter the valid Email id");
        return response;
    }
}

