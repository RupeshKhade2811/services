package com.massil.dto;

import com.factory.appraisal.factoryService.ExceptionHandle.Response;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TokenSrvc {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("token_type")
    private String tokenType;
    @JsonProperty("expires_in")
    private String expiresIn;
    @JsonProperty("organization_name")
    private String organizationName;
    private String status;
    @JsonProperty("issued_at")
    private String issuedAt;

}
