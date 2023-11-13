package com.massil.repository;


import com.massil.persistence.model.EAutoBidBump;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AutoBidBumpRepo extends JpaRepository<EAutoBidBump,Long> {

    @Query("select e from EAutoBidBump e where e.startPrice< :buyerQuote and e.endPrice>= :buyerQuote and valid =true")
    EAutoBidBump findBump(Double buyerQuote);


}
