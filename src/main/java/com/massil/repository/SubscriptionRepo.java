package com.massil.repository;

import com.massil.persistence.model.EFactorySubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface SubscriptionRepo extends JpaRepository<EFactorySubscription,Long> {

    @Query("select s from EFactorySubscription s where s.user.id =:userId and s.valid = true and s.unSubscription=null")
    EFactorySubscription findRecord(UUID userId);

}
