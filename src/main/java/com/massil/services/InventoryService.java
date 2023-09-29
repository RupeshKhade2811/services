package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.CardsPage;

import java.util.UUID;


public interface InventoryService {

    /**
     * This method gets all appraisal vehicle cards whose status is 'inventory'
     * @param userId receiving user id from UI in header
     * @param pageNumber receiving pageNumber from UI in header
     * @param pageSize receiving PageSize from UI in header
     * @return CardsPage
     */
    CardsPage inventoryCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * This method will show all inventory cards except the current user
     * @param userId receiving user id from UI in header
     * @param pageNumber receiving pageNumber from UI in header
     * @param pageSize receiving PageSize from UI in header
     * @return cardsPage
     */

    CardsPage searchFactory(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException;


    Response holdAppraisal(Long apprRef) throws AppraisalException;

    Response UnHoldAppraisal(Long apprRef) throws AppraisalException;


    Response makeSoldRetailOn(Long apprRef) throws AppraisalException;

    Response makeSoldRetailOff(Long apprRef) throws AppraisalException;
    Response makeSoldWholesaleOn(Long apprRef) throws AppraisalException;

    Response makeSoldWholesaleOff(Long apprRef) throws AppraisalException;

}
