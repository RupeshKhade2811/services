package com.massil.repository;


import com.massil.persistence.model.SearchFactoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface SearchFactoryViewRepo extends JpaRepository<SearchFactoryView,Long>, JpaSpecificationExecutor<SearchFactoryView> {
}
