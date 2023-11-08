package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
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

import jakarta.persistence.*;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

import java.util.List;

/**
 * This EStatus class which will give information about
 * buying or selling status of user or dealer
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
@Entity
@Indexed(index = "OFFER_STATUS")
@Table(name = "OFFER_STATUS")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "STATUS_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EStatus extends TransactionEntity{
    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OFFER_STATUS_SEQ")
    @GenericGenerator(name = "OFFER_STATUS_SEQ", type = CustomIDGenerator.class)
    private Long id;
    @FullTextField
    private String buyerStatus;
    @FullTextField
    private String sellerStatus;
    @Column(name = "STATUS_DESC")
    @FullTextField
    private String offerStatus;
    @GenericField
    private Long color;
    @FullTextField
    private String statusCode;
    @FullTextField
    private String status;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "status",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private ENotificationTable notification;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "status",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EOffers> offers;

}
