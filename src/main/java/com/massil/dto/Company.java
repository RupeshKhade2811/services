package com.massil.dto;
//@Author: Yudhister vijay


import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Company {
    private Long id;
    @Size(max = 15 ,message = "maximum size of company Name is 15")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Only letters and spaces are allowed")
    private String name;

    @Size(max = 15 ,message = "maximum size of groupName is 15")
    @Pattern(regexp = "^[a-zA-Z ]*$", message = "Only letters and spaces are allowed")
    private String groupName;

    @Size(max = 13, message = "give 13 digit American phone number with +1 code sample example:+1 5551234567")
    @Pattern(regexp = "^\\+1\\s\\(?\\d{3}\\)?[- ]?\\d{3}[- ]?\\d{4}$",message = "Invalid Phone Number")
    private String phNumber;

    private String address;

    private String emailId;
}
