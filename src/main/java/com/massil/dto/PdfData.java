package com.massil.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class PdfData {

    private String slrName;
    private String slrState;
    private String slrAddress;
    private String slrMail;
    private String buyerName;
    private String buyerAddress;
    private String buyerMail;
    private String vinNumber;
    private String vehicleSeries;
    private String vehicleYear;
    private String vehicleModel;
    private String vehicleMileage;
    private String vehicleMake;
    private String vehExtColor;
    private Double price;
    private String slrUserName;
    private String slrCity;
    private String slrZip;
    private String byrState;
    private String byrUserName;
    private String byrCity;
    private String byrZip;
    private Long vehExtColorCode;

}
