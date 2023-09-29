package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;



@Entity
@Table(name = "corporate_admin_view")
@Immutable
@Data
public class CorporateAdminView {
    @Id
    private Long userId;
    private Long roleId;
    private Long managerId;
    private String userName;
    private String firstName;
    private String lastName;
}
