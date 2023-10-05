package com.massil.repository;

import com.massil.persistence.model.ECreateOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Repository
@Transactional
public interface CreateOtpRepo extends JpaRepository<ECreateOtp,Long> {
    @Query(value = "SELECT e FROM ECreateOtp e WHERE e.email=:email AND e.valid = true AND e.createdOn = ("
            + "SELECT MAX(e2.createdOn) FROM ECreateOtp e2 WHERE e2.email=:email)")
    ECreateOtp gettingLatestOtp(String email);

}
