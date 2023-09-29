package com.massil.services;

import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.Role;
import com.massil.dto.RoleDropDowns;

public interface RoleService {
    /**
     * This method creates the role
     * @return message
     */
    Response addRole(Role role);

    /**
     * This method is used to update role of user
     * @param role
     * @param roleId
     * @return
     */
    Response updateRole(Role role,Long roleId) throws GlobalException;

    /**
     * This method is used to delete role of the user
     * @param roleId
     * @return
     */

    Response deleteRole(Long roleId) throws GlobalException;

    RoleDropDowns getRoleList();
}
