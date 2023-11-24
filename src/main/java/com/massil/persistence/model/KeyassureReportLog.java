package com.massil.persistence.model;



import com.massil.constants.AppraisalConstants;
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

@Entity
@Table(name = "keyassure_Report_Log")
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
@AttributeOverride(name = "id", column = @Column(name = "ka_report_id"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class KeyassureReportLog extends TransactionEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ka_report_log_seq")
    @GenericGenerator(name = "ka_report_log_seq", strategy= AppraisalConstants.CUSTOM_SEQUENCE_GENERATOR)
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    private EUserRegistration user;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "APPR_REF_ID ", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    private EAppraiseVehicle appraisalRef;
}
