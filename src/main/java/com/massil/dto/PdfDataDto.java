package com.massil.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class PdfDataDto  {

    private String slrName;
    private String slrState;
    private String slrAptNum;
    private String slrCity;
    private String slrMail;
    private String slrPhNm;
    private String slrZip;
    private String slrStrtAdr;

    private String buyerName;
    private String buyerState;
    private String buyerAptNum;
    private String buyerCity;
    private String buyerMail;
    private String buyerPhNm;
    private String buyerZip;
    private String buyerStrtAdr;

    private String vinNumber;
    private String vehicleSeries;
    private String vehicleYear;
    private String vehicleModel;
    private String vehicleMileage;
    private String vehicleMake;
    private String vehExtColor;
    private String intrColor;
    private Double price;
    private String offerStatus;
    private Double consumerAskPrice;
    private Integer daysSinceInventory;
    private Double daysInInvntry;
    private Double wholesalePrice;
    private Double saleFee;
    private Double netPrice;
    private Double totalPrice;
    private Double buyFee;

    private String buyerSign;
    private String sellerSign;
    private Boolean buyerAgreed;
    private Boolean sellerAgreed;
    private String picLink;


    //prestart measurement
    private String engineTemp;
    private String batteryVoltage;
    private String fuelPressure;
    private String warmUps;
    private String timeSince;
    private String mileSince;
    private String currentTroubleCodes;
    private String pendingTroubleCodes;
    private String odometer;
    private String checkVin;
    private  String scannedVin;

    private List<TestDriveMes> testDriveMes;

    private String dataSummary;
    private String trCodesSummary;

    private String membertype;
    private UUID memberId;
    private String searchby;
    private Long offerId;




}
