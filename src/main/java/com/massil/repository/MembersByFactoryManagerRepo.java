package com.massil.repository;

import com.massil.persistence.model.MembersByFactoryManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembersByFactoryManagerRepo extends JpaRepository<MembersByFactoryManager,UUID>, JpaSpecificationExecutor<MembersByFactoryManager> {
    List<MembersByFactoryManager> findByFactoryManager(UUID fmUserId);
  Page<MembersByFactoryManager> findByFactoryManager(UUID fmUserId, Pageable pageable);

}
