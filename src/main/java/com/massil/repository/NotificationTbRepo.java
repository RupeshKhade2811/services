package com.massil.repository;

import com.massil.persistence.model.ENotificationTable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
@Transactional
public interface NotificationTbRepo extends JpaRepository<ENotificationTable, Long> {

    /**
     * this will give the notification frequency
     * @param offerId receive from UI
     * @return Integer
     */
    @Query(value = "SELECT e.notifyFrequency FROM ENotificationTable e WHERE e.offers.id=:offerId  ")
    Integer GetNotificationFreq(Long offerId);

}
