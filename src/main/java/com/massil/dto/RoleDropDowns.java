package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RoleDropDowns extends Response {

    private List<Role> roleList;

    private List<Role> facAdminRoleList;
}
