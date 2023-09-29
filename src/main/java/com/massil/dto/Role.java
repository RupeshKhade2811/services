package com.massil.dto;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role  {
    private Long id;
    @NotNull
    @Size(max = 15)
    private String role;
    @NotNull
    @Size(max = 30)
    private String roleDesc;
    @NotNull
    @Size(max = 20)
    private String roleGroup;
}
