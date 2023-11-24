package com.massil.repository;



import com.massil.persistence.model.D2DlrList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface D2DlrListRepo extends JpaRepository<D2DlrList,Long>, JpaSpecificationExecutor<D2DlrList> {

    List<D2DlrList> findByDealerAdmin(UUID userId);
}
