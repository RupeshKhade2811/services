package com.massil.persistence.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.Immutable;


@Entity
@Immutable
@Data
public class MakeView {
    @Id
    private String make;
}
