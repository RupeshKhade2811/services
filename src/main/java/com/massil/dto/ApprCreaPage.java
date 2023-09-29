package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

import jakarta.validation.constraints.*;
import java.util.List;
import java.util.UUID;

/**
 * This class is a DTO which is used to crete Appraisal page
 */



@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ApprCreaPage extends Response {

    //AppraiseVehicle
    //@NotNull(message = "client First Name should not be null")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Only letters and spaces are allowed")
    @Size(max=15)
    private String clientFirstName;
    @Size(max=15)
    //@NotNull(message = "client Last Name should not be null")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Only letters and spaces are allowed")
    private String clientLastName;

    //@NotNull(message = "phoneNumber should not be null")
    @Size(max = 13, message = "give 13 digit American phone number with +1 code sample example:+1 5551234567")
   // @Pattern(regexp = "^\\+1\\s\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$",message = "Inavalid Phone Number")
    private String clientPhNum;

    private UUID dealershipUserNames;

    @NotNull
    @Size(min = 17,max = 20,message = "Vin number must be 17 characters")
    private String vinNumber;
    @Max(value = 9999,message = "Year must be in YYYY format")
    @NotNull
    private Long vehicleYear;
    @Size(max=25,message = "length must be less than equal to 25 characters")
    @NotNull
    private String vehicleMake;

    @Size(max=50,message = "length must be less than equal to 50 characters")
    @NotNull
    //AppraisalVehicle
    private String vehicleModel;
    @Size(max=50,message = "length must be less than equal to 25 characters")
    @NotNull
    //AppraisalVehicle
    private String vehicleSeries;

    //testDrivests
    @NotNull
    @Max(value = 9999999999l,message = "Vehicle Mileage must be less than equal to 10 digits")
    private Long vehicleMileage;
    //AppraisalTestDriveSts
    @NotNull
    @Size(max=10 ,message = "engineType must be  less than equal to 10 characters")
    private String engineType;
    @NotNull
    @Size(max=10,message = "transmissionType must be less than equal to 10 characters ")
    private String transmissionType;

    //Test Drive Sts
    @NotNull
    private Long vehicleExtColor;

    @NotNull
    private Long vehicleInterior;

    //VehicleDrivingWarnLightSts
    private List<Long> dashWarningLights;

    //AppraisalVehicleAcCondition
    private List<Long> acCondition;
    //AppraisalVehicleStereoSts
    private List<Long> stereoSts;
    private List<Long> interiorCondn;
    private List<Long> oilCondition;
    private List<Long> tireCondition;

    //AppraisalTestDriveSts
    @NotNull
    private Long doorLocks;
    @NotNull
    private Long roofType;
    //AppraisalTestDriveSts
    @NotNull
    private Long leftfrWinSts;
    @NotNull
    private Long frRightWinSts;
    @NotNull
    private Long rearLeftWinSts;
    @NotNull
    private Long rearRightWinSts;


    //Pic
    @NotNull
    @Size(max = 50,message = "vehiclePic1 must be  less than equal to equal to 50 characters ")
    private String vehiclePic1;
    @Size(max = 50,message = "vehiclePic2 must be  less than equal to equal to 50 characters ")
    private String vehiclePic2;
    @Size(max = 50,message = "vehiclePic3 must be  less than equal to equal to 50 characters ")
    private String vehiclePic3;
    @Size(max = 50,message = "vehiclePic4 must be  less than equal to equal to 50 characters ")
    private String vehiclePic4;
    @Size(max = 50,message = "vehiclePic5 must be  less than equal to equal to 50 characters ")
    private String vehiclePic5;
    @Size(max = 50,message = "vehiclePic6 must be  less than equal to equal to 50 characters ")
    private String vehiclePic6;
    @Size(max = 50,message = "vehiclePic7 must be  less than equal to equal to 50 characters ")
    private String vehiclePic7;
    @Size(max = 50,message = "vehiclePic8 must be  less than equal to equal to 50 characters ")
    private String vehiclePic8;
    @Size(max = 50,message = "vehiclePic9 must be  less than equal to equal to 50 characters ")
    private String vehiclePic9;
    @Size(max = 50,message = "vehicleVideo1 must be  less than equal to equal to 50 characters ")
    private String vehicleVideo1;


    //Appraisal test Drive Sts

    private Boolean externalDmgSts;

    //AppraisalTestDriveSts
    @Size(max = 3,message = "frDrSideDmgSts must be less than equal to equal to 3 characters")
    private String frDrSideDmgSts;
    @Size(max = 100,message = "frDrSideDmgTxtBox must be less than equal to equal to 50 characters")
    private String frDrSideDmgTxtBox;
    @Size(max = 50,message = "frDrSideDmgPic must be less than equal to equal to 50 characters")
    private String frDrSideDmgPic;
    @Size(max = 3,message = "rearDrSideDmgSts must be less than equal to equal to 3 characters")
    private String rearDrSideDmgSts;
    @Size(max = 100,message = "rearDrSideDmgTxtBox must be less than equal to equal to 50 characters")
    private String rearDrSideDmgTxtBox;
    @Size(max = 50,message = "rearDrSideDmgPic must be less than equal to equal to 50 characters")
    private String rearDrSideDmgPic;
    @Size(max = 3,message = "rearPassenSideDmgSts must be less than equal to equal to 3 characters")
    private String rearPassenSideDmgSts;
    @Size(max = 100,message = "rearPassenSideDmgTxtBox must be less than equal to equal to 50 characters")
    private String rearPassenSideDmgTxtBox;
    @Size(max = 50,message = "rearPassenSideDmgPic must be less than equal to equal to 50 characters")
    private String rearPassenSideDmgPic;
    @Size(max = 3,message = "frPassenSideDmgSts must be less than equal to equal to 3 characters")
    private String frPassenSideDmgSts;
    @Size(max = 100,message = "frPassenSideDmgTxtBox must be less than equal to equal to 50 characters")
    private String frPassenSideDmgTxtBox;
    @Size(max = 50,message = "frPassenSideDmgPic must be less than equal to equal to 50 characters")
    private String frPassenSideDmgPic;
    private Boolean paintWork;
    @Size(max = 3,message = "frDrSidePntWrkSts must be less than equal to equal to 3 characters")
    private String frDrSidePntWrkSts;
    @Size(max = 100,message = "frDrSidePntWrkTxtBox must be less than equal to equal to 50 characters")
    private String frDrSidePntWrkTxtBox;
    @Size(max = 50,message = "frDrSidePntWrkPic must be less than equal to equal to 50 characters")
    private String frDrSidePntWrkPic;
    @Size(max = 3,message = "rearDrSidePntWrk must be less than equal to equal to 3 characters")
    private String rearDrSidePntWrk;
    @Size(max = 100,message = "rearDrSidePntWrkTxtBox must be less than equal to equal to 50 characters")
    private String rearDrSidePntWrkTxtBox;
    @Size(max = 50,message = "rearDrSidePntWrkPic must be less than equal to equal to 50 characters")
    private String rearDrSidePntWrkPic;

    @Size(max = 3,message = "rearPassenSidePntWrk must be less than equal to equal to 3 characters")
    private String rearPassenSidePntWrk;
    @Size(max = 100,message = "rearPassenSidePntWrkTxtBox must be less than equal to equal to 50 characters")
    private String rearPassenSidePntWrkTxtBox;
    @Size(max = 50,message = "rearPassenSidePntWrkPic must be less than equal to equal to 50 characters")
    private String rearPassenSidePntWrkPic;
    @Size(max = 3,message = "frPassenSidePntWrk must be less than equal to equal to 3 characters")
    private String frPassenSidePntWrk;
    @Size(max = 100,message = "frPassenSidePntWrkTxtBox must be less than equal to equal to 50 characters")
    private String frPassenSidePntWrkTxtBox;
    @Size(max = 50,message = "frPassenSidePntWrkPic must be less than equal to equal to 50 characters")
    private String frPassenSidePntWrkPic;

    @NotNull
    private Long frWindshieldDmg;
    private String rearGlassDmg;


    //@NotNull
    @Size(max = 10,message = "keyAssureYes must be less than equal to equal to 10 characters")
    private String keyAssureYes;

    private Boolean subscribToKeyAssure;
    //@NotNull
    @Size(max = 50,message = "keyAssureFiles must be less than equal to equal to 50 characters")
    private String keyAssureFiles;
    private List<Long>  brakingSysSts;

    //Appraisal vehicle
    private List<Long> enginePerfor;
    private List<Long> transmiSts;

    //AppraisalTest Drive Sts
    private List<Long> steeringFeelSts;
    private List<Long> booksAndKeys;

    private List<Long> rearWindowDamage;

    @NotNull
    private Long titleSts;

    @NotNull
    @Size(max=250,message = "profOpinion must be less than equal to equal to 250 characters")
    private String profOpinion;
    //ConfigurationCodes
    //@NotNull
    @Size(max = 250,message = "vehicleDesc must be less than equal to equal to 250 characters")
    private String vehicleDesc;


    //SignDet
    @Size(max = 17,message = "eSign must be less than equal to equal to 17 characters")
    private String eSign;

    //EAppraisalTestDriveSts at present later we will shift to esign
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedWholePoor;
    @Size(max=10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedWholeFair;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedWholeGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedWholeVeryGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedWholeExcelnt;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedFinanPoor;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedFinanFair;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedFinanGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedFinanVeryGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedFinanExcelnt;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedRetailPoor;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedRetailFair;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedRetailGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedRetailVeryGood;
    @Size(max = 10,message = "length must be less than equal to equal to 10 characters")
    private String adjustedRetailExcelnt;

    //AppraisalVehicle
    //@NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double appraisedValue;


    //@NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double dealerReserve;
    //@NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double consumerAskPrice;
    //@NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double delrRetlAskPrice;

    private Boolean pushForBuyFig;

    //prestart measurement
    private PrestartMeasurement preStartMeas;

    //test drive measurement

    List<TestDriveMes>testDrive;

    private String fromMkt;



}
