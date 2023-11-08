package com.massil.dto;

import com.massil.ExceptionHandle.Response;
import lombok.Data;

@Data
public class HighReserveValuePop extends Response {
    private Boolean isNewReserveHigh;
    private Boolean areBidsLTNewReserve;
}
