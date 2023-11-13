package com.massil.services;

import com.massil.ExceptionHandle.OfferException;
import com.massil.dto.AutoBidBumps;

import java.util.List;

public interface AutoBidService {
    void sellerAutoBid() throws OfferException;
    String addBumpsAndRange();
    void updateBumpsAndRange();
    void deleteBumpsAndRange();
    List <AutoBidBumps> getAllBumpsAndRange();

}
