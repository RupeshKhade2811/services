package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.OfferException;
import com.massil.dto.AutoBidBumps;

import java.util.List;

public interface AutoBidService {
    void sellerAutoBid() throws OfferException, AppraisalException;
    String addBumpsAndRange();
    void updateBumpsAndRange();
    void deleteBumpsAndRange();
    List <AutoBidBumps> getAllBumpsAndRange();

}
