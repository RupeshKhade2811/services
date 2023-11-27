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
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;


import java.util.Date;

@Entity
@Table(name = "PAYMENT_DETAILS")
@Audited
@Indexed(index = "PAYMENT_DETAILS")
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
@AttributeOverride(name = "id", column = @Column(name = "PAYMENT_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class PaymentDetails extends TransactionEntity{
    @Id
    @GenericField(aggregable = Aggregable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "paym_dtls_seq")
    @GenericGenerator(name = "paym_dtls_seq", strategy= AppraisalConstants.CUSTOM_SEQUENCE_GENERATOR)
    private Long id;
    @FullTextField
    private String email;
    @FullTextField
    private String trxMsg;
    @FullTextField
    private String trxSts;
    @FullTextField
    private String planId;
    @GenericField
    private Date trxDate;
    @GenericField
    private Double amount;
    @FullTextField
    @Column(name = "transacid")
    private String transacId;
    @GenericField
    private Boolean subscription;
    @GenericField
    @Column(name = "totalquota")
    private Integer totalQuota;
    @GenericField
    @Column(name = "consumequota")
    private Integer consumeQuota;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    private EUserRegistration user;
}

