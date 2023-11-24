package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.util.UUID;


@Entity
@Table(name = "corporate_admin_view")
@Immutable
@Data
public class CorporateAdminView {
    @Id
    private UUID userId;
    private Long roleId;
    private UUID managerId;
    private String userName;
    private String firstName;
    private String lastName;
}
