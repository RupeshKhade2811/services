package com.massil.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;




//@Table(name = "MKT_Inventory",schema = "marketcheck")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
public class EInventoryVehicles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //vinInfo
    private String vin;
    private String heading;
    private Integer price;
    private Integer refPrice;
    private Integer miles;
    private Integer msrp;
    private String exteriorColor;
    private String  interiorColor;
    private String  baseIntColor;
    private String baseExtColor;
    private String sellerType;
    private String inventoryType;
    private String source;
    private Boolean inTransit;
    private String availabilityStatus;

    //dealer this mkDealerID
    private String dealerId;

    //build
    private Integer year;
    private String make;
    private String model;
    private String trim;
    private String bodyType;
    private String vehicleType;
    private String transmission;
    private String drivetrain;
    private String fuelType;
    private String engine;
    private String doors;
    private String madeIn;
    private String overallHeight;
    private String overallLength;
    private String overallWidth;
    private String stdSeating;
    private String highwayMpg;
    private String cityMpg;
    private String powertrainType;

    //media
    private String vehiclePic1;
    private String vehiclePic2;
    private String vehiclePic3;
    private String vehiclePic4;
    private String vehiclePic5;
    private String vehiclePic6;
    private String vehiclePic7;
    private String vehiclePic8;
    private String vehiclePic9;

}
