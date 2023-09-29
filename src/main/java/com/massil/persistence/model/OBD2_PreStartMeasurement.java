package com.massil.persistence.model;

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

@Entity
@Indexed(index = "PRESTART_MEASURE")
@Table(name = "PRESTART_MEASURE")
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
@AttributeOverride(name = "id", column = @Column(name = "PS_MEASURE_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class OBD2_PreStartMeasurement extends TransactionEntity {
    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRESTART_MEASURE_SEQ")
    @GenericGenerator(name = "PRESTART_MEASURE_SEQ", type = CustomIDGenerator.class)
    private Long id;
    @Column(name = "SC_VIN")
    @FullTextField
    private String scannedVin;
    @FullTextField
    private String engineTemp;
    @FullTextField
    private String batteryVoltage;
    @FullTextField
    private String fuelPressure;
    @Column(name = "WARM_UPS_LMC")
    @FullTextField
    private String warmUps;
    @Column(name = "TIME_SINCE")
    @FullTextField
    private String timeSince;
    @Column(name = "MILE_LMC")
    @FullTextField
    private String mileSince;
    @Column(name = "CURT_TROUBLE_CODES")
    @FullTextField
    private String currentTroubleCodes;
    @Column(name = "PNDG_TROUBLE_CODES")
    @FullTextField
    private String pendingTroubleCodes;
    @FullTextField
    private String odometer;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EAppraiseVehicle.class, fetch = FetchType.LAZY)
    @JoinColumn(name="APPR_REF_ID",nullable = false)
    @Where(clause ="IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EAppraiseVehicle apprRef;

}
