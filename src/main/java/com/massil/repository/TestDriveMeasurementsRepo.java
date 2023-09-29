package com.massil.repository;


import com.massil.persistence.model.OBD2_TestDriveMeasurements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface TestDriveMeasurementsRepo extends JpaRepository<OBD2_TestDriveMeasurements,Long> {

    @Query("select e from OBD2_TestDriveMeasurements e where e.valid=true and e.apprRef.id=:apprId")
    List<OBD2_TestDriveMeasurements> getTestDrMeasByApprRef(Long apprId);

    @Query("select t from OBD2_TestDriveMeasurements t where t.valid=true and t.id=:testId")
    OBD2_TestDriveMeasurements findByTestDrId(Long testId);
}
