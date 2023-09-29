package com.massil.persistence.model;
//@Author:Yudhister vijay

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
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;

/**
 * This is an Entity class  ESignDet
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
@Indexed(index = "E_SIGN_DET")
@Table(name = "E_SIGN_DET")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "E_SIGN_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class ESignDet extends TransactionEntity {
    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "E_SIGN_DET_SEQ")
    @GenericGenerator(name = "E_SIGN_DET_SEQ", type = CustomIDGenerator.class)
    private Long id;

    @Column(name = "E_SIGN_DOC")
    @FullTextField
    private String signDocument;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "APPR_REF_ID", nullable = false)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle apprRef;

    @Column(name = "ADJ_WS_POOR")
    @FullTextField
    private String adjustedWholePoor;
    @Column(name = "ADJ_WS_FAIR")
    @FullTextField
    private String adjustedWholeFair;
    @Column(name = "ADJ_WS_GOOD")
    @FullTextField
    private String adjustedWholeGood;
    @Column(name = "ADJ_WS_VG")
    @FullTextField
    private String adjustedWholeVeryGood;
    @Column(name = "ADJ_WS_XLNT")
    @FullTextField
    private String adjustedWholeExcelnt;
    @Column(name = "ADJ_FINANACE_POOR")
    @FullTextField
    private String adjustedFinanPoor;
    @Column(name = "ADJ_FINANACE_FAIR")
    @FullTextField
    private String adjustedFinanFair;
    @Column(name = "ADJ_FINANACE_GOOD")
    @FullTextField
    private String adjustedFinanGood;
    @Column(name = "ADJ_FINANACE_VG")
    @FullTextField
    private String adjustedFinanVeryGood;
    @Column(name = "ADJ_FINANACE_XLNT")
    @FullTextField
    private String adjustedFinanExcelnt;
    @Column(name = "ADJ_RETAIL_POOR")
    @FullTextField
    private String adjustedRetailPoor;
    @Column(name = "ADJ_RETAIL_FAIR")
    @FullTextField
    private String adjustedRetailFair;
    @Column(name = "ADJ_RETAIL_GOOD")
    @FullTextField
    private String adjustedRetailGood;
    @Column(name = "ADJ_RETAIL_VG")
    @FullTextField
    private String adjustedRetailVeryGood;
    @Column(name = "ADJ_RETAIL_XLNT")
    @FullTextField
    private String adjustedRetailExcelnt;


}