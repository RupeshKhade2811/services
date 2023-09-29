package com.massil.repository;



import com.massil.persistence.model.D2DlrList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface D2DlrListRepo extends JpaRepository<D2DlrList,Long>, JpaSpecificationExecutor<D2DlrList> {



}
