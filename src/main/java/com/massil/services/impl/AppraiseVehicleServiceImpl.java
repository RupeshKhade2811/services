package com.massil.services.impl;
//Author:Rupesh khade,yogesh,kalyan,vijay



import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.Mapper.AppraisalConfigMapper;
import com.massil.config.AuditConfiguration;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.AppraisalVehicleERepo;
import com.massil.services.AppraiseVehicleService;
import com.massil.util.CompareUtils;
import com.massil.util.DealersUser;
import jakarta.persistence.EntityManager;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.io.FilenameUtils;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppraiseVehicleServiceImpl implements AppraiseVehicleService {

    Logger log = LoggerFactory.getLogger(AppraiseVehicleServiceImpl.class);

    @Value("${image_folder_path}")
    private String imageFolderPath;
    @Value("${video_folder_path}")
    private String videoFolderPath;

    @Value("${saved_pdf_Path}")
    private String pdfpath;

    @Value("${access_key}")
    private String accesskey;

    @Value(("${secret}"))
    private String secret;

    @Value(("${amazonS3_url}"))
    private String amazonS3Url;

    @Value("${profile_pic_path}")
    private String profilePicPath;
    @Autowired
    public MarketCheckApiServiceImpl marketCheckService;
    @Autowired
    @Lazy
    private FactoryPdfGeneratorImpl factoryPdfGenerator;
    @Autowired
    private PreStartMeasurementRepo prestartMeasurementRepo;

    @Autowired
    private TestDriveMeasurementsRepo testDriveMeasurementsRepo;

    @Autowired
    private CompareUtils utils;

    @Autowired
    private AppraiseVehicleRepo eAppraiseVehicleRepo;
    @Autowired
    private SignDetRepo signDetRepo;
    @Autowired
    private DealerRegistrationRepo dealerRepo;

    @Autowired
    private UserRegistrationRepo userRegistrationRepo;
    @Autowired
    ApprTestDrStsRepo eApprTestDrStsRepo;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;

    @Autowired
    private ConfigCodesRepo configurationCodesRepo;
    @Autowired
    private OffersMapper mapper;

    @Autowired
    private UserWishlistRepository wishRepo;

    @Autowired
    private AppraisalConfigMapper configMapper;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private ApprTestDrStsRepo testDrStsRepo;
   @Autowired
   private AuditConfiguration auditConfiguration;
   @Autowired
   private DealersUser dealersUser;
   @Autowired
   private TestDriveMeasurementsRepo testDrMeasRepo;

   @Autowired
   private PreStartMeasurementRepo preStRepo;
   @Autowired
   private ApplicationContext applicationContext;
   @Autowired
   private ApprTestDrStsRepo tdRepo;
   
    @Autowired
   private PaymentDetailsRepo paymentDetailsRepo;

   @Autowired
   private ResourceLoader resourceLoader;
   @Autowired
   private AppraisalVehicleERepo appraisalVehicleERepo;
    @Autowired
   private KeyassureRptLogRepo rptLogRepo;
   @Autowired
   private RoleMappingRepo roleMappingRepo;
   

    @Override
    public Response checkVinAvaliable(String vin, UUID userId) {
        Response response=new Response();

        ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);
        if(byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")){
            userId=byUserId.getDealerAdmin();
        }

        List<UUID> usersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        List< EAppraiseVehicle > vehicle= eAppraiseVehicleRepo.findByVinAndDlrId(vin, usersUnderDealer);
        if(null==vehicle||vehicle.size()==0){
            response.setStatus(false);
            response.setMessage("Vin number not found for Id "+userId);
        }
        else {
            response.setStatus(true);
            response.setMessage(" the vehicle has been previously appraised on "+vehicle.get(0).getCreatedOn() +"date. would you like to Edit this appraisal? ");
            response.setApprId(vehicle.get(0).getId());
        }
        return response;

    }


    private ApprCreaPage setColors(ApprCreaPage apprvehi, String extColor, String intColor){
        if(null!=extColor) {
            List< Long> extColor1 = configurationCodesRepo.getCodeForExtColor(extColor.toUpperCase());
            if(null!=extColor1) {
                apprvehi.setVehicleExtColor(extColor1.get(0));
            }
        }
        if(null!=intColor) {
            List<Long> intColor1 = configurationCodesRepo.getCodeForIntColor(intColor.toUpperCase());
            if(null!=intColor1) {
                apprvehi.setVehicleInterior(intColor1.get(0));
            }
        }

        return apprvehi;
    }
    private ApprCreaPage setMedia(List<String> picList,ApprCreaPage apprvehi) {
        if (null != picList && !picList.isEmpty()) {

            for (int j = 0; j < picList.size(); j++) {

                if (null != picList.get(j)) {

                    switch (j) {
                        case 0:
                            apprvehi.setVehiclePic1(getImagesFromMtkApi(picList.get(0)));
                            break;
                        case 1:
                            apprvehi.setVehiclePic2(getImagesFromMtkApi(picList.get(1)));
                            break;
                        case 2:
                            apprvehi.setVehiclePic3(getImagesFromMtkApi(picList.get(2)));
                            break;
                        case 3:
                            apprvehi.setVehiclePic4(getImagesFromMtkApi(picList.get(3)));
                            break;
                        case 4:
                            apprvehi.setVehiclePic5(getImagesFromMtkApi(picList.get(4)));
                            break;
                        case 5:
                            apprvehi.setVehiclePic6(getImagesFromMtkApi(picList.get(5)));
                            break;
                        case 6:
                            apprvehi.setVehiclePic7(getImagesFromMtkApi(picList.get(6)));
                            break;
                        case 7:
                            apprvehi.setVehiclePic8(getImagesFromMtkApi(picList.get(7)));
                            break;
                        case 8:
                            apprvehi.setVehiclePic9(getImagesFromMtkApi(picList.get(8)));
                            break;

                    }
                }

            }
        }
        return apprvehi;
    }
    private String getImagesFromMtkApi(String imageUrl) {
        Mono<String> mono;
        try {
            mono = downloadImageAsMultipartFile(imageUrl);
        } catch (AppraisalException e) {
            return "";
        }

        try {
            String result = mono.block();
            if (result != null) {
                return result;
            }
        } catch (HttpClientErrorException e) {
            return null;
        }

        return "";
    }
    public Mono<String> downloadImageAsMultipartFile(String imageUrl) throws AppraisalException {
        WebClient webClient = WebClient.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // Set the buffer size to 10 MB
                .build();

        return webClient.get()
                .uri(imageUrl)
                .accept(MediaType.IMAGE_JPEG, MediaType.IMAGE_PNG)
                .retrieve()
                .onStatus(HttpStatus.BAD_REQUEST::equals, response -> {
                    if (response.statusCode().is4xxClientError()) {
                        return Mono.just(new HttpClientErrorException(response.statusCode()));
                    }
                    return Mono.just(new AppraisalException(""));
                })
                .bodyToMono(byte[].class)
                .flatMap(imageData -> {
                    try {
                        if (imageData == null) {
                            return Mono.empty();
                        }

                        String filename = UUID.randomUUID().toString() + ".jpg";
                        Path filePath = Paths.get(imageFolderPath + filename);
                        Files.write(filePath, imageData);
                        return Mono.just(filename);
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                });
    }

    @Override
    public Response addAppraiseVehicle(ApprCreaPage apprCreaPage, UUID userId,String apprStatus) throws AppraisalException {

                EAppraiseVehicle eAppraiseVehicle = appraisalVehicleMapper.appCreaPageToEAppVehCond(apprCreaPage);
                 EUserRegistration user = userRegistrationRepo.findUserById(userId);

            if (null != user) {
                eAppraiseVehicle.setDealer(user.getDealer());
                eAppraiseVehicle.setUser(user);
                eAppraiseVehicle.setCreatedOn(new Date());
                eAppraiseVehicle.setValid(true);
                eAppraiseVehicle.setInvntrySts(apprStatus);
                EApprTestDrSts eApprTestDrSts = appraisalVehicleMapper.appCreaPageToEAppTestDrSts(apprCreaPage);


                EAprVehImg eAprVehImg = appraisalVehicleMapper.appCreaPageToEAprVehImg(apprCreaPage);


                auditConfiguration.setAuditorName(eAppraiseVehicle.getUser().getUserName());




                List<EConfigCodes> eCodesList = addApprConfigToEntity(apprCreaPage);
                if (null != eCodesList && !eCodesList.isEmpty()) {
                    Map<String, List<EConfigCodes>> apprConfigList = eCodesList.stream().collect(Collectors.groupingBy(EConfigCodes::getCodeType));
                    configMapper.updateAcCondn(eApprTestDrSts, apprConfigList.get(AppraisalConstants.AC_CONDITION));
                    configMapper.updateDrWarnLight(eApprTestDrSts, apprConfigList.get(AppraisalConstants.DASH_WARN_LIGHTS));
                    configMapper.updateInteriCondn(eApprTestDrSts, apprConfigList.get(AppraisalConstants.INTERIOR_CONDITION));
                    configMapper.updateOilCondn(eApprTestDrSts, apprConfigList.get(AppraisalConstants.OIL_CONDITION));
                    configMapper.updateStereoSts(eApprTestDrSts, apprConfigList.get(AppraisalConstants.STEREO_STATUS));
                    configMapper.updateBrakingSys(eApprTestDrSts, apprConfigList.get(AppraisalConstants.BRAKE_SYSTEM_FEEL));
                    configMapper.updateEngPerformance(eApprTestDrSts, apprConfigList.get(AppraisalConstants.ENGINE_PERFORMANCE));
                    configMapper.updateTransmnSys(eApprTestDrSts, apprConfigList.get(AppraisalConstants.TRANSMISSION_STATUS));
                    configMapper.updateSteeringFeel(eApprTestDrSts, apprConfigList.get(AppraisalConstants.STEERING_FEEL_STATUS));
                    configMapper.updateBookAndKeys(eApprTestDrSts, apprConfigList.get(AppraisalConstants.BOOKS_AND_KEYS));
                    configMapper.updateRearWindowDamage(eApprTestDrSts, apprConfigList.get(AppraisalConstants.REAR_WINDOW_DAMAGE));
                    configMapper.updateTireCondition(eApprTestDrSts, apprConfigList.get(AppraisalConstants.TIRE_CONDITION));
                    updateTestDrvStatus(apprConfigList, eApprTestDrSts);

                }

                ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);
                if(byUserId.getRole().getRoleGroup().equalsIgnoreCase("P")){
                    PaymentDetails user1 = paymentDetailsRepo.getUser(userId);
                    if (null != user1 && user1.getConsumeQuota() < user1.getTotalQuota()) {
                        log.info("new record inserted");
                        user1.setConsumeQuota(user1.getConsumeQuota() + 1);
                        paymentDetailsRepo.save(user1);
                        if (null!=apprCreaPage.getPreStartMeas()) {
                            List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                            OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(apprCreaPage.getPreStartMeas());
                            ps.add(preStart);
                            preStart.setApprRef(eAppraiseVehicle);
                            eAppraiseVehicle.setPreStart(ps);
                        }
                        if (null!=apprCreaPage.getTestDrive()) {
                            List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(apprCreaPage.getTestDrive());
                            for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                                testDrMeas.setApprRef(eAppraiseVehicle);
                            }
                            eAppraiseVehicle.setTestDriveMeas(testDriveList);
                        }
                    }
                }else{
                    if (null!=apprCreaPage.getPreStartMeas()) {
                        List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                        OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(apprCreaPage.getPreStartMeas());
                        ps.add(preStart);
                        preStart.setApprRef(eAppraiseVehicle);
                        eAppraiseVehicle.setPreStart(ps);
                    }
                    if (null!=apprCreaPage.getTestDrive()) {
                        List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(apprCreaPage.getTestDrive());
                        for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                            testDrMeas.setApprRef(eAppraiseVehicle);
                        }
                        eAppraiseVehicle.setTestDriveMeas(testDriveList);
                    }
                }



                EUserRegistration userById = userRegistrationRepo.findUserById(apprCreaPage.getDealershipUserNames());

                eAprVehImg.setTdStatus(eApprTestDrSts);
                if(null != eApprTestDrSts) {
                    eApprTestDrSts.setAprVehImg(eAprVehImg);
                    eApprTestDrSts.setAppraisalRef(eAppraiseVehicle);
                }
                eAppraiseVehicle.setDlrsUserNames(userById);
                eAppraiseVehicle.setTdStatus(eApprTestDrSts);
                EAppraiseVehicle save = eAppraiseVehicleRepo.save(eAppraiseVehicle);

                if(byUserId.getRole().getRoleGroup().equalsIgnoreCase("P")){
                    KeyassureReportLog kReportLog = new KeyassureReportLog();
                    kReportLog.setAppraisalRef(save);
                    kReportLog.setUser(user);
                    rptLogRepo.save(kReportLog);
                }


            } else throw new AppraisalException("User not found with id: "+userId);

            Response response= new Response();
        response.setMessage("saved successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(true);
        return response;

        }




    @Override
    public CardsPage findAllAppraisalCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {
        Page<EAppraiseVehicle> pageResult=null;
        CardsPage cardsPage=null;
        CardsPage pageInfo = new CardsPage();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.CREATEDON).descending());


        ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);
        if(byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")){
            userId=byUserId.getDealerAdmin();
        }
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
         pageResult = eAppraiseVehicleRepo.appraisalCards(allUsersUnderDealer, true,pageable);

        if(null!= pageResult && pageResult.getTotalElements()!=0) {
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                List<EAppraiseVehicle> apv = pageResult.toList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(apv);
                pageInfo.setCards(appraiseVehicleDtos);
        }
        else if(null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()){

            pageInfo.setTotalRecords(cardsPage.getTotalRecords());
            pageInfo.setTotalPages((long) cardsPage.getTotalPages());
            List<EAppraiseVehicle> apv = cardsPage.getAppraiseVehicleList();
            List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(apv);
            pageInfo.setCards(appraiseVehicleDtos);

        }
        else throw new AppraisalException("AppraisalCards not available");
        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setMessage("Cards showing successfully");
        pageInfo.setStatus(true);
        return pageInfo;

    }


    @Override
    public ApprCreaPage showInEditPage(Long appraisalId) throws AppraisalException, IOException, JRException, JDOMException {

        keyassureFileCheck(appraisalId);
        ApprCreaPage apprCreaPage = new ApprCreaPage();

        ApprCreaPage apprCrePg = new ApprCreaPage();



            EAppraiseVehicle eAppraiseVehicles = eAppraiseVehicleRepo.getAppraisalById(appraisalId);


            if (null != eAppraiseVehicles) {
                List<TestDriveMes> testDriveMes = appraisalVehicleMapper.lTestDriveMeaTolApprCreaPage(eAppraiseVehicles.getTestDriveMeas());

                EApprTestDrSts apprTstDriSts = eAppraiseVehicles.getTdStatus();


                PrestartMeasurement prestartMeasurement = appraisalVehicleMapper.ePrestartToPrestart(preStRepo.getPreStartMeasByApprRef(appraisalId));

                apprCrePg.setPreStartMeas(prestartMeasurement);
                apprCreaPage = appraisalVehicleMapper.showApprcreaPage(eAppraiseVehicles, apprCrePg);


                List<EConfigCodes> apprConfigList=null;

                apprConfigList = showInEditSetAcCond(apprTstDriSts.getApprVehAcCondn(), apprConfigList);
                apprConfigList = showInEditSetSteroSts(apprTstDriSts.getApprVehStereoSts(), apprConfigList);
                apprConfigList = showInEditSetInteriCond(apprTstDriSts.getApprVehInteriCondn(), apprConfigList);
                apprConfigList = showInEditSetOilCond(apprTstDriSts.getApprVehOilCondn(),apprConfigList);
                apprConfigList = showInEditSetWarnLightsts(apprTstDriSts.getVehDrWarnLightSts(),apprConfigList);
                apprConfigList = showInEditSetBrakingSts(apprTstDriSts.getApprBrakingSysSts(), apprConfigList);
                apprConfigList = showInEditSetEnginePerfor(apprTstDriSts.getApprEnginePer(),apprConfigList);
                apprConfigList = showInEditSetTransmiSts(apprTstDriSts.getApprTransmissionSts(),apprConfigList);
                apprConfigList = showInEditSetSteeringFeel(apprTstDriSts.getSteeringFeel(),apprConfigList);
                apprConfigList = showInEditSetBookAndKeys(apprTstDriSts.getBookAndKeys(),apprConfigList);
                apprConfigList = showInEditTireCondition(apprTstDriSts.getApprVehTireCondn(), apprConfigList);
                apprConfigList=showInEditSetRearWindowDamage(apprTstDriSts.getRearWindow(),apprConfigList);

                apprCrePg.setVehicleInterior(selctOneShowInUi(apprTstDriSts.getIntrColor()));
                apprCrePg.setVehicleExtColor(selctOneShowInUi(apprTstDriSts.getExtrColor()));

                if (null!=eAppraiseVehicles.getDlrsUserNames()) {
                    apprCrePg.setDealershipUserNames(eAppraiseVehicles.getDlrsUserNames().getId());
                }

                if(null!=apprConfigList && !apprConfigList.isEmpty() && apprConfigList.size()>0){
                    List<ConfigDropDown> configCodesList=appraisalVehicleMapper.lEConfigCodeToConfigCode(apprConfigList);

                    Map<String, List<Long>> configMap = configCodesList.stream()
                            .collect(Collectors.groupingBy(ConfigDropDown::getCodeType,Collectors.mapping(ConfigDropDown::getId, Collectors.toList())));

                    apprCreaPage.setInteriorCondn(configMap.get(AppraisalConstants.INTERIOR_CONDITION));
                    apprCreaPage.setOilCondition(configMap.get(AppraisalConstants.OIL_CONDITION));
                    apprCreaPage.setDashWarningLights(configMap.get(AppraisalConstants.DASH_WARN_LIGHTS));
                    apprCreaPage.setAcCondition(configMap.get(AppraisalConstants.AC_CONDITION));
                    apprCreaPage.setStereoSts(configMap.get(AppraisalConstants.STEREO_STATUS));
                    apprCreaPage.setSteeringFeelSts(configMap.get(AppraisalConstants.STEERING_FEEL_STATUS));
                    apprCreaPage.setBooksAndKeys(configMap.get(AppraisalConstants.BOOKS_AND_KEYS));
                    apprCrePg.setBrakingSysSts(configMap.get(AppraisalConstants.BRAKE_SYSTEM_FEEL));
                    apprCrePg.setEnginePerfor(configMap.get(AppraisalConstants.ENGINE_PERFORMANCE));
                    apprCrePg.setTransmiSts(configMap.get(AppraisalConstants.TRANSMISSION_STATUS));
                    apprCrePg.setRearWindowDamage(configMap.get(AppraisalConstants.REAR_WINDOW_DAMAGE));
                    apprCrePg.setTireCondition(configMap.get(AppraisalConstants.TIRE_CONDITION));
                    apprCrePg.setDoorLocks(selctOneShowInUi(apprTstDriSts.getDoorLock()));
                    apprCrePg.setLeftfrWinSts(selctOneShowInUi(apprTstDriSts.getFLWinStatus()));
                    apprCrePg.setFrRightWinSts(selctOneShowInUi(apprTstDriSts.getFRWinStatus()));
                    apprCrePg.setRearLeftWinSts(selctOneShowInUi(apprTstDriSts.getRLWinStatus()));
                    apprCrePg.setRearRightWinSts(selctOneShowInUi(apprTstDriSts.getRRWinStatus()));
                    apprCrePg.setVehicleInterior(selctOneShowInUi(apprTstDriSts.getIntrColor()));
                    apprCrePg.setVehicleExtColor(selctOneShowInUi(apprTstDriSts.getExtrColor()));
                    apprCrePg.setRoofType(selctOneShowInUi(apprTstDriSts.getRoofTypes()));
                    apprCrePg.setFrWindshieldDmg(selctOneShowInUi(apprTstDriSts.getWindShieldDmg()));
                    apprCrePg.setTitleSts(selctOneShowInUi(apprTstDriSts.getTitleSt()));
                    if (null!=eAppraiseVehicles.getDlrsUserNames()) {
                        apprCrePg.setDealershipUserNames(eAppraiseVehicles.getDlrsUserNames().getId());
                    }
                }
                apprCrePg.setTestDrive(testDriveMes);

            } else throw new AppraisalException("Invalid Appraisal Id");

        apprCreaPage.setCode(HttpStatus.OK.value());
        apprCreaPage.setMessage("Successfully getting cards in edit page");
        apprCreaPage.setStatus(true);
        return apprCreaPage;

    }

    @Transactional
    private void keyassureFileCheck(Long appraisalId) throws JRException, IOException, JDOMException {
        EAppraiseVehicle appraisalById = eAppraiseVehicleRepo.getAppraisalById(appraisalId);
        if (null!=appraisalById){
            if (null!=testDrMeasRepo.getTestDrMeasByApprRef(appraisalId)&&null!=preStRepo.getPreStartMeasByApprRef(appraisalId)){
                appraisalById.getTdStatus().setKeyAssureYes("YES");
            }else{
                appraisalById.getTdStatus().setKeyAssureYes("NO");
            }
            String keyAssureYes=appraisalById.getTdStatus().getKeyAssureYes();
            String keyAssureFiles=appraisalById.getTdStatus().getKeyAssureFiles();

            if ((keyAssureYes.equalsIgnoreCase("yes")&&keyAssureFiles==null)||(keyAssureYes.equalsIgnoreCase("yes")&& keyAssureFiles.equals(""))) {
                if(Boolean.FALSE.equals(utils.isDocPresent(keyAssureFiles))) {
                    appraisalById.getTdStatus().setKeyAssureFiles(vehReportPdf(setDataToPdf(appraisalId)));
                }
            }
            auditConfiguration.setAuditorName(appraisalById.getUser().getUserName());
            eAppraiseVehicleRepo.save(appraisalById);
        }
    }


    @Override
    @Transactional
    public Response updateAppraisal(ApprCreaPage page, Long apprId) throws AppraisalException, IOException, JRException, JDOMException {

        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprId);


            if (null!=vehicle) {
                auditConfiguration.setAuditorName(vehicle.getUser().getUserName());

                EApprTestDrSts testDriveStatus = vehicle.getTdStatus();

                vehicle = appraisalVehicleMapper.updateEAppraiseVehicle(page, vehicle);

                 //update esign
                //update EAppraisalTestDriveStatus

                EApprTestDrSts eApprTestDrSts = updateTestDrSts(testDriveStatus, page);
                if(vehicle.getInvntrySts().equalsIgnoreCase(AppraisalConstants.DRAFT)){
                    vehicle.setInvntrySts(AppraisalConstants.CREATED);
                }



                List<EConfigCodes> eCodesList = addApprConfigToEntity(page);
                if(null!=eCodesList && !eCodesList.isEmpty()){
                    Map<String, List<EConfigCodes>> apprConfigList = eCodesList.stream().collect(Collectors.groupingBy(EConfigCodes::getCodeType));
                    configMapper.updateAcCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.AC_CONDITION));
                    configMapper.updateDrWarnLight(eApprTestDrSts,apprConfigList.get(AppraisalConstants.DASH_WARN_LIGHTS));
                    configMapper.updateInteriCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.INTERIOR_CONDITION));
                    configMapper.updateOilCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.OIL_CONDITION));
                    configMapper.updateStereoSts(eApprTestDrSts,apprConfigList.get(AppraisalConstants.STEREO_STATUS));
                    configMapper.updateBrakingSys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.BRAKE_SYSTEM_FEEL));
                    configMapper.updateEngPerformance(eApprTestDrSts,apprConfigList.get(AppraisalConstants.ENGINE_PERFORMANCE));
                    configMapper.updateTransmnSys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.TRANSMISSION_STATUS));
                    configMapper.updateSteeringFeel(eApprTestDrSts,apprConfigList.get(AppraisalConstants.STEERING_FEEL_STATUS));
                    configMapper.updateBookAndKeys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.BOOKS_AND_KEYS));
                    configMapper.updateTireCondition(eApprTestDrSts,apprConfigList.get(AppraisalConstants.TIRE_CONDITION));
                    configMapper.updateRearWindowDamage(eApprTestDrSts,apprConfigList.get(AppraisalConstants.REAR_WINDOW_DAMAGE));
                    updateTestDrvStatus( apprConfigList, eApprTestDrSts);
                }
                if (((null!=vehicle.getDlrsUserNames()&&null!=page.getDealershipUserNames())&&(vehicle.getDlrsUserNames().getId()!=page.getDealershipUserNames()))||(null==vehicle.getDlrsUserNames()&&null!=page.getDealershipUserNames())){
                    vehicle.setDlrsUserNames(userRegistrationRepo.findUserById(page.getDealershipUserNames()));
                }else {
                    vehicle.setDlrsUserNames(vehicle.getDlrsUserNames());
                }

                ERoleMapping byUserId = roleMappingRepo.findByUserId(vehicle.getUser().getId());
                if(byUserId.getRole().getRole().equalsIgnoreCase("P")){
                    KeyassureReportLog record = rptLogRepo.getRecord(vehicle.getUser().getId(), apprId);
                    if(null!=record){
                        if (null!=page.getPreStartMeas()) {
                            List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                            OBD2_PreStartMeasurement byApprId = preStRepo.getPreStartMeasByApprRef(apprId);
                            if (null!=byApprId) {
                                byApprId.setValid(Boolean.FALSE);
                                preStRepo.save(byApprId);
                                OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                                ps.add(preStart);
                                preStart.setApprRef(vehicle);
                                vehicle.setPreStart(ps);
                                vehicle.getTdStatus().setKeyAssureFiles(null);
                            }else{
                                OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                                ps.add(preStart);
                                preStart.setApprRef(vehicle);
                                vehicle.setPreStart(ps);
                                vehicle.getTdStatus().setKeyAssureFiles(null);
                            }
                        }
                    }else{
                        KeyassureReportLog  reportLog= new KeyassureReportLog();
                        reportLog.setAppraisalRef(vehicle);
                        reportLog.setUser(vehicle.getUser());
                        rptLogRepo.save(reportLog);
                        if (null!=page.getPreStartMeas()) {
                            List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                            OBD2_PreStartMeasurement byApprId = preStRepo.getPreStartMeasByApprRef(apprId);
                            if (null!=byApprId) {
                                byApprId.setValid(Boolean.FALSE);
                                preStRepo.save(byApprId);
                                OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                                ps.add(preStart);
                                preStart.setApprRef(vehicle);
                                vehicle.setPreStart(ps);
                                vehicle.getTdStatus().setKeyAssureFiles(null);
                            }else{
                                OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                                ps.add(preStart);
                                preStart.setApprRef(vehicle);
                                vehicle.setPreStart(ps);
                                vehicle.getTdStatus().setKeyAssureFiles(null);
                            }
                        }
                    }
                }else{
                    if (null!=page.getPreStartMeas()) {
                        List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                        OBD2_PreStartMeasurement byApprId = preStRepo.getPreStartMeasByApprRef(apprId);
                        if (null!=byApprId) {
                            byApprId.setValid(Boolean.FALSE);
                            preStRepo.save(byApprId);
                            OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                            ps.add(preStart);
                            preStart.setApprRef(vehicle);
                            vehicle.setPreStart(ps);
                            vehicle.getTdStatus().setKeyAssureFiles(null);
                        }else{
                            OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                            ps.add(preStart);
                            preStart.setApprRef(vehicle);
                            vehicle.setPreStart(ps);
                            vehicle.getTdStatus().setKeyAssureFiles(null);
                        }
                    }
                }



                if (null!=page.getTestDrive()) {
                    List<OBD2_TestDriveMeasurements> byApprId1=new ArrayList<>();
                    List<OBD2_TestDriveMeasurements> byApprId = testDrMeasRepo.getTestDrMeasByApprRef(apprId);
                    if (null!=byApprId) {
                        for (OBD2_TestDriveMeasurements test : byApprId) {
                            OBD2_TestDriveMeasurements byTestDrId = testDrMeasRepo.findByTestDrId(test.getId());
                            byTestDrId.setValid(Boolean.FALSE);
                            byApprId1.add(byTestDrId);
                        }
                        testDrMeasRepo.saveAll(byApprId1);
                        List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(page.getTestDrive());
                        for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                            testDrMeas.setApprRef(vehicle);
                        }
                        vehicle.setTestDriveMeas(testDriveList);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }else{
                        List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(page.getTestDrive());
                        for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                            testDrMeas.setApprRef(vehicle);
                        }
                        vehicle.setTestDriveMeas(testDriveList);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }

                }
                auditConfiguration.setAuditorName(vehicle.getUser().getFirstName());
                eAppraiseVehicleRepo.save(vehicle);
                log.info("appraisal dtls saved ");

            }


            else throw new AppraisalException("Invalid Appraisal id Send..");

    Response response =new Response();
            response.setMessage("Updated Successfully");
            response.setCode(HttpStatus.OK.value());
            response.setStatus(true);
            return response;
    }

