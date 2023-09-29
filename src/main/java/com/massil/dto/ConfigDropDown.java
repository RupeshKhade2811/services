package com.massil.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;



/**
 * This class is a DTO which is used for dropdowns
 * only the provided fields of configCodes will be seen in the DropDowns
 */


@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class ConfigDropDown {

    private Long id;
    @NotNull
    @Size(max = 50,message = "lenght must be less than equal to 50 characters")
    private String longCode;
    @NotNull
    @Size(max = 25,message = "lenght must be less than equal to 25 characters")
    private String shortCode;
    @NotNull
    @Size(max = 50,message = "lenght must be less than equal to 50 characters")
    private String shortDescrip;
    @NotNull
    @Size(max = 50,message = "lenght must be less than equal to 50 characters")
    private String longDescrip;
    @NotNull
    @Size(max = 30,message = "lenght must be less than equal to 30 characters")
    private String codeType;
    @NotNull
    @Size(max = 20,message = "lenght must be less than equal to 20 characters")
    private String configGroup;
    private Integer intValue;
}
