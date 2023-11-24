package com.massil.persistence.mapper;

import com.massil.dto.*;
import com.massil.dto.OfferReport;
import com.massil.persistence.model.*;
import com.massil.persistence.model.TransactionReport;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;

@Mapper(componentModel = "spring")
public interface OffersMapper {
    Logger log = LoggerFactory.getLogger(OffersMapper.class);

    /**
     *This method mapping EAppraisal vehicle to OfferCards
     * @param eAppraiseVehicle
     * @return OffersCards
     */
    @Mapping(target="isVehicleFav",expression = "java(checkFavVehi(eAppraiseVehicle,userId))")
    @Mapping(target = "createdOn",expression = "java(customDate(eAppraiseVehicle.getCreatedOn()))")
    @Mapping(target = "modifiedOn",expression ="java(customDate(eAppraiseVehicle.getModifiedOn()))")
    @Mapping(target = "vehiclePic1",source = "eAppraiseVehicle.tdStatus.aprVehImg.vehiclePic1")
    @Mapping(target = "titleSts",expression = "java(setTitelStsConfig(eAppraiseVehicle))")
    @Mapping(target = "isOfferMade",expression = "java(setIsOfferMade(eAppraiseVehicle.getOffers(),allUsersUnderDealer))")
    AppraisalVehicleCard eApprVehiToOffersCards(EAppraiseVehicle eAppraiseVehicle, UUID userId,List<UUID> allUsersUnderDealer);



    /**
     *This method mapping Eoofer to OfferCards
     * @param eOffers
     * @return
     */
    @Mapping(source = "appRef.vehicleMake",target = "vehicleMake")
    @Mapping(source = "appRef.vehicleModel",target = "vehicleModel")
    @Mapping(source = "appRef.vehicleYear",target = "vehicleYear")
    @Mapping(source = "appRef.vehicleMileage",target = "vehicleMileage")
    @Mapping(source = "appRef.vehicleSeries",target = "vehicleSeries")
    @Mapping(target = "titleSts",expression = "java(setTitelStsConfig(eOffers.getAppRef()))")
    @Mapping(target = "offerStatus",expression = "java(setOfferStatus(eOffers))")
    @Mapping(source = "status.color",target = "color")
    @Mapping(source = "appRef.id",target = "apprRef")
    @Mapping(source = "id",target = "offerId")
    @Mapping(source = "status.id",target = "statusCodeId")
    @Mapping(source = "appRef.tdStatus.aprVehImg.vehiclePic1",target = "vehiclePic1")
    @Mapping(target = "createdOn",expression ="java(customDate(eOffers.getCreatedOn()))")
    @Mapping(target = "modifiedOn",expression ="java(customDate(eOffers.getModifiedOn()))")
    @Mapping(source = "status",target = "status")
    @Mapping(source = "shipment.id", target = "shipmentId")
    @Mapping(source = "shipment.buyerAgreed", target = "buyerAgreed")
    @Mapping(source = "shipment.sellerAgreed", target = "sellerAgreed")
    @Mapping(source = "appRef.dealerReserve",target = "dealerReserve")
    @Mapping (target = "dsName",source = "appRef.dlrsUserNames.userName")
    @Mapping(target = "role",expression = "java(setRoleOfCreator(eOffers.getAppRef().getUser().getRoleMapping()))")
    AppraisalVehicleCard eoffersToOffersCards(EOffers eOffers);

    Role eRoleToRole(ERole role);

    default Role setRoleOfCreator(List<ERoleMapping> roleMapping){
        if(!roleMapping.isEmpty()){
            ERoleMapping eRoleMapping = roleMapping.get(0);
            ERole role = eRoleMapping.getRole();
            return eRoleToRole(role);
        }
        return null;
    }

    /**
     *This method mpping list of Eoffers to OfferCards
     * @param eOffers
     * @return
     */
    List<AppraisalVehicleCard> lEoffersToOffersCards(List<EOffers> eOffers);


