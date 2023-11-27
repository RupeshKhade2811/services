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
import com.massil.config.AuditConfiguration;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AppraisalVehicleCard;
import com.massil.dto.CardsPage;
import com.massil.dto.Shipment;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.OffersERepo;
import com.massil.services.PaymentGatewayService;
import com.massil.services.ShipmentService;
import com.massil.util.CompareUtils;
import com.massil.util.DealersUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class ShipmentImpl implements ShipmentService {

    @Value("${image_folder_path}")
    private String imageFolderPath;
    @Value("${access_key}")
    private String accesskey;

    @Value(("${secret}"))
    private String secret;

    @Value(("${amazonS3_url}"))
    private String amazonS3Url;
    @Autowired
    private UserRegistrationRepo userRepo;
    @Autowired
    private DealerRegistrationRepo dealerRepo;
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private OffersMapper offersMapper;
    @Autowired
    private ShipmentRepo shipmentRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private ConfigCodesRepo configCodesRepo;
    @Autowired
    private RoleMappingRepo roleMappingRepo;
    @Autowired
    private CompareUtils compareUtils;
    @Autowired
    private ConfigCodesRepo configurationCodesRepo;
    @Autowired
    private OffersERepo offersERepo;
	@Autowired
    private DealersUser dealersUser;
    @Autowired
    private AuditConfiguration auditConfiguration;

    @Autowired
    private PaymentGatewayService payGatewyService;

    Logger log = LoggerFactory.getLogger(ShipmentImpl.class);


    @Override
    public CardsPage mySellsCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {

        CardsPage cardsPage = null;
        CardsPage pageInfo = new CardsPage();
        Pageable pageable = PageRequest.of(pageNumber, pageSize,Sort.by(AppraisalConstants.MODIFIEDON).descending());
        Page<EOffers> pageResult = null;
        EUserRegistration userById = userRepo.findUserById(userId);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userById.getId());
        if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = offersRepo.findBySellerUserIdSold(allUsersUnderDealer,true, AppraisalConstants.BUYERACCEPTED,AppraisalConstants.SELLERACCEPTED, pageable);
        }else {
            cardsPage = offersERepo.mySaleCards(allUsersUnderDealer, pageNumber, pageSize);
        }
        if(null!= pageResult && pageResult.getTotalElements()!=0) {
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                List<EOffers> apv = pageResult.toList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
                pageInfo.setCards(appraiseVehicleDtos);
        }
        else if(null!=cardsPage && !cardsPage.getEOffersList().isEmpty()){
                pageInfo.setTotalRecords(cardsPage.getTotalRecords());
                pageInfo.setTotalPages((long) cardsPage.getTotalPages());
                List<EOffers> apv = cardsPage.getEOffersList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
                pageInfo.setCards(appraiseVehicleDtos);
        }
        else throw new AppraisalException("Shipment Cards not available");
        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setMessage("Getting all My selling cards in offers page");
        pageInfo.setStatus(true);
        return pageInfo;

    }

    @Override
    public CardsPage myBuyerCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {

        CardsPage cardsPage = null;
        CardsPage pageInfo = new CardsPage();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());
        Page<EOffers> pageResult = null;
        EUserRegistration userById = userRepo.findUserById(userId);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userById.getId());
        if (Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = offersRepo.findByBuyerUserIdSold(allUsersUnderDealer, true, AppraisalConstants.BUYERACCEPTED, AppraisalConstants.SELLERACCEPTED, pageable);
        } else {
            cardsPage = offersERepo.myPurchaseCards(allUsersUnderDealer, pageNumber, pageSize);
        }

         if (null != pageResult && pageResult.getTotalElements() != 0) {
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                List<EOffers> apv = pageResult.toList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
                pageInfo.setCards(appraiseVehicleDtos);
         } else if (null != cardsPage && !cardsPage.getEOffersList().isEmpty()) {
                pageInfo.setTotalRecords(cardsPage.getTotalRecords());
                pageInfo.setTotalPages((long) cardsPage.getTotalPages());
                List<EOffers> apv = cardsPage.getEOffersList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
                pageInfo.setCards(appraiseVehicleDtos);
         } else throw new AppraisalException("Shipment Cards not available");
         pageInfo.setCode(HttpStatus.OK.value());
         pageInfo.setMessage("Getting all My Purchased cards in offers page");
         pageInfo.setStatus(true);
         return pageInfo;
    }

    @Override
    @Transactional
    public Response buyerAgreedService(Shipment shipment,Long shipId) throws Exception {
        Response response=new Response();
        EShipment byShipId = shipmentRepo.findByShipId(shipId);
        auditConfiguration.setAuditorName(byShipId.getOffers().getBuyerUserId().getUserName());
        if (null!=byShipId && shipment.getBuyerAgreed()){
            byShipId.setBuyerAgreed(true);
            if (null!=shipment.getBuyerSign()) {
                byte[] bytes = convertBase64ToByteArray(shipment.getBuyerSign());
                byShipId.setBuyerSign(imageUpload(bytes));
            }
            if (null!=shipment.getBuyerUserId()) {
                byShipId.setBuyerUserId(userRepo.findUserById(shipment.getBuyerUserId()));
            }

            ERole byRole = roleRepo.findByRole(roleMappingRepo.findByUserId(byShipId.getBuyerUserId().getId()).getRole().getId());
            String roleGroup = byRole.getRoleGroup();
            if(roleGroup.equals("D")||roleGroup.equals("DM")){
                byShipId.setBuyFee(configCodesRepo.getFee(AppraisalConstants.BUY_FEE));
            }else {
                byShipId.setBuyFee(0.0);
            }

            EShipment save = shipmentRepo.save(byShipId);
            if(save.getBuyerAgreed() && save.getSellerAgreed()){
                payGatewyService.feePaymentService(shipment,shipId);
            }
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Shipment updated successfully after buyer buyer accept");
            response.setStatus(Boolean.TRUE);
        }

        else throw new GlobalException("Shipment object not available");
        return response;
    }
    @Override
    @Transactional
    public Response sellerAgreedService(Shipment shipment,Long shipId) throws Exception {
        Response response=new Response();
        EShipment byShipId = shipmentRepo.findByShipId(shipId);
        auditConfiguration.setAuditorName(byShipId.getOffers().getSellerUserId().getUserName());
        if (null!=byShipId && shipment.getSellerAgreed()){
            byShipId.setSellerAgreed(true);
            if (null!=shipment.getSellerSign()) {
                byte[] bytes = convertBase64ToByteArray(shipment.getSellerSign());
                byShipId.setSellerSign(imageUpload(bytes));
            }
            if (null!=shipment.getSellerUserId()) {
                byShipId.setSellerUserId(userRepo.findUserById(shipment.getSellerUserId()));
            }

            ERole byRole = roleRepo.findByRole(roleMappingRepo.findByUserId(byShipId.getSellerUserId().getId()).getRole().getId());
            String roleGroup = byRole.getRoleGroup();
            if(roleGroup.equals("D")||roleGroup.equals("DM")){
                byShipId.setSaleFee(configCodesRepo.getFee(AppraisalConstants.SALE_FEE));
            }else {
                byShipId.setSaleFee(0.0);
            }
            EShipment save = shipmentRepo.save(byShipId);

            if(save.getBuyerAgreed() && save.getSellerAgreed()){
                payGatewyService.feePaymentService(shipment,shipId);
            }
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Shipment updated successfully after seller accepted");
            response.setStatus(Boolean.TRUE);
        }else throw new GlobalException("Shipment object not available");
        return response;
    }


    public static byte[] convertBase64ToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public String imageUpload(byte[] byteArray) throws IOException {
        String filename = UUID.randomUUID() + "." + "png";
        File tempFile = File.createTempFile("tempFile", ".tmp");
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(byteArray);
        }
        //object storing
        return  compareUtils.uploadFileInBucket(tempFile,imageFolderPath, filename);
    }





}
