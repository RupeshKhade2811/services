package com.massil.persistence.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;
import jakarta.persistence.*;
import java.util.UUID;


@Entity
@Immutable
@Data
public class MembersByFactorySalesmen {
    @Id
    private UUID userId ;
    private String firstName;
    private String lastName;
    private String streetAddress;
   // private Date signFrom;
    //private Double amount;
    private UUID factorySalesman;
    @Column(name = "company_name")
    private String compName;
    private String fsFirstName;
    private String fsLastName;

}
