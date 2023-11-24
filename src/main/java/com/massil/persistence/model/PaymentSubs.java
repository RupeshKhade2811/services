package com.massil.persistence.model;



import com.massil.constants.AppraisalConstants;
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



@Entity
@Table(name = "PAYMENT_SUBS")
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
@AttributeOverride(name = "id", column = @Column(name = "PAYMENTSUBS_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class PaymentSubs extends TransactionEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paym_subs_seq")
    @GenericGenerator(name = "paym_subs_seq", strategy= AppraisalConstants.CUSTOM_SEQUENCE_GENERATOR)
    private Long id;

    @Column(name = "CUST_VAULT_ID")
    private String customerVaultId;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    private EUserRegistration user;

}
