package com.massil.repository;


import com.massil.persistence.model.AppraisalFilterParamView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AppraisalFilterParamViewRepo extends JpaRepository<AppraisalFilterParamView,Long>, JpaSpecificationExecutor<AppraisalFilterParamView> {
}
