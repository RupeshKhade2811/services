package com.massil.repository;


import com.massil.persistence.model.EApprVehTransSts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprTransmStsRepo extends JpaRepository<EApprVehTransSts,Long> {
}
