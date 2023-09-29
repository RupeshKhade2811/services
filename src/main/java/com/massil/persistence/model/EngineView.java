package com.massil.persistence.model;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import jakarta.persistence.*;

@Entity
@Immutable
@Data
public class EngineView {
    @Id
    private String engine;
}
