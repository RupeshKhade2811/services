package com.massil.dto;
//@author:Rupesh Khade


import jakarta.validation.constraints.*;
import lombok.*;


import java.util.Date;

/**
 * This class is a DTO of AppraiseVehicle
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppraiseVehicle {

 private Long id;

 private Date apprRetensionTime;

 @Size(max = 20)
 private double appraisedValue;

 @Size(max = 10)
 private String blackBookValue;

 @Size(max = 15)
 private String brakeSysFuel;

 @NotNull
 @Max(999999999999999l)
 private Long  eSignId;

 @Size(max = 50)
 private String enginePerfor;

 @Size(max = 15)
 private String field1;

 @Size(max = 15)
 private String field2;


 @Size(max = 50)
 private String latitude;

 @Size(max = 50)
 private String longitude;

 @Size(max = 250)
 private String profOpinion;


 @Max(999999999999999l)
 private Long stockNumber;

 @Size(max = 100)
 private String transmiSts ;

 @Size(max = 15)
 private String vehicleMake;

 @Max(9999999999l)
 private Long miles;
 @Size(max = 15)
 private String  vehicleModel;
 @Size(max = 10)
 private String  vehicleSeries;
 @Size(max = 10)
 private String  style;
 @Max(9999)
 private Long vehicleYear;
 @Size(max = 17)
 private String vinNumber ;
 private String clientFirstName;
 private String clientLastName;
 private String clientPhoneNumber;

 private String invntrySts;







}
