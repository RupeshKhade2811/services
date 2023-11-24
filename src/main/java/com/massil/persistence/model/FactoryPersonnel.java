package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;


import java.util.UUID;

@Entity(name = "factory_personnel_view")
@Immutable
@Data
public class FactoryPersonnel {
    @Id
    private UUID userId;
    private String firstName;
    private String lastName;
    private Long roleId;

}