@Override
@Transactional
    public Response updateDraftAppraisal(ApprCreaPage page, Long apprId) throws AppraisalException, IOException, JRException, JDOMException {

        EAppraiseVehicle oldCopy = eAppraiseVehicleRepo.getAppraisalById(apprId);

        EAppraiseVehicle vehicle = oldCopy;

            if (null!=vehicle) {
                auditConfiguration.setAuditorName(vehicle.getUser().getUserName());

                EApprTestDrSts testDriveStatus = vehicle.getTdStatus();

                vehicle = appraisalVehicleMapper.updateEAppraiseVehicle(page, vehicle);

                EApprTestDrSts eApprTestDrSts = updateTestDrSts(testDriveStatus, page);

                if (null!=testDrMeasRepo.getTestDrMeasByApprRef(apprId)&&null!=preStRepo.getPreStartMeasByApprRef(apprId)){
                    eApprTestDrSts.setKeyAssureYes("YES");
                }else {
                    eApprTestDrSts.setKeyAssureYes("NO");
                }
                if (eApprTestDrSts.getKeyAssureYes().equalsIgnoreCase("yes")&&eApprTestDrSts.getKeyAssureFiles()==null) {
                    eApprTestDrSts.setKeyAssureFiles(vehReportPdf(setDataToPdf(apprId)));
                }
                tdRepo.save(eApprTestDrSts);

                List<EConfigCodes> eCodesList = addApprConfigToEntity(page);
                if(null!=eCodesList && !eCodesList.isEmpty()){
                    Map<String, List<EConfigCodes>> apprConfigList = eCodesList.stream().collect(Collectors.groupingBy(EConfigCodes::getCodeType));
                    configMapper.updateAcCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.AC_CONDITION));
                    configMapper.updateDrWarnLight(eApprTestDrSts,apprConfigList.get(AppraisalConstants.DASH_WARN_LIGHTS));
                    configMapper.updateInteriCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.INTERIOR_CONDITION));
                    configMapper.updateOilCondn(eApprTestDrSts,apprConfigList.get(AppraisalConstants.OIL_CONDITION));
                    configMapper.updateStereoSts(eApprTestDrSts,apprConfigList.get(AppraisalConstants.STEREO_STATUS));
                    configMapper.updateBrakingSys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.BRAKE_SYSTEM_FEEL));
                    configMapper.updateEngPerformance(eApprTestDrSts,apprConfigList.get(AppraisalConstants.ENGINE_PERFORMANCE));
                    configMapper.updateTransmnSys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.TRANSMISSION_STATUS));
                    configMapper.updateSteeringFeel(eApprTestDrSts,apprConfigList.get(AppraisalConstants.STEERING_FEEL_STATUS));
                    configMapper.updateBookAndKeys(eApprTestDrSts,apprConfigList.get(AppraisalConstants.BOOKS_AND_KEYS));
                    configMapper.updateTireCondition(eApprTestDrSts,apprConfigList.get(AppraisalConstants.TIRE_CONDITION));
                    configMapper.updateRearWindowDamage(eApprTestDrSts,apprConfigList.get(AppraisalConstants.REAR_WINDOW_DAMAGE));
                    updateTestDrvStatus( apprConfigList, eApprTestDrSts);

                }
                if ((null!=vehicle.getDlrsUserNames()&&null!=page.getDealershipUserNames()&&(vehicle.getDlrsUserNames().getId()!=page.getDealershipUserNames()))||(null==vehicle.getDlrsUserNames()&&null!=page.getDealershipUserNames())){
                    vehicle.setDlrsUserNames(userRegistrationRepo.findUserById(page.getDealershipUserNames()));
                }else {
                    vehicle.setDlrsUserNames(vehicle.getDlrsUserNames());
                }
                if (null!=page.getPreStartMeas()) {
                    List<OBD2_PreStartMeasurement> ps=new ArrayList<>();
                    OBD2_PreStartMeasurement byApprId = preStRepo.getPreStartMeasByApprRef(apprId);
                    if (null!=byApprId) {
                        byApprId.setValid(Boolean.FALSE);
                        preStRepo.save(byApprId);
                        OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                        ps.add(preStart);
                        preStart.setApprRef(vehicle);
                        vehicle.setPreStart(ps);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }else{
                        OBD2_PreStartMeasurement preStart = appraisalVehicleMapper.appCreaPageToPreStrt(page.getPreStartMeas());
                        ps.add(preStart);
                        preStart.setApprRef(vehicle);
                        vehicle.setPreStart(ps);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }
                }

                if (null!=page.getTestDrive()) {
                    List<OBD2_TestDriveMeasurements> byApprId1=new ArrayList<>();
                    List<OBD2_TestDriveMeasurements> byApprId = testDrMeasRepo.getTestDrMeasByApprRef(apprId);
                    if (null!=byApprId) {
                        for (OBD2_TestDriveMeasurements test : byApprId) {
                            OBD2_TestDriveMeasurements byTestDrId = testDrMeasRepo.findByTestDrId(test.getId());
                            byTestDrId.setValid(Boolean.FALSE);
                            byApprId1.add(byTestDrId);
                        }
                        testDrMeasRepo.saveAll(byApprId1);
                        List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(page.getTestDrive());
                        for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                            testDrMeas.setApprRef(vehicle);
                        }
                        vehicle.setTestDriveMeas(testDriveList);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }else{
                        List<OBD2_TestDriveMeasurements> testDriveList = appraisalVehicleMapper.apprCreaPageToLTestDriveMeas(page.getTestDrive());
                        for (OBD2_TestDriveMeasurements testDrMeas : testDriveList) {
                            testDrMeas.setApprRef(vehicle);
                        }
                        vehicle.setTestDriveMeas(testDriveList);
                        vehicle.getTdStatus().setKeyAssureFiles(null);
                    }

                }

                

                eAppraiseVehicleRepo.save(vehicle);
                log.info("draft appraisal dtls saved ");

            }

            else throw new AppraisalException("Invalid Appraisal id Send..");

        Response response =new Response();
        response.setMessage("Updated Successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(true);
        return response;
    }



    @Override
    public String videoUpload(MultipartFile file) throws AppraisalException, IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (null!=extension && (extension.equalsIgnoreCase("mp4")||extension.equalsIgnoreCase("webm")||extension.equalsIgnoreCase("mov"))) {
            String filename = UUID.randomUUID().toString() + "." + extension;

            //object to AmazonS3
            File file1 = File.createTempFile(file.getOriginalFilename(), ".temp");// Create a new temporary file
            file.transferTo(file1);// Transfer the content from the multipart file to the new file

            return utils.uploadFileInBucket(file1,videoFolderPath,filename);
        }else throw new AppraisalException("Wrong file type, use only extension with mp4,webm and mov");
    }

    @Override
    public VideoAndImageResponse videoDownload(String videoName) throws IOException {
        
        VideoAndImageResponse responseDTO = new VideoAndImageResponse();
        //object from amazons3
        byte[] responseBytes = utils.fileDownloadfromBucket(videoFolderPath, videoName);
        responseDTO.setVideoBytes(responseBytes);
        responseDTO.setCode(HttpStatus.OK.value());
        responseDTO.setStatus(true);
        responseDTO.setMessage("Video send successfully");
        return responseDTO;
    }


    @Override
    public VideoAndImageResponse downloadImageFromFileSystem(String imageName) throws IOException{
        VideoAndImageResponse responseDTO = new VideoAndImageResponse();
        byte[] responseBytes = utils.fileDownloadfromBucket(imageFolderPath,imageName );
        responseDTO.setImageBytes(responseBytes);
        responseDTO.setCode(HttpStatus.OK.value());
        responseDTO.setStatus(true);
        responseDTO.setMessage("Image send successfully");
        return responseDTO;

    }



    @Override
    public String imageUpload(MultipartFile file) throws AppraisalException, IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(null!=extension && (extension.equalsIgnoreCase("jpeg")|| extension.equalsIgnoreCase("png")|| extension.equalsIgnoreCase("jpg")||extension.equalsIgnoreCase("pdf"))) {
            String filename = UUID.randomUUID().toString() + "." + extension;

            //object to AmazonS3
            // Create a new temporary file
            File file1 = File.createTempFile(file.getOriginalFilename(), ".temp");
            // Transfer the content from the multipart file to the new file
            file.transferTo(file1);
            return utils.uploadFileInBucket(file1, imageFolderPath, filename);
        }throw new AppraisalException("only jpeg, png, pdf and jpg extensions are allowed");

    }

    @Override
    @Transactional
    public Response deleteAppraisalVehicle(Long apprRef) throws AppraisalException {
        
        EAppraiseVehicle appraisal = eAppraiseVehicleRepo.getAppraisalById(apprRef);
         Response response=new Response();

             if (null!= appraisal && appraisal.getValid()) {
                 appraisal.setValid(false);
                 auditConfiguration.setAuditorName(appraisal.getUser().getUserName());
                 eAppraiseVehicleRepo.save(appraisal);
                 response.setMessage("Deleted Successfully");
                 response.setCode(HttpStatus.OK.value());
                 response.setStatus(true);
             } else
                 throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
        return response;
    }



    private Long selctOneShowInUi(EConfigCodes config) {
        if(null!=config){
            return config.getId();
        }
        return null;
    }

    /**
     * This method sets the EApprVehAcCondn ConfigCodes in the List of EConfigurationCodes object
     * @param acCondn This is the Object of EApprVehAcCondn
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetAcCond(EApprVehAcCondn acCondn, List<EConfigCodes> apprConfigList){

        if(null!=acCondn) {
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getColdAir()); //it is using null argument list object for every call
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getBadDisplay());
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getFadedDisOrBtn());
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getFanSpeedMalfun());
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getClimateCtrlMalfun());
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getHotOrWarmAir());
            apprConfigList = addApprConfigToList(apprConfigList, acCondn.getNotOperational());
        }
        log.debug("SHOWING AC CONDITION IN EDIT PAGE :",apprConfigList);
        return apprConfigList;
    }




    /**
     *This method sets the EApprVehOilCondn ConfigCodes in the List of EConfigurationCodes object
     * @param oilCondn This is the Object of EApprVehOilCondn
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetOilCond(EApprVehOilCondn oilCondn, List<EConfigCodes> apprConfigList) {
        if(null!=oilCondn) {
            addApprConfigToList(apprConfigList, oilCondn.getCleanOil());
            addApprConfigToList(apprConfigList, oilCondn.getDirtyOil());
            addApprConfigToList(apprConfigList, oilCondn.getWaterInOil());
            addApprConfigToList(apprConfigList, oilCondn.getCorrectLevel());
            addApprConfigToList(apprConfigList, oilCondn.getOneQuartLow());
            addApprConfigToList(apprConfigList, oilCondn.getGreaterThanAQuartLow());
            addApprConfigToList(apprConfigList, oilCondn.getElectronicGauge());
            addApprConfigToList(apprConfigList, oilCondn.getElectricVeh());

        }
        return apprConfigList;

    }

    /**
     *This method sets the EApprVehStereoSts ConfigCodes in the List of EConfigurationCodes object
     * @param stereoStatus This is the Object of EApprVehStereoSts
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */

    public List<EConfigCodes>  showInEditSetSteroSts(EApprVehStereoSts stereoStatus, List<EConfigCodes> apprConfigList) {

        if(null!=stereoStatus) {
            addApprConfigToList(apprConfigList, stereoStatus.getFactoryEquptOperat());
            addApprConfigToList(apprConfigList, stereoStatus.getFactoryEquptNotOperat());
            addApprConfigToList(apprConfigList, stereoStatus.getMissingButtons());
            addApprConfigToList(apprConfigList, stereoStatus.getAftMktSoundSys());
            addApprConfigToList(apprConfigList, stereoStatus.getAftMktNavigaSys());
            addApprConfigToList(apprConfigList, stereoStatus.getAftMktRearEntertainSys());
            addApprConfigToList(apprConfigList, stereoStatus.getFactoryRearEntertainSys());
            addApprConfigToList(apprConfigList, stereoStatus.getCd());
            addApprConfigToList(apprConfigList, stereoStatus.getCrckdBrknScreen());
            addApprConfigToList(apprConfigList, stereoStatus.getFadedDisBtn());
            addApprConfigToList(apprConfigList, stereoStatus.getPrmSoundSys());
            addApprConfigToList(apprConfigList, stereoStatus.getNvgSys());

        }
        return apprConfigList;
    }

    /**
     *This method sets the EApprVehInteriCondn ConfigCodes in the List of EConfigurationCodes object
     * @param intrCondn This is the Object of EApprVehInteriCondn
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes>  showInEditSetInteriCond(EApprVehInteriCondn intrCondn, List<EConfigCodes> apprConfigList) {
       if(null!=intrCondn) {
           addApprConfigToList(apprConfigList, intrCondn.getCleanFL());
           addApprConfigToList(apprConfigList, intrCondn.getGoodMnrRepaisNeed());
           addApprConfigToList(apprConfigList, intrCondn.getSmokersCar());
           addApprConfigToList(apprConfigList, intrCondn.getOddSmelling());
           addApprConfigToList(apprConfigList, intrCondn.getVeryDirty());
           addApprConfigToList(apprConfigList, intrCondn.getStrongPetSmell());
           addApprConfigToList(apprConfigList, intrCondn.getDriversSeatWear());
           addApprConfigToList(apprConfigList, intrCondn.getHeadlineNeedRplc());
           addApprConfigToList(apprConfigList, intrCondn.getDriverSeatRipped());
           addApprConfigToList(apprConfigList, intrCondn.getDashCrackedMinor());
           addApprConfigToList(apprConfigList, intrCondn.getDashCrackBrknMaj());
           addApprConfigToList(apprConfigList, intrCondn.getPassenSeatRipped());
           addApprConfigToList(apprConfigList, intrCondn.getCarpetBadlyWorn());
           addApprConfigToList(apprConfigList, intrCondn.getInterTrimBrknnMiss());
           addApprConfigToList(apprConfigList, intrCondn.getPoorNeedsRepair());
       }

        return apprConfigList;
    }

    /**
     *This method sets the EVehDWarnLightStatus ConfigCodes in the List of EConfigurationCodes object
     * @param warnLightSts This is the Object of EVehDWarnLightStatus
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetWarnLightsts(EVehDWarnLightStatus warnLightSts, List<EConfigCodes> apprConfigList) {

        if(null!=warnLightSts) {
            addApprConfigToList(apprConfigList, warnLightSts.getNoFaults());
            addApprConfigToList(apprConfigList, warnLightSts.getAbsFault());
            addApprConfigToList(apprConfigList, warnLightSts.getAirBagFault());
            addApprConfigToList(apprConfigList, warnLightSts.getBatteryLight());
            addApprConfigToList(apprConfigList, warnLightSts.getBrakeSystemFault());
            addApprConfigToList(apprConfigList, warnLightSts.getBrakePadWearLight());
            addApprConfigToList(apprConfigList, warnLightSts.getChargingSystemFault());
            addApprConfigToList(apprConfigList, warnLightSts.getClngSysFault());
            addApprConfigToList(apprConfigList, warnLightSts.getCoolantLow());
            addApprConfigToList(apprConfigList, warnLightSts.getCheckEngineLight());
            addApprConfigToList(apprConfigList, warnLightSts.getOilPressureLow());
            addApprConfigToList(apprConfigList, warnLightSts.getServiceSoon());
            addApprConfigToList(apprConfigList, warnLightSts.getChngOilIndctr());
            addApprConfigToList(apprConfigList, warnLightSts.getLowOilIndctr());
            addApprConfigToList(apprConfigList, warnLightSts.getTractionControl());
            addApprConfigToList(apprConfigList, warnLightSts.getTransmiFault());
            addApprConfigToList(apprConfigList, warnLightSts.getTpms());
            addApprConfigToList(apprConfigList, warnLightSts.getDislParticulateFilt());
            addApprConfigToList(apprConfigList, warnLightSts.getMainBtryFault());
        }


        return apprConfigList;
    }

    /**
     *This method sets the ESteeringFeelStatus ConfigCodes in the List of EConfigurationCodes object
     * @param steeringFeel This is the Object of ESteeringFeelStatus
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetSteeringFeel(ESteeringFeelStatus steeringFeel, List<EConfigCodes> apprConfigList) {

        if(null!=steeringFeel) {

            addApprConfigToList(apprConfigList, steeringFeel.getNormalOperation());
            addApprConfigToList(apprConfigList, steeringFeel.getPowerSearingPumpWhine());
            addApprConfigToList(apprConfigList, steeringFeel.getSloppySteeringWheel());
            addApprConfigToList(apprConfigList, steeringFeel.getNoPowerAssist());
            addApprConfigToList(apprConfigList, steeringFeel.getPullsToLeft());
            addApprConfigToList(apprConfigList, steeringFeel.getPullsToRight());
            addApprConfigToList(apprConfigList, steeringFeel.getBadTieRods());
            addApprConfigToList(apprConfigList, steeringFeel.getSteeringWheelWobble());
            addApprConfigToList(apprConfigList, steeringFeel.getBadSteeringRack());
        }

        return apprConfigList;
    }
    /**
     *This method sets the EApprVehTireCondn ConfigCodes in the List of EConfigurationCodes object
     * @param apprVehTireCondn This is the Object of EApprVehTireCondn
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */

    public List<EConfigCodes> showInEditTireCondition(EApprVehTireCondn apprVehTireCondn, List<EConfigCodes> apprConfigList) {

        if(null!=apprVehTireCondn) {

            addApprConfigToList(apprConfigList, apprVehTireCondn.getAllMatchingSizeAndMake());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getMismatched());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getTreadDepth10_32New());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getTreadDepth6_32orhigherGood());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getTreadDepth4_32Poor());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getFrontsWornUneven());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getRearsWorn());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getNoSpare());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getGoodSpare());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getRimDamage());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getSdWallsChkd());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getSpareOnCar());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getStockOffset());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getAllSameMake());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getNeedRplcmt());
            addApprConfigToList(apprConfigList, apprVehTireCondn.getRunFlats());

        }

        return apprConfigList;
    }


    /**
     *This method sets the EBookAndKeys ConfigCodes in the List of EConfigurationCodes object
     * @param bookAndKeys This is the Object of EBookAndKeys
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetBookAndKeys(EBookAndKeys bookAndKeys, List<EConfigCodes> apprConfigList) {

       if(null!=bookAndKeys) {

           addApprConfigToList(apprConfigList, bookAndKeys.getHaveBothKeysAndAllBooks());
           addApprConfigToList(apprConfigList, bookAndKeys.getHave_1_Key());
           addApprConfigToList(apprConfigList, bookAndKeys.getHave_1_KeyAndBooks());
           addApprConfigToList(apprConfigList, bookAndKeys.getNoBooks());
           addApprConfigToList(apprConfigList, bookAndKeys.getHave_2Keys());
           addApprConfigToList(apprConfigList, bookAndKeys.getHaveFullSetOfKeys());
           addApprConfigToList(apprConfigList, bookAndKeys.getHaveBooks());
       }
        return apprConfigList;
    }

    /**
     *This method sets the EApprEnginePer ConfigCodes in the List of EConfigurationCodes object
     * @param enginePer This is the Object of EApprEnginePer
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetEnginePerfor(EApprEnginePer enginePer, List<EConfigCodes> apprConfigList) {

        if(null!=enginePer) {
            addApprConfigToList(apprConfigList, enginePer.getStrongRunningNoIssues());
            addApprConfigToList(apprConfigList, enginePer.getIdlesRoughDrivesWell());
            addApprConfigToList(apprConfigList, enginePer.getStalledOnTestDrive());
            addApprConfigToList(apprConfigList, enginePer.getClearExhaust());
            addApprConfigToList(apprConfigList, enginePer.getRoughIdleLowPower());
            addApprConfigToList(apprConfigList, enginePer.getSluggishPerformance());
            addApprConfigToList(apprConfigList, enginePer.getSmokeFromExhaust());
            addApprConfigToList(apprConfigList, enginePer.getHasATickSound());
            addApprConfigToList(apprConfigList, enginePer.getHasAKnockSound());
            addApprConfigToList(apprConfigList, enginePer.getPoorAccelerationForTheModel());
        }

        return apprConfigList;
    }

    /**
     *This method sets the EApprBrakingSysSts ConfigCodes in the List of EConfigurationCodes object
     * @param brakingSysSts This is the Object of EApprBrakingSysSts
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetBrakingSts(EApprBrakingSysSts brakingSysSts, List<EConfigCodes> apprConfigList) {

        if(null!=brakingSysSts) {
            addApprConfigToList(apprConfigList, brakingSysSts.getNoIssuesGoodFeelStopsWell());
            addApprConfigToList(apprConfigList, brakingSysSts.getSoftPedalHardStopping());
            addApprConfigToList(apprConfigList, brakingSysSts.getVibrationsInPedalAndSteeringWheel());
            addApprConfigToList(apprConfigList, brakingSysSts.getPullsRight());
            addApprConfigToList(apprConfigList, brakingSysSts.getPullsLeft());
            addApprConfigToList(apprConfigList, brakingSysSts.getGrindingNoise());
            addApprConfigToList(apprConfigList, brakingSysSts.getHardPedalHardToStop());
        }

        return apprConfigList;
    }

    /**
     *This method sets the EApprVehTransSts ConfigCodes in the List of EConfigurationCodes object
     * @param transmissionSts This is the Object of EApprVehTransSts
     * @param apprConfigList This is the Object of List<EConfigurationCodes>
     */
    public List<EConfigCodes> showInEditSetTransmiSts(EApprVehTransSts transmissionSts, List<EConfigCodes> apprConfigList) {

        if(null!=transmissionSts) {
            addApprConfigToList(apprConfigList, transmissionSts.getShiftsNormally());
            addApprConfigToList(apprConfigList, transmissionSts.getHardShifting());
            addApprConfigToList(apprConfigList, transmissionSts.getSlippingInGear());
            addApprConfigToList(apprConfigList, transmissionSts.getNeedsReplacing());
            addApprConfigToList(apprConfigList, transmissionSts.getKnocksGoingIntoGear());
            addApprConfigToList(apprConfigList, transmissionSts.getHasAWhine());
            addApprConfigToList(apprConfigList, transmissionSts.getClutchPedalLow());
            addApprConfigToList(apprConfigList, transmissionSts.getNoReverse());
            addApprConfigToList(apprConfigList, transmissionSts.getOddSound());
            addApprConfigToList(apprConfigList, transmissionSts.getNewClutch());
            addApprConfigToList(apprConfigList, transmissionSts.getClutchNeedsReplacement());
            addApprConfigToList(apprConfigList, transmissionSts.getDoesNotShiftProperly());
        }

        return apprConfigList;
    }

    public List<EConfigCodes> showInEditSetRearWindowDamage(ERearWndwDmg eRearWndwDmg, List<EConfigCodes> apprConfigList){
        if(null!= eRearWndwDmg){
            addApprConfigToList(apprConfigList, eRearWndwDmg.getBrokenWindow());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getTintPealing());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getDicolored());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getDefrosterNotWorking());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getTrimMissing());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getTrimRustedOrPealing());
            addApprConfigToList(apprConfigList, eRearWndwDmg.getNoDamage());

        }
        return apprConfigList;
    }


    /**
     * This method updates the fields of EApprTestDrSts object which having images names
     * @param testDriveStatus This is the object of EApprTestDrSts entity
     * @param page This is the object of ApprCreaPage dto
     * @return EApprTestDrSts
     */
    private EApprTestDrSts updateTestDrSts(EApprTestDrSts testDriveStatus,ApprCreaPage page) throws IOException {


        testDriveStatus= appraisalVehicleMapper.updateEApprTestDrSts(page,testDriveStatus);
        EAprVehImg img = testDriveStatus.getAprVehImg();


            if (Boolean.TRUE.equals(updatePics(page.getVehiclePic1(), img.getVehiclePic1()))) {

                img.setVehiclePic1(page.getVehiclePic1());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic2(), img.getVehiclePic2())) ) {

                img.setVehiclePic2(page.getVehiclePic2());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic3(), img.getVehiclePic3())) ) {

                img.setVehiclePic3(page.getVehiclePic3());
            }
            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic4(), img.getVehiclePic4())) ) {

                img.setVehiclePic4(page.getVehiclePic4());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic5(), img.getVehiclePic5())) ) {

                img.setVehiclePic5(page.getVehiclePic5());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic6(), img.getVehiclePic6())) ) {

                img.setVehiclePic6(page.getVehiclePic6());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic7(), img.getVehiclePic7())) ) {

                img.setVehiclePic7(page.getVehiclePic7());
            }

            if(Boolean.TRUE.equals(updatePics(page.getVehiclePic8(), img.getVehiclePic8())) ) {

                img.setVehiclePic8(page.getVehiclePic8());
            }


            if (Boolean.TRUE.equals(updatePics(page.getVehiclePic9(), img.getVehiclePic9()))) {

                img.setVehiclePic9(page.getVehiclePic9());
            }

            if (Boolean.TRUE.equals(updatePics(page.getFrDrSideDmgPic(), img.getFrDrSideDmgPic()))) {

                img.setFrDrSideDmgPic(page.getFrDrSideDmgPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getRearDrSideDmgPic(), img.getRearDrSideDmgPic()))) {

                img.setRearDrSideDmgPic(page.getRearDrSideDmgPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getRearPassenSideDmgPic(), img.getRearPassenSideDmgPic()))) {

                img.setRearPassenSideDmgPic(page.getRearPassenSideDmgPic());
            }
            if (Boolean.TRUE.equals(updatePics(page.getRearPassenSidePntWrkPic(), img.getRearPassenSidePntWrkPic()))) {

                img.setRearPassenSidePntWrkPic(page.getRearPassenSidePntWrkPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getFrPassenSideDmgPic(), img.getFrPassenSideDmgPic()))) {

                img.setFrPassenSideDmgPic(page.getFrPassenSideDmgPic());
            }


            if (Boolean.TRUE.equals(updatePics(page.getFrDrSidePntWrkPic(), img.getFrDrSidePntWrkPic()))) {

                img.setFrDrSidePntWrkPic(page.getFrDrSidePntWrkPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getRearDrSidePntWrkPic(), img.getRearDrSidePntWrkPic()))) {

                img.setRearDrSidePntWrkPic(page.getRearDrSidePntWrkPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getFrPassenSidePntWrkPic(), img.getFrPassenSidePntWrkPic()))) {

                img.setFrPassenSidePntWrkPic(page.getFrPassenSidePntWrkPic());
            }

            if (Boolean.TRUE.equals(updatePics(page.getVehicleVideo1(), img.getVehicleVideo1()))) {

                img.setVehicleVideo1(page.getVehicleVideo1());
            }

        testDriveStatus.setAprVehImg(img);
        return testDriveStatus;
    }

    /**
     * This method checks the old image and new image having same file name or not. If file name is not same then deleting the old image and returns
     * true else returns false
     * @param newPic This is the file name of new image
     * @param oldPic This is the file name of old image
     * @return Boolean
     */
    public Boolean updatePics(String newPic, String oldPic) throws IOException {

        if(null==newPic && null!=oldPic ||(null!=newPic && null==oldPic) || (null!=newPic && null !=oldPic && !newPic.equals(oldPic)) ){

            if(null != oldPic && (oldPic.endsWith(".mp4")||oldPic.endsWith(".webm")||oldPic.endsWith(".mov"))){
/*                Path filePath = Paths.get(videoFolderPath + oldPic);
                Files.delete(filePath);*/
                utils.deleteObject(videoFolderPath,oldPic);
            }
            else {

                if (null != oldPic) {
/*                    Path filePath = Paths.get(imageFolderPath + oldPic);
                    Files.delete(filePath);*/
                    utils.deleteObject(imageFolderPath,oldPic);
                }
            }

            return true;

        }
            return false;
    }







    /**
     *This method sets the ConfigurationCodes of OilCondition,StereoSts,InteriorCondn,AcCondition,
     * getDashWarningLights into the List of EConfigurationCodes object and returns this object
     * @param apprCreaPage This is the object of ApprCreaPage dto
     * @return List<EConfigurationCodes>
     */
    private List<EConfigCodes> addApprConfigToEntity(ApprCreaPage apprCreaPage){

        List<EConfigCodes> codesList=null;

        ArrayList<Long> configList=new ArrayList<>();
        addCodeIdToList(apprCreaPage.getOilCondition(),configList);
        addCodeIdToList(apprCreaPage.getStereoSts(),configList);
        addCodeIdToList(apprCreaPage.getInteriorCondn(),configList);
        addCodeIdToList(apprCreaPage.getAcCondition(),configList);
        addCodeIdToList(apprCreaPage.getDashWarningLights(),configList);
        addCodeIdToList(apprCreaPage.getBrakingSysSts(),configList);
        addCodeIdToList(apprCreaPage.getEnginePerfor(),configList);
        addCodeIdToList(apprCreaPage.getTransmiSts(),configList);
        addCodeIdToList(apprCreaPage.getSteeringFeelSts(),configList);
        addCodeIdToList(apprCreaPage.getBooksAndKeys(),configList);
        addCodeIdToList(apprCreaPage.getTireCondition(),configList);
        addCodeIdToList(apprCreaPage.getRearWindowDamage(),configList);
        addCodeIdToList(apprCreaPage.getDoorLocks(),configList);
        addCodeIdToList(apprCreaPage.getRoofType(),configList);
        addCodeIdToList(apprCreaPage.getFrRightWinSts(),configList);
        addCodeIdToList(apprCreaPage.getDoorLocks(),configList);
        addCodeIdToList(apprCreaPage.getRoofType(),configList);
        addCodeIdToList(apprCreaPage.getFrRightWinSts(),configList);
        addCodeIdToList(apprCreaPage.getLeftfrWinSts(),configList);
        addCodeIdToList(apprCreaPage.getRearLeftWinSts(),configList);
        addCodeIdToList(apprCreaPage.getRearRightWinSts(),configList);
        addCodeIdToList(apprCreaPage.getVehicleInterior(),configList);
        addCodeIdToList(apprCreaPage.getVehicleExtColor(),configList);
        addCodeIdToList(apprCreaPage.getRoofType(),configList);
        addCodeIdToList(apprCreaPage.getFrWindshieldDmg(),configList);
        addCodeIdToList(apprCreaPage.getTitleSts(),configList);

        if(null!=configList && !configList.isEmpty()){
         codesList=configurationCodesRepo.findByCodeId(configList);
        }
        return codesList;
    }
    private void addCodeIdToList(Long codeId,List<Long> configList){
        if(null!= codeId ){
            configList.add(codeId);
        }
    }
    /**
     * This method gets the codeId of ConfigCodes from the object of List<ConfigCodes> and adds into the object of List<Long>
     * @param apprConfigList This the Object of List<ConfigCodes>
     * @param configList This is the Object of List<Long>
     */
    private void addCodeIdToList(List<Long> apprConfigList,List<Long> configList){
        if(null!= apprConfigList && !apprConfigList.isEmpty()){
             configList.addAll(apprConfigList);
        }
    }

    /**
     * This method adds the EConfigurationCodes object into the List<EConfigurationCodes>
     * @param codesList This is the object of List<EConfigurationCodes>
     * @param configCodes This is the object of EConfigurationCodes
     */
    private List<EConfigCodes> addApprConfigToList(List<EConfigCodes> codesList, EConfigCodes configCodes){
        if(null== codesList || codesList.isEmpty()){
            codesList=new ArrayList<>();

        }
        if(null!=configCodes){
            codesList.add(configCodes);
            log.debug("ConfigCodes Object {}",codesList);
        }
        return codesList;

    }
    @Override
    @Transactional
    public Response moveToInventory(Long apprRef) throws AppraisalException {

        EAppraiseVehicle mveToInvt = eAppraiseVehicleRepo.getAppraisalById(apprRef);
        Response response = new Response();

            if (null != mveToInvt && mveToInvt.getValid()) {

                    mveToInvt.setInvntrySts(AppraisalConstants.INVENTORY);
                    mveToInvt.setInvntryDate(new Date());
                    if(mveToInvt.getTdStatus().getPushForBuyFig()){
                        mveToInvt.getTdStatus().setPushForBuyFig(Boolean.FALSE);
                       List<EOffers> eOffersList= mveToInvt.getOffers();
                       if (null!= eOffersList && !eOffersList.isEmpty()){
                           eOffersList.forEach(offer->{
                               offer.setIsTradeBuy(Boolean.FALSE);
                           });
                       }
                       if(null != mveToInvt.getShipment()){
                           mveToInvt.getShipment().setPushForBuyFig(Boolean.FALSE);
                       }

                    }
                auditConfiguration.setAuditorName(mveToInvt.getUser().getUserName());
                    eAppraiseVehicleRepo.save(mveToInvt);
                    response.setMessage("added to inventory Successfully");
                    response.setCode(HttpStatus.OK.value());
                    response.setStatus(true);
            } else
                throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
        return response;

    }
    @Override
    @Transactional
    public Response moveToWishList(Long apprRef,UUID userId) throws AppraisalException {
        Response response=new Response();

        EAppraiseVehicle wish = eAppraiseVehicleRepo.findVehicleByInventorySts(apprRef,AppraisalConstants.INVENTORY);
        EUserWishlist wishList = wishRepo.findByAppraisalId(userId,apprRef);
        EUserWishlist userWish=new EUserWishlist();
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        auditConfiguration.setAuditorName(userById.getUserName());
            if (wishList!=null){
                wishList.setValid(true);
                wishRepo.save(wishList);
                response.setCode(HttpStatus.OK.value());
                response.setMessage("WishListed successfully");
                response.setStatus(true);
            }
            else if (wish!=null){
                userWish.setAppRef(wish.getTdStatus().getAppraisalRef());
                userWish.setUser(userRegistrationRepo.findUserById(userId));
                userWish.setWishlist(true);
                wishRepo.save(userWish);
                response.setCode(HttpStatus.OK.value());
                response.setStatus(true);
                response.setMessage("WishListed successfully");

            }else throw new AppraisalException("Did not find appraisal vehicle");

        return response;
    }

    @Override
    public CardsPage findFavoriteVehicle(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {
        CardsPage pageInfo=new CardsPage();

            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Page<EUserWishlist> pageResult = wishRepo.findByUserIdAndValidOrderByModifiedOnDesc(userId, true, pageable);
            if(pageResult.getTotalElements()!=0){
                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                List<EUserWishlist> wish = pageResult.toList();
                List<AppraisalVehicleCard> wishList = appraisalVehicleMapper.lEUserwishToLApprVehiCard(wish);
                pageInfo.setMessage("Success");
                pageInfo.setCode(HttpStatus.OK.value());
                pageInfo.setStatus(true);
                pageInfo.setCards(wishList);

            }else throw new AppraisalException("WishList cards not available");

        return pageInfo;
    }

    @Override
    @Transactional
    public Response removeVehicleFromFavoritePage(Long apprRef, UUID userId) throws AppraisalException {
        Response response=new Response();
            EUserWishlist eUserWishlist = wishRepo.findByAppraisalId(userId,apprRef);
        auditConfiguration.setAuditorName(eUserWishlist.getUser().getUserName());
            if (eUserWishlist != null) {
                eUserWishlist.setValid(false);
                wishRepo.save(eUserWishlist);
                response.setCode(HttpStatus.OK.value());
                response.setStatus(true);
                response.setMessage("Successfully removed from favorites page");
            }else throw new AppraisalException("Appraised vehicle not found");

        return response;
    }

    @Override
    public String vehReportPdf(PdfDataDto pdfDataDto) throws JRException, IOException, JDOMException {
        List<PdfDataDto> dataList = new ArrayList<>();
        dataList.add(pdfDataDto);

        String fileName = UUID.randomUUID() + AppraisalConstants.pdf;

        JasperPrint jasperPrint = null;
        JasperReport jasperReport = null;
        Resource resource = applicationContext.getResource("classpath:vehicleReport.jrxml");
        if (resource.exists()) {
            InputStream stream = resource.getInputStream();
            SAXBuilder saxBuilder = new SAXBuilder();
            Document document = saxBuilder.build(stream);
            XMLOutputter xmlOutputter = new XMLOutputter();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            xmlOutputter.output(document, out);
            byte[] bytes = out.toByteArray();
            InputStream inputStream = new ByteArrayInputStream(bytes);
            jasperReport = JasperCompileManager.compileReport(inputStream);
        }
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataList);
        Map<String, Object> parameters = new HashMap<>();
        jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        if (jasperPrint != null) {
            //creating temp file
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint,byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            File tempFile = File.createTempFile("tempFile", ".tmp");
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(byteArray);
            //object storing
            return utils.uploadFileInBucket(tempFile,pdfpath,fileName);
        }
        return "";
    }

    @Override
    public Long getTotalVehiclesInSystem() {
        return eAppraiseVehicleRepo.getTotalVehiclesInSystem();
    }

    public PdfDataDto setDataToPdf(Long apprRefId)  {

        EAppraiseVehicle byApprId = eAppraiseVehicleRepo.getAppraisalById(apprRefId);

        List<OBD2_TestDriveMeasurements> testDriveMeas = testDrMeasRepo.getTestDrMeasByApprRef(apprRefId);
        OBD2_PreStartMeasurement preStart = preStRepo.getPreStartMeasByApprRef(apprRefId);

        PdfDataDto pdfDataDto = mapper.apprToPdfData(byApprId);

        PdfDataDto pdfDataDto1 = mapper.preStartToPdfDataDto(preStart,pdfDataDto);

        if (null!=testDriveMeas&&null!=preStart) {
            List<TestDriveMes> testDriveMes = mapper.lOBD2TolTestDrMeas(testDriveMeas);

            pdfDataDto1.setTestDriveMes(testDriveMes);
            pdfDataDto1.setTrCodesSummary(factoryPdfGenerator.setTroubleCodesSummary(pdfDataDto1));
            pdfDataDto1.setDataSummary(factoryPdfGenerator.getDataSummary(apprRefId));
            if (!preStart.getScannedVin().equals(null) && (byApprId.getVinNumber().equals(preStart.getScannedVin()))) {
                pdfDataDto1.setCheckVin("Yes");
            } else {
                pdfDataDto1.setCheckVin("No");
            }
        }
        return pdfDataDto1;
    }



    private void updateTestDrvStatus(Map<String, List<EConfigCodes>> apprConfigList,EApprTestDrSts testDrSts){

        if (null!=testDrSts){
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.DOOR_LOCKS),AppraisalConstants.DOOR_LOCKS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.FRONT_LEFT_SIDE_WINDOW_STATUS),AppraisalConstants.FRONT_LEFT_SIDE_WINDOW_STATUS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.FRONT_RIGHT_SIDE_WINDOW_STATUS),AppraisalConstants.FRONT_RIGHT_SIDE_WINDOW_STATUS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.REAR_LEFT_SIDE_WINDOW_STATUS),AppraisalConstants.REAR_LEFT_SIDE_WINDOW_STATUS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.REAR_RIGHT_SIDE_WINDOW_STATUS),AppraisalConstants.REAR_RIGHT_SIDE_WINDOW_STATUS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.INTERIOR_COLOR),AppraisalConstants.INTERIOR_COLOR);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.EXTERIOR_COLOR),AppraisalConstants.EXTERIOR_COLOR);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.ROOF),AppraisalConstants.ROOF);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.FRONT_WIND_SHIELD_STATUS),AppraisalConstants.FRONT_WIND_SHIELD_STATUS);
            validateAndUpdateApprStatusConfigs(testDrSts,apprConfigList.get(AppraisalConstants.TITLE_STATUS),AppraisalConstants.TITLE_STATUS);


        }

    }

    private void validateAndUpdateApprStatusConfigs(EApprTestDrSts testDrSts,List<EConfigCodes> configList,String codeGroup){
        EConfigCodes configCodes= (null!=configList && !configList.isEmpty())?configList.get(0):null;  //select one

        switch(codeGroup){
            case AppraisalConstants.DOOR_LOCKS: if(isUpdateTestDrConfReq(configCodes,testDrSts.getDoorLock())){
                testDrSts.setDoorLock(configCodes);
            }
                break;
            case AppraisalConstants.FRONT_LEFT_SIDE_WINDOW_STATUS:if(isUpdateTestDrConfReq(configCodes,testDrSts.getFLWinStatus())){
                testDrSts.setFLWinStatus(configCodes);
            }
                break;
            case AppraisalConstants.FRONT_RIGHT_SIDE_WINDOW_STATUS: if (isUpdateTestDrConfReq(configCodes,testDrSts.getFRWinStatus())){
                testDrSts.setFRWinStatus(configCodes);
            }
                break;
            case AppraisalConstants.REAR_LEFT_SIDE_WINDOW_STATUS: if (isUpdateTestDrConfReq(configCodes,testDrSts.getRLWinStatus())){
                testDrSts.setRLWinStatus(configCodes);
            }
                break;
            case AppraisalConstants.REAR_RIGHT_SIDE_WINDOW_STATUS: if (isUpdateTestDrConfReq(configCodes,testDrSts.getRRWinStatus())){
                testDrSts.setRRWinStatus(configCodes);
            }
                break;
            case AppraisalConstants.INTERIOR_COLOR:if (isUpdateTestDrConfReq(configCodes,testDrSts.getIntrColor())){
                testDrSts.setIntrColor(configCodes);
            }
                break;
            case AppraisalConstants.EXTERIOR_COLOR: if (isUpdateTestDrConfReq(configCodes,testDrSts.getExtrColor())){
                testDrSts.setExtrColor(configCodes);
            }
                break;
            case AppraisalConstants.ROOF:if (isUpdateTestDrConfReq(configCodes,testDrSts.getRoofTypes())){
                testDrSts.setRoofTypes(configCodes);
            }
                break;
            case AppraisalConstants.FRONT_WIND_SHIELD_STATUS:if (isUpdateTestDrConfReq(configCodes,testDrSts.getWindShieldDmg())){
                testDrSts.setWindShieldDmg(configCodes);
            }
                break;
            case AppraisalConstants.TITLE_STATUS:if (isUpdateTestDrConfReq(configCodes,testDrSts.getTitleSt())){
                testDrSts.setTitleSt(configCodes);
            }
                break;
            default:
                break;
        }
    }
    private Boolean isUpdateTestDrConfReq(EConfigCodes newCode,EConfigCodes oldCode ){
        if((null== newCode && null!=oldCode && null!=oldCode.getId()) || (null!= newCode && null!= newCode.getId() && null==oldCode) || (null!=newCode && null!=oldCode && !newCode.getId().equals(oldCode.getId()))){
            return true;
        }
        return false;
    }

    @Override
    public byte[] keyAssureReport(Long apprId,UUID userId) throws IOException, AppraisalException {
        byte[] bytes=null;
        EAppraiseVehicle appraisalById = eAppraiseVehicleRepo.getAppraisalById(apprId);
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        ERoleMapping roleMapping = roleMappingRepo.findByUserId(userId);
        if(null!=userById && null!= roleMapping) {
            if((roleMapping.getRole().getRole().equals("D2") || roleMapping.getRole().getRole().equals("D3") || roleMapping.getRole().getRole().equals("S1") || roleMapping.getRole().getRole().equals("M1"))){
                userId=roleMapping.getDealerAdmin();
            }
            PaymentDetails user = paymentDetailsRepo.getUser(userId);
            if(!roleMapping.getRole().getRoleGroup().equalsIgnoreCase("P") && null!=user){
                bytes = utils.fileDownloadfromBucket(pdfpath, appraisalById.getTdStatus().getKeyAssureFiles());
                return bytes;
            }
            /* this repo is checking if this user previously opened keyAssure report or not
                 * if opened the record will be available in keyassureReport log  */
                KeyassureReportLog record = rptLogRepo.getRecord(userId, apprId);
                if (null != record) {
                    log.info("record exist");
                    //object from amazons3
                    bytes = utils.fileDownloadfromBucket(pdfpath, appraisalById.getTdStatus().getKeyAssureFiles());
                    return bytes;
                }
                if (null != user && user.getConsumeQuota() < user.getTotalQuota()) {
                    log.info("new record inserted");
                    KeyassureReportLog kReportLog = new KeyassureReportLog();
                    kReportLog.setAppraisalRef(appraisalById);
                    kReportLog.setUser(userById);
                    rptLogRepo.save(kReportLog);
                    user.setConsumeQuota(user.getConsumeQuota() + 1);
                    paymentDetailsRepo.save(user);
                    //object from amazons3
                    bytes = utils.fileDownloadfromBucket(pdfpath, appraisalById.getTdStatus().getKeyAssureFiles());
                    return bytes;
                } else throw new AppraisalException("limit Exceeded");

        }else throw new AppraisalException("user not found");
    }

    @Override
    public byte[] previewPdf(String name) throws IOException {
        //object from amazons3
        return utils.fileDownloadfromBucket(pdfpath,name);
    }



}