    /**
     * This method map EOffers to offerList
     * @param eOffers
     * @param user
     * @return
     */

    @Mapping(source = "user.firstName",target = "firstName")
    @Mapping(source = "user.lastName",target = "lastName")
    @Mapping(source = "eOffers.id",target = "offerId")
    OfferList eOffersToOffersList(EOffers eOffers, EUserRegistration user);



    /**
     * This method mapping EStatus to Status
     * @param status
     * @return Status
     */
     Status eStatusToStatus(EStatus status);

    /**
     *This method check favorite vehicle
     * @param eAppraiseVehicle
     * @return boolean
     */
    default Boolean checkFavVehi(EAppraiseVehicle eAppraiseVehicle,UUID userId){

        if(null!=eAppraiseVehicle && null!=eAppraiseVehicle.getWishlist()) {
            ArrayList<EUserWishlist> wishlist= new ArrayList<>(eAppraiseVehicle.getWishlist());

            for(EUserWishlist v:wishlist){
                if(Objects.equals(v.getAppRef().getId(), eAppraiseVehicle.getId()) && v.getValid() && v.getUser().getId().equals(userId)) {
                    return true;
                }
            }
        }

    return false;
    }


    default String customDate(Date date)  {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM yyyy");
        String format = dateFormat.format(date);
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
        String format1 = timeFormat.format(date);
        return format+" "+format1;
    }

    default String setOfferStatus(EOffers eOffers){
        if(null!=eOffers && null!=eOffers.getAppRef() && null!=eOffers.getStatus() ){

            if((eOffers.getAppRef().getField1() || eOffers.getAppRef().getField2() )){
                return "sold";
            }else {
                return eOffers.getStatus().getOfferStatus();
            }
        }

       return "";
    }






    @Mapping(target = "vehExtColor",expression ="java(setExteriorColor(vehicle.getTdStatus().getExtrColor()))")
    PdfData EAppraisalToPdfData(EAppraiseVehicle vehicle);

    @Mapping(target = "slrName",expression ="java(checkDbVariable(offers.getSellerUserId().getFirstName()))")
    @Mapping(target = "slrState",expression ="java(checkDbVariable(offers.getSellerUserId().getState()))")
    @Mapping(target = "slrAddress",expression ="java(checkDbVariable(offers.getSellerUserId().getStreetAddress()))")
    @Mapping(target = "slrMail",expression ="java(checkDbVariable(offers.getSellerUserId().getEmail()))")
    @Mapping(target = "buyerName",expression ="java(checkDbVariable(offers.getBuyerUserId().getFirstName()))")
    @Mapping(target = "buyerAddress",expression ="java(checkDbVariable(offers.getBuyerUserId().getStreetAddress()))")
    @Mapping(target = "buyerMail",expression ="java(checkDbVariable(offers.getBuyerUserId().getEmail()))")
    @Mapping(target = "slrUserName",expression ="java(checkDbVariable(offers.getSellerUserId().getUserName()))")
    @Mapping(target = "slrCity",expression ="java(checkDbVariable(offers.getSellerUserId().getCity()))")
    @Mapping(target = "slrZip",expression ="java(checkDbVariable(offers.getSellerUserId().getZipCode()))")
    @Mapping(target = "byrState",expression ="java(checkDbVariable(offers.getBuyerUserId().getState()))")
    @Mapping(target = "byrUserName",expression ="java(checkDbVariable(offers.getBuyerUserId().getUserName()))")
    @Mapping(target = "byrCity",expression ="java(checkDbVariable(offers.getBuyerUserId().getCity()))")
    @Mapping(target = "byrZip",expression ="java(checkDbVariable(offers.getBuyerUserId().getZipCode()))")
    @Mapping(target = "price",expression ="java(checkDbVariable(offers.getPrice()))")
    PdfData offersToPdfData(EOffers offers, @MappingTarget PdfData dto);

