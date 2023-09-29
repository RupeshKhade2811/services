package com.massil.repository;


import com.massil.persistence.model.AppraisalFormView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface AppraisalFormViewRepo extends JpaRepository<AppraisalFormView,Long> {

    @Query("select a from AppraisalFormView a where a.createdOn between :start and :end")
    Page<AppraisalFormView> findAllByDate(Date start ,Date end, Pageable pageable);

    @Query("select a from AppraisalFormView a where a.createdOn between :start and :end")
    List<AppraisalFormView> findAllByDate(Date start , Date end);

}
