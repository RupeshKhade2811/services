package com.massil.repository;


import com.massil.persistence.model.InventoryFilterParamView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public interface InventoryFilterParamViewRepo extends JpaRepository<InventoryFilterParamView,Long>, JpaSpecificationExecutor<InventoryFilterParamView> {
}
