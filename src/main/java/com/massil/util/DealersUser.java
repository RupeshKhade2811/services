package com.massil.util;

import com.massil.persistence.model.EDealerRegistration;
import com.massil.persistence.model.ERoleMapping;
import com.massil.persistence.model.EUserRegistration;
import com.massil.repository.DealerRegistrationRepo;
import com.massil.repository.RoleMappingRepo;
import com.massil.repository.UserRegistrationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DealersUser {
    @Autowired
    private RoleMappingRepo mappingRepo;
    @Autowired
    private UserRegistrationRepo userRepo;
    @Autowired
    private DealerRegistrationRepo dealerRepo;

    public List<UUID> getAllUsersUnderDealer(UUID userId){
        List<UUID> userIds=new ArrayList<>();
        ERoleMapping mapping = mappingRepo.findByUserId(userId);
        String roleGroup = mapping.getRole().getRoleGroup();

        switch (roleGroup) {
                case "D":
                    if(mapping.getRole().getRole().equals("D1")) {
                        List<ERoleMapping> dealerUsers = mappingRepo.findByDealerAdmin(userId);
                        if(null!= dealerUsers &&!dealerUsers.isEmpty()) {
                            List<EUserRegistration> users = dealerUsers.stream().map(ERoleMapping::getUser).toList();
                            userIds = users.stream().map(EUserRegistration::getId).collect(Collectors.toList());
                        }
                        userIds.add(userId);
                    }else
                        userIds.add(userId);

                break;
                case "DM":
                userIds= mappingRepo.findUsersUnderManager(userId);
                userIds.add(userId);
                break;
                case "DS":
                    case "P":
                userIds.add(userId);
                break;
                default: userIds.add(userId);
                return userIds;
        }
        return userIds;
    }
}
