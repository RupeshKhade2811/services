package com.massil.repository;


import com.massil.persistence.model.DlrSalesView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface DlrSalesRepo extends JpaRepository<DlrSalesView,Long>, JpaSpecificationExecutor<DlrSalesView> {

    @Query("SELECT e FROM DlrSalesView e WHERE e.sellerUserId in (:userId) "
            + "AND e.valid = true AND e.isTradeBuy = false AND e.statusCode in ('s004','s005') AND e.createdOn BETWEEN :start AND :end")
    List<DlrSalesView> findBySellerUserIdSold(List<UUID> userId,Date start, Date end);


    @Query("SELECT e FROM DlrSalesView e WHERE e.buyerUserId in (:userId) "
            + "AND e.valid = true AND e.isTradeBuy = false AND e.statusCode in ('s004','s005') AND e.createdOn BETWEEN :start AND :end")
    List<DlrSalesView> findByPrchase(List<UUID> userId, Date start, Date end);


    @Query("SELECT d FROM DlrSalesView d WHERE d.buyerUserId in (:userId) AND d.valid = true AND d.isTradeBuy = false "
            +"AND d.statusCode in ('s004','s005') AND d.createdOn BETWEEN :start AND :end")
    Page<DlrSalesView> prchaseListTable(List<UUID> userId, Pageable pageable, Date start, Date end);

    @Query("SELECT d FROM DlrSalesView d WHERE d.sellerUserId in (:userId) AND d.valid = true AND d.isTradeBuy = false "
            +"AND d.statusCode in ('s004','s005') AND d.createdOn BETWEEN :start AND :end")
    Page<DlrSalesView> saleListTable(List<UUID> userId, Pageable pageable, Date start, Date end);



}