    default String setExteriorColor(EConfigCodes configCodes){
        if(null!=configCodes){
            return  configCodes.getShortDescrip();
        }
        return "Null";
    }

    default String checkDbVariable(String dbValue) {
        String val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = "";
        }
        return val;

    }

    default Boolean checkDbVariable(Boolean dbValue) {
        Boolean val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = false;
        }
        return val;
    }

    /**
     * This method check the database values
     * @param dbValue
     * @return val
     */

    default Long checkDbVariable(Long dbValue) {
        Long val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = 0L;
        }
        return val;

    }

    /**
     * This method check the database values
     * @param dbValue
     * @return val
     */

    default Double checkDbVariable(Double dbValue) {
        Double val;
        if (null != dbValue) {
            val = dbValue;
        } else {
            val = 0.0;
        }
        return val;
    }
    /**
     * This method maps EConfigurationCodes to ConfigDropDown
     * @param configCodes
     * @return ConfigDropDown
     */
    ConfigDropDown eConfigCodesToConfigDropDown(EConfigCodes configCodes);

    default ConfigDropDown setTitelStsConfig(EAppraiseVehicle vehicle){
        if(null!=vehicle && null!=vehicle.getTdStatus()) {

            return eConfigCodesToConfigDropDown(vehicle.getTdStatus().getTitleSt());
        }
        return null;
    }


    @Mapping(target = "extrColor", expression = "java(setConfigCodes(vehicle.getTdStatus().getExtrColor()))")
    @Mapping(target = "intrColor", expression = "java(setConfigCodes(vehicle.getTdStatus().getIntrColor()))")
    @Mapping(target = "flWinStatus", expression = "java(setConfigCodes(vehicle.getTdStatus().getFLWinStatus()))")
    @Mapping(target = "frWinStatus", expression = "java(setConfigCodes(vehicle.getTdStatus().getFRWinStatus()))")
    @Mapping(target = "rlWinStatus", expression = "java(setConfigCodes(vehicle.getTdStatus().getRLWinStatus()))")
    @Mapping(target = "rrWinStatus", expression = "java(setConfigCodes(vehicle.getTdStatus().getRRWinStatus()))")
    @Mapping(target = "vehicleMileage", expression = "java(checkDbVariable(String.valueOf(vehicle.getVehicleMileage())))")
    @Mapping(target = "firstName", expression = "java(checkDbVariable(vehicle.getUser().getFirstName()))")
    @Mapping(target = "lastName", expression = "java(checkDbVariable(vehicle.getUser().getLastName()))")
    @Mapping(target = "phoneNumber", expression = "java(checkDbVariable(vehicle.getUser().getPhoneNumber()))")
    @Mapping(target = "email", expression = "java(checkDbVariable(vehicle.getUser().getEmail()))")
    @Mapping(target = "address", expression = "java(checkDbVariable(vehicle.getUser().getApartmentNumber()))")
    @Mapping(target = "city", expression = "java(checkDbVariable(vehicle.getUser().getCity()))")
    @Mapping(target = "state", expression = "java(checkDbVariable(vehicle.getUser().getState()))")
    @Mapping(target = "zipCode", expression = "java(checkDbVariable(vehicle.getUser().getZipCode()))")
    @Mapping(target = "externalDmgSts", expression = "java(checkDbVariable(String.valueOf(vehicle.getTdStatus().getExternalDmgSts())))")
    @Mapping(target = "windShieldDmg", expression = "java(setConfigCodes(vehicle.getTdStatus().getWindShieldDmg()))")
    @Mapping(target = "titleSt", expression = "java(setConfigCodes(vehicle.getTdStatus().getTitleSt()))")
    @Mapping(target = "engineType", expression = "java(checkDbVariable(vehicle.getTdStatus().getEngineType()))")
    @Mapping(target = "transmissionType", expression = "java(checkDbVariable(vehicle.getTdStatus().getTransmissionType()))")
    @Mapping(target = "frDrSideDmgTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrDrSideDmgTxtBox()))")
    @Mapping(target = "frPassenSideDmgTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrPassenSideDmgTxtBox()))")
    @Mapping(target = "frDrSidePntWrkTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrDrSidePntWrkTxtBox()))")
    @Mapping(target = "frPassenSidePntWrkTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrPassenSidePntWrkTxtBox()))")
    @Mapping(target = "rearDrSidePntWrkTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearDrSidePntWrkTxtBox()))")
    @Mapping(target = "rearPassenSidePntWrkTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearPassenSidePntWrkTxtBox()))")
    @Mapping(target = "rearDrSideDmgTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearDrSideDmgTxtBox()))")
    @Mapping(target = "rearPassenSideDmgTxtBox", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearPassenSideDmgTxtBox()))")
    @Mapping(target = "frDrSideDmgSts", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrDrSideDmgSts()))")
    @Mapping(target = "frPassenSideDmgSts", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrPassenSideDmgSts()))")
    @Mapping(target = "frDrSidePntWrkSts", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrDrSidePntWrkSts()))")
    @Mapping(target = "frPassenSidePntWrk", expression = "java(checkDbVariable(vehicle.getTdStatus().getFrPassenSidePntWrk()))")
    @Mapping(target = "rearDrSidePntWrk", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearDrSidePntWrk()))")
    @Mapping(target = "rearPassenSidePntWrk", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearPassenSidePntWrk()))")
    @Mapping(target = "rearDrSideDmgSts", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearDrSideDmgSts()))")
    @Mapping(target = "rearPassenSideDmgSts", expression = "java(checkDbVariable(vehicle.getTdStatus().getRearPassenSideDmgSts()))")
    @Mapping(target = "roofTypes", expression = "java(setConfigCodes(vehicle.getTdStatus().getRoofTypes()))")
    @Mapping(target = "doorLock", expression = "java(setConfigCodes(vehicle.getTdStatus().getDoorLock()))")
    @Mapping(target = "vehiclePic1",expression = "java(vehicle.getTdStatus().getAprVehImg().getVehiclePic1())")
    @Mapping(target = "frDrSideDmgPic",expression = "java(vehicle.getTdStatus().getAprVehImg().getFrDrSideDmgPic())")
    @Mapping(target = "frPassenSideDmgPic",expression = "java(vehicle.getTdStatus().getAprVehImg().getFrPassenSideDmgPic())")
    @Mapping(target = "rearDrSideDmgPic",expression = "java(vehicle.getTdStatus().getAprVehImg().getRearDrSideDmgPic())")
    @Mapping(target = "rearPassenSideDmgPic",expression = "java(vehicle.getTdStatus().getAprVehImg().getRearPassenSideDmgPic())")
    @Mapping(target = "price",expression = "java(String.valueOf(checkDbVariable(vehicle.getAppraisedValue())))")
    @Mapping(target = "date", expression = "java(checkDbVariable(String.valueOf(vehicle.getModifiedOn())))")
    ApprFormDto apprToPdf(EAppraiseVehicle vehicle);

    ApprFormDto apprToPdfSlctMny(ApprFormSelectManyDto apprFormSltMnyDto,@MappingTarget ApprFormDto apprFormPdf);

    default String setConfigCodes(EConfigCodes configCodes){
        if(null!=configCodes){
            return configCodes.getShortDescrip();
        }
        return "Null";
    }

    @Mapping(target = "vehExtColor", expression = "java(setExteriorColor(vehicle.getTdStatus().getExtrColor()))")
    @Mapping(target = "intrColor", expression = "java(setExteriorColor(vehicle.getTdStatus().getIntrColor()))")
    PdfDataDto apprToPdfData(EAppraiseVehicle vehicle);
    
    
    TestDriveMes OBD2TestDrToTestDrMeas(OBD2_TestDriveMeasurements test);
    List<TestDriveMes> lOBD2TolTestDrMeas(List<OBD2_TestDriveMeasurements> test);

    List<PdfDataDto> lEApprToPdfDataDto(List<EAppraiseVehicle>appraisalList);

    @Mapping(source = "role",target = "membertype")
    @Mapping(source = "userId",target = "memberId")
    //@Mapping(source = "",target = "searchby")
    PdfDataDto lApprFormViewToPdfDataDto(AppraisalFormView appraisalList);
    List<PdfDataDto> lApprFormViewToPdfDataDto(List<AppraisalFormView>appraisalList);

    @Mapping(target = "slrName", expression = "java(checkDbVariable(offers.getSellerUserId().getFirstName()))")
    @Mapping(target = "slrState", expression = "java(checkDbVariable(offers.getSellerUserId().getState()))")
    @Mapping(target = "slrAptNum", expression = "java(checkDbVariable(offers.getSellerUserId().getApartmentNumber()))")
    @Mapping(target = "slrCity", expression = "java(checkDbVariable(offers.getSellerUserId().getCity()))")
    @Mapping(target = "slrMail", expression = "java(checkDbVariable(offers.getSellerUserId().getEmail()))")
    @Mapping(target = "slrPhNm", expression = "java(checkDbVariable(offers.getSellerUserId().getPhoneNumber()))")
    @Mapping(target = "slrZip", expression = "java(checkDbVariable(offers.getSellerUserId().getZipCode()))")
    @Mapping(target = "slrStrtAdr", expression = "java(checkDbVariable(offers.getSellerUserId().getStreetAddress()))")
    @Mapping(target = "buyerName", expression = "java(checkDbVariable(offers.getBuyerUserId().getFirstName()))")
    @Mapping(target = "buyerState", expression = "java(checkDbVariable(offers.getBuyerUserId().getState()))")
    @Mapping(target = "buyerAptNum", expression = "java(checkDbVariable(offers.getBuyerUserId().getApartmentNumber()))")
    @Mapping(target = "buyerCity", expression = "java(checkDbVariable(offers.getBuyerUserId().getCity()))")
    @Mapping(target = "buyerMail", expression = "java(checkDbVariable(offers.getBuyerUserId().getEmail()))")
    @Mapping(target = "buyerPhNm", expression = "java(checkDbVariable(offers.getBuyerUserId().getPhoneNumber()))")
    @Mapping(target = "buyerZip", expression = "java(checkDbVariable(offers.getBuyerUserId().getZipCode()))")
    @Mapping(target = "buyerStrtAdr", expression = "java(checkDbVariable(offers.getBuyerUserId().getStreetAddress()))")
    @Mapping(target = "buyerSign", expression = "java(checkDbVariable(shipment.getBuyerSign()))")
    @Mapping(target = "sellerSign", expression = "java(checkDbVariable(shipment.getSellerSign()))")
    @Mapping(target = "buyerAgreed", expression = "java(checkDbVariable(shipment.getBuyerAgreed()))")
    @Mapping(target = "sellerAgreed", expression = "java(checkDbVariable(shipment.getSellerAgreed()))")
    PdfDataDto offersToPdf(EOffers offers,EShipment shipment,@MappingTarget PdfDataDto dto);

    @Mapping(target = "vinNumber", expression = "java(checkDbVariable(offers.getAppRef().getVinNumber()))")
    @Mapping(target = "vehicleMake", expression = "java(checkDbVariable(offers.getAppRef().getVehicleMake()))")
    @Mapping(target = "vehicleModel", expression = "java(checkDbVariable(offers.getAppRef().getVehicleModel()))")
    @Mapping(target = "vehicleYear", expression = "java(String.valueOf(checkDbVariable(offers.getAppRef().getVehicleYear())))")
    @Mapping(target = "vehicleMileage", expression = "java(String.valueOf(checkDbVariable(offers.getAppRef().getVehicleMileage())))")
    @Mapping(target = "slrName", expression = "java(checkDbVariable(offers.getSellerUserId().getFirstName()))")
    @Mapping(target = "vehExtColor", expression = "java(setConfigCodes(offers.getAppRef().getTdStatus().getExtrColor()))")
    @Mapping(target = "price",expression = "java(checkDbVariable(offers.getPrice()))")
    @Mapping(target = "offerStatus",expression = "java(String.valueOf(checkDbVariable(offers.getStatus().getOfferStatus())))")
    PdfDataDto EOffersToPdfDataDto(EOffers offers);
    List<PdfDataDto> lEOffersToPdfDataDto(List<EOffers> offers);


    default String concatName(String first, String last){
        String name=first+" "+last;
        return name;
    }

    @Mapping(target = "vehicleYear", source = "vehYear")
    @Mapping(target = "vehicleMake",source = "make")
    @Mapping(target = "vehicleModel",source = "model")
    @Mapping(target = "vehicleMileage",source = "miles")
    @Mapping(target = "vehExtColor",source = "colour")
    @Mapping(target = "slrName",expression = "java(concatName(tranList.getFirstName(),tranList.getLastName()))")
    @Mapping(target = "memberId",source = "userId")
    @Mapping(target = "offerStatus", source = "status")
    @Mapping(target = "offerId",source = "offerId")
    PdfDataDto transReportToPdfDataDto(TransactionReport tranList);
    List<PdfDataDto>lTransReportToPdfDataDto(List<TransactionReport> tranList);

    @Mapping(target = "sellername", expression = "java(concatName(offerList.getFirstName(),offerList.getLastName()))")
    @Mapping(target = "sellerId", source = "sellerUserId")
    @Mapping(target = "vin", source = "vin")
    @Mapping(target = "offerCount", source = "offerCount")
    OfferReport offerPdfToOfferReport(OfferPdf offerList);
    List<OfferReport> lOfferPdfToOfferReport(List<OfferPdf> offerList);


    default Long daysSinceInv(Date invntryDate){
        if(null!=invntryDate){
            return (new Date().getTime() - invntryDate.getTime()) /(24*60*60*1000);
        }
      return 0L;

    }
    @Mapping(target = "daysSinceInventory", expression = "java(daysSinceInv(vehicle.getInvntryDate()))")
    PdfDataDto dlrInvViewToPdfDataDto(DlrInvntryView vehicle);
    List<PdfDataDto> lDlrInvViewToPdfDataDto(List<DlrInvntryView> appraisalList);


    @Mapping(target = "saleDate",expression = "java(dateModification(vehicle.getSaleDate()))")
    PdfDataDto dlrSalesViewToPdfDataDto(DlrSalesView vehicle);
    List<PdfDataDto> lDlrSalesViewToPdfDataDto(List<DlrSalesView> offerList);

    DlrInvntryPdfFilter dlrInvViewToDlrFilterDto(DlrInvntryView vehicle);
    List<DlrInvntryPdfFilter> lDlrInvViewToDlrFilterDto(List<DlrInvntryView> appraisalList);

    PdfDataDto preStartToPdfDataDto(OBD2_PreStartMeasurement preStart,@MappingTarget PdfDataDto pdfDataDto);

    default String dateModification(Date date)  {
        SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM yyyy");
        return dateFormat.format(date);
    }

    default Boolean setIsOfferMade(List<EOffers> offers, List<UUID>allUsersUnderDealer){

        if(null!= offers && null!= allUsersUnderDealer && !offers.isEmpty() && !allUsersUnderDealer.isEmpty()){
            for (EOffers offers1:offers) {
                if(allUsersUnderDealer.contains( offers1.getBuyerUserId().getId())){
                    return true;
                }
            }
        }
        return false;
    }
}
