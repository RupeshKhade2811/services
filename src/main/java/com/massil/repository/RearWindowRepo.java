package com.massil.repository;

import com.massil.persistence.model.ERearWndwDmg;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
@Transactional
public interface RearWindowRepo extends JpaRepository<ERearWndwDmg,Long> {
}
