package com.massil.repository;


import com.massil.persistence.model.OBD2_PreStartMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface PreStartMeasurementRepo extends JpaRepository<OBD2_PreStartMeasurement,Long> {
    @Query("select p from OBD2_PreStartMeasurement p where p.valid=true and p.id=:preStartId")
    OBD2_PreStartMeasurement findByPreStartId(Long preStartId);

    @Query("select p from OBD2_PreStartMeasurement p where p.valid=true and p.apprRef.id=:apprId")
    OBD2_PreStartMeasurement getPreStartMeasByApprRef(Long apprId);
}
