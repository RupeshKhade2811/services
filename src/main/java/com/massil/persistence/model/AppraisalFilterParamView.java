package com.massil.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;


import java.util.UUID;

@Entity
@Table(name = "appraisal_filter_param_view")
@Immutable
@Data
public class AppraisalFilterParamView {
    @Id
    private Long apprRefId;
    @Column(name="VEH_YEAR")
    private Long vehicleYear;
    @Column(name="MAKE")
    private String vehicleMake;
    @Column(name="MODEL")
    private String  vehicleModel ;
    private UUID userId;
}
