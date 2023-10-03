package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import java.util.List;

/**
 * This Entity class EOffers
 */

@Audited
@AuditOverrides({
        @AuditOverride(forClass=TransactionEntity.class, name="createdBy", isAudited=true),
        @AuditOverride(forClass=TransactionEntity.class, name="createdOn", isAudited=true),
        @AuditOverride(forClass=TransactionEntity.class, name="modifiedBy", isAudited=true),
        @AuditOverride(forClass=TransactionEntity.class, name="modifiedOn", isAudited=true),
        @AuditOverride(forClass= IdEntity.class, name="version",isAudited=true),
        @AuditOverride(forClass=IdEntity.class, name="sourceSystem", isAudited=true),
        @AuditOverride(forClass= IdEntity.class, name="valid", isAudited=true)
})
@Indexed(index = "OFFERS")
@Entity
@Table(name = "OFFERS")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "OFFER_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EOffers extends TransactionEntity{

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFERS_SEQ")
    @GenericGenerator(name = "OFFERS_SEQ", type = CustomIDGenerator.class)
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "APPR_REF_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle appRef;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "offers", cascade = CascadeType.ALL)
    @IndexedEmbedded(includeDepth = 1)
    private List<EOfferQuotes> offerQuotes;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EStatus.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded
    private EStatus status;

    @GenericField
    private Double price;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "SLR_USER_ID",nullable = false,updatable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration sellerUserId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EDealerRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "SLR_DLR_ID",nullable = false,updatable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EDealerRegistration sellerDealerId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_USER_ID",nullable = false,updatable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration buyerUserId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EDealerRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_DLR_ID",nullable = false,updatable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EDealerRegistration buyerDealerId;

    @GenericField
    private Integer makeNewOffer;
    @GenericField
    private Boolean isTradeBuy=false;
    @GenericField
    private Boolean buyerAccept=false;
    @GenericField
    private Boolean buyerCounter=false;
    @GenericField
    private Boolean buyerDecline;
    @GenericField
    @Column(name="SLR_ACCEPT")
    private Boolean sellerAccept;
    @GenericField
    @Column(name="SLR_COUNTER")
    private Boolean sellerCounter;
    @Column(name="SLR_DECLINE")
    @GenericField
    private Boolean sellerDecline;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "offers",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EShipment shipment;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "offers",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private ENotificationTable notifications;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "offers",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EFileStatus> fileStatuses;

}
