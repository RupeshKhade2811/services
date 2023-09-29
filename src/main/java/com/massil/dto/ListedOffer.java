package com.massil.dto;

import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ListedOffer extends Response {

    private List<OfferList> offersCards;


}
