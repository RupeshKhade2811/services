package com.massil.repository;

import com.massil.persistence.model.ModelView;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelViewRepo extends JpaRepository<ModelView,String > {
}
