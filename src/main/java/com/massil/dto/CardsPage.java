package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EOffers;
import lombok.*;

import java.util.List;

/**
 * This class is a DTO for showing Pagination details
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CardsPage extends Response {

    List<AppraisalVehicleCard> cards;
    List<EAppraiseVehicle> appraiseVehicleList;
    List<EOffers> eOffersList;
    private String roleType;
    private String roleGroup;
    private Long totalPages;
    private Long totalRecords;

}
