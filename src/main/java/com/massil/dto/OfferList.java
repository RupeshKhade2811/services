package com.massil.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OfferList {
    private String firstName;
    private String lastName;
    private Long offerId;
    private String offerStatus;
    private String offerDesc;
}
