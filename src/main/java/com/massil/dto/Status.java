package com.massil.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Status {
    private String buyerStatus;
    private String sellerStatus;
    private String offerStatus;
    private Long color;
    private String statusCode;
    private String status;

}
