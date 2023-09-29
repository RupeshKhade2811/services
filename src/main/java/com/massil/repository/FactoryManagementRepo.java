package com.massil.repository;


import com.massil.persistence.model.EFactoryManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface FactoryManagementRepo extends JpaRepository<EFactoryManagement, Long> {

    @Query("select count(m.id) from EFactoryManagement m where m.factoryUser.id=:factoryUSerId and m.user.id=:userId and m.valid=true and m.signTo=null")
    int findRecordAvailable(UUID factoryUSerId, UUID userId);

    @Query("select m from EFactoryManagement m ")
    EFactoryManagement findRecordByUSer();
}
