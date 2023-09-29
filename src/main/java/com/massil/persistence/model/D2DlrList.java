package com.massil.persistence.model;


import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity(name = "dealer_d2_view")
@Immutable
@Data
public class D2DlrList {
    @Id
    private UUID userId;
    private String firstName;
    private String lastName;
    private UUID dealerAdmin;
    private Long roleId;


}
