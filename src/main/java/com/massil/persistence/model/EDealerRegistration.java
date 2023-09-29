package com.massil.persistence.model;
// authorName:YogeshKumarV


import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
import lombok.Getter;
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

import java.util.List;

/**
 * This is an Entity class  EDealerRegistration
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
@Indexed(index = "DEALER_REG")
@Table(name = "DEALER_REG")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@AttributeOverride(name = "id", column = @Column(name = "DEALER_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EDealerRegistration extends TransactionEntity {

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEALER_REG_SEQ")
    @GenericGenerator(name = "DEALER_REG_SEQ", type = CustomIDGenerator.class)
    private Long id;
    @FullTextField
    private String name;
    @FullTextField
    private String firstName;
    @FullTextField
    private String lastName;
    @Column(name ="APT_NUMBER")
    @FullTextField
    private String aptmentNumber;
    @FullTextField
    private String city;
    @FullTextField
    private String country;
    @FullTextField
    private String email;
    @FullTextField
    private String password;
    @FullTextField
    private String phoneNumber;
    @Column(name = "PROFILE_PIC")
    @FullTextField
    private String profilePicture;
    @FullTextField
    private String state;
    @FullTextField
    private String streetAddress;
    @FullTextField
    private String zipCode;
    @FullTextField
    private String latitude;
    @FullTextField
    private String longitude;
    @Column(name = "TAX_CERT")
    @FullTextField
    private String taxCertificate;
    @Column(name = "DS_NAME", unique = true)
    @FullTextField
    private String dealershipNames;
    @Column(name = "DS_ADDR")
    @FullTextField
    private String dealershipAddress;
    @Column(name = "DS_STREET")
    @FullTextField
    private String dealershipStreet;
    @Column(name = "DS_CITY")
    @FullTextField
    private String dealershipCity;
    @Column(name = "DS_ZIP_CODE")
    @FullTextField
    private String dealershipZipCode;
    @Column(name = "DS_PHONE_NO")
    @FullTextField
    private String dealershipPhNum;
    @Column(name = "DS_PIC")
    @FullTextField
    private String dealerPic;
    @Column(name = "DS_LIC")
    @FullTextField
    private String dealerLicense;
    @Column(name = "CORP_NAME")
    @FullTextField
    private String corporationName;
    @FullTextField
    private String dealerCert;
    @FullTextField
    private String status;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @ManyToOne(targetEntity = ECompany.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "COMP_ID",nullable = false)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    private ECompany company;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "dealer",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EAppraiseVehicle> appraiseVehicles;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(mappedBy = "dealer")
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private EUserRegistration user;





}
