package com.massil.services.impl;


import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.DealerRegistration;
import com.massil.dto.MailTeamSprt;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.EmailService;
import com.massil.services.FactoryPdfGenerator;
import com.massil.util.CompareUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;

import jakarta.mail.internet.MimeMessage;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.springframework.ui.freemarker.FreeMarkerTemplateUtils.processTemplateIntoString;


@Service
public class EmailServiceImpl implements EmailService {
    Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Autowired
    private UserRegistrationRepo userRegistrationRepo;

    @Autowired
    private AppraiseVehicleRepo appraiseVehicleRepo;

    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private UserRegistrationRepo userRepo;

    @Autowired
    private FileStatusRepo fileRepo;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private FactoryPdfGenerator pdfGenerator;

    @Autowired
    private DealerRegistrationRepo dealerRegRepo;

    @Autowired
    private RoleMappingRepo roleMappingRepo;

    @Autowired
    private Configuration config;
    @Autowired
    private CompareUtils comUtl;
    @Value("${saved_pdf_Path}")
    private String pdfpath;
    @Value("${image_folder_path}")
    private String imageFolderPath;
    @Value("${spring.mail.username}")
    private String fromMail;

    @Value("${access_key}")
    private String accesskey;

    @Value(("${secret}"))
    private String secret;

    @Value(("${amazonS3_url}"))
    private String amazonS3Url;



    @Override
    public Response sendCreationEmail(UUID userId) throws AppraisalException, TemplateException, IOException, MessagingException {

        Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
        Response response = new Response();
        MimeMessage message = sender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            EUserRegistration userById = userRegistrationRepo.findUserById(userId);

            if(null!=userById.getEmail()) {
                EAppraiseVehicle apprVehByUserId = appraiseVehicleRepo.getAppraisalDetails(userId);
                EApprTestDrSts apprTestDrSts = apprVehByUserId.getTdStatus();
                String name;
                if (null!=apprVehByUserId.getClientFirstName()&&null!=apprVehByUserId.getClientLastName()) {
                    name = comUtl.checkDbVariable(apprVehByUserId.getClientFirstName()) + " " + comUtl.checkDbVariable(apprVehByUserId.getClientLastName());
                }else {
                    name="-";
                }
                String vin = comUtl.checkDbVariable(apprVehByUserId.getVinNumber());
                String vehYr = comUtl.checkDbVariable(apprVehByUserId.getVehicleYear().toString());
                String vehSer = comUtl.checkDbVariable(apprVehByUserId.getVehicleSeries());
                String vehMod = comUtl.checkDbVariable(apprVehByUserId.getVehicleModel());
                Long vehMil = comUtl.checkDbVariable(apprVehByUserId.getVehicleMileage());
                String vehMake = comUtl.checkDbVariable(apprVehByUserId.getVehicleMake());
                Integer appVal = comUtl.checkDbVariable(apprVehByUserId.getAppraisedValue()).intValue();
                String frDrSdDgSts = comUtl.checkDbVariable(apprTestDrSts.getFrDrSideDmgSts());
                String rrDrSdDgSts = comUtl.checkDbVariable(apprTestDrSts.getRearDrSideDmgSts());
                String rrPsSdDgSts = comUtl.checkDbVariable(apprTestDrSts.getRearPassenSideDmgSts());
                String frPsSdDgSts = comUtl.checkDbVariable(apprTestDrSts.getFrPassenSideDmgSts());
                String frDrSdPaWkSts = comUtl.checkDbVariable(apprTestDrSts.getFrDrSidePntWrkSts());
                String rrDrSdPaWkSts = comUtl.checkDbVariable(apprTestDrSts.getRearDrSidePntWrk());
                String frPsSdPaWkSts = comUtl.checkDbVariable(apprTestDrSts.getFrPassenSidePntWrk());
                String rrPsSdPaWkSts = comUtl.checkDbVariable(apprTestDrSts.getRearPassenSidePntWrk());

                Template t = config.getTemplate("email-template.ftl");

                Map<String, Object> model1 = new HashMap<>();
                model1.put(AppraisalConstants.NAME, name);
                model1.put(AppraisalConstants.VIN, vin);
                model1.put(AppraisalConstants.VEHICLE_YEAR, vehYr);
                model1.put(AppraisalConstants.VEHICLE_SERIES, vehSer);
                model1.put(AppraisalConstants.VEHICLE_MODEL, vehMod);
                model1.put(AppraisalConstants.VEHICLE_MILEAGE, vehMil);
                model1.put(AppraisalConstants.VEHICLE_MAKE, vehMake);
                model1.put(AppraisalConstants.APPRAISAL_VALUE, appVal);
                model1.put(AppraisalConstants.FRONT_DRIVER_SIDE_DAMAGE_STATUS, frDrSdDgSts);
                model1.put(AppraisalConstants.REAR_DRIVER_SIDE_DAMAGE_STATUS, rrDrSdDgSts);
                model1.put(AppraisalConstants.REAR_PASSENGER_SIDE_DAMAGE_STATUS, rrPsSdDgSts);
                model1.put(AppraisalConstants.FRONT_PASSENGER_SIDE_DAMAGE_STATUS, frPsSdDgSts);
                model1.put(AppraisalConstants.FRONT_DRIVER_SIDE_PAINTWORK_STATUS, frDrSdPaWkSts);
                model1.put(AppraisalConstants.REAR_DRIVER_SIDE_PAINTWORK_STATUS, rrDrSdPaWkSts);
                model1.put(AppraisalConstants.FRONT_PASSENGER_SIDE_PAINTWORK_STATUS, frPsSdPaWkSts);
                model1.put(AppraisalConstants.REAR_PASSENGER_SIDE_PAINTWORK_STATUS, rrPsSdPaWkSts);

                String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model1);

                helper.setFrom(fromMail);
                helper.setTo(userById.getEmail());
                
                helper.setText(html, true);
                helper.setSubject(AppraisalConstants.SUBJECT);
                sender.send(message);
                response.setCode((HttpStatus.OK.value()));
                response.setMessage("mail send to : " + userById.getEmail());
                response.setStatus(Boolean.TRUE);
                log.info("mail send to : {}" , userById.getEmail());

            }else throw new AppraisalException("Email Id not found");

