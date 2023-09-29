package com.massil.repository;


import com.massil.persistence.model.EDealerRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface DealerRegistrationRepo extends JpaRepository<EDealerRegistration,Long>, JpaSpecificationExecutor<EDealerRegistration> {
    /**
     * This method returns the valid  EDealerRegistration object
     * @param dealerId This is the dealerId field of EDealerRegistration class
     * @author YogeshKumarV
     * @return EDealerRegistration
     */
    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.id=:dealerId")
    EDealerRegistration findDealerById(Long dealerId);

    /**
     *it will check the dealers username is present
     * @param name
     * @return EDealer registration
     */


    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.name=:name")
    EDealerRegistration chkDlrUsrNamePresent(String name);

    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.company.id = null")
    Page<EDealerRegistration> getDlrListOfcompNameNull(Pageable pageable);

    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.dealershipNames=:dealerShipName")
    List<EDealerRegistration> chkDlrShpName(String dealerShipName);

    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.status='pending'")
    List<EDealerRegistration> getPendingDlrList(Pageable pageable);

    @Query(value = "select e from EDealerRegistration e where e.valid=true and e.name=:name")
    EDealerRegistration findByEmail(String name);
}
