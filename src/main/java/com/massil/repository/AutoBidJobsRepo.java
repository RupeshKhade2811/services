package com.massil.repository;


import com.massil.persistence.model.EAutoBidJobs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Repository
public interface AutoBidJobsRepo extends JpaRepository<EAutoBidJobs,Long> {

    @Query("select e from EAutoBidJobs e where e.valid=true and e.status= 0 and (EXTRACT(epoch from (CURRENT_TIMESTAMP)) - EXTRACT(epoch from (e.createdOn))) >=1*60 order by e.createdOn")
    List<EAutoBidJobs> findPendingJobs();
    @Query("update EAutoBidJobs o set o.status=1 where o.id in(:jobIds) ")
    @Modifying
    int updatePendingJobsStatus(List<Long> jobIds);
    @Query("update EAutoBidJobs o set o.status=1 where o.id =:jobIds ")
    @Modifying
    int updatePendingJobStatus(Long jobIds);
}
