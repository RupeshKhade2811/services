package com.massil.repository;


import com.massil.persistence.model.EOffers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface OffersRepo extends JpaRepository<EOffers,Long> {

    /**
     * This method returns the page of EOffers base on BuyerUserId
     * @param userId This is userId
     * @param inventory This the inventory status
     * @param valid This is Eoffers valid or not
     * @param isTradeBuy This is Appraisal is TradeBuy or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT e FROM EOffers e WHERE e.buyerUserId.id = :userId "
            + "AND e.appRef.invntrySts = :inventory AND e.valid = :valid AND e.appRef.valid=true "
            + "AND e.isTradeBuy = :isTradeBuy ORDER BY e.modifiedOn DESC")
    Page<EOffers> findByBuyerUserIdJPQL(@Param("userId") UUID userId, @Param("inventory") String inventory, @Param("valid") boolean valid, @Param("isTradeBuy") boolean isTradeBuy, Pageable pageable);

    /**
     *  This method returns the page of EOffers base on BuyerDealerId
     * @param dealerId This is buyer dealer id
     * @param inventory This the inventory status
     * @param valid This is Eoffers valid or not
     * @param isTradeBuy This is Appraisal is TradeBuy or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT e FROM EOffers e WHERE e.buyerDealerId.id = :dealerId "
            + "AND e.appRef.invntrySts = :inventory AND e.valid = :valid AND e.appRef.valid=true "
            + "AND e.isTradeBuy=:isTradeBuy ORDER BY e.modifiedOn DESC")
    Page<EOffers> findByBuyerDealerIdJPQL(@Param("dealerId") Long dealerId, @Param("inventory") String inventory, @Param("valid") boolean valid, @Param("isTradeBuy") boolean isTradeBuy, Pageable pageable);

    /**
     * this will send mail to user u have make offer less than 24 hours
     * @return list of EOffers
     */

    String jpql = "SELECT e FROM EOffers e WHERE ( EXTRACT(epoch from (CURRENT_TIMESTAMP)) - EXTRACT(epoch from (e.modifiedOn)) )<= 24*60*60 " +
            "AND (EXTRACT(epoch from (CURRENT_TIMESTAMP ))- EXTRACT(epoch from ( e.modifiedOn)) )>= 22*60*60 " +
            "AND (e.status.statusCode = 's001' OR e.status.statusCode = 's002' OR e.status.statusCode = 's003')";
/*    @Query(value = "SELECT e FROM EOffers e WHERE EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - e.modifiedOn))<=24*60*60 AND EXTRACT(EPOCH FROM (CURRENT_TIMESTAMP - e.modifiedOn))>=22*60*60 AND (e.status.statusCode='s001' OR e.status.statusCode='s002' OR e.status.statusCode='s003')")
    List<EOffers>  listOfMakeOfferLessThn24hrs();*/
@Query(value = jpql)
List<EOffers>  listOfMakeOfferLessThn24hrs();



    /**
     * This method returns the page of EOffers base on Seller UserId
     * @param userId This is Seller UserId
     * @param isTradeBuy This is Appraisal is TradeBuy or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT o FROM EOffers o WHERE o.sellerUserId.id = :userId AND o.isTradeBuy = isTradeBuy "
            +"AND (o.appRef, o.modifiedOn) IN (SELECT o2.appRef, MAX(o2.modifiedOn) FROM EOffers o2 "
            +"WHERE o2.sellerUserId.id = :userId AND o2.isTradeBuy = :isTradeBuy AND o2.valid=true AND o2.appRef.valid=true "
            +"GROUP BY o2.appRef) ORDER BY o.modifiedOn DESC")
    Page<EOffers> findBySellerUserIdJPQL(@Param("userId") UUID userId,
                                         @Param("isTradeBuy") boolean isTradeBuy, Pageable pageable);


    /**
     * This method returns the page of EOffers base on Seller DealerId
     * @param dealerId This is Seller DealerId
     * @param isTradeBuy This is Appraisal is TradeBuy or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT o FROM EOffers o WHERE o.sellerDealerId.id = :dealerId AND o.isTradeBuy = isTradeBuy "
            +"AND (o.appRef, o.modifiedOn) IN (SELECT o2.appRef, MAX(o2.modifiedOn) FROM EOffers o2 "
            +"WHERE o2.sellerDealerId.id = :dealerId AND o2.isTradeBuy = :isTradeBuy AND o2.valid=true AND o2.appRef.valid=true "
            +"GROUP BY o2.appRef) ORDER BY o.modifiedOn DESC")
    Page<EOffers> findBySellerDealerIdJPQL(@Param("dealerId") Long dealerId,@Param("isTradeBuy") boolean isTradeBuy, Pageable pageable);


    /**
     * this method used to find inventoryStatus as created and pushForBuyFigure is true, of same userid
     * @param userId this is the userId
     * @param pageable This is object of Pageable
     * @return EAppraiseVehicle
     */
    @Query(value = "SELECT o FROM EOffers o WHERE o.sellerUserId.id = :userId AND o.isTradeBuy = isTradeBuy "
            +"AND (o.appRef, o.modifiedOn) IN (SELECT o2.appRef, MAX(o2.modifiedOn) FROM EOffers o2 "
            +"WHERE o2.sellerUserId.id = :userId AND o2.isTradeBuy = :isTradeBuy AND o2.valid=true AND o2.appRef.valid=true "
            +"GROUP BY o2.appRef) ORDER BY o.modifiedOn DESC")
    Page<EOffers> findBySlrUserIdInGetFctryOfrsJPQL(@Param("userId") UUID userId,
                                                    @Param("isTradeBuy") boolean isTradeBuy,
                                                    Pageable pageable);

    /**
     * this method used to find inventoryStatus as created and pushForBuyFigure is true, of  same dealer id
     * @param dealerId this is the userId or DealerId
     * @param pageable This is object of Pageable
     * @return EAppraiseVehicle
     */
    @Query(value = "SELECT o FROM EOffers o WHERE o.sellerDealerId.id = :dealerId AND o.isTradeBuy = isTradeBuy "
            +"AND (o.appRef, o.modifiedOn) IN (SELECT o2.appRef, MAX(o2.modifiedOn) FROM EOffers o2 "
            +"WHERE o2.sellerDealerId.id = :dealerId AND o2.isTradeBuy = :isTradeBuy AND o2.valid=true AND o2.appRef.valid=true "
            +"GROUP BY o2.appRef) ORDER BY o.modifiedOn DESC")
    Page<EOffers> findBySlrDlrIdInGetFctryOfrsJPQL(@Param("dealerId") Long dealerId,
                                                   @Param("isTradeBuy") boolean isTradeBuy,
                                                   Pageable pageable);



    /**
     * This method returns the page of EOffers which are sold base on Seller UserId
     * @param userId This is Seller UserId
     * @param valid This is Eoffers valid or not
     * @param statusCode1 This is the status code for sold or not
     * @param statusCode2 This is the status code for sold or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT e FROM EOffers e WHERE e.sellerUserId.id = :userId "
            + "AND e.valid = :valid AND e.appRef.valid=true and e.isTradeBuy=false  AND e.status.statusCode in (:code1,:code2) ORDER BY e.modifiedOn DESC")
    Page<EOffers> findBySellerUserIdSold(@Param("userId") UUID userId, @Param("valid") boolean valid,@Param("code1") String statusCode1,@Param("code2") String statusCode2, Pageable pageable);
/**
     * This method returns the page of EOffers which are sold base on Seller dealerId
     * @param dealerId This is Seller UserId
     * @param valid This is Eoffers valid or not
     * @param statusCode1 This is the status code for sold or not
     * @param statusCode2 This is the status code for sold or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT e FROM EOffers e WHERE e.sellerDealerId.id = :dealerId "
            + "AND e.valid = :valid AND e.appRef.valid=true and e.isTradeBuy=false  AND e.status.statusCode in(:code1,:code2) ORDER BY e.modifiedOn DESC")
    Page<EOffers> findBySellerDealerIdSold(@Param("dealerId") Long dealerId, @Param("valid") boolean valid,@Param("code1") String statusCode1,@Param("code2") String statusCode2, Pageable pageable);

    /**
     * This method returns the page of EOffers which are sold base on Buyer UserId
     * @param userId This is Buyer UserId
     * @param valid This is Eoffers valid or not
     * @param statusCode1 This is the status code for sold or not
     * @param pageable This is pageable onject having page size and page number information
     * @return Page of Eoffers
     */
    @Query("SELECT e FROM EOffers e WHERE e.buyerUserId.id = :userId "
            + "AND e.valid = :valid AND e.appRef.valid=true and e.isTradeBuy=false AND e.status.statusCode in (:code1,:code2) ORDER BY e.modifiedOn DESC")
    Page<EOffers> findByBuyerUserIdSold(@Param("userId") UUID userId, @Param("valid") boolean valid,@Param("code1") String statusCode1,@Param("code2") String statusCode2,Pageable pageable);

    /**
     *
     * @param appraisalId
     * @param valid
     * @return
     */
    @Query("SELECT e FROM EOffers e WHERE e.appRef.id = :appraisalId AND e.valid =:valid ORDER BY e.modifiedOn DESC")
    List<EOffers> getOfferList(@Param("appraisalId") Long appraisalId,@Param("valid") boolean valid);


    /**
     *
     * @param appraisalId
     * @param buyerUserId
     * @return
     */
    @Query("SELECT e FROM EOffers e WHERE e.appRef.id = :appraisalId AND e.buyerUserId.id = :buyerUserId "
            + "AND e.valid = true AND e.modifiedOn = (SELECT MAX(e2.modifiedOn) FROM EOffers e2 WHERE e2.appRef.id = :appraisalId AND e2.buyerUserId.id = :buyerUserId)")
    EOffers findOffer(@Param("appraisalId") Long appraisalId, @Param("buyerUserId") UUID buyerUserId);

    /**
     *
     * @param apprId
     * @param statusCode1
     * @param statusCode2
     * @return
     */
    @Query("SELECT e FROM EOffers e WHERE e.appRef.id = :apprId AND e.status.statusCode in (:code1,:code2) ORDER BY e.modifiedOn DESC")
    EOffers findByApprId(@Param("apprId") Long apprId,@Param("code1") String statusCode1,@Param("code2") String statusCode2);

    /**
     *
     * @param id
     * @param appRefId
     * @return
     */
    @Query("update EOffers o set o.status.id=4 where o.id=:id and o.appRef.id=:appRefId ")
    @Modifying
    int  updateOfferSetPurchasedSellerAccept(Long id,Long appRefId);

    /**
     *
     * @param id
     * @param appRefId
     * @return
     */
    @Query("update EOffers o set o.status.id=5 where o.id=:id and o.appRef.id=:appRefId ")
    @Modifying
    int  updateOfferSetPurchasedBuyerAccept(Long id,Long appRefId);

    /**
     *
     * @param id
     * @param appRefId
     * @return
     */
    @Query("update EOffers o set o.status.id=8 where o.id<>:id and o.appRef.id=:appRefId ")
    @Modifying
    int updateOfferSetSold(Long id, Long appRefId);

    /**
     *
     * @param offerId
     * @return
     */
    @Query(value = "select e from EOffers e where e.valid= true and e.id=:offerId and e.status.statusCode in ('s004','s005')")
    EOffers findByOfferId(Long offerId);

    @Query("select o from EOffers o where o.valid=true and o.status.statusCode in ('s001','s003') and o.appRef.id=:appRefId and o.appRef.dealerReserve>0 order by o.createdOn")
    List<EOffers> findOffersPendingForSellerCounter(Long appRefId);
    @Query(value = "select  count(*)  from EOfferQuotes oq,EAppraiseVehicle av  where oq.createdOn in(" +
            "            select max(q.createdOn)FROM EOfferQuotes q where q.offers.id in" +
            "            (select o.id from EOffers o  where o.status.id not in(4,5,8) and o.appRef.id=:appRefId and o.valid=true) and q.valid=true" +
            "    group by q.offers.id ) and av.id =:appRefId and av.valid=true and oq.valid=true  and oq.buyerQuote < :newReservePrice")
    Integer anyOfferLowerThanReserve(Long appRefId,Double newReservePrice);




}
