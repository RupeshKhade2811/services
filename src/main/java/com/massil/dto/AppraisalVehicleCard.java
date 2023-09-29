package com.massil.dto;

/**
 *
 * This is the Model class use for to send the data to UI
 *
 * @author Rupesh Khade
 */


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


/**
 * This class is a DTO which will give show the provided fields in card
 */

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
@Schema(description = "Appraisal card details")
public class AppraisalVehicleCard {


    private Long id;
  @Schema(description = "Manufacturer name")
    private String vehicleMake ;
    private String  vehicleModel ;
    private double appraisedValue;
    private String createdBy;
    private Long vehicleYear;
    private Long vehicleMileage;
    private String  vehicleSeries;
    private String offerStatus;
    private Boolean isVehicleFav;
    private String createdOn;
    private String modifiedOn;
    private Long apprRef;
    private Long offerId;
    private Long color;
    private Long statusCodeId;
    private String soldSts;
    private String vehiclePic1;
    private String vinNumber;
    private String style;
    private String invntrySts;
    private Boolean isHold;
    private Boolean field1 =Boolean.FALSE;
    private Boolean field2=Boolean.FALSE;
    private Boolean isOfferMade=false;
    private ConfigDropDown titleSts;
    private Status status;
    private Boolean isSold=false;
    private Long shipmentId;
    private Boolean buyerAgreed;
    private Boolean sellerAgreed;
    private Role role;     //newly added
    private Boolean isPrivateParty;
}
