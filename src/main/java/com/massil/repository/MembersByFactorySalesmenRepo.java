package com.massil.repository;


import com.massil.persistence.model.MembersByFactorySalesmen;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository

public interface MembersByFactorySalesmenRepo extends JpaRepository<MembersByFactorySalesmen,UUID>, JpaSpecificationExecutor<MembersByFactorySalesmen> {
    List<MembersByFactorySalesmen> findByFactorySalesman(UUID fsUserId );
    Page<MembersByFactorySalesmen> findByFactorySalesman(UUID fsUserId, Pageable pageable);

}
