package com.massil.repository;


import com.massil.persistence.model.AllDealersView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AllDealersViewRepo extends JpaRepository<AllDealersView,Long>, JpaSpecificationExecutor<AllDealersView> {

    @Query(value = "select e from AllDealersView e where e.companyId = null")
    Page<AllDealersView> getDlrListOfcompNameNull(Pageable pageable);
}
