package com.massil.repository;



import com.massil.persistence.model.CorporateAdminView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CorporateAdminViewRepo extends JpaRepository<CorporateAdminView,Long>, JpaSpecificationExecutor<CorporateAdminView> {

}
