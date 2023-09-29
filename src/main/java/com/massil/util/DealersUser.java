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
                EDealerRegistration dealer = userRepo.findUserById(userId).getDealer();
                List<EUserRegistration> users = userRepo.findUserByDealerId(dealer.getId());
                userIds = users.stream().map(EUserRegistration::getId).collect(Collectors.toList());
                break;
                case "DM":
                userIds= mappingRepo.findUsersUnderManager(userId);
                break;
                case "DS":
                    case "P":
                userIds.add(userId);
                break;
                default: return userIds;
        }
        return userIds;
    }
}
