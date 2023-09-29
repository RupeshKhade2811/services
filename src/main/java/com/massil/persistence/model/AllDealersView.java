package com.massil.persistence.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;


import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "all_dealers_view")
@Immutable
@Data
public class AllDealersView {
    @Id
    private UUID userId;
    private Long dealerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String zipCode;
    @Column(name = "ds_addr")
    private String streetAddress;
    private String state;
    @Column(name = "comp_id")
    private Long companyId;
    private Long roleMpngId;
    private UUID factorySalesman;
    private UUID factoryManager;
    private UUID managerId;
    private Date createdOn;

}
