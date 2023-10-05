package com.massil.persistence.model;

import com.factory.appraisal.factoryService.constants.AppraisalConstants;
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

import javax.persistence.*;
import java.util.UUID;

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
@Table(name = "OTP")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "OTP_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class ECreateOtp extends TransactionEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OTP_SEQ")
    @GenericGenerator(name = "OTP_SEQ", strategy= AppraisalConstants.CUSTOM_SEQUENCE_GENERATOR)
    private Long id;
    private String email;
    private String otp;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY,cascade = {CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    @JoinColumn(name = "USER_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    private EUserRegistration user;

}
