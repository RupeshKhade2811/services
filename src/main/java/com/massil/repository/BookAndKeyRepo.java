package com.massil.repository;


import com.massil.persistence.model.EBookAndKeys;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Transactional
@Repository
public interface BookAndKeyRepo extends JpaRepository<EBookAndKeys,Long> {

}
