package com.massil.persistence.model;
//@author:Rupesh Khade

import com.massil.persistence.generator.CustomIDGenerator;
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
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * This is an Entity class  EAppraiseVehicle
 */
@Indexed(index = "APPRAISAL_VEHICLE")
@Entity
@Table(name = "APPRAISAL_VEHICLE")
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
@AttributeOverride(name = "id", column = @Column(name = "APPR_REF_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EAppraiseVehicle extends TransactionEntity {

    @Id
    @GenericField(aggregable = Aggregable.YES)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "appr_veh_seq")
    @GenericGenerator(name = "appr_veh_seq", type = CustomIDGenerator.class)
    private Long id;
    @GenericField
    @Column(name="APPR_RETENSION_TIME")
    private Date apprRetensionTime;
    @GenericField
    @Column(name="APPR_VALUE")
    private Double appraisedValue;
    @GenericField
    @Column(name="BB_VALUE")
    private Double blackBookValue;
    @GenericField
    @Column(name = "DLR_RESERVE_VALUE")
    private Double dealerReserve ;
    @GenericField
    @Column(name = "DLR_ASK_PRICE")
    private Double delrRetlAskPrice ;
    @GenericField
    @Column(name = "CNSR_ASK_PRICE")
    private Double consumerAskPrice ;
    @FullTextField
    @Column(name = "CLT_FIRST_NAME")
    private String clientFirstName;
    @FullTextField
    @Column(name = "CLT_LAST_NAME")
    private String clientLastName;
    @FullTextField
    @Column(name = "CLT_PH_NUMBER")
    private String clientPhNum;
    @GenericField
    @Column(name="FIELD1")
    private Boolean field1=Boolean.FALSE;
    @GenericField
    @Column(name="FIELD2")
    private Boolean field2=Boolean.FALSE;
    @GenericField
    @Column(name="INVENTORY_DATE")
    private Date invntryDate;
    @FullTextField
    @Column(name="PROF_OPINION")
    private String profOpinion ;
    @FullTextField
    @Column(name = "VEHICLE_DESC")
    private  String vehicleDesc ;
    @GenericField
    @Column(name = "STOCK_NUMBER")
    private Long stockNumber;
    @FullTextField
    @Column(name="MAKE")
    private String vehicleMake;
    @GenericField(aggregable = Aggregable.YES)
    @Column(name="MILES")
    private Long vehicleMileage;
    @FullTextField
    @Column(name="MODEL")
    private String  vehicleModel ;
    @FullTextField
    @Column(name="SERIES")
    private String  vehicleSeries;
    @GenericField(aggregable = Aggregable.YES)
    @Column(name="VEH_YEAR")
    private Long vehicleYear;
    @FullTextField
    @Column(name="VIN_NUMBER ")
    private String vinNumber;
    @FullTextField
    @Column(name = "INV_STATUS")
    private String invntrySts;
    @GenericField
    @Column(name = "IS_OFFER_MADE")
    private Boolean isOfferMade=false;
    @GenericField
    @Column(name = "IS_HOLD")
    private Boolean isHold=false;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "DLRS_USER_NAMES")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration dlrsUserNames;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(targetEntity = EDealerRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "DEALER_ID")
    @Where(clause = "IS_ACTIVE = true")
    private EDealerRegistration dealer;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(targetEntity = EUserRegistration.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID",nullable = false,updatable = false)
    @Where(clause = "IS_ACTIVE = true")
    private EUserRegistration user;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "appraisalRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private EApprTestDrSts tdStatus;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "apprRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<OBD2_PreStartMeasurement> preStart;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "apprRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<OBD2_TestDriveMeasurements> testDriveMeas;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "apprRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private ESignDet signDet;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "appRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<EUserWishlist> wishlist;

    @IndexedEmbedded(structure = ObjectStructure.NESTED)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "appRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<EOffers> offers;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "appraisalRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private EShipment shipment;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "appRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List< EOfferQuotes> offerQuotes;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "appraisalRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<EFileStatus> fileStatuses;

    @IndexedEmbedded(includeDepth = 1)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "appraisalRef",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private ECountdownClockHighBid clockHighBid;

}
