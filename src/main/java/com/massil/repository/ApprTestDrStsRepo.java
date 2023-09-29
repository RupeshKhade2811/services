package com.massil.repository;
//Author:Rupesh khade


import com.massil.persistence.model.EApprTestDrSts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface ApprTestDrStsRepo extends JpaRepository<EApprTestDrSts,Long> {


}
