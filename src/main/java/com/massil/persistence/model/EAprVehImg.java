package com.massil.persistence.model;

import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
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
@Indexed(index = "APR_VEH_IMG")
@Table(name = "APR_VEH_IMG")
@EqualsAndHashCode(callSuper = true)
@Data
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "VEH_IMG_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EAprVehImg extends TransactionEntity{

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APR_VEH_IMG_SEQ")
    @GenericGenerator(name = "APR_VEH_IMG_SEQ", type = CustomIDGenerator.class)
    private Long id;

    @Column(name = "F_DRV_S_DMG_IMG")
    @FullTextField
    private String frDrSideDmgPic ;
    @FullTextField
    @Column(name = "F_PAX_S_DMG_IMG")
    private String frPassenSideDmgPic ;
    @FullTextField
    @Column(name = "R_DRV_S_DMG_IMG")
    private String rearDrSideDmgPic ;
    @FullTextField
    @Column(name = "R_PAX_S_DMG_IMG")
    private String rearPassenSideDmgPic ;
    @FullTextField
    @Column(name = "F_DRV_S_PNTWRK_IMG")
    private String frDrSidePntWrkPic ;
    @FullTextField
    @Column(name = "F_PAX_S_PNTWRK_IMG")
    private String frPassenSidePntWrkPic ;
    @FullTextField
    @Column(name = "R_DRV_S_PNTWRK_IMG")
    private String rearDrSidePntWrkPic ;
    @FullTextField
    @Column(name = "R_PAX_S_PNTWRK_IMG")
    private String rearPassenSidePntWrkPic ;
    @FullTextField
    @Column(name = "VEH_IMG1")
    private String vehiclePic1 ;
    @FullTextField
    @Column(name = "VEH_IMG2")
    private String vehiclePic2 ;
    @FullTextField
    @Column(name = "VEH_IMG3")
    private String vehiclePic3 ;
    @FullTextField
    @Column(name = "VEH_IMG4")
    private String vehiclePic4 ;
    @FullTextField
    @Column(name = "VEH_IMG5")
    private String vehiclePic5 ;
    @FullTextField
    @Column(name = "VEH_IMG6")
    private String vehiclePic6 ;
    @FullTextField
    @Column(name = "VEH_IMG7")
    private String vehiclePic7 ;
    @FullTextField
    @Column(name = "VEH_IMG8")
    private String vehiclePic8 ;
    @FullTextField
    @Column(name = "VEH_IMG9")
    private String vehiclePic9 ;
    @FullTextField
    @Column(name = "ENG_VIDEO")
    private String vehicleVideo1 ;


    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EApprTestDrSts.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "TD_STATUS_ID", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprTestDrSts tdStatus;



}
