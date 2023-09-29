package com.massil.persistence.mapper;
//@author:Rupesh Khade


import com.massil.dto.*;
import com.massil.persistence.model.*;
import com.massil.repository.CompanyRepo;
import com.massil.repository.UserRegistrationRepo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This is an interface used to map DTO with Entities
 * we are using Mapstruct for this process
 */
@Mapper(componentModel = "spring")
public interface AppraisalVehicleMapper {
    Logger log = LoggerFactory.getLogger(AppraisalVehicleMapper.class);

    /**
     * This method maps the ApprCreaPage object with EApprTestDrSts object
     * @param apprCreaPage This is the object of ApprCreaPage
     * @return EApprTestDrSts
     */
    @Mapping(target = "frDrSideDmgSts",expression ="java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getFrDrSideDmgSts()))")
    @Mapping(target = "frDrSideDmgTxtBox",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getFrDrSideDmgTxtBox()))")
    @Mapping(target = "rearDrSideDmgSts",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getRearDrSideDmgSts()))")
    @Mapping(target = "rearDrSideDmgTxtBox",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getRearDrSideDmgTxtBox()))")
    @Mapping(target = "rearPassenSideDmgSts",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getRearPassenSideDmgSts()))")
    @Mapping(target = "rearPassenSideDmgTxtBox",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getRearPassenSideDmgTxtBox()))")
    @Mapping(target = "frPassenSideDmgSts",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getFrPassenSideDmgSts()))")
    @Mapping(target = "frPassenSideDmgTxtBox",expression = "java( externalDmgSts(apprCreaPage.getExternalDmgSts(),apprCreaPage.getFrPassenSideDmgTxtBox()))")

    @Mapping(target = "frDrSidePntWrkSts",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getFrDrSidePntWrkSts()))")
    @Mapping(target = "frDrSidePntWrkTxtBox",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getFrDrSidePntWrkTxtBox()))")
    @Mapping(target = "rearDrSidePntWrk",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getRearDrSidePntWrk()))")
    @Mapping(target = "rearDrSidePntWrkTxtBox",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getRearDrSidePntWrkTxtBox()))")
    @Mapping(target = "frPassenSidePntWrk",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getFrPassenSidePntWrk()))")
    @Mapping(target = "frPassenSidePntWrkTxtBox",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getFrPassenSidePntWrkTxtBox()))")
    @Mapping(target = "rearPassenSidePntWrk",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getRearPassenSidePntWrk()))")
    @Mapping(target = "rearPassenSidePntWrkTxtBox",expression = "java( paintWork(apprCreaPage.getPaintWork(),apprCreaPage.getRearPassenSidePntWrkTxtBox()))")
    EApprTestDrSts appCreaPageToEAppTestDrSts(ApprCreaPage apprCreaPage);


    EAprVehImg appCreaPageToEAprVehImg(ApprCreaPage apprCreaPage);


    /**
     * This method maps ApprCreaPage with EAppraiseVehicle
     * @param apprCreaPage
     * @return EAppraiseVehicle
     */
    EAppraiseVehicle appCreaPageToEAppVehCond(ApprCreaPage apprCreaPage);
    List<UserRegistration> lEUserRegToUserRegis(List<EUserRegistration> corporateDlr);

    /**
     * This method maps EAppraiseVehicle to AppraiseVehicle
     * @param eAppraiseVehicle
     * @return AppraiseVehicle
     */
    AppraiseVehicle eApprVehiToApprVehicle(EAppraiseVehicle eAppraiseVehicle);

    /**
     * This method maps AppraiseVehicle to EAppraiseVehicle
     * @param appraiseVehicle
     * @return EAppraiseVehicle
     */
    EAppraiseVehicle apprVehiToEapprVehi(AppraiseVehicle appraiseVehicle);

    /**
     * This method maps EAppraiseVehicle to AppraisalVehicleCard
     * @param eAppraiseVehicle
     * @return AppraisalVehicleCard
     */


    @Mapping(target = "createdOn",expression = "java(customDate(eAppraiseVehicle.getCreatedOn()))")
    @Mapping(target = "modifiedOn",expression ="java(customDate(eAppraiseVehicle.getModifiedOn()))")
    @Mapping(source = "tdStatus.aprVehImg.vehiclePic1", target = "vehiclePic1" )
    @Mapping(target = "titleSts",expression = "java(setTitelStsConfig(eAppraiseVehicle))")
    @Mapping(target="isSold",expression = "java(isShAvlbl(eAppraiseVehicle.getShipment()))")
    AppraisalVehicleCard eApprVehiToApprVehiCard(EAppraiseVehicle eAppraiseVehicle);

    default Boolean checkIsOfferMade(EAppraiseVehicle eAppraiseVehicle,UUID userId){
        if(null!=eAppraiseVehicle && null!=eAppraiseVehicle.getOffers()) {
            ArrayList<EOffers> offersList= new ArrayList<>(eAppraiseVehicle.getOffers());
            for(EOffers v:offersList){
                if(Objects.equals(v.getAppRef().getId(), eAppraiseVehicle.getId()) && v.getValid() && v.getBuyerUserId().getId().equals(userId))
                    return true;
            }
        }
        return false;
    }

    default Long checkColorCode(EAppraiseVehicle eAppraiseVehicle,UUID userId){
        if(null!=eAppraiseVehicle && null!=eAppraiseVehicle.getOffers()) {
            ArrayList<EOffers> offersList= new ArrayList<>(eAppraiseVehicle.getOffers());
            for(EOffers v:offersList){
                if(Objects.equals(v.getAppRef().getId(), eAppraiseVehicle.getId()) && v.getValid() && v.getBuyerUserId().getId().equals(userId))
                    return v.getStatus().getColor();
            }
        }
        return 7l;
    }

    default String checkStatus(EAppraiseVehicle eAppraiseVehicle,UUID userId){
        if(null!=eAppraiseVehicle && null!=eAppraiseVehicle.getOffers()) {
            ArrayList<EOffers> offersList= new ArrayList<>(eAppraiseVehicle.getOffers());
            for(EOffers v:offersList){
                if(Objects.equals(v.getAppRef().getId(), eAppraiseVehicle.getId()) && v.getValid() && v.getBuyerUserId().getId().equals(userId))
                    return v.getStatus().getOfferStatus();
            }
        }
        return "";
    }


    default Long findOfferId(EAppraiseVehicle eAppraiseVehicle,UUID userId){
        if(null!=eAppraiseVehicle && null!=eAppraiseVehicle.getOffers()) {
            ArrayList<EOffers> offersList= new ArrayList<>(eAppraiseVehicle.getOffers());
            for(EOffers v:offersList){
                if(Objects.equals(v.getAppRef().getId(), eAppraiseVehicle.getId()) && v.getValid() && v.getBuyerUserId().getId().equals(userId))
                    return v.getId();
            }
        }
        return null;
    }
    /**
     * This methods maps List of EAppraiseVehicle to list of AppraisalVehicleCard
     * @param eAppraiseVehicleList
     * @return
     */
    List<AppraisalVehicleCard> lEApprVehiToLApprVehiCard(List<EAppraiseVehicle> eAppraiseVehicleList);

    @Mapping(target="isOfferMade",expression = "java(checkIsOfferMade(eAppraiseVehicle,userId))")
    @Mapping(target="offerId",expression = "java(findOfferId(eAppraiseVehicle,userId))")
    @Mapping(target="color",expression = "java(checkColorCode(eAppraiseVehicle,userId))")
    @Mapping(target="offerStatus",expression = "java(checkStatus(eAppraiseVehicle,userId))")
    @Mapping(target = "vehiclePic1", source = "eAppraiseVehicle.tdStatus.aprVehImg.vehiclePic1")
    @Mapping(target = "titleSts",expression = "java(setTitelStsConfig(eAppraiseVehicle))")
    @Mapping(target = "isPrivateParty",expression = "java(isPrivateParty(eAppraiseVehicle))")
    AppraisalVehicleCard lEApprVehiToLApprVehiCard1(EAppraiseVehicle eAppraiseVehicle, UUID userId);



    /**
     * This method maps EUserWishlist to AppraisalVehicleCard
     * @param wish object of EUserWishlist
     * @return
     */
    @Mapping(source = "appRef.vehicleMake",target = "vehicleMake")
    @Mapping(source = "appRef.vehicleModel",target = "vehicleModel")
    @Mapping(source = "appRef.appraisedValue",target = "appraisedValue")
    @Mapping(source = "appRef.createdBy",target = "createdBy")
    @Mapping(source = "appRef.vehicleYear",target = "vehicleYear")
    @Mapping(source = "appRef.vehicleMileage",target = "vehicleMileage")
    @Mapping(source = "appRef.vehicleSeries",target = "vehicleSeries")
    @Mapping(source = "appRef.tdStatus.aprVehImg.vehiclePic1",target = "vehiclePic1")
    @Mapping(target = "titleSts",expression = "java(setTitelStsConfig(wish.getAppRef()))")
    @Mapping(source = "appRef.id",target = "id")
    AppraisalVehicleCard eUserWishToApprVehiCard(EUserWishlist wish);

    /**
     * This methods maps List of EAppraiseVehicle to list of AppraisalVehicleCard
     * @param wishlist object of list of EUserWishlist
     * @return
     */
    List<AppraisalVehicleCard> lEUserwishToLApprVehiCard(List<EUserWishlist> wishlist);




    /**
     * This method maps ConfigCodes to EConfigurationCodes
     * @param configCodes
     * @return  EConfigCodes
     */
    EConfigCodes configCodeToEConfigCode(ConfigDropDown configCodes);

    /**
     * This method maps list of EConfigurationCodes to list of ConfigCodes
     * @param econfigurationCodes
     * @return List<ConfigCodes>
     */
    List<ConfigDropDown> lEConfigCodeToConfigCode(List<EConfigCodes> econfigurationCodes);

    /**
     * This method maps list of ConfigCodes to list of EConfigurationCodes
     * @param configCodes
     * @return List<EConfigCodes>
     */
    List<EConfigCodes> lConfigCodeToEConfigCode(List<ConfigDropDown> configCodes);

    ERole roleToERole(Role role);


    List<ERole> lRoleTolERole(List<Role> role);

    List<Role> lERoleTolRole(List<ERole> eRoles);

    Role eRoleToRole(ERole role);


    /**
     * This method maps UserRegistration to EUserRegistration
     * @param userRegistration
     * @return EUserRegistration
     */
    EUserRegistration userRegisToEUserRegis(UserRegistration userRegistration);

    /**
     * This method maps EUserRegistration to UserRegistration
     * @param eUserRegistration
     * @return UserRegistration
     */
    UserRegistration eUserRegisToUserRegis(EUserRegistration eUserRegistration);

    /**
     * This method maps DealerRegistration to EDealerRegistration
     * @param dealerRegistration
     * @return EDealerRegistration
     */
    @Mapping(target = "dealershipNames",ignore = true)
    EDealerRegistration dealerRegToEdealerReg(DealerRegistration dealerRegistration);



    /**
     * This method maps EDealerRegistration to DealerRegistration
     * @param eDealerRegistration
     * @return
     */
    @Mapping(source = "id",target = "dealerId")
    @Mapping(target = "compName",expression = "java(chkCompName(eDealerRegistration.getCompany()))")
    @Mapping(target = "companyId",expression = "java(chkCompId(eDealerRegistration.getCompany()))")
    DealerRegistration edealerRegToDealerReg(EDealerRegistration eDealerRegistration);
    List<DealerRegistration> eDlrRegToDlrReg(List<EDealerRegistration> dealerRegList);

    @Mapping(source = "aptmentNumber",target = "apartmentNumber")
    @Mapping(source = "name",target = "userName")
    UserRegistration dealerToUser(DealerRegistration dealerRegistration);



    /**
     * This method maps list of EConfigurationCodes to list of ConfigDropDown
     * @param configCodes
     * @return
     */
    List<ConfigDropDown> lEConfigCodestoConfigDropDown(List<EConfigCodes> configCodes);

    /**
     * This method maps EConfigurationCodes to ConfigDropDown
     * @param configCodes
     * @return ConfigDropDown
     */

    ConfigDropDown eConfigCodesToConfigDropDown(EConfigCodes configCodes);

    /**
     * This method maps list of EUserRegistration to list of UserDropDown
     * @param userReg
     * @return List<UserDropDown>
     */
    List<UserDropDown> lEUserRegToUserDropDowns(List<EUserRegistration> userReg);

    /**
     * This method maps EUserRegistration to UserDropDown
     * @param userReg
     * @return UserDropDown
     */
    UserDropDown eUserRegToUserDropDown(EUserRegistration userReg);


    /**
     * This method getting the fields values from EAppraiseVehicle object and  setting the fields of ApprCreaPage
     * @param eAppraiseVehicle This is the obejct of EAppraiseVehicle
     * @param page This is the object of ApprCreaPage
     * @return ApprCreaPage
     */


   @Mapping(source = "tdStatus.engineType",target = "engineType")
   @Mapping(source = "tdStatus.transmissionType",target = "transmissionType")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic1",target = "vehiclePic1")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic2",target = "vehiclePic2")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic3",target = "vehiclePic3")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic4",target = "vehiclePic4")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic5",target = "vehiclePic5")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic6",target = "vehiclePic6")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic7",target = "vehiclePic7")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic8",target = "vehiclePic8")
   @Mapping(source = "tdStatus.aprVehImg.vehiclePic9",target = "vehiclePic9")
   @Mapping(source = "tdStatus.aprVehImg.vehicleVideo1",target = "vehicleVideo1")
   @Mapping(source = "tdStatus.externalDmgSts",target = "externalDmgSts")
   @Mapping(source = "tdStatus.frDrSideDmgSts",target = "frDrSideDmgSts")
   @Mapping(source = "tdStatus.frDrSideDmgTxtBox",target = "frDrSideDmgTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.frDrSideDmgPic",target = "frDrSideDmgPic")
   @Mapping(source = "tdStatus.rearDrSideDmgSts",target = "rearDrSideDmgSts")
   @Mapping(source = "tdStatus.rearDrSideDmgTxtBox",target = "rearDrSideDmgTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.rearDrSideDmgPic",target = "rearDrSideDmgPic")
   @Mapping(source = "tdStatus.rearPassenSideDmgSts",target = "rearPassenSideDmgSts")
   @Mapping(source = "tdStatus.rearPassenSideDmgTxtBox",target = "rearPassenSideDmgTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.rearPassenSideDmgPic",target = "rearPassenSideDmgPic")
   @Mapping(source = "tdStatus.frPassenSideDmgSts",target = "frPassenSideDmgSts")
   @Mapping(source = "tdStatus.frPassenSideDmgTxtBox",target = "frPassenSideDmgTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.frPassenSideDmgPic",target = "frPassenSideDmgPic")
   @Mapping(source = "tdStatus.paintWork",target = "paintWork")
   @Mapping(source = "tdStatus.frDrSidePntWrkSts",target = "frDrSidePntWrkSts")
   @Mapping(source = "tdStatus.frDrSidePntWrkTxtBox",target = "frDrSidePntWrkTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.frDrSidePntWrkPic",target = "frDrSidePntWrkPic")
   @Mapping(source = "tdStatus.rearDrSidePntWrk",target = "rearDrSidePntWrk")
   @Mapping(source = "tdStatus.rearDrSidePntWrkTxtBox",target = "rearDrSidePntWrkTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.rearDrSidePntWrkPic",target = "rearDrSidePntWrkPic")
   @Mapping(source = "tdStatus.rearPassenSidePntWrk",target = "rearPassenSidePntWrk")
   @Mapping(source = "tdStatus.rearPassenSidePntWrkTxtBox",target = "rearPassenSidePntWrkTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.rearPassenSidePntWrkPic",target = "rearPassenSidePntWrkPic")
   @Mapping(source = "tdStatus.frPassenSidePntWrk",target = "frPassenSidePntWrk")
   @Mapping(source = "tdStatus.frPassenSidePntWrkTxtBox",target = "frPassenSidePntWrkTxtBox")
   @Mapping(source = "tdStatus.aprVehImg.frPassenSidePntWrkPic",target = "frPassenSidePntWrkPic")
   @Mapping(source = "tdStatus.keyAssureYes",target = "keyAssureYes")
   @Mapping(source = "tdStatus.subscribToKeyAssure",target = "subscribToKeyAssure")
   @Mapping(source = "tdStatus.keyAssureFiles",target = "keyAssureFiles")
   @Mapping(source = "profOpinion",target = "profOpinion")
   @Mapping(source = "vehicleDesc",target = "vehicleDesc")
   @Mapping(source = "signDet.adjustedWholePoor",target = "adjustedWholePoor")
   @Mapping(source = "signDet.adjustedWholeFair",target = "adjustedWholeFair")
   @Mapping(source = "signDet.adjustedWholeGood",target = "adjustedWholeGood")
   @Mapping(source = "signDet.adjustedWholeVeryGood",target = "adjustedWholeVeryGood")
   @Mapping(source = "signDet.adjustedWholeExcelnt",target = "adjustedWholeExcelnt")
   @Mapping(source = "signDet.adjustedFinanPoor",target = "adjustedFinanPoor")
   @Mapping(source = "signDet.adjustedFinanFair",target = "adjustedFinanFair")
   @Mapping(source = "signDet.adjustedFinanGood",target = "adjustedFinanGood")
   @Mapping(source = "signDet.adjustedFinanVeryGood",target = "adjustedFinanVeryGood")
   @Mapping(source = "signDet.adjustedFinanExcelnt",target = "adjustedFinanExcelnt")
   @Mapping(source = "signDet.adjustedRetailPoor",target = "adjustedRetailPoor")
   @Mapping(source = "signDet.adjustedRetailFair",target = "adjustedRetailFair")
   @Mapping(source = "signDet.adjustedRetailGood",target = "adjustedRetailGood")
   @Mapping(source = "signDet.adjustedRetailVeryGood",target = "adjustedRetailVeryGood")
   @Mapping(source = "signDet.adjustedRetailExcelnt",target = "adjustedRetailExcelnt")
   @Mapping(source = "dealerReserve",target = "dealerReserve")
   @Mapping(source = "consumerAskPrice",target = "consumerAskPrice")
   @Mapping(source = "delrRetlAskPrice",target = "delrRetlAskPrice")
   @Mapping(source = "tdStatus.pushForBuyFig",target = "pushForBuyFig")
   ApprCreaPage showApprcreaPage(EAppraiseVehicle eAppraiseVehicle, @MappingTarget ApprCreaPage page);


   PrestartMeasurement ePrestartToPrestart(OBD2_PreStartMeasurement prestart);


    /**
     * This method updates the EAppraiseVehicle object
     * @param page This is the object of ApprCreaPage dto
     * @param vehicle This is the object of EAppraiseVehicle entity
     * @return EAppraiseVehicle
     */
    @Mapping(target = "appraisedValue", expression = "java( customUpdate(page.getAppraisedValue(),vehicle.getAppraisedValue()))")
    @Mapping(target = "clientFirstName", expression = "java( customUpdate(page.getClientFirstName(),vehicle.getClientFirstName()))")
    @Mapping(target = "clientLastName", expression = "java( customUpdate(page.getClientLastName(),vehicle.getClientLastName()))" )
    @Mapping(target = "clientPhNum", expression = "java( customUpdate(page.getClientPhNum(),vehicle.getClientPhNum()))")
    @Mapping(target = "profOpinion", expression = "java( customUpdate(page.getProfOpinion(),vehicle.getProfOpinion()))")
    @Mapping(target = "vehicleMake",ignore = true)
    @Mapping(target = "vehicleModel",ignore = true)
    @Mapping(target = "vehicleSeries",ignore = true)
    @Mapping(target = "vehicleYear",ignore = true)
    @Mapping(target = "vinNumber",ignore = true)
    EAppraiseVehicle updateEAppraiseVehicle(ApprCreaPage page, @MappingTarget EAppraiseVehicle vehicle);



    /**
     * This method updates the EApprTestDrSts object
     * @param page This is the object of ApprCreaPage dto
     * @param testDrSts This is the object of EApprTestDrSts entity
     * @return  EApprTestDrSts
     */
    @Mapping(target = "engineType", expression = "java( customUpdate(page.getEngineType(),testDrSts.getEngineType()))")
    @Mapping(target = "transmissionType", expression = "java( customUpdate(page.getTransmissionType(),testDrSts.getTransmissionType()))")
    @Mapping(target = "frDrSideDmgSts", expression = "java( customUpdate(page.getFrDrSideDmgSts(),testDrSts.getFrDrSideDmgSts()))")
    @Mapping(target = "rearDrSideDmgSts", expression = "java( customUpdate(page.getRearDrSideDmgSts(),testDrSts.getRearDrSideDmgSts()))")
    @Mapping(target = "rearDrSideDmgTxtBox", expression = "java( customUpdate(page.getRearDrSideDmgTxtBox(),testDrSts.getRearDrSideDmgTxtBox()))")
    @Mapping(target = "rearPassenSideDmgSts", expression = "java( customUpdate(page.getRearPassenSideDmgSts(),testDrSts.getRearPassenSideDmgSts()))")
    @Mapping(target = "rearPassenSideDmgTxtBox", expression = "java( customUpdate(page.getRearPassenSideDmgTxtBox(),testDrSts.getRearPassenSideDmgTxtBox()))")
    @Mapping(target = "frPassenSideDmgSts", expression = "java( customUpdate(page.getFrPassenSideDmgSts(),testDrSts.getFrPassenSideDmgSts()))")
    @Mapping(target = "frPassenSideDmgTxtBox", expression = "java( customUpdate(page.getFrPassenSideDmgTxtBox(),testDrSts.getFrPassenSideDmgTxtBox()))")
    @Mapping(target = "frDrSidePntWrkSts", expression = "java( customUpdate(page.getFrDrSidePntWrkSts(),testDrSts.getFrDrSidePntWrkSts()))")
    @Mapping(target = "rearDrSidePntWrk", expression = "java( customUpdate(page.getRearDrSidePntWrk(),testDrSts.getRearDrSidePntWrk()))")
    @Mapping(target = "rearDrSidePntWrkTxtBox", expression = "java( customUpdate(page.getRearDrSidePntWrkTxtBox(),testDrSts.getRearDrSidePntWrkTxtBox()))")
    @Mapping(target = "frPassenSidePntWrk", expression = "java( customUpdate(page.getFrPassenSidePntWrk(),testDrSts.getFrPassenSidePntWrk()))")
    @Mapping(target = "frPassenSidePntWrkTxtBox", expression = "java( customUpdate(page.getFrPassenSidePntWrkTxtBox(),testDrSts.getFrPassenSidePntWrkTxtBox()))")


    @Mapping(target = "keyAssureYes", expression = "java( customUpdate(page.getKeyAssureYes(),testDrSts.getKeyAssureYes()))")
    @Mapping(target = "subscribToKeyAssure", expression = "java( customUpdate(page.getSubscribToKeyAssure(),testDrSts.getSubscribToKeyAssure()))")
    @Mapping(target = "keyAssureFiles", expression = "java( customUpdate(page.getKeyAssureFiles(),testDrSts.getKeyAssureFiles()))")

    @Mapping(target = "signDet.signDocument", expression = "java( customUpdate(page.getESign(),testDrSts.signDet.getESign()))")
    @Mapping(target = "signDet.adjustedFinanFair", expression = "java( customUpdate(page.getAdjustedFinanFair(),testDrSts.signDet.getAdjustedFinanFair()))")
    @Mapping(target = "signDet.adjustedFinanGood", expression = "java( customUpdate(page.getAdjustedFinanGood(),testDrSts.signDet.getAdjustedFinanGood()))")
    @Mapping(target = "signDet.adjustedFinanVeryGood", expression = "java( customUpdate(page.getAdjustedFinanVeryGood(),testDrSts.signDet.getAdjustedFinanVeryGood()))")
    @Mapping(target = "signDet.adjustedFinanExcelnt", expression = "java( customUpdate(page.getAdjustedFinanExcelnt(),testDrSts.signDet.getAdjustedFinanExcelnt()))")
    @Mapping(target = "signDet.adjustedRetailPoor", expression = "java( customUpdate(page.getAdjustedRetailPoor(),testDrSts.signDet.getAdjustedRetailPoor()))")
    @Mapping(target = "signDet.adjustedRetailFair", expression = "java( customUpdate(page.getAdjustedRetailFair(),testDrSts.signDet.getAdjustedRetailFair()))")
    @Mapping(target = "signDet.adjustedRetailGood", expression = "java( customUpdate(page.getAdjustedRetailGood(),testDrSts.signDet.getAdjustedRetailGood()))")
    @Mapping(target = "signDet.adjustedRetailVeryGood", expression = "java( customUpdate(page.getAdjustedRetailVeryGood(),testDrSts.signDet.getAdjustedRetailVeryGood()))")
    @Mapping(target = "signDet.adjustedRetailExcelnt", expression = "java( customUpdate(page.getAdjustedRetailExcelnt(),testDrSts.signDet.getAdjustedRetailExcelnt()))")

    @Mapping(target = "signDet.adjustedWholePoor", expression = "java( customUpdate(page.getAdjustedWholePoor(),testDrSts.signDet.getAdjustedWholePoor()))")
    @Mapping(target = "signDet.adjustedWholeFair", expression = "java( customUpdate(page.getAdjustedWholeFair(),testDrSts.signDet.getAdjustedWholeFair()))")
    @Mapping(target = "signDet.adjustedWholeGood", expression = "java( customUpdate(page.getAdjustedWholeGood(),testDrSts.signDet.getAdjustedWholeGood()))")
    @Mapping(target = "signDet.adjustedWholeVeryGood", expression = "java( customUpdate(page.getAdjustedWholeVeryGood(),testDrSts.signDet.getAdjustedWholeVeryGood()))")
    @Mapping(target = "signDet.adjustedWholeExcelnt", expression = "java( customUpdate(page.getAdjustedWholeExcelnt(),testDrSts.signDet.getAdjustedWholeExcelnt()))")
    @Mapping(target = "signDet.adjustedFinanPoor", expression = "java( customUpdate(page.getAdjustedFinanPoor(),testDrSts.signDet.getAdjustedFinanPoor()))")
    @Mapping(target = "frDrSideDmgTxtBox",expression = "java( customUpdate(page.getFrDrSideDmgTxtBox(),testDrSts.getFrDrSideDmgTxtBox()))")
    @Mapping(target = "aprVehImg.frDrSideDmgPic",ignore = true)
    @Mapping(target = "aprVehImg.rearPassenSideDmgPic",ignore = true)
    @Mapping(target = "aprVehImg.frPassenSideDmgPic",ignore = true)
    @Mapping(target = "frDrSidePntWrkTxtBox",expression = "java( customUpdate(page.getFrDrSidePntWrkTxtBox(),testDrSts.getFrDrSidePntWrkTxtBox()))")
    @Mapping(target = "aprVehImg.frDrSidePntWrkPic",ignore = true)
    @Mapping(target = "aprVehImg.frPassenSidePntWrkPic",ignore = true)
    @Mapping(target = "aprVehImg.rearDrSidePntWrkPic",ignore = true)
    @Mapping(target = "rearPassenSidePntWrk",expression = "java( customUpdate(page.getRearPassenSidePntWrk(),testDrSts.getRearPassenSidePntWrk()))")
    @Mapping(target = "rearPassenSidePntWrkTxtBox",expression = "java( customUpdate(page.getRearPassenSidePntWrkTxtBox(),testDrSts.getRearPassenSidePntWrkTxtBox()))")
    @Mapping(target = "aprVehImg.rearPassenSidePntWrkPic",ignore = true)
    @Mapping(target = "aprVehImg.rearDrSideDmgPic",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic1",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic2",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic3",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic4",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic5",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic6",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic7",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic8",ignore = true)
    @Mapping(target = "aprVehImg.vehiclePic9",ignore = true)
    @Mapping(target = "aprVehImg.vehicleVideo1",ignore = true)
    EApprTestDrSts updateEApprTestDrSts(ApprCreaPage page, @MappingTarget EApprTestDrSts testDrSts);


    /**
     * If oldValue and newValue are not same then returns newValue else returns oldValue
     *
     * @param newValue This is new field
     * @param oldValue This is old field
     * @return String
     */
    default String customUpdate(String newValue, String oldValue) {

    if(Boolean.FALSE.equals(compareValues(oldValue,newValue))) {
        log.info("value is change from {} to {}",oldValue,newValue);
        return newValue;
    }

      else return oldValue;


    }

    /**
     * If oldValue and newValue are not same then returns newValue else returns oldValue
     * @param newValue This is new field
     * @param oldValue This is old field
     * @return Double
     */
    default Double customUpdate(Double newValue,Double oldValue) {

        if(Boolean.FALSE.equals((compareValues(oldValue,newValue)))) {
            log.info("value is change from {} to {}",oldValue,newValue);
            return newValue;
        }

        else return oldValue;


    }

    default Boolean customUpdate(Boolean newValue,Boolean oldValue) {

        if(Boolean.FALSE.equals(compareValues(oldValue,newValue))) {
            log.info("value is change from {} to {}",oldValue,newValue);
            return newValue;
        }

        else return oldValue;


    }

    /**
     * If oldValue and newValue are not same then returns newValue else returns oldValue
     * @param newValue This is new field
     * @param oldValue This is old field
     * @return Double
     */
    default Long customUpdate(Long newValue,Long oldValue) {

        if(!compareValues(oldValue,newValue)) {
            log.info("value is change from  {} to {}",oldValue,newValue);
            return newValue;
        }

        else return oldValue;


    }

    /**
     *Returns true when oldValue and newValue are not same
     * @param oldValue This is old field
     * @param newValue This is new field
     * @return Boolean
     */
    default Boolean compareValues(String oldValue, String newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }

    default Boolean compareValues(Boolean oldValue, Boolean newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }

    /**
     *Returns true when oldValue and newValue are not same
     * @param oldValue This is old field
     * @param newValue This is new field
     * @return Boolean
     */
    default Boolean compareValues(Double oldValue, Double newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue && null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }

    /**
     * If oldValue and newValue are not same then returns newValue else returns oldValue
     * @param newValue This is new field
     * @param oldValue This is old field
     * @return Double
     */
    default boolean compareValues(Long oldValue, Long newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }


    /**
     * If external damage status is true then returns filed else returns null
     * @param sts This is external damage status
     * @param field  This is the filed
     * @return String
     */
    default String externalDmgSts(Boolean sts,String field){
        if(null!=sts && sts && null != field ) {
            return field;
        }
        return null;
    }

    /**
     * If paint Work status  is true then returns filed else returns null
     * @param sts This is paint work status
     * @param field This is the filed
     * @return String
     */
    default String paintWork(Boolean sts, String field){
        if (null!=sts && sts && null != field) {
            return field;
        }
        return null;
    }


    default String customDate(Date date)  {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM yyyy");
        String format = dateFormat.format(date);
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
        String format1 = timeFormat.format(date);
        return format+" "+format1;
    }

    /**
     * This method updates the EUserRegistration object
     * @param userRegistration This is the object of UserRegistration dto
     * @param user This is the object of EUserRegistration entity
     * @author YogeshKumarV
     * @return  EUserRegistration
     */
    @Mapping(target="apartmentNumber", expression = "java(customUpdate(userRegistration.getApartmentNumber(),user.getApartmentNumber()))")
    @Mapping(target="city", expression = "java(customUpdate(userRegistration.getCity(),user.getCity()))")
    @Mapping(target="email", expression = "java(customUpdate(userRegistration.getEmail(),user.getEmail()))")
    @Mapping(target="firstName", expression = "java(customUpdate(userRegistration.getFirstName(),user.getFirstName()))")
    @Mapping(target="lastName",expression = "java(customUpdate(userRegistration.getLastName(),user.getLastName()))")
    @Mapping(target="password", expression = "java(customUpdate(userRegistration.getPassword(),user.getPassword()))")
    @Mapping(target="phoneNumber", expression = "java(customUpdate(userRegistration.getPhoneNumber(),user.getPhoneNumber()))")
    @Mapping(target="state", expression = "java(customUpdate(userRegistration.getState(),user.getState()))")
    @Mapping(target="streetAddress", expression = "java(customUpdate(userRegistration.getStreetAddress(),user.getStreetAddress()))")
    @Mapping(target = "userName",ignore = true)
    @Mapping(target="zipCode", expression = "java(customUpdate(userRegistration.getZipCode(),user.getZipCode()))")
    @Mapping(target = "id" ,ignore = true)
    EUserRegistration updateEUserRegisteration(UserRegistration userRegistration,@MappingTarget EUserRegistration user);

    @Mapping(target = "codeType",expression = "java(customUpdate(configCodes.getCodeType(),config.getCodeType()))")
    @Mapping(target = "shortCode",expression = "java(customUpdate(configCodes.getShortCode(),config.getShortCode()))")
    @Mapping(target = "longCode",expression = "java(customUpdate(configCodes.getLongCode(),config.getLongCode()))")
    @Mapping(target = "shortDescrip",expression = "java(customUpdate(configCodes.getShortDescrip(),config.getShortDescrip()))")
    @Mapping(target = "longDescrip",expression = "java(customUpdate(configCodes.getLongDescrip(),config.getLongDescrip()))")
    @Mapping(target = "configGroup",expression = "java(customUpdate(configCodes.getConfigGroup(),config.getConfigGroup()))")
    @Mapping(target = "id",ignore = true)
    EConfigCodes updateEConfigCodes(ConfigDropDown configCodes,@MappingTarget EConfigCodes config);

    @Mapping(target="role",expression = "java(customUpdate(role.getRole(),eRole.getRole()))")
    @Mapping(target="roleDesc",expression = "java(customUpdate(role.getRoleDesc(),eRole.getRoleDesc()))")
    @Mapping(target="roleGroup",expression = "java(customUpdate(role.getRoleGroup(),eRole.getRoleGroup()))")
    @Mapping(target = "id",ignore = true)
    ERole updateERole(Role role,@MappingTarget ERole eRole);


    /**
     * This method updates the EDealerRegistration object
     * @param dealerRegistration This is the object of DealerRegistration dto
     * @param dealer This is the object of EDealerRegistration entity
     * @author YogeshKumarV
     * @return  EDealerRegistration
     */
    @Mapping(target="name",expression = "java(customUpdate(dealerRegistration.getName(),dealer.getName()))")
    @Mapping(target="firstName",expression = "java(customUpdate(dealerRegistration.getFirstName(),dealer.getFirstName()))")
    @Mapping(target="lastName",expression = "java(customUpdate(dealerRegistration.getLastName(),dealer.getLastName()))")
    @Mapping(target="aptmentNumber",expression = "java(customUpdate(dealerRegistration.getAptmentNumber(),dealer.getAptmentNumber()))")
    @Mapping(target="city",expression = "java(customUpdate(dealerRegistration.getCity(),dealer.getCity()))")
    @Mapping(target="email",expression = "java(customUpdate(dealerRegistration.getEmail(),dealer.getEmail()))")
    @Mapping(target="password",expression = "java(customUpdate(dealerRegistration.getPassword(),dealer.getPassword()))")
    @Mapping(target="phoneNumber",expression = "java(customUpdate(dealerRegistration.getPhoneNumber(),dealer.getPhoneNumber()))")
    @Mapping(target="state",expression = "java(customUpdate(dealerRegistration.getState(),dealer.getState()))")
    @Mapping(target="streetAddress",expression = "java(customUpdate(dealerRegistration.getStreetAddress(),dealer.getStreetAddress()))")
    @Mapping(target="zipCode",expression = "java(customUpdate(dealerRegistration.getZipCode(),dealer.getZipCode()))")
    @Mapping(target="latitude",expression = "java(customUpdate(dealerRegistration.getLatitude(),dealer.getLatitude()))")
    @Mapping(target="longitude",expression = "java(customUpdate(dealerRegistration.getLongitude(),dealer.getLongitude()))")
    @Mapping(target="dealershipNames",expression = "java(customUpdate(dealerRegistration.getDealershipNames(),dealer.getDealershipNames()))")
    @Mapping(target="dealershipAddress",expression = "java(customUpdate(dealerRegistration.getDealershipAddress(),dealer.getDealershipAddress()))")
    @Mapping(target="dealershipStreet",expression = "java(customUpdate(dealerRegistration.getDealershipStreet(),dealer.getDealershipStreet()))")
    @Mapping(target="dealershipCity",expression = "java(customUpdate(dealerRegistration.getDealershipCity(),dealer.getDealershipCity()))")
    @Mapping(target="dealershipZipCode",expression = "java(customUpdate(dealerRegistration.getDealershipZipCode(),dealer.getDealershipZipCode()))")
    @Mapping(target="dealershipPhNum",expression = "java(customUpdate(dealerRegistration.getDealershipPhNum(),dealer.getDealershipPhNum()))")
    @Mapping(target="dealerPic",ignore = true)
    @Mapping(target="taxCertificate",ignore = true)
    @Mapping(target = "dealerCert",ignore = true)
    @Mapping(target="corporationName",expression = "java(customUpdate(dealerRegistration.getCorporationName(),dealer.getCorporationName()))")
    EDealerRegistration updEDlrReg(DealerRegistration dealerRegistration,@MappingTarget EDealerRegistration dealer);


    @Mapping(target = "userName",source = "name")
    @Mapping(target = "id",ignore = true)
     EUserRegistration updateEUserReg(EDealerRegistration dealer,@MappingTarget EUserRegistration user);
    /**
     * this method maps CompDetails to ECompDetails
     * @param compDetails
     * @author yudhister
     * @return ECompany
     */
    ECompany compDetsToECompDets(Company compDetails);

    Company eCompDetailsToCompDetails(ECompany eCompany);

    List<Company> leCompDetailsTolCompDetails(List<ECompany> eCompanies);



    /**
     * this method updates company details
     * @param compDetails
     * @param company
     * @author yudhister
     * @return ECompDetails
     */
    @Mapping(target = "name", expression = "java(customUpdate(compDetails.getName(), company.getName()))")
    @Mapping(target = "groupName", expression = "java(customUpdate(compDetails.getGroupName(), company.getGroupName()))")
    @Mapping(target = "address", expression = "java(customUpdate(compDetails.getAddress(), company.getAddress()))")
    @Mapping(target = "phNumber", expression = "java(customUpdate(compDetails.getPhNumber(), company.getPhNumber()))")
    @Mapping(target = "emailId", expression = "java(customUpdate(compDetails.getEmailId(), company.getEmailId()))")
    ECompany updateCompDetails(Company compDetails, @MappingTarget ECompany company);


    EStatus statusToEStatus(Status statuses);
    /**
     * this method maps list of Status to Estatus
     * @param statuses
     * @author yudhister
     * @return
     */
    List<EStatus> lStatusToEStatus(List<Status> statuses);


    /** this method updates status table
     * @param status
     * @param sts
     * @author yudhister
     * @return EStatus
     */
    @Mapping(target="buyerStatus", expression = "java(customUpdate(status.getBuyerStatus(),sts.getBuyerStatus()))")
    @Mapping(target="sellerStatus", expression = "java(customUpdate(status.getSellerStatus(),sts.getSellerStatus()))")
    @Mapping(target="offerStatus", expression = "java(customUpdate(status.getOfferStatus(),sts.getOfferStatus()))")
    @Mapping(target="color", expression = "java(customUpdate(status.getColor(),sts.getColor()))")
    @Mapping(target ="statusCode", expression = "java(customUpdate(status.getStatusCode(),sts.getStatusCode()))")
    @Mapping(target="status", expression = "java(customUpdate(status.getStatus(),sts.getStatus()))")
    EStatus updateOfferStatus(Status status,@MappingTarget EStatus sts);


    /**
     * this method convert offer dto to EOffer
     * @param offers
     * @return EOffers
     */
    EOffers offersToEOffers(Offers offers);

    @Mapping(target = "apartmentNumber", source = "aptmentNumber")
    @Mapping(target = "userName",source = "name")
    EUserRegistration dealerRegToEUserReg(DealerRegistration dealerRegistration);


    default ConfigDropDown setTitelStsConfig(EAppraiseVehicle vehicle){
        if(null!=vehicle && null!=vehicle.getTdStatus()) {

            return eConfigCodesToConfigDropDown(vehicle.getTdStatus().getTitleSt());
        }
        return null;
    }


    EFtryTraining ftryTrainToEFtryTrain(FtryTraining ftryTraining);

    @Mapping(source = "id",target = "factTranId")
    FtryTraining eFtryTrainToFtryTrain(EFtryTraining eFtryTraining);

    List<FtryTraining> eFtryTrainToFtryTrain(List<EFtryTraining> eFtryTraining);

    default Boolean  isShAvlbl(EShipment shipment){
        return (null!=shipment);
    }


  //  OBD2_PreStartMeasurement appCreaPageToPreStrt(ApprCreaPage apprCreaPage);


    OBD2_PreStartMeasurement appCreaPageToPreStrt(PrestartMeasurement preStart);
    OBD2_TestDriveMeasurements apprCreaPageToTestDriveMeas(TestDriveMes testDriveMes);
    List<OBD2_TestDriveMeasurements> apprCreaPageToLTestDriveMeas(List<TestDriveMes> testDriveMes);
    TestDriveMes testDriveMeasToApprCreaPage(OBD2_TestDriveMeasurements testDrive);
    List<TestDriveMes> lTestDriveMeaTolApprCreaPage(List<OBD2_TestDriveMeasurements> testDriveMeasurements);
    @Mapping(source = "userId",target = "memberId")
    @Mapping(target = "memberName",expression = "java(concatName(membersView.getMemberFirstName(),membersView.getMemberLastName()))")
    @Mapping(source = "streetAddress",target = "location")
    @Mapping(target = "signUpDate",constant = "")
    @Mapping(source = "role",target = "memberType")
    @Mapping(target = "factorySalesMan",expression = "java(concatName(membersView.getFactorySalesmanFirstName(),membersView.getFactorySalesmanLastName()))")
    @Mapping(target = "factoryMgr",expression = "java(concatName(membersView.getFactoryManagerFirstName(),membersView.getFactoryManagerLastName()))")
    @Mapping(target = "totalRev",constant = "")
    FactoryReport totalMembersToFactoryRpt( TotalMembersView membersView);
    List< FactoryReport> totalMembersToFactoryRpt( List<TotalMembersView> membersView);

    @Mapping(source = "userId",target = "memberId")
    @Mapping(target = "memberName",expression = "java(concatName(membersView.getFirstName(),membersView.getLastName()))")
    @Mapping(source = "streetAddress",target = "location")
    //@Mapping(source = "signFrom",target = "signUpDate")
    //@Mapping(source = "amount",target = "totalRev")
    FactoryReport membersToFactoryRpt( MembersByFactorySalesmen membersView);
    List< FactoryReport> membersToFactoryRpt(List< MembersByFactorySalesmen> membersView);
    @Mapping(source = "userId",target = "memberId")
    @Mapping(target = "memberName",expression = "java(concatName(membersView.getFirstName(),membersView.getLastName()))")
    @Mapping(source = "streetAddress",target = "location")
    //@Mapping(source = "signFrom",target = "signUpDate")
    //@Mapping(source = "amount",target = "totalRev")
    FactoryReport managersMembersToFactoryRpt( MembersByFactoryManager membersView);
    List< FactoryReport> managersMembersToFactoryRpt(List< MembersByFactoryManager> membersView);
    default String concatName(String first, String last){
        if(first==null && last ==null) {first="";}
        return first+" "+last;
    }
    @Mapping(target = "userId", source = "userId")
    SellingDealer eTranReportToSellingDealer(TransactionReport all);
    List<SellingDealer> lTransaRepToSellingDealer(List<TransactionReport> all);

    @Mapping(source = "fmFirstName",target = "firstName")
    @Mapping(source = "fmLastName",target = "lastName")
    @Mapping(source = "factoryManager",target = "userId")
    SellingDealer eMangerRepToSellingDealer(MembersByFactoryManager all);
    List<SellingDealer> lMangerRepToSellingDealer(List<MembersByFactoryManager> all);
    @Mapping(source = "fsFirstName",target = "firstName")
    @Mapping(source = "fsLastName",target = "lastName")
    @Mapping(source = "factorySalesman",target = "userId")
    SellingDealer eSaleToSellingDealer(MembersByFactorySalesmen all);
    List<SellingDealer> lSaleToSellingDealer(List<MembersByFactorySalesmen> all);

    @Mapping(target = "compName",expression = "java(findNames(dealersView.getCompanyId(),companyRepo))")
    @Mapping(target = "fsFirstName",expression = "java(findNames(dealersView.getFactorySalesman(),userRegRepo))")
    @Mapping(target = "fmFirstName",expression = "java(findNames(dealersView.getFactoryManager(),userRegRepo))")
    @Mapping(target = "mngFirstName",expression = "java(findNames(dealersView.getManagerId(),userRegRepo))")
    DealerRegistration allDealersViewToDealerReg(AllDealersView dealersView, UserRegistrationRepo userRegRepo, CompanyRepo companyRepo) ;

    public default String chkCompName(ECompany compId) {
        String val;
        if (null != compId) {
            val = compId.getName();
        } else {
            val = null;
        }
        return val;
    }
    public default Long chkCompId(ECompany compId) {
        Long val;
        if (null != compId) {
            val = compId.getId();
        } else {
            val = null;
        }
        return val;
    }
    default String findNames(UUID userId, UserRegistrationRepo userRegRepo) {
        if(null!=userId && null!=userRegRepo){
            EUserRegistration userById = userRegRepo.findUserById(userId);
            return userById.getFirstName()+ " "+userById.getLastName();
        }
        return "";
    }
    default String findNames(Long compId, CompanyRepo companyRepo) {
        if(null!=compId && null!= companyRepo){
            ECompany companyId = companyRepo.findByCompanyId(compId);
            return companyId.getName();
        }
        return "";
    }


    @Mapping(source = "firstName", target = "name.givenName")
    @Mapping(source = "lastName", target = "name.familyName")
    @Mapping(source = "userName",target = "userName")
    @Mapping(source = "password", target = "password")
    Users EUserRegToUsers(EUserRegistration e);


    @Mapping(target = "id", source = "userId")
    UserRegistration userListViewToUserReg(UserListView all1);
    List<UserRegistration> lUserListViewToUserReg(List<UserListView> all1);




    @Mapping(source = "vin",target = "vinNumber")
    @Mapping(source = "refPrice",target = "delrRetlAskPrice")
    @Mapping(source = "miles",target = "vehicleMileage")
   /* @Mapping(target = "vehicleExtColor",expression = "java()")
    @Mapping(target = "vehicleInterior",expression = "java()")*/
    @Mapping(source = "year",target = "vehicleYear")
    @Mapping(source = "make",target = "vehicleMake")
    @Mapping(source = "model",target = "vehicleModel")
    @Mapping(target = "engineType",expression = "java(setEngineType(inventoryVehicles))")
    @Mapping(source = "transmission",target = "transmissionType")
    @Mapping(target = "vehicleSeries",expression = "java(setSeries(inventoryVehicles))")
    ApprCreaPage invToApprCreaPage(EInventoryVehicles inventoryVehicles);

    default String setEngineType(EInventoryVehicles inventoryVehicles){
        String engine = inventoryVehicles.getEngine();
        String fuelType = inventoryVehicles.getFuelType();

        if(null!=engine && null!=fuelType) {

            if (!engine.equals("")) {
                return engine;
            } else {
                return fuelType;
            }
        }
        return "";

    }

    default String setSeries(EInventoryVehicles inventoryVehicles){
        return  inventoryVehicles.getTrim()+" "+inventoryVehicles.getVehicleType()+" "+inventoryVehicles.getDrivetrain()+" "+inventoryVehicles.getBodyType();

    }
    default Boolean isPrivateParty(EAppraiseVehicle eAppraiseVehicle){
        return null == eAppraiseVehicle.getDealer();
    }

    @Mapping(target = "name",source = "userName")
    @Mapping(target = "id",ignore = true)
    EDealerRegistration eUserToEdealer(EUserRegistration userById);
}
