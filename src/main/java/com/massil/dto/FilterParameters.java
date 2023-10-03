package com.massil.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FilterParameters {
    private Long minDistance;
    private Long maxDistance;

    private Long minYear;
    private Long maxYear;
    private Long year;
    private String make;
    private String model;
    private String series;
    private String style;
    private String engine;
    private String transmission;
    private Date fromDate;
    private Date toDate;

}
