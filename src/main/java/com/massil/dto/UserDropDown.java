package com.massil.dto;

import lombok.*;

import java.util.UUID;

/**
 * This class is a DTO which is used for dropdowns
 * only the provided fields of User Registration will be seen in the DropDowns
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDropDown {
    private UUID id;
    private String userName;
}
