package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AppraisalVehicleCard;
import com.massil.dto.CardsPage;
import com.massil.dto.Shipment;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.OffersERepo;
import com.massil.services.ShipmentService;
import com.massil.util.CompareUtils;
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

    Logger log = LoggerFactory.getLogger(ShipmentImpl.class);


    @Override
    public CardsPage mySellsCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {

        CardsPage cardsPage = null;
        CardsPage pageInfo = new CardsPage();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());
        Page<EOffers> pageResult = null;

        if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = offersRepo.findBySellerUserIdSold(userId,true, AppraisalConstants.BUYERACCEPTED,AppraisalConstants.SELLERACCEPTED, pageable);
        }else {
            cardsPage = offersERepo.mySaleCards(userId, pageNumber, pageSize);
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

        if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = offersRepo.findByBuyerUserIdSold(userId,true, AppraisalConstants.BUYERACCEPTED,AppraisalConstants.SELLERACCEPTED, pageable);
        }else {
            cardsPage = offersERepo.myPurchaseCards(userId, pageNumber, pageSize);
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
        pageInfo.setMessage("Getting all My Purchased cards in offers page");
        pageInfo.setStatus(true);
        return pageInfo;

    }

    @Override
    public Response buyerAgreedService(Shipment shipment,Long shipId) throws GlobalException, IOException {
        Response response=new Response();
        EShipment byShipId = shipmentRepo.findByShipId(shipId);
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
                byShipId.setBuyFee(0);
            }

            shipmentRepo.save(byShipId);
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Shipment updated successfully after buyer buyer accept");
            response.setStatus(Boolean.TRUE);
        }

        else throw new GlobalException("Shipment object not available");
        return response;
    }
    @Override
    public Response sellerAgreedService(Shipment shipment,Long shipId) throws GlobalException, IOException {
        Response response=new Response();
        EShipment byShipId = shipmentRepo.findByShipId(shipId);
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
                byShipId.setSaleFee(0);
            }
            shipmentRepo.save(byShipId);
            response.setCode(HttpStatus.OK.value());
            response.setMessage("Shipment updated successfully after seller accepted");
            response.setStatus(Boolean.TRUE);
        }else throw new GlobalException("Shipment object not available");
        return response;
    }


    public static byte[] convertBase64ToByteArray(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    public String imageUpload(byte[] file) throws IOException {
         String filename = UUID.randomUUID() + "." + "png";
         Path filePath = Paths.get(imageFolderPath + filename);
            Files.write(filePath, file);
            if( compareUtils.isDocPresent(imageFolderPath,filename)) {
               return filename;
           }
           return null;
    }



}
