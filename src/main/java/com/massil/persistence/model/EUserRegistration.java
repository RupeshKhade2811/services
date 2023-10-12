package com.massil.persistence.model;
//@Author: Yudhister vijay

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

import jakarta.persistence.*;
import org.hibernate.search.engine.backend.types.ObjectStructure;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * This is an Entity class  EUserRegistration
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
@Indexed(index = "USER_REG")
@Entity
@Table(name = "USER_REG")
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@DynamicInsert
@AttributeOverride(name = "id", column = @Column(name = "USER_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EUserRegistration extends TransactionEntity {

/*    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_REG_SEQ")
    @GenericGenerator(name = "USER_REG_SEQ", type = CustomIDGenerator.class)*/
//    private Long id;
    @Id
    @GenericField
    private UUID id;
    @Column(name = "APT_NUMBER")
    @FullTextField
    private String apartmentNumber;
    @FullTextField
    private String city;
    @FullTextField
    private String email;
    @FullTextField
    private String firstName;
    @FullTextField
    private String lastName;
    @FullTextField
    private String password;
    @FullTextField
    private String phoneNumber;
    @FullTextField
    private String state;
    @FullTextField
    private String streetAddress;
    @Column(unique = true)
    @FullTextField
    private String userName;
    @FullTextField
    private String zipCode;
    @Column(name = "PROFILE_PIC")
    @FullTextField
    private String profilePicture;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToOne(targetEntity = EDealerRegistration.class, fetch = FetchType.EAGER)
    @JoinColumn(name = "DEALER_ID",nullable = true)
    @Where(clause = "IS_ACTIVE=true")
    @IndexedEmbedded(includeDepth = 1)
    private EDealerRegistration dealer;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<ERoleMapping> roleMapping;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EAppraiseVehicle> appraiseVehicles;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "dlrsUserNames",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EAppraiseVehicle> appraiseVehiclesOfDlrsUserNames;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "factoryManager",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<ERoleMapping> factoryManagerRoleMapping;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "factorySalesman",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<ERoleMapping> factorySalesmanRoleMapping;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "manager",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<ERoleMapping> managerRoleMapping;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "buyerUserId",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EShipment> buyerUserIdShipments;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "sellerUserId",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EShipment> sellerUserIdShipments;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EUserWishlist> userWishlists;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EFtryTraining> ftryTrainings;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "sellerUserId",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EOffers> sellersOffers;
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "buyerUserId",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    @IndexedEmbedded(includeDepth = 1)
    private List<EOffers> buyersOffers;

    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    @Where(clause = "IS_ACTIVE = true")
    private List<ECreateOtp> otp;




}