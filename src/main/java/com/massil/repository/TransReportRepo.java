package com.massil.repository;

import com.massil.persistence.model.TransactionReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TransReportRepo extends JpaRepository<TransactionReport,Long>, JpaSpecificationExecutor<TransactionReport> {
    @Query("SELECT t FROM TransactionReport t WHERE t.userId = :id AND t.createdOn BETWEEN :start AND :end")
    List<TransactionReport>getTranList(UUID id, Date start, Date end);

    @Query("select t from TransactionReport t WHERE t.createdOn BETWEEN :start AND :end")
    List<TransactionReport>getSoldList(Date start, Date end);

    @Query("select t from TransactionReport t")
    List<TransactionReport>getOfferList();

    @Query("SELECT t FROM TransactionReport t WHERE t.userId = :id AND t.createdOn BETWEEN :start AND :end")
    Page<TransactionReport> tranListTable(UUID id,Pageable pageable, Date start, Date end);

    @Query("SELECT t FROM TransactionReport t WHERE t.createdOn BETWEEN :start AND :end")
    Page<TransactionReport> soldTable(Date start, Date end,Pageable pageable);



}
