package com.massil.repository;

import com.massil.persistence.model.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public interface RoleRepo extends JpaRepository<ERole,Long> {

    /**
     * Returns ERole object
     * @param roleId
     * @return
     */
    @Query("select e from ERole e where e.id = :roleId")
    ERole findByRole(@Param("roleId") Long roleId);

    @Query("select e from ERole e where e.role=:role")
    ERole findRoleOfPublicUser(String role);
    @Query("select r from ERole r where r.valid=true")
    List<ERole> findRoleForDealerAndPublicUser();
    @Query("select e.id from ERole e where e.role='D1'")
    Long findRoleIdOfD1();



}
