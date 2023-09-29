package com.massil.repository;

import com.massil.persistence.model.OfferPdf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OfferPdfRepo extends JpaRepository<OfferPdf,Long> {

    @Query("select t from OfferPdf t WHERE t.createdOn BETWEEN :start AND :end")
    List<OfferPdf> getOfferList(Date start, Date end);

    @Query("select t from OfferPdf t WHERE t.createdOn BETWEEN :start AND :end")
    Page<OfferPdf> getOffer(Date start, Date end,Pageable pageable);
}
