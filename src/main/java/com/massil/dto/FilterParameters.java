package com.massil.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FilterParameters {
    private Integer minDistance;
    private Integer maxDistance;
    private Integer minYear;
    private Integer maxYear;
    private Integer year;
    private String make;
    private String model;
    private String series;
    private String style;
    private String engine;
    private String transmission;
    private Date fromDate;
    private Date toDate;

}
