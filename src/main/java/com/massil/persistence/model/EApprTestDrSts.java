package com.massil.persistence.model;

/**
 * This is an Entity class EApprTestDrSts
 * @author Rupesh Khade
 */


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
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.time.chrono.MinguoDate;

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
@Indexed(index = "APR_TEST_DR_STATUS")
@Table(name = "APR_TEST_DR_STATUS")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "TD_STATUS_ID"))
@AttributeOverride(name = "valid",column= @Column(name = "IS_ACTIVE"))
public class EApprTestDrSts extends TransactionEntity {

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APR_TEST_DR_STATUS_SEQ")
    @GenericGenerator(name = "APR_TEST_DR_STATUS_SEQ", type = CustomIDGenerator.class)
    private Long id;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "APPR_REF_ID ", nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle appraisalRef;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehAcCondn apprVehAcCondn;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehInteriCondn apprVehInteriCondn;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehOilCondn apprVehOilCondn;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehStereoSts apprVehStereoSts;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehTireCondn apprVehTireCondn;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EVehDWarnLightStatus vehDrWarnLightSts;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private ESteeringFeelStatus steeringFeel;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EBookAndKeys bookAndKeys;


    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprBrakingSysSts apprBrakingSysSts;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprEnginePer apprEnginePer;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EApprVehTransSts apprTransmissionSts;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus", cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private ERearWndwDmg rearWindow;

    @Column(name = "ENGINE_TYPE")
    private String engineType;
    @Column(name = "TRANSMISSION_TYPE ")
    @FullTextField
    private String transmissionType;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "DOOR_LOCKS")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes doorLock ;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "F_L_WIN_STATUS")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes  fLWinStatus ;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "F_R_WIN_STATUS")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes  fRWinStatus ;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "R_L_WIN_STATUS")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes  rLWinStatus;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "R_R_WIN_STATUS")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes   rRWinStatus;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "INTR_COLOR")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes  intrColor ;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "EXTR_COLOR")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes  extrColor ;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "ROOF_TYPE")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private EConfigCodes   roofTypes ;


    @FullTextField
    @Column(name = "APR_FOLLOW_UP ")
    private String apprFollowUp;
    @FullTextField
    @Column(name = "APR_INV_STATUS ")
    private String apprInvenSts;
    @GenericField
    @Column(name = "EXTR_DMG_STATUS ")
    private Boolean externalDmgSts;
    @FullTextField
    @Column(name = "F_DR_SIDE_DMG_STS")
    private String frDrSideDmgSts;
    @FullTextField
    @Column(name = "F_DR_SIDE_DMG_DESC")
    private String frDrSideDmgTxtBox;
    @FullTextField
    @Column(name = "F_P_SIDE_DMG_STS ")
    private String frPassenSideDmgSts;
    @FullTextField
    @Column(name = "F_P_SIDE_DMG_DESC")
    private String frPassenSideDmgTxtBox;
    @FullTextField
    @Column(name = "PNTWRK_FD_SIDE_STS ")
    private String frDrSidePntWrkSts;
    @FullTextField
    @Column(name = "PNTWRK_FD_SIDE_STS_DESC")
    private String frDrSidePntWrkTxtBox;
    @FullTextField
    @Column(name = "PNTWRK_FP_SIDE_STS")
    private String frPassenSidePntWrk ;
    @FullTextField
    @Column(name = "PNTWRK_FP_SIDE_STS_DESC")
    private String frPassenSidePntWrkTxtBox;
    @FullTextField
    @Column(name = "PNTWRK_RD_SIDE_STS")
    private String rearDrSidePntWrk;
    @FullTextField
    @Column(name = "PNTWRK_RD_SIDE_STS_DESC")
    private String rearDrSidePntWrkTxtBox;
    @FullTextField
    @Column(name = "PNTWRK_RP_SIDE_STS")
    private String rearPassenSidePntWrk;
    @FullTextField
    @Column(name = "PNTWRK_RP_SIDE_STS_DESC")
    private String rearPassenSidePntWrkTxtBox;
    @GenericField
    @Column(name = "PNTWRK_STATUS")
    private Boolean paintWork;
    @FullTextField
    @Column(name = "R_DR_SIDE_DMG_STS ")
    private String rearDrSideDmgSts;
    @FullTextField
    @Column(name = "R_DR_SIDE_DMG_DESC")
    private String rearDrSideDmgTxtBox;
    @FullTextField
    @Column(name = "R_PASS_SIDE_DMG_STS ")
    private String rearPassenSideDmgSts;
    @FullTextField
    @Column(name = "R_PASS_SIDE_DMG_DESC")
    private String rearPassenSideDmgTxtBox;
    @GenericField
    @Column(name = "WS_BUY_FIG_STS ")
    private Boolean pushForBuyFig=false;
    @FullTextField
    @Column(name = "KEY_ASSURE_YES")
    private String keyAssureYes;
    @GenericField
    @Column(name = "SUBS_KEY_ASSURE")
    private Boolean subscribToKeyAssure;
    @GenericField
    @Column(name = "KEY_ASSURE_FILE")
    private String keyAssureFiles;
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "WIND_SHIELD_DMG")
    @Where(clause = "IS_ACTIVE = true")
    private  EConfigCodes  windShieldDmg ;
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EConfigCodes.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "TITLE_STS")
    @Where(clause = "IS_ACTIVE = true")
    private  EConfigCodes titleSt ;
    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "tdStatus",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private EAprVehImg aprVehImg;


}