package com.massil.dto;



import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SellingDealer  {
    private String firstName;
    private String lastName;
    private UUID userId;
    private Long dealerId;
    private String compName;
}
