package com.massil.services.impl;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AppraisalVehicleCard;
import com.massil.dto.CardsPage;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.AppraisalVehicleERepo;
import com.massil.services.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryServiceImpl implements InventoryService {

    Logger log = LoggerFactory.getLogger(AppraiseVehicleServiceImpl.class);

    @Autowired
    private AppraiseVehicleRepo eAppraiseVehicleRepo;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;
    @Autowired
    private OffersMapper offersMapper;
    @Autowired
    private UserRegistrationRepo userRepo;
    @Autowired
    private DealerRegistrationRepo dealerRepo;
    @Autowired
    private RoleMappingRepo roleMappingRepo;
    @Autowired
    private AppraisalVehicleERepo appraisalVehicleERepo;
    @Autowired
    private ConfigCodesRepo configurationCodesRepo;



    @Override
    public CardsPage inventoryCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {

        CardsPage cardsPage=null;
        CardsPage pageInfo = new CardsPage();
        Page<EAppraiseVehicle> pageResult=null;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());



           pageResult = eAppraiseVehicleRepo.findUserAndInvntrySts(userId, AppraisalConstants.INVENTORY,true,pageable);
            if (pageResult.getTotalElements() != 0) {
                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                List<EAppraiseVehicle> invtry = pageResult.toList();
                List<AppraisalVehicleCard> inventoryVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(invtry);
                pageInfo.setCode(HttpStatus.OK.value());
                pageInfo.setMessage("Successfully found Inventory cards");
                pageInfo.setStatus(Boolean.TRUE);
                pageInfo.setCards(inventoryVehicleDtos);
            }
            else throw new AppraisalException("Inventory Cards not available");

        if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = eAppraiseVehicleRepo.findUserAndInvntrySts(userId, AppraisalConstants.INVENTORY,true,pageable);
        }else {
            cardsPage = appraisalVehicleERepo.inventoryCards(userId,pageNumber, pageSize);
        }

        if(null!= pageResult && pageResult.getTotalElements()!=0) {
            pageInfo.setTotalRecords(pageResult.getTotalElements());
            pageInfo.setTotalPages((long) pageResult.getTotalPages());
            List<EAppraiseVehicle> invtry = pageResult.toList();
            List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(invtry);
            pageInfo.setCards(appraiseVehicleDtos);
        }
        else if(null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()){

            pageInfo.setTotalRecords(cardsPage.getTotalRecords());
            pageInfo.setTotalPages((long) cardsPage.getTotalPages());
            List<EAppraiseVehicle> invtry = cardsPage.getAppraiseVehicleList();
            List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(invtry);
            pageInfo.setCards(appraiseVehicleDtos);

        }
        else throw new AppraisalException("Inventory Cards not available");
        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setMessage("Cards showing successfully");
        pageInfo.setStatus(true);
        return pageInfo;
    }

    @Override
    public CardsPage searchFactory(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException {
        CardsPage cardsPage=null;
        CardsPage pageInfo = new CardsPage();
        Page<EAppraiseVehicle> pageResult=null;

            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());
            EUserRegistration userById = userRepo.findUserById(id);
            List<AppraisalVehicleCard> offersCards=new ArrayList<>();

            if(null!=userById) {
                if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
                    pageResult = eAppraiseVehicleRepo.findByUserIdNot(id, AppraisalConstants.INVENTORY, true, pageable);
                }else {
                     cardsPage = appraisalVehicleERepo.searchFactory(id, pageNumber, pageSize);
                }

            }

            if (null!=pageResult && pageResult.getTotalElements() != 0) {

                pageInfo.setTotalPages((long) pageResult.getTotalPages());
                pageInfo.setTotalRecords(pageResult.getTotalElements());

                List<EAppraiseVehicle> invtry = pageResult.toList();

                for (EAppraiseVehicle vehicle:invtry) {
                    ERoleMapping byUserId = roleMappingRepo.findByUserId(vehicle.getUser().getId());
                    AppraisalVehicleCard appraisalVehicleCard = offersMapper.eApprVehiToOffersCards(vehicle, id);
                    appraisalVehicleCard.setRole(appraisalVehicleMapper.eRoleToRole(byUserId.getRole()));

                    offersCards.add(appraisalVehicleCard);
                }

                pageInfo.setCode(HttpStatus.OK.value());
                pageInfo.setMessage("Successfully found Search Factory cards");
                pageInfo.setStatus(Boolean.TRUE);
              pageInfo.setCards(offersCards);
                ERoleMapping byUserId = roleMappingRepo.findByUserId(id);
                pageInfo.setRoleType(byUserId.getRole().getRole());
              pageInfo.setRoleGroup(byUserId.getRole().getRoleGroup());

            }else if(null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()) {

                pageInfo.setTotalPages(cardsPage.getTotalPages());
                pageInfo.setTotalRecords(cardsPage.getTotalRecords());

                List<EAppraiseVehicle> invtry = cardsPage.getAppraiseVehicleList();

                for (EAppraiseVehicle vehicle:invtry) {
                    ERoleMapping byUserId = roleMappingRepo.findByUserId(vehicle.getUser().getId());
                    AppraisalVehicleCard appraisalVehicleCard = offersMapper.eApprVehiToOffersCards(vehicle, id);
                    appraisalVehicleCard.setRole(appraisalVehicleMapper.eRoleToRole(byUserId.getRole()));
                    offersCards.add(appraisalVehicleCard);
                }

                pageInfo.setCode(HttpStatus.OK.value());
                pageInfo.setMessage("Successfully found Search Factory cards");
                pageInfo.setStatus(Boolean.TRUE);
                pageInfo.setCards(offersCards);
                ERoleMapping byUserId = roleMappingRepo.findByUserId(id);
                pageInfo.setRoleType(byUserId.getRole().getRole());
                pageInfo.setRoleGroup(byUserId.getRole().getRoleGroup());


            }
            else throw new AppraisalException("No Cards available");

        return pageInfo;
    }

    @Override
    public Response holdAppraisal(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
            if (null!=vehicle&& vehicle.getValid()) {
                vehicle.setIsHold(Boolean.TRUE);
                eAppraiseVehicleRepo.save(vehicle);
                response.setMessage("Vehicle is on HOLD");
                response.setStatus(true);
                response.setCode(HttpStatus.OK.value());
                return response;
            }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
    }

    @Override
    public Response UnHoldAppraisal(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
            if (null!=vehicle&&vehicle.getValid()){
                vehicle.setIsHold(Boolean.FALSE);
                eAppraiseVehicleRepo.save(vehicle);
                response.setMessage("Vehicle is removed from HOLD");
                response.setStatus(true);
                response.setCode(HttpStatus.OK.value());
                return response;
            }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
    }

    @Override
    public Response makeSoldRetailOn(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
        if (null!=vehicle) {
            if (Boolean.FALSE.equals(vehicle.getField2())){
                vehicle.setField1(true);
              eAppraiseVehicleRepo.save(vehicle);
            }else throw new AppraisalException("vehicle is in sold wholesale ");

            response.setMessage("Vehicle is sold retail ");
            response.setStatus(true);
            response.setCode(HttpStatus.OK.value());
            return response;
        }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef );

    }

    @Override
    public Response makeSoldRetailOff(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
        if (null!=vehicle){
            vehicle.setField1(false);
            eAppraiseVehicleRepo.save(vehicle);
            response.setMessage("Vehicle is removed from sold retail");
            response.setStatus(true);
            response.setCode(HttpStatus.OK.value());
            return response;
        }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
    }

    @Override
    public Response makeSoldWholesaleOn(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
        if (null!=vehicle) {
            if (Boolean.FALSE.equals(vehicle.getField1())){
                vehicle.setField2(true);
                eAppraiseVehicleRepo.save(vehicle);
            }else throw new AppraisalException("vehicle is in sold retail ");

            response.setMessage("Vehicle is sold wholesale ");
            response.setStatus(true);
            response.setCode(HttpStatus.OK.value());
            return response;
        }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef );
    }

    @Override
    public Response makeSoldWholesaleOff(Long apprRef) throws AppraisalException {
        Response response=new Response();
        EAppraiseVehicle vehicle = eAppraiseVehicleRepo.getAppraisalById(apprRef);
        if (null!=vehicle){
            vehicle.setField2(false);
            eAppraiseVehicleRepo.save(vehicle);
            response.setMessage("Vehicle is removed from sold wholesale");
            response.setStatus(true);
            response.setCode(HttpStatus.OK.value());
            return response;
        }else throw new AppraisalException("Did not find AppraisalVehicle of  - " + apprRef);
    }


}
