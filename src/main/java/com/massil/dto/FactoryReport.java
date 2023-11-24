package com.massil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FactoryReport {

    private UUID memberId;
    private String memberName;
    private String location;
    private String signupDate;
    private String searchby;
    private String memberType;
    private String factorySalesMan;
    private String factoryMgr;
    private Double totalRevenue;

}
