package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.CardsPage;
import com.massil.dto.Shipment;

import java.io.IOException;
import java.util.UUID;

public interface ShipmentService {
    /**
     * This method returns all cards which are of my sell section
     * @param userId This is seller userId or seller dealerId
     * @param pageNumber
     * @param pageSize
     * @return CardsPage
     */
    CardsPage mySellsCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * This method returns all cards which are of my buyer section
     * @param userId This is buyer userId or seller dealerId
     * @param pageNumber
     * @param pageSize
     * @return CardsPage
     */

    CardsPage myBuyerCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;


    Response buyerAgreedService(Shipment shipment,Long ship_id) throws Exception;


    Response sellerAgreedService(Shipment shipment,Long ship_id) throws Exception;

}
