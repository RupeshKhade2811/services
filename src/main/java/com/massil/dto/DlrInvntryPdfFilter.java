package com.massil.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DlrInvntryPdfFilter {

    private String vehicleMake;
    private Integer daysSinceInventory;
    private Double consumerAskPrice;
    private List<UUID> users;

}
