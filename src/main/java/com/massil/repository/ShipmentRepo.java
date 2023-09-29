package com.massil.repository;

import com.massil.persistence.model.EShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface ShipmentRepo extends JpaRepository<EShipment,Long> {
    @Query(value = "select e from EShipment e where e.valid=true and e.id=:shipId and e.pushForBuyFig=false")
    EShipment findByShipId(Long shipId);

    @Query(value = "select e from EShipment e where e.valid=true and e.appraisalRef.id=:apprId and e.pushForBuyFig=false")
    EShipment findByApprId(@Param("apprId") Long apprId);


}
