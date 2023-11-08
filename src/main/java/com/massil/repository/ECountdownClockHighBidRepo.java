package com.massil.repository;

import com.massil.persistence.model.ECountdownClockHighBid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ECountdownClockHighBidRepo extends JpaRepository<ECountdownClockHighBid,Long> {
    @Query(value = "select c from ECountdownClockHighBid c where c.appraisalRef.id=:appRefId and c.valid=true")
    ECountdownClockHighBid findByAppRefId(Long appRefId);
}
