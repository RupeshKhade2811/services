package com.massil.services.impl;
/**
 * @author Rupesh
 */

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.*;
import com.massil.persistence.model.EConfigCodes;
import com.massil.persistence.model.ERoleMapping;
import com.massil.persistence.model.EUserRegistration;
import com.massil.repository.*;
import com.massil.repository.ConfigCodesRepo;
import com.massil.repository.RoleMappingRepo;
import com.massil.repository.UserRegistrationRepo;
import com.massil.repository.elasticRepo.AppraisalVehicleERepo;
import com.massil.services.ConfigCodesService;
import com.massil.util.AppraisalSpecification;
import com.massil.util.DealersUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class ConfigCodesServiceImpl implements ConfigCodesService {

    Logger log= LoggerFactory.getLogger(ConfigCodesServiceImpl.class);

    @Autowired
    private ConfigCodesRepo configRepo;
    @Autowired
    private UserRegistrationRepo userRepo;
    @Autowired
    private AppraisalVehicleMapper mapper;
    @Autowired
    private RoleMappingRepo mappingRepo;
    @Autowired
    private EngineViewRepo engineViewRepo;
    @Autowired
    private SeriesViewRepo seriesViewRepo;
    @Autowired
    private MakeViewRepo makeViewRepo;
    @Autowired
    private ModelViewRepo modelViewRepo;
    @Autowired
    private TransmissionViewRepo transmissionViewRepo;
    @Autowired
    private YearViewRepo yearViewRepo;
    @Autowired
    private AppraisalVehicleERepo appraisalVehicleERepo;

    @Autowired
    private DealersUser dealersUser;
    @Autowired
    private AppraiseVehicleRepo appraiseVehicleRepo;
    @Autowired
    private AppraisalFilterParamViewRepo appFilParamRepo;
    @Autowired
    private InventoryFilterParamViewRepo invFilParamRepo;
    @Autowired
    private SearchFactoryViewRepo searchFactoryRepo;

    @Override
    public String addConfigCode(List<ConfigDropDown> configCodes) {

        List<EConfigCodes> codes = mapper.lConfigCodeToEConfigCode(configCodes);
        configRepo.saveAll(codes);

        return "saved successfully";
    }

    @Override
    public AppraisalConfigs getAppraisalConfigs(UUID userId) throws AppraisalException {
        log.info("GETTING APPRAISAL CONFIGS");
        AppraisalConfigs configs = new AppraisalConfigs();
            List<EUserRegistration> dealerUsers = getDealerUsers(userId);
            if (null!=dealerUsers) {
                List<UserDropDown> userDropDowns = mapper.lEUserRegToUserDropDowns(dealerUsers);
                setApprConfigVal(configs);
                configs.setDealershipUserNames(userDropDowns);
                configs.setMessage("Getting All DropDowns successfully");
                configs.setCode(HttpStatus.OK.value());
                configs.setStatus(true);
                return configs;
            }
            else throw new AppraisalException("Invalid user Id");
    }

    @Override
    public Response updateConfig(ConfigDropDown configCodes, Long codeId) throws GlobalException {
        Response response=new Response();
        EConfigCodes configCodes1 = configRepo.configByCodeId(codeId);
        if (null!=configCodes1){
            configCodes1.setModifiedOn(new Date());
            configRepo.save(mapper.updateEConfigCodes(configCodes,configCodes1));
            response.setStatus(Boolean.TRUE);
            response.setMessage("ConfigCodes Updated successfully");
            response.setCode(HttpStatus.OK.value());
        }else throw new GlobalException("ConfigCodes not found");
        return response;
    }

    @Override
    public Response deleteConfig(Long codeId) throws GlobalException {
        Response response=new Response();
        EConfigCodes configCodes = configRepo.configByCodeId(codeId);
        if (null!=configCodes){
            configCodes.setValid(Boolean.FALSE);
            configRepo.save(configCodes);
            response.setStatus(Boolean.TRUE);
            response.setMessage("ConfigCodes Deleted successfully");
            response.setCode(HttpStatus.OK.value());
        }else throw new GlobalException("ConfigCode not found");
        return response;
    }

    /**
     *This method returns the List<EUserRegistration> base on userId
     * @param userId This is the userId
     * @return List<EUserRegistration>
     */
    private List<EUserRegistration> getDealerUsers(UUID userId){

        log.info("CHECKING USER IS DEALER OR NOT");
        List<EUserRegistration> userByDealerId=null;
        ERoleMapping byUserId = mappingRepo.findByUserId(userId);
        if (null!=byUserId){
            if ((byUserId.getRole().getRoleGroup().equalsIgnoreCase("D"))){
                if(byUserId.getRole().getRole().equalsIgnoreCase("D1")){
                    userByDealerId=userRepo.findDlrshipUsersByDlrAdmin(userId);
                    this.log.debug("USERS UNDER DEALER {}",userByDealerId);
                }else{
                    userByDealerId=userRepo.findDlrshipUsersByDlrAdmin(byUserId.getDealerAdmin());
                    this.log.debug("USERS UNDER DEALER {}",userByDealerId);
                }
            } else if (byUserId.getRole().getRole().equalsIgnoreCase("S1") || byUserId.getRole().getRole().equalsIgnoreCase("M1")) {
                userByDealerId=userRepo.findDlrSales(userId);
                this.log.debug("DEALER SALES {}",userByDealerId);
            }
            else{
                userByDealerId= new ArrayList<>();
                userByDealerId.add(byUserId.getUser());
                this.log.debug("USER {}",userByDealerId);
            }
        }
        return userByDealerId;
    }

    /**
     *This method retrieve all configCodes from db and store in List<EConfigurationCodes> and using Collectors grouping the configCodes
     * by codeType then setting into the fields of AppraisalConfigs class
     * @param apprConfigVal This is the object of AppraisalConfigs class
     *
     */
    private void setApprConfigVal(AppraisalConfigs apprConfigVal){
        List<EConfigCodes> eCodesList = configRepo.getCodesByConfigGroup(AppraisalConstants.APPRAISAL_CONFIG);
        List<ConfigDropDown> codesList = mapper.lEConfigCodestoConfigDropDown(eCodesList);
        Map<String, List<ConfigDropDown>> map2 = codesList.stream().collect(Collectors.groupingBy(ConfigDropDown::getCodeType));
        apprConfigVal.setDashWarnLights(map2.get(AppraisalConstants.DASH_WARN_LIGHTS));
        apprConfigVal.setAcCond(map2.get(AppraisalConstants.AC_CONDITION));
        apprConfigVal.setVehicleIntrColor((map2.get(AppraisalConstants.INTERIOR_COLOR)));
        apprConfigVal.setVehicleExtrColor(map2.get(AppraisalConstants.EXTERIOR_COLOR));
        apprConfigVal.setInteriorCond(map2.get(AppraisalConstants.INTERIOR_CONDITION));
        apprConfigVal.setDoorLocks(map2.get(AppraisalConstants.DOOR_LOCKS));
        apprConfigVal.setRoofType(map2.get(AppraisalConstants.ROOF));
        apprConfigVal.setBrakingSysSts(map2.get(AppraisalConstants.BRAKE_SYSTEM_FEEL));
        apprConfigVal.setEnginePerformance(map2.get(AppraisalConstants.ENGINE_PERFORMANCE));
        apprConfigVal.setTransmissionStatus(map2.get(AppraisalConstants.TRANSMISSION_STATUS));
        apprConfigVal.setFrontLeftWinSts(map2.get(AppraisalConstants.FRONT_LEFT_SIDE_WINDOW_STATUS));
        apprConfigVal.setFrontRightWinSts(map2.get(AppraisalConstants.FRONT_RIGHT_SIDE_WINDOW_STATUS));
        apprConfigVal.setRearLeftWinSts(map2.get(AppraisalConstants.REAR_LEFT_SIDE_WINDOW_STATUS));
        apprConfigVal.setRearRightWinSts(map2.get(AppraisalConstants.REAR_RIGHT_SIDE_WINDOW_STATUS));
        apprConfigVal.setOilCond(map2.get(AppraisalConstants.OIL_CONDITION));
        apprConfigVal.setFrontWindShieldDamage(map2.get(AppraisalConstants.FRONT_WIND_SHIELD_STATUS));
        apprConfigVal.setStereoSts(map2.get(AppraisalConstants.STEREO_STATUS));
        apprConfigVal.setSteeringFeelSts(map2.get(AppraisalConstants.STEERING_FEEL_STATUS));
        apprConfigVal.setBookAndKeys(map2.get(AppraisalConstants.BOOKS_AND_KEYS));
        apprConfigVal.setTitleSts(map2.get(AppraisalConstants.TITLE_STATUS));
        apprConfigVal.setRearWindowDamage(map2.get(AppraisalConstants.REAR_WINDOW_DAMAGE));
        apprConfigVal.setTireCondition(map2.get(AppraisalConstants.TIRE_CONDITION));
        apprConfigVal.setMake(map2.get(AppraisalConstants.MAKE.toUpperCase()));
        apprConfigVal.setModel(map2.get(AppraisalConstants.MODEL.toUpperCase()));
        apprConfigVal.setEngine(map2.get(AppraisalConstants.ENGINE.toUpperCase()));
        apprConfigVal.setTransmission(map2.get(AppraisalConstants.TRANSMISSION.toUpperCase()));
        apprConfigVal.setSeries(map2.get(AppraisalConstants.SERIES.toUpperCase()));
        this.log.debug("Getting AppraisalConstants and setting to AppraisalConfigs {}",apprConfigVal);
    }



    public FilterDropdowns sendFilterParams(FilterParameters filter, UUID userId, String module) throws AppraisalException {
        ERoleMapping byUserId = mappingRepo.findByUserId(userId);
        FilterDropdowns dropdowns = new FilterDropdowns();
        if (null != byUserId && null!=module) {
            if (byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")) {
                userId = byUserId.getDealerAdmin();
            }
            List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
            List<Long> yearList = null;
            List<String> makeList =null;
            List<String> modelList = null;
            List<AppraisalFilterParamView> appVehicles=null;
            List<InventoryFilterParamView> invVehicles=null;
            List<SearchFactoryView> searchFactVehicles=null;

            if(module.equalsIgnoreCase("Appraisal")){
                appVehicles = appFilParamRepo.findAll(AppraisalSpecification.getAppFilParaSpec(filter, allUsersUnderDealer));
                yearList = appVehicles.stream().map(AppraisalFilterParamView::getVehicleYear).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                yearList.sort(Collections.reverseOrder());
                makeList = appVehicles.stream().map(AppraisalFilterParamView::getVehicleMake).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                Collections.sort(makeList);
                modelList = appVehicles.stream().map(AppraisalFilterParamView::getVehicleModel).filter(Objects::nonNull).distinct().collect(Collectors.toList());
                Collections.sort(modelList);
            }
           if(module.equalsIgnoreCase("Inventory")){

               invVehicles = invFilParamRepo.findAll(AppraisalSpecification.getInvFilParaSpec(filter, allUsersUnderDealer));
               yearList = invVehicles.stream().map(InventoryFilterParamView::getVehicleYear).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               yearList.sort(Collections.reverseOrder());
               makeList = invVehicles.stream().map(InventoryFilterParamView::getVehicleMake).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               Collections.sort(makeList);
               modelList = invVehicles.stream().map(InventoryFilterParamView::getVehicleModel).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               Collections.sort(modelList);
           }

           if(module.equalsIgnoreCase("SearchTheFactory")){
               searchFactVehicles = searchFactoryRepo.findAll(AppraisalSpecification.getSearchFactSpec(filter, allUsersUnderDealer));
               yearList = searchFactVehicles.stream().map(SearchFactoryView::getVehicleYear).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               yearList.sort(Collections.reverseOrder());
               makeList = searchFactVehicles.stream().map(SearchFactoryView::getVehicleMake).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               Collections.sort(makeList);
               modelList = searchFactVehicles.stream().map(SearchFactoryView::getVehicleModel).filter(Objects::nonNull).distinct().collect(Collectors.toList());
               Collections.sort(modelList);
           }

            dropdowns.setYear(yearList);
            dropdowns.setMake(makeList);
            dropdowns.setModel(modelList);
            dropdowns.setStatus(true);
            dropdowns.setMessage("dropdowns send successfully");
            dropdowns.setCode(HttpStatus.OK.value());
            return dropdowns;
        }
        else throw new AppraisalException("User Not Found");

    }



}
