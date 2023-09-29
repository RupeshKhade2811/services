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
@Table(name ="ofrreport")
@Immutable
@NoArgsConstructor
@Getter
@Setter
public class OfferPdf {
    @Id
    private Long offerId;
    private String vin;
    private Date createdOn;
    private String firstName;
    private String lastName;
    private String userName;
    private UUID sellerUserId;
    private Long miles;
    private Long offerCount;

}
