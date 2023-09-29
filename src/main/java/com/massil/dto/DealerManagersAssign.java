package com.massil.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DealerManagersAssign {
  //  private UUID userId;
  //  private Long roleId;
    private Long dealerId;
    private Long companyId;
    private UUID factoryManager;
    private UUID factorySalesman;
    private UUID managerId;

    private String status;
}
