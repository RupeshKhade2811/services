package com.massil.persistence.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "total_sale_fee")
@Immutable
@Data
public class totalSaleFee {

    @Id
    @Column(name = "USER_ID")
    private UUID userId;

    private Double totalSaleFee;
}
