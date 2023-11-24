package com.massil.repository;



import com.massil.persistence.model.PaymentSubs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Repository
@Transactional
public interface PaymentSubsRepo extends JpaRepository<PaymentSubs,Long> {


    @Query(value = "SELECT e FROM PaymentSubs e WHERE e.user.id=:userId and e.valid= 'true' AND e.createdOn = " +
            "(SELECT MAX(e1.createdOn) FROM PaymentDetails e1 WHERE e1.user.id=:userId )")
    PaymentSubs findByUserId(UUID userId);
}
