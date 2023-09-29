package com.massil.dto;


import lombok.*;

import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString

public class OfferReport {

private String sellername;
private UUID sellerId;
private String offerCount;
private String miles;
private String vin;
private String searchby;
}
