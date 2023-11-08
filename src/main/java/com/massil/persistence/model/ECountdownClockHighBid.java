package com.massil.persistence.model;

import com.massil.persistence.generator.CustomIDGenerator;
import jakarta.persistence.*;
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
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.UUID;

@Indexed(index = "countdownclock_highbid")
@Entity
@Table(name = "countdownclock_highbid")
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
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "clock_highbid_id"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class ECountdownClockHighBid extends TransactionEntity{
    @Id
    @GenericField(aggregable = Aggregable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "clock_highbid_seq")
    @GenericGenerator(name = "clock_highbid_seq", type = CustomIDGenerator.class)
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "APPR_REF_ID ", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle appraisalRef;
    @GenericField
    private Integer timer=30;
    @GenericField
    private Double highBid;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EOfferQuotes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "QUOTE_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EOfferQuotes offersQuotes;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAutoBidJobs.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EAutoBidJobs autoBidJobs;
    @GenericField
    @Column(name = "job_runr_job_id")
    private UUID jobRunRTimerId;

}
