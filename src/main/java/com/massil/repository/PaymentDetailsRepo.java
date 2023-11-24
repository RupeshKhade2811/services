package com.massil.repository;


import com.massil.persistence.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;


@Repository
@Transactional
public interface PaymentDetailsRepo extends JpaRepository<PaymentDetails,Long> {

    @Query(value = "SELECT e FROM PaymentDetails e WHERE e.user.id=:userId  AND e.createdOn = ("
            + "SELECT MAX(e1.createdOn) FROM PaymentDetails e1 WHERE e1.user.id=:userId AND e1.trxSts= 'success')")
    PaymentDetails getUser(UUID userId);

    @Query("SELECT e FROM PaymentDetails e WHERE e.valid = true AND e.transacId IN :transacIds")
    List<PaymentDetails> findByTransacIds(@Param("transacIds") List<String> transacIds);
}
