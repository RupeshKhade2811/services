package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.Immutable;


import java.util.UUID;

@Entity
@Table(name = "userlistview")
@Immutable
@Data
public class UserListView {
    @Id
    private Long  roleId;
    private String firstName;
    private String lastName;
    private UUID userId;
    private String roleGroup;

}
