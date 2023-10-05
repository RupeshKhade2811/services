package com.massil.ExceptionHandle;

import lombok.*;

import java.util.UUID;

/**
 * This class is used as return type for every API
 */

@Getter
@Setter
@NoArgsConstructor

public class Response {

    private Integer code;
    private String message;
    private Boolean status;
    private String fileName;
    private Long apprId;
    private Long totalVehicles;
    private UUID userId;


    public Response(int value, String roleCreated, boolean status) {
       this.code=value;
       this.message=roleCreated;
       this.status=status;
    }
}
