package com.massil.dto;
//@Author:Yudhister vijay

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;



/**
 * This class is a DTO of SignDet
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignDet {
    private Long signId;
    @NotNull(message = "sign Document should not be null")
    @Size(max = 17 , message = "maximum size of signDocument is 17")
    private String signDocument;



}