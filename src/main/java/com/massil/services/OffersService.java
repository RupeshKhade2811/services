package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.OfferException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.*;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.UUID;

public interface OffersService {


    /**
     * This method will get all cards which are procurement section
     * @param id receiving from Ui in header
     * @param pageNumber receiving from Ui
     * @param pageSize receiving from Ui
     * @return CardsPage
     */
     CardsPage procurementCards(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * This method will get all cards which are in liquidation section
     * @param id receiving from Ui in header
     * @param pageNumber receiving from Ui
     * @param pageSize receiving from Ui
     * @return CardsPage
     */
     CardsPage liquidationCards(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException;

    /**
     * This method will take make offer from buyer
     * @param AppraisalId this is appraisal id
     * @param offers this is the offer object
     * @param buyerUserId this is user id of buyer
     * @return message
     */
     Response makeOfferByBuyer(Long AppraisalId, Offers offers, UUID buyerUserId) throws OfferException;

    /**
     * This method will take seller counter
     * @param offers this is the offer object
     * @param offerId this is offer id
     * @return message
     */
     Response sellerCounter(Long offerId, Offers offers) throws OfferException;

    /**
     * This method will take buyer counter
     * @param offers this is the offer object
     * @param offerId this is offer id
     * @return message
     */
     Response buyerCounter(Long offerId, Offers offers) throws OfferException;

     /**
     * This method will update status of seller
     * @param offerId this is offer id
     * @return message
     */

     Response sellerAccept(Long offerId) throws OfferException;

    /**
     * This method will update status of buyer
     * @param offerId this is offer id
     * @return message
     */

     Response buyerAccept(Long offerId) throws OfferException;

    /**
     * This method will update status of seller
     * @param offerId this is offer id
     * @return message
     */

     Response sellerRejected(Long offerId) throws OfferException;

    /**
     * This method will update status of buyer
     * @param offerId this is offer id
     * @return message
     */

     Response buyerRejected(Long offerId) throws OfferException;

    /**
     * This method will give offer information in procurement page
     * @param offerId this is offer id
     * @return message
     */

     OfferInfo procurementOfferInfo(Long offerId) throws OfferException;

    /**
     * This method will give offer information in liquidation page
     * @param offerId this is offer id
     * @return message
     */
    OfferInfo liquidationOfferInfo(Long offerId) throws OfferException;

    /**
     * It will send email to buyer and seller after 22 hr of making offer
     * @return  Response
     * @throws IOException
     * @throws TemplateException
     * @throws MessagingException
     */

    Response myScheduledTask() throws IOException, TemplateException, MessagingException, MessagingException;


    /**
     * This method providing all the offer made to a car
     * @param appraisalId
     * @return
     */
    ListedOffer getOfferList(Long appraisalId) throws OfferException;

    HighReserveValuePop isReserveHigh(Long appraisalId, Double newDealerReserve) throws AppraisalException;



}
