package com.massil.repository;


import com.massil.persistence.model.DlrInvntryView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface DlrInvntryViewRepo extends JpaRepository<DlrInvntryView,Long>, JpaSpecificationExecutor<DlrInvntryView> {


    @Query(value = "SELECT e FROM DlrInvntryView e WHERE e.user in (:userId) AND " +
            "e.invntrySts = 'inventory' AND e.valid = true")
    List<DlrInvntryView> allAppraisalCards(List<UUID> userId);


    @Query("SELECT d FROM DlrInvntryView d WHERE d.user in (:userId) AND "
        +"d.invntrySts = 'inventory' AND d.valid = true")
    List<DlrInvntryView> dlrInvntryListTable(List<UUID> userId, Pageable pageable);

    @Query("SELECT DISTINCT e.vehicleMake FROM DlrInvntryView e where e.user in (:userId) and e.invntrySts ='inventory' and e.valid = true ORDER BY e.vehicleMake asc")
    List<String> dlrInvntryVehMakeDropDown(List<UUID> userId);


}
