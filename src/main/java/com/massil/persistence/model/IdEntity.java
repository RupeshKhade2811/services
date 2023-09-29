package com.massil.persistence.model;



import jakarta.persistence.*;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;

import java.io.Serializable;


/**
 * This is class used for overriding attributes
 */


@MappedSuperclass
public abstract class IdEntity  implements Serializable {



    @Version
    @GenericField
    private int version;
    @FullTextField
    private String sourceSystem="SYSTEM";
    @GenericField
    private Boolean valid=Boolean.TRUE;

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }
}
