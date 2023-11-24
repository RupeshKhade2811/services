package com.massil.persistence.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name="total_subcription_amount")
@Immutable
@Data
public class totalSubscriptionAmt {

    @Id
    @Column(name = "USER_ID")
    private UUID userId;

    private Double totalSubcriptionAmount;
    private Date signupDate;
}
