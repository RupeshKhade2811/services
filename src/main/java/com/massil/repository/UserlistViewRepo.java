package com.massil.repository;


import com.massil.persistence.model.UserListView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserlistViewRepo extends JpaRepository<UserListView,Long>, JpaSpecificationExecutor<UserListView> {

}
