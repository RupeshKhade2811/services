package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
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
@Indexed(index = "AUTO_BID_JOBS")
@Table(name = "AUTO_BID_JOBS")
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "job_id"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EAutoBidJobs extends TransactionEntity{

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTO_BID_JOBS_SEQ")
    @GenericGenerator(name = "AUTO_BID_JOBS_SEQ", strategy= AppraisalConstants.CUSTOM_SEQUENCE_GENERATOR)
    private Long id;
    @GenericField
    private Integer status;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EOffers.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "OFFER_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EOffers offerId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "APPR_REF_ID ", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EAppraiseVehicle appraisalRef;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAutoBidBump.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "bump ", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EAutoBidBump bump;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "autoBidJobs",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private ECountdownClockHighBid clockHighBid;


}
