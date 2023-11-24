package com.massil.repository;


import com.massil.persistence.model.UserListView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserlistViewRepo extends JpaRepository<UserListView,Long>, JpaSpecificationExecutor<UserListView> {

    @Query(value = "select r from UserListView r where r.roleGroup=:roleGroup")
    List<UserListView> findByRoleGroup(String roleGroup);
}
