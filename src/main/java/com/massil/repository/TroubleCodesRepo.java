package com.massil.repository;

import com.massil.persistence.model.ETroubleCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface TroubleCodesRepo extends JpaRepository<ETroubleCodes,Long> {
    @Query("select e from ETroubleCodes e where e.valid=true and e.troubleCode=:trCodes")
    ETroubleCodes findByTroubleCodes(String trCodes);
}
