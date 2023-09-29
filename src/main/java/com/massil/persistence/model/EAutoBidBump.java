package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

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
@Indexed(index = "AUTO_BID_BUMP")
@Table(name = "AUTO_BID_BUMP")
@Data
@EqualsAndHashCode(callSuper = true)
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "auto_bid_bump_id"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EAutoBidBump extends TransactionEntity {
    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AUTO_BID_BUMP_SEQ")
    @GenericGenerator(name = "AUTO_BID_BUMP_SEQ",type = CustomIDGenerator.class)
    private Long id;
    @GenericField
    private Double startPrice;
    @GenericField
    private Double endPrice;
    @GenericField
    private Double bump;
}
