package com.massil.dto;

import com.massil.ExceptionHandle.Response;
import lombok.Data;
@Data
public class AutoBidBumps  {
    private Long id;
    private Double startPrice;
    private Double endPrice;
    private Double bump;
}
