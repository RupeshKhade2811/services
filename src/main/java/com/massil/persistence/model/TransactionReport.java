package com.massil.persistence.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;


import java.util.Date;
import java.util.UUID;

@Entity
@Table(name ="soldreports")
@Immutable
@NoArgsConstructor
@Getter
@Setter
public class TransactionReport {
    @Id
    private Long offerId;
    private Double price;
    private String vinNumber;
    private String vehYear;
    private String status;
    private String make;
    private String model;
    private Long miles;
    private String colour;
    private String firstName;
    private String lastName;
    private String userName;
    private UUID userId;
    private Date createdOn;
//    private String compName;



}
