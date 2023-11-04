package com.massil.persistence.model;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.engine.backend.types.Sortable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


import java.util.Date;


/**
 * This is a class used for auditing
 */


@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class TransactionEntity extends IdEntity {

    @CreatedBy
    @FullTextField
    private String createdBy;


    @CreatedDate
    @GenericField(aggregable = Aggregable.YES,sortable = Sortable.YES)
    //@DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private Date createdOn;


    @LastModifiedBy
    @FullTextField
    private String modifiedBy;

    @LastModifiedDate
    @GenericField(aggregable = Aggregable.YES,sortable = Sortable.YES)
    //@DateTimeFormat(pattern = "MM/dd/yyyy HH:mm:ss")
    private Date modifiedOn;


}
