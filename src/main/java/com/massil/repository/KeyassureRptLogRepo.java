package com.massil.repository;




import com.massil.persistence.model.KeyassureReportLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface KeyassureRptLogRepo extends JpaRepository<KeyassureReportLog,Long> {

    @Query(value = "select e from KeyassureReportLog e where e.user.id =:userId and e.appraisalRef.id=:apprId")
    KeyassureReportLog getRecord( UUID userId,Long apprId);
}
