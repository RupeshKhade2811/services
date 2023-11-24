package com.massil.persistence.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name="total_revenue_table")
@Immutable
@Data
public class totalRvnueView {

    @Id
    @Column(name = "USER_ID")
    private UUID userId;

    private Double totalBuyFee;
    private Double totalSaleFee;
    private Double totalSubcriptionAmount;
    private Double revenue;

}
