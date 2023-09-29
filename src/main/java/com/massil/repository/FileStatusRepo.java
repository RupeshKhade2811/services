package com.massil.repository;



import com.massil.persistence.model.EFileStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface FileStatusRepo extends JpaRepository<EFileStatus,Long>, JpaSpecificationExecutor<EFileStatus> {

    @Query(value = "select e from EFileStatus e where e.offers.id = :offerId and valid=true")
    List<EFileStatus> findByAppId(Long offerId);

    @Query(value = "select e from EFileStatus e where e.offers.id = :offerId and valid = true and e.module=:name")
    EFileStatus getFileRecrd(Long offerId,String name);
}
