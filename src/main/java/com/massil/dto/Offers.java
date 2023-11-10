package com.massil.dto;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;




@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Offers {

    @DecimalMin(value = "1", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double buyerQuote;
    @DecimalMin(value = "1", inclusive = false)
    @DecimalMax(value = "9999999999999.99",message = "value must be less than 13 digits")
    private Double sellerQuote;

    public Offers(Double sellerQuote) {
        this.sellerQuote = sellerQuote;
    }
}
