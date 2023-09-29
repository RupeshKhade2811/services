package com.massil.repository;


import com.massil.persistence.model.EAutoBidProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface AutoBidProcessRepo extends JpaRepository <EAutoBidProcess,Long>{


}
