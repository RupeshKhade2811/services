package com.massil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Quotes {
    private Double BuyerQuote;
    private Double SellerQuote;
    private Double appraisedValue;
    private String status;

}
