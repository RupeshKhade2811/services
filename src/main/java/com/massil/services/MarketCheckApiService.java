package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.dto.MarketCheckData;

public interface MarketCheckApiService {
    /**
     * This method sends marketCheck data
     * @param vin This is vin number
     * @return MarketCheckData object
     * @author Rupesh
     */
    MarketCheckData getMarketCheckData(String vin) throws AppraisalException;

}
