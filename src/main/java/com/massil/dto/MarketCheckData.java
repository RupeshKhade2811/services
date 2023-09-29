package com.massil.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.massil.ExceptionHandle.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * This class is a DTO which will give predefined Market Check Data
 */


@Getter
@Setter
@NoArgsConstructor
@ToString
public class MarketCheckData extends Response {


    private String year;
    private String make;
    private String model;
    private String transmission;
    private String engine;
    private Long miles;
    @JsonProperty("vehicle_type")
    private String vehicleType;
    @JsonProperty("body_type")
    private String bodyType;
    @JsonProperty("body_subtype")
    private String  bodySubtype;
    private String drivetrain;
    private String  trim;
    private String series;
    @JsonProperty("fuel_type")
    private String fuelType;





}
