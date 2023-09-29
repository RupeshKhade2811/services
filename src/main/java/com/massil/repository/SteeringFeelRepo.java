package com.massil.repository;

import com.massil.persistence.model.ESteeringFeelStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface SteeringFeelRepo extends JpaRepository<ESteeringFeelStatus,Long> {

}
