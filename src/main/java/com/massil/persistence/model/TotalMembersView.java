package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.util.UUID;

@Entity
@Immutable
@Data
public class TotalMembersView {
    @Id
    private UUID userId;
    private String memberFirstName;
    private String memberLastName;
    private String streetAddress;
    private String role;
   // private Date signFrom;
    private String factorySalesmanFirstName;
    private String factorySalesmanLastName;
    private String factoryManagerFirstName;
    private String factoryManagerLastName;
   // private Double amount;

}
