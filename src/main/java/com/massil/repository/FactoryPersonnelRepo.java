package com.massil.repository;



import com.massil.persistence.model.FactoryPersonnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FactoryPersonnelRepo extends JpaRepository<FactoryPersonnel,Long>, JpaSpecificationExecutor<FactoryPersonnel> {
}
