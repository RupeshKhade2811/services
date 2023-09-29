package com.massil.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;


import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "appraisalform_view")
@Immutable
@Data
public class AppraisalFormView {
    @Id
    private Long  apprRefId;
    private Integer vehicleYear;
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleMileage;
    private String vinNumber;
    private String vehExtColor;
    private UUID userId;
    private Long dealerId;
    private String role;
    private Date createdOn;
    private Date modifiedOn;
}