        return response;
    }
    @Override
    public Response offerUpdateEmail(Long offerId) throws AppraisalException, MessagingException, TemplateException, IOException {

        Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
        Response response = new Response();
//        MimeMessage message = sender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());
            EOffers offerById = offersRepo.findById(offerId).orElse(null);
            if(null!=offerById) {

                String statusCode = offerById.getStatus().getStatusCode();

                EUserRegistration userById = userRegistrationRepo.findUserById(offerById.getBuyerUserId().getId());
                String buyerEmail = userById.getEmail();

                EAppraiseVehicle apprVeh = appraiseVehicleRepo.getAppraisalDetails(offerById.getSellerUserId().getId());
                String sellerEmail = apprVeh.getUser().getEmail();

                String message1 = "";
                String price = "";
                String message2="";
                if (statusCode.equals(AppraisalConstants.BUYERACCEPTED)) {
                    message1 = "Please Note, Your offer has been accepted successfully. ";
                    message2 = "Please Note, Buyer Accepted your vehicle";
                    price = offerById.getPrice().toString();

                } else if (statusCode.equals(AppraisalConstants.SELLERREJECTED)) {
                    message1 = "Please Note, Your offer has been rejected by the seller. ";
                    message2 = "Please Note, You Rejected your vehicle to sell to Buyer";
                    price = offerById.getPrice().toString();
                } else if (statusCode.equals(AppraisalConstants.BUYERREJECTED)) {
                    message1 = "Please Note, Your offer has been rejected . ";
                    message2 = "Please Note, Buyer Rejected your vehicle";
                    price = offerById.getPrice().toString();
                } else if (statusCode.equals(AppraisalConstants.SELLERACCEPTED)) {
                    message1 = "Please Note, Your offer has been accepted successfully by the seller. ";
                    message2 = "Please Note, you Accepted your vehicle to the Buyer";
                    price = offerById.getPrice().toString();
                }

                String name = comUtl.checkDbVariable(apprVeh.getUser().getFirstName()) + " " + comUtl.checkDbVariable(apprVeh.getUser().getLastName());
                String vin = comUtl.checkDbVariable(apprVeh.getVinNumber());

                Map<String, Object> model1 = new HashMap<>();
                model1.put(AppraisalConstants.SELLERNAME, name);
                model1.put(AppraisalConstants.VIN, vin);
                model1.put(AppraisalConstants.MESSAGE, message1);
                model1.put(AppraisalConstants.VALUE, price);

                Map<String, Object> model2 = new HashMap<>();
                model2.put(AppraisalConstants.SELLERNAME, name);
                model2.put(AppraisalConstants.VIN, vin);
                model2.put(AppraisalConstants.MESSAGE, message2);
                model2.put(AppraisalConstants.VALUE, price);

                mailCall(model1,buyerEmail);
                mailCall(model2,sellerEmail);

                response.setCode((HttpStatus.OK.value()));
                response.setMessage("mail send to : " + buyerEmail + " and " +sellerEmail);
                response.setStatus(Boolean.TRUE);
                log.info("mail send to : {}" ,buyerEmail,sellerEmail);
            } else throw new AppraisalException("invalid offer id");

        return response;
    }

    public void mailCall (Map model1,String email) throws IOException, TemplateException, MessagingException {

        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

        Template t = config.getTemplate("offerEmail.ftl");
        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model1);

                helper.setFrom(fromMail);
                helper.setTo(email);
                
                helper.setText(html, true);
                helper.setSubject(AppraisalConstants.SUBJECT);

        sender.send(message);
    }


    @Override
    public Response mailToSprtTeam(MailTeamSprt mailTeamSprt, UUID userId) throws AppraisalException, TemplateException, MessagingException, IOException {

        Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
        Response response = new Response();

        EUserRegistration usrId = userRepo.findUserById(userId);
        if(null!=usrId) {

            String usrEmail = usrId.getEmail();
            String userName = usrId.getUserName();

            String message = mailTeamSprt.getSprtWithIssue();

            Map<String, Object> model = new HashMap<>();
            model.put(AppraisalConstants.MESSAGE, message);
            model.put(AppraisalConstants.USERNAME,userName);

            mailToTeam(model, usrEmail);

            response.setCode((HttpStatus.OK.value()));
            response.setMessage("mail send to : " + usrEmail);
            response.setStatus(Boolean.TRUE);
            log.info("mail send to : {}" ,usrEmail);
        } else throw new AppraisalException("invalid user id");



        return response;
    }


    public void mailToTeam (Map model, String email)  {
        try {
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,StandardCharsets.UTF_8.name());

            Template u = config.getTemplate("mailSupport.ftl");
            String html = processTemplateIntoString(u, model);

            helper.setTo(email);
            helper.setFrom(fromMail);
            helper.setText(html, true);
            helper.setSubject("KeyAssure Message");


            sender.send(message);
        }
        catch (Exception e){
            log.info("unable to send the mail please contact the admin");
        }

    }




    @Override
    public void sendMailWithAttachment(Long offerId) throws MessagingException, IOException, AppraisalException, JRException, JDOMException, GlobalException, TemplateException {
        MimeMessage message = sender.createMimeMessage();
       // SimpleMailMessage helper = new SimpleMailMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        List<EFileStatus> fileData = fileRepo.findByAppId(offerId);
        if( fileData.size()==0) {
            Response response = pdfGenerator.pdfTable(offerById.getId());

        }

        List<EFileStatus> fileDataAfterCreate = fileRepo.findByAppId(offerId);
        if(null != fileDataAfterCreate) {

            EUserRegistration userById = userRegistrationRepo.findUserById(offerById.getSellerUserId().getId());

            String email = userById.getEmail();
            helper.setFrom(fromMail);
            helper.setTo(email);
            helper.setSubject("PDF attachments of Factory key Assure");
            helper.setText("the purchased vehicle details");

            for(int i=0; i<fileDataAfterCreate.size();i++){

                String pdf = comUtl.checkDbVariable(fileDataAfterCreate.get(i).getFileName());

                //object from amazons3
                byte[] responseBytes = comUtl.fileDownloadfromBucket(pdfpath, pdf);

                ByteArrayResource attachment = new ByteArrayResource(responseBytes);
                helper.addAttachment(pdf, attachment);
            }
            sender.send(message);

        }
    }

    @Override
    public Response sendToDealer(DealerRegistration dealerReg) throws MessagingException, IOException, TemplateException {
        log.info("This method is used to send mail to dealer during dealer creation");
        Response response = new Response();
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message,true);

        List<ERoleMapping> factoryAdmin = roleMappingRepo.findByRole();

        ArrayList<String> emailList= new ArrayList<>();
        for (ERoleMapping email:factoryAdmin) {
            emailList.add(email.getUser().getEmail());
        }

        Template t = config.getTemplate("dealerCreationTemp.ftl");

        Map<String, Object> model1 = new HashMap<>();
        model1.put(AppraisalConstants.NAME,dealerReg.getFirstName()+" "+dealerReg.getLastName());
        model1.put(AppraisalConstants.USERNAME,dealerReg.getName());
        model1.put(AppraisalConstants.STREETADR,dealerReg.getStreetAddress());
        model1.put(AppraisalConstants.DLRSHPNAME,dealerReg.getDealershipNames());

        List<String> pdfList= new ArrayList<>();
        if(null!=dealerReg.getDealerCert()){
            pdfList.add(dealerReg.getDealerCert());
        }
        if(null!=dealerReg.getTaxCertificate()){
            pdfList.add(dealerReg.getTaxCertificate());
        }
        if(null!=dealerReg.getDealerLicense()){
            pdfList.add(dealerReg.getDealerLicense());
        }
        for (int i=0;i<pdfList.size();i++) {
            //object from amazons3
            byte[] responseBytes = comUtl.fileDownloadfromBucket(pdfpath, pdfList.get(i));
            ByteArrayResource attachment = new ByteArrayResource(responseBytes);
            helper.addAttachment(pdfList.get(i),attachment);
        }

        String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model1);


        helper.setFrom(fromMail);
        helper.setTo(emailList.toArray(new String[0]));
        helper.setText(html, true);
        helper.setSubject(AppraisalConstants.DEALERCREATON);
        sender.send(message);
        response.setCode((HttpStatus.OK.value()));
        response.setMessage("mail has been send successfully ");
        response.setStatus(Boolean.TRUE);
        log.info("mail send to : {}" , (emailList));
        return response;
    }


}

