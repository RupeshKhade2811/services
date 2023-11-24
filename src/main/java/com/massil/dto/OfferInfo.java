package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
////@ApiModel(description = "Offer card info")
public class OfferInfo extends Response {
    private AppraisalVehicleCard card;
    private Status statusInfo;
    List<Quotes> quotesList;
    private Long offerId;
    private String buyFee;
    private String saleFee;

}
