package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.util.UUID;

/**
 * This class is a DTO of Dealer Registration
 */


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DealerRegistration extends Response {

    private Long dealerId;

    @Pattern(regexp = "^(?=[a-zA-Z0-9]{10,30}$)[a-zA-Z0-9]+$")
    @Size(max = 30,min = 12,message = "The User Name should not exceed 30 characters and it should not be below 12 characters and special characters are not allowed")
    @NotNull
    private String name;
    @Size(max = 15,message = "length must be less than equal to 15 characters")
    @NotNull
    private String firstName;
    @Size(max = 15,message = "length must be less than equal to 15 characters")
    @NotNull
    private String lastName;

    @Size(max = 20)
    private String aptmentNumber;
    @Size(max = 20,message = "length must be less than equal to 20 characters")

    private String city;
    @Size(max = 50,message = "length must be less than equal to 50 characters")
    @NotNull
    private String email;
    @Size(max = 20,message = "length must be less than equal to 20 characters")
    @NotNull
    private String password;

    @Size(max=13, min = 10)
    @NotNull
    private String phoneNumber;

    private String profilePicture;
    @Size(max = 20)


    private String state;
    @Size(max = 50,message = "length must be less than equal to 50 characters")

    private String streetAddress;
    @Size(max = 5,message = "length must be less than equal to 5 characters")

    private String zipCode;

    private String latitude;

    private String longitude;

    private String taxCertificate;
    @Size(max = 30,message = "length must be less than equal to 30 characters")

    private String dealershipNames;

    private String dealershipAddress;

    private String dealershipStreet;

    private String dealershipCity;

    private String dealershipZipCode;

    private String dealershipPhNum;
    private String dealerPic;

    private String dealerLicense;
    private String corporationName;
    @NotNull
    private Long roleId;
    private UUID userId;
    private String dealerCert;
    private Long companyId;
    private UUID managerId;
    private UUID factorySalesman;
    private UUID factoryManager;
    private Role roleOfUser;
    private String compName;
    private String fsFirstName;
    private String fmFirstName;
    private String mngFirstName;
    private UUID dealerAdmin;





}
