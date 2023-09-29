package com.massil.repository;


import com.massil.persistence.model.EFtryTraining;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface FtryTrainingRepo extends JpaRepository<EFtryTraining,Long> {

    @Query(value = "SELECT e FROM EFtryTraining e where e.valid=true")
    List<EFtryTraining> findAllByValid();
    @Query(value = "SELECT e FROM EFtryTraining e where e.valid=true and e.viewer='PU'")
    List<EFtryTraining> findAllVideoForPU();
    @Query(value = "SELECT e FROM EFtryTraining e where e.valid=true and e.viewer='D'")
    List<EFtryTraining> findAllVideoForD();



}
