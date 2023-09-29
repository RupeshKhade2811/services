package com.massil.repository;


//kalyan

import com.massil.persistence.model.EApprVehInteriCondn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ApprVehInteriCondnRepo extends JpaRepository<EApprVehInteriCondn,Long> {
}
