package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

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
@Entity
@Indexed(index = "SHIPMENT")
@Table(name = "SHIPMENT")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "SHIPMENT_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EShipment extends TransactionEntity {


    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHIPMENT_SEQ")
    @GenericGenerator(name = "SHIPMENT_SEQ", type = CustomIDGenerator.class)
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "APPR_REF_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle appraisalRef;


    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EOffers.class, fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "OFFER_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EOffers offers;

    @Column(name = "WS_BUY_FIG_STS")
    @GenericField
    private Boolean pushForBuyFig=false;
    @GenericField
    private Boolean buyerAgreed=false;
    @GenericField
    private Boolean sellerAgreed=false;
    @FullTextField
    private String buyerSign;
    @FullTextField
    private String sellerSign;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_USER_ID")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration buyerUserId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_USER_ID")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration sellerUserId;
    @GenericField
    private Double saleFee;
    @GenericField
    private Double buyFee;

}
