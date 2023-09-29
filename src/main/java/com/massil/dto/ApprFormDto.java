package com.massil.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApprFormDto {


    private String picLink;

    //client info
    private String date;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    //vehicle info
    private String vehicleYear;
    private String vehicleMake;
    private String  vehicleModel ;
    private String  vehicleSeries;
    private String engineType;
    private String transmissionType;
    private String extrColor;
    private String intrColor;
    private String vehicleMileage;
    private String vinNumber;
    //Exterior Inspection
    private String externalDmgSts;
    private String frDrSideDmgSts;
    private String frDrSideDmgTxtBox;
    private String frPassenSideDmgSts;
    private String frPassenSideDmgTxtBox;
    private String frDrSidePntWrkSts;
    private String frDrSidePntWrkTxtBox;
    private String frPassenSidePntWrk ;
    private String frPassenSidePntWrkTxtBox;
    private String rearDrSidePntWrk;
    private String rearDrSidePntWrkTxtBox;
    private String rearPassenSidePntWrk;
    private String rearPassenSidePntWrkTxtBox;
    private String rearDrSideDmgSts;
    private String rearDrSideDmgTxtBox;
    private String rearPassenSideDmgSts;
    private String rearPassenSideDmgTxtBox;
    private  String  windShieldDmg ;
    private String rearWindow;
    //interior inspection
    private String vehDrWarnLightSts;
    private String apprVehAcCondn;
    private String apprVehStereoSts;
    private String roofTypes;
    private String doorLock;
    private String apprVehInteriCondn;
    private String flWinStatus;
    private String frWinStatus;
    private String rlWinStatus;
    private String   rrWinStatus;
    //test drive
    private String apprEnginePer;
    private String apprVehOilCondn;
    private String  apprBrakingSysSts;
    private String apprTransmissionSts;
    private String steeringFeel;
    private String apprVehTireCondn;
    private String bookAndKeys;
    private  String titleSt;
    private String profOpinion;
    private String price;
    private String buyerName;
    private String vehiclePic1;
    private String frDrSideDmgPic;
    private String frPassenSideDmgPic;
    private String rearDrSideDmgPic;
    private String rearPassenSideDmgPic;



}
