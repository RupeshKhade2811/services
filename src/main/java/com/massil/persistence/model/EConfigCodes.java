package com.massil.persistence.model;
/**
 * @author : YogeshKumarV,Rupesh Khade
 */


import com.massil.constants.AppraisalConstants;
import com.massil.persistence.generator.CustomIDGenerator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.bridge.mapping.annotation.ValueBridgeRef;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

/**
 * This is an Entity class  EConfigurationCodes
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
@Indexed
@Entity
@Table(name = "CONFIG_CODES")
@DynamicUpdate
@DynamicInsert
@Getter
@Setter
@NoArgsConstructor
@AttributeOverride(name = "id", column = @Column(name = "CODE_ID"))
@AttributeOverride(name = "valid", column = @Column(name = "IS_ACTIVE"))
public class EConfigCodes extends TransactionEntity {

    @Id
    @GenericField
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CONFIG_CODES_SEQ")
    @GenericGenerator(name = "CONFIG_CODES_SEQ", type = CustomIDGenerator.class)
    private Long id;
    @FullTextField
    private String codeType;
    @FullTextField
    private String shortCode;
    @FullTextField
    private String longCode;
    @FullTextField
    @Column(name = "SHORT_DESC")
    private String shortDescrip;
    @Column(name = "LONG_DESC")
    @FullTextField
    private String longDescrip;
    @Column(name = "CODE_GROUP")
    @FullTextField
    private String configGroup;
    @Column(name = "INT_VALUE")
    @GenericField
    private Double intValue;
    public EConfigCodes(String codeType, String shortCode, String longCode, String shortDescription,String LongDescrip, String configGroup, Double intValue) {

        this.codeType = codeType;
        this.shortCode = shortCode;
        this.longCode = longCode;
        this.shortDescrip = shortDescription;
        this.longDescrip=LongDescrip;
        this.configGroup=configGroup;
        this.intValue=intValue;
    }
}