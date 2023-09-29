package com.massil.repository;

import com.massil.persistence.model.EOfferQuotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface OfferQuotesRepo extends JpaRepository<EOfferQuotes,Long> {

    /**
     * Return EOfferQuotes based on offer Id
     * @param offerId this is the offer id
     * @return EOfferQuotes
     */

    EOfferQuotes findTopByOffersIdOrderByCreatedOnDesc(Long offerId);

    List<EOfferQuotes> findByOffersIdOrderByCreatedOnDesc(Long offerId);

    @Query(value = "SELECT e FROM EOfferQuotes e WHERE e.offers.id=:offerId AND e.createdOn = ("
            + "SELECT MAX(e1.createdOn) FROM EOfferQuotes e1 WHERE e1.offers.id=:offerId)")
    EOfferQuotes getLatestQuo(Long offerId);
    @Query(value = "SELECT e.id FROM EOfferQuotes e WHERE e.offers.id=:offerId AND e.createdOn = ("
            + "SELECT MAX(e1.createdOn) FROM EOfferQuotes e1 WHERE e1.offers.id=:offerId)")
    Long getLatestQuotID(Long offerId);

}
