package com.massil.persistence.model;

import jakarta.persistence.Column;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "dlr_sales_view")
@Data
@Immutable
public class DlrSalesView {
    @Id
    @Column(name = "OFFER_ID")
    private Long offerId;
    @Column(name="VIN_NUMBER ")
    private String vinNumber;
    @Column(name="SERIES")
    private String vehicleSeries;
    @Column(name="VEH_YEAR")
    private String vehicleYear;
    @Column(name="MODEL")
    private String vehicleModel;
    @Column(name="MILES")
    private String vehicleMileage;
    @Column(name="MAKE")
    private String vehicleMake;
    @Column(name = "EXTR_COLOR")
    private String vehExtColor;
    @Column(name = "INTR_COLOR")
    private String intrColor;

    @Column(name = "STATUS_DESC")
    private String offerStatus;
    private String statusCode;
    @Column(name = "SLR_USER_ID")
    private UUID sellerUserId;
    @Column(name = "BUYER_USER_ID")
    private UUID buyerUserId;
    @Column(name = "IS_ACTIVE")
    private Boolean valid;
    private Boolean isTradeBuy;
    private Double price;
    private Double saleFee;
    private Double totalPrice;
    private Double buyFee;
    private Double netPrice;

    private Date createdOn;



}
