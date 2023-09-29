package com.massil.persistence.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.Immutable;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "pdf_data")
@Data
@Immutable
public class DlrInvntryView {

    @Id
    @Column(name = "APPR_REF_ID")
    private Long apprRefId;
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

    @Column(name = "USER_ID")
    private UUID user;
    @Column(name = "INV_STATUS")
    private String invntrySts;

    @Column(name = "IS_ACTIVE")
    private Boolean valid;
    @Column(name = "CNSR_ASK_PRICE")
    private Double consumerAskPrice;
    @Column(name="INVENTORY_DATE")
    private Date invntryDate;
    @Column(name = "days_since_inventory")
    private Integer daysSinceInventory;


}
