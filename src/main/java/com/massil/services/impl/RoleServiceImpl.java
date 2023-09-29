package com.massil.services.impl;

import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.Role;
import com.massil.dto.RoleDropDowns;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.ERole;
import com.massil.repository.RoleRepo;
import com.massil.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepo roleRepo;

    @Autowired
    private AppraisalVehicleMapper mapper;

    @Override
    public Response addRole(Role role){
           Response response=new Response();
                response.setCode(HttpStatus.OK.value());
                response.setMessage("Role saved successfully");
                response.setStatus(Boolean.TRUE);
                roleRepo.save(mapper.roleToERole(role));
        return response;
    }

    @Override
    public Response updateRole(Role role, Long roleId) throws GlobalException {
        Response response=new Response();
        ERole byRole = roleRepo.findByRole(roleId);
        if (null!=byRole){
            byRole.setModifiedOn(new Date());
            roleRepo.save(mapper.updateERole(role,byRole));
            response.setStatus(Boolean.TRUE);
            response.setMessage("Role Updated successfully");
            response.setCode(HttpStatus.OK.value());
        }else throw new GlobalException("Role not found");
        return response;
    }

    @Override
    public Response deleteRole(Long roleId) throws GlobalException {
        Response response=new Response();
        ERole byRole = roleRepo.findByRole(roleId);
        if (null!=byRole){
            byRole.setValid(Boolean.FALSE);
            response.setStatus(Boolean.TRUE);
            response.setMessage("Role deleted successfully");
            response.setCode(HttpStatus.OK.value());
            roleRepo.save(byRole);
        }else throw new GlobalException("Role not found");
        return response;
    }

    @Override
    public RoleDropDowns getRoleList() {
        RoleDropDowns dropDowns=new RoleDropDowns();
        dropDowns.setRoleList(mapper.lERoleTolRole(roleRepo.findRoleForDealerAndPublicUser()));
        dropDowns.setCode(HttpStatus.OK.value());
        dropDowns.setStatus(Boolean.TRUE);
        dropDowns.setMessage("Getting DropDowns For Role Successfully");
        return dropDowns;
    }


}
