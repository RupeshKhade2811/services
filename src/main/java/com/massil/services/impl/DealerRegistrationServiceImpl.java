package com.massil.services.impl;

/**
 * DealerRegistration method is taking the dealerRegistration object as input and creating user
 * @author Kalyan
 */


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;

import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.DealerRegistrationService;
import com.massil.services.UserRegistrationService;
import com.massil.util.CompareUtils;
//import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@Service
public class DealerRegistrationServiceImpl implements DealerRegistrationService {

    Logger log = LoggerFactory.getLogger(DealerRegistrationServiceImpl.class);

    @Autowired
    private DealerRegistrationRepo dlrRegRepo;
    @Value("${image_folder_path}")
    private String imageFolderPath;


    @Value("${saved_pdf_Path}")
    private String pdfpath;

    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private UserRegistrationRepo userRegRepo;

    @Autowired
    private AppraisalVehicleMapper apprVehMapper;

    @Autowired
    private RoleMappingRepo roleMapRepo;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private UserRegistrationService userRegService;
    @Autowired
    private AllDealersViewRepo allDealersViewRepo;

    @Autowired
    private CompareUtils compareUtils;

    @Override
    public String createDealer(DealerRegistration dealerRegistration) throws AppraisalException {
        log.info("This method is used to create Dealer");
        EDealerRegistration eDealerRegistration = apprVehMapper.dealerRegToEdealerReg(dealerRegistration);
        log.debug("Object coming for creating dealer {}", eDealerRegistration);
        eDealerRegistration.setCreatedOn(new Date());
        if(null!=dealerRegistration.getDealershipNames()){
            eDealerRegistration.setDealershipNames(dealerRegistration.getDealershipNames().toLowerCase());
            eDealerRegistration.setStatus(AppraisalConstants.PENDING);
        }
        ERole byRole = roleRepo.findByRole(dealerRegistration.getRoleId());

        if(byRole.getRole().equalsIgnoreCase("D2") || byRole.getRole().equalsIgnoreCase("D3")){
            eDealerRegistration.setStatus(null);
        }
        EDealerRegistration save = dlrRegRepo.save(eDealerRegistration);

        log.info("Dealer is saved and Process started for user registration");
        if (byRole.getRole().equalsIgnoreCase("D2") || byRole.getRole().equalsIgnoreCase("D3")) {
            if(null!=dealerRegistration.getDealerAdmin()){
                ERoleMapping byUserId = roleMapRepo.findByUserId(dealerRegistration.getDealerAdmin());

                if(null!=byUserId.getManager()){
                    dealerRegistration.setManagerId(byUserId.getManager().getId());
                }
                if(null!=byUserId.getFactorySalesman()){
                    dealerRegistration.setFactorySalesman(byUserId.getFactorySalesman().getId());
                }
                if(null!=byUserId.getFactoryManager()){
                    dealerRegistration.setFactoryManager(byUserId.getFactoryManager().getId());
                }
            }
            UserRegistration userRegistration = apprVehMapper.dealerToUser(dealerRegistration);
            userRegService.createUser(userRegistration, save.getId());
        }
        return "Dealer Has Been saved Successfully";
    }


    @Override
    public DealerRegistration showInDealerEditPage(UUID d2UserId) throws AppraisalException {
        log.info("Showing Dealer in Edit page is triggered **Service IMPL**");

        if(null!=d2UserId) {
            EUserRegistration userById = userRegRepo.findUserById(d2UserId);
            DealerRegistration dealerRegistration = apprVehMapper.edealerRegToDealerReg(
                    dlrRegRepo.findDealerById(userById.getDealer().getId()));


            if(null!=userById.getRoleMapping().get(0).getManager()){
                dealerRegistration.setManagerId(userById.getRoleMapping().get(0).getManager().getId());
                dealerRegistration.setMngFirstName(concatName(userById.getRoleMapping().get(0).getManager().getFirstName(),
                        userById.getRoleMapping().get(0).getManager().getLastName()));
            }
            if(null!=userById.getRoleMapping().get(0).getFactorySalesman()){
                dealerRegistration.setFactorySalesman(userById.getRoleMapping().get(0).getFactorySalesman().getId());
                dealerRegistration.setFsFirstName(concatName(userById.getRoleMapping().get(0).getFactorySalesman().getFirstName(),
                        userById.getRoleMapping().get(0).getFactorySalesman().getLastName()));
            }
            if(null!=userById.getRoleMapping().get(0).getFactoryManager()){
                dealerRegistration.setFactoryManager(userById.getRoleMapping().get(0).getFactoryManager().getId());
                dealerRegistration.setFmFirstName(concatName(userById.getRoleMapping().get(0).getFactoryManager().getFirstName(),
                        userById.getRoleMapping().get(0).getFactoryManager().getFirstName()));
            }
            dealerRegistration.setPassword(null);
            log.debug("OBJECT FOR SHOWING DEALER IN EDIT PAGE **Service IMPL**");
            dealerRegistration.setCode(HttpStatus.OK.value());
            dealerRegistration.setMessage("Successfully showing in edit page");
            dealerRegistration.setStatus(true);
            return dealerRegistration;
        }else throw new AppraisalException("Invalid Dealer Id");

    }

    @Override
    public Response updateDealer(DealerRegistration newDealer, UUID d2UserId) throws AppraisalException, IOException {
        log.info("Dealer update method is triggered **Service IMPL**");
        EDealerRegistration oldDealer=null;
        UpdateUserIS updateUserIS=null;
        EDealerRegistration eDealerReg=null;
        if(null!=d2UserId){
            EUserRegistration userById = userRegRepo.findUserById(d2UserId);
            if(null!=userById.getDealer()){
                oldDealer = userById.getDealer();
                updateUserIS = forUserUpdateIS(newDealer, oldDealer);
                eDealerReg = dlrRegRepo.save(apprVehMapper.updEDlrReg(newDealer, oldDealer));
            }else{
                eDealerReg = apprVehMapper.eUserToEdealer(userById);
                updateUserIS = forUserUpdateIS(newDealer, eDealerReg);
            }

        EUserRegistration eUserReg = apprVehMapper.updateEUserReg(eDealerReg, userById);
        ERoleMapping byUserId = roleMapRepo.findByUserId(userById.getId());

        if(null!=newDealer.getFactoryManager()){
            byUserId.setFactoryManager(userRegRepo.findUserById(newDealer.getFactoryManager()));
        }
        if(null!=newDealer.getFactorySalesman()){
            byUserId.setFactorySalesman(userRegRepo.findUserById(newDealer.getFactorySalesman()));
        }
        if(null!=newDealer.getManagerId()){
            byUserId.setManager(userRegRepo.findUserById(newDealer.getManagerId()));
        }

        roleMapRepo.save(byUserId);
        eUserReg.setModifiedOn(new Date());
        userRegRepo.save(eUserReg);

        log.info("updating dealer in identity server");
        userRegService.updateUserInIS(updateUserIS, d2UserId);

        } else throw new AppraisalException("Invalid Dealer Id");

        Response response = new Response();
        response.setMessage("Updated Successfully");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(true);
        return response;
    }

    private UpdateUserIS forUserUpdateIS(DealerRegistration newDealer,EDealerRegistration oldDealer) {

        Map<String,Object>map= new HashMap<>();
        if(Boolean.FALSE.equals(compareValues(oldDealer.getFirstName(),newDealer.getFirstName()))){
            map.put("givenName",newDealer.getFirstName());
        }
        if(Boolean.FALSE.equals(compareValues(oldDealer.getLastName(),newDealer.getLastName()))){
            map.put("familyName",newDealer.getLastName());
        }
        if(Boolean.FALSE.equals(compareValues(oldDealer.getPassword(),newDealer.getPassword()))){
            map.put("password",newDealer.getPassword());
        }
        if(Boolean.FALSE.equals(compareValues(oldDealer.getEmail(),newDealer.getEmail()))){
            List<UserEmail> emails= new ArrayList<>();
            UserEmail userEmail= new UserEmail();
            userEmail.setValue(newDealer.getEmail());
            userEmail.setPrimary(true);
            emails.add(userEmail);
            map.put("emails",emails);
        }
        Details details= new Details();
        details.setOp("replace");
        details.setValue(map);
        List<Details>detail1= new ArrayList<>();
        detail1.add(details);
        UpdateUserIS updateUserIS= new UpdateUserIS();
        updateUserIS.setOperations(detail1);
        return updateUserIS;
    }

    @Override
    public Response deleteDealer(Long dealerId) throws GlobalException {
        Response response = new Response();
        EDealerRegistration dealerById = dlrRegRepo.findDealerById(dealerId);
        if (null != dealerById) {
            dealerById.setValid(Boolean.FALSE);
            EUserRegistration eUserRegistration = userRegRepo.checkUserNamePresent(dealerById.getName());
            eUserRegistration.setValid(Boolean.FALSE);
            response.setStatus(Boolean.TRUE);
            response.setMessage("dealer deleted successfully");
            response.setCode(HttpStatus.OK.value());
            userRegRepo.save(eUserRegistration);
            dlrRegRepo.save(dealerById);
        } else throw new GlobalException("Dealer not found");
        return response;
    }

    @Override
    public DlrList getDlrList(Integer pageNo, Integer pageSize) throws GlobalException, AppraisalException {
        log.info("Showing Dealer list of companyName is null is triggered **Service IMPL**");
        DlrList pageInfo = new DlrList();
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(AppraisalConstants.CREATEDON).descending());
        Page<AllDealersView> pageResult = allDealersViewRepo.getDlrListOfcompNameNull(pageable);
        if (pageResult.getTotalElements() != 0) {
            pageInfo.setTotalRecords(pageResult.getTotalElements());
            pageInfo.setTotalPages(pageResult.getTotalPages());
            List<DealerRegistration> sellingDealers = new ArrayList<>();
            List<AllDealersView> dealersViews = pageResult.toList();
            for (AllDealersView view : dealersViews) {
                sellingDealers.add(apprVehMapper.allDealersViewToDealerReg(view, userRegRepo, companyRepo));
            }
            pageInfo.setDlrWithNoCmp(sellingDealers);

        } else throw new AppraisalException("Dealer list not available");
        pageInfo.setStatus(Boolean.TRUE);
        pageInfo.setMessage("dealer list with no company successfully");
        pageInfo.setCode(HttpStatus.OK.value());

        return pageInfo;
    }

    @Transactional
    @Override
    public Response dealerD1Approval(DealerManagersAssign managersAssign) throws AppraisalException {
        Response response = new Response();
        EDealerRegistration dealer = dlrRegRepo.findDealerById(managersAssign.getDealerId());
        if (managersAssign.getStatus().equalsIgnoreCase(AppraisalConstants.APPROVED)) {
            dealer.setStatus(AppraisalConstants.APPROVED);
            EDealerRegistration eDealerRegistration = dlrRegRepo.save(dealer);
            DealerRegistration dealerRegistration = apprVehMapper.edealerRegToDealerReg(eDealerRegistration);
            if (null != managersAssign.getFactoryManager()) {
                dealerRegistration.setFactoryManager(managersAssign.getFactoryManager());
            }
            if (null != managersAssign.getFactorySalesman()) {
                dealerRegistration.setFactorySalesman(managersAssign.getFactorySalesman());
            }
            if (null != managersAssign.getCompanyId()) {
                dealerRegistration.setCompanyId(managersAssign.getCompanyId());
            }
            if (null != managersAssign.getManagerId()) {
                dealerRegistration.setManagerId(managersAssign.getManagerId());
            }

            UserRegistration userRegistration = apprVehMapper.dealerToUser(dealerRegistration);
            userRegistration.setRoleId(roleRepo.findRoleIdOfD1());
            userRegService.createUser(userRegistration, managersAssign.getDealerId());

            response.setMessage("data saved successfully");
            response.setCode(HttpStatus.OK.value());
            response.setStatus(true);
            return response;
        } else {

            dealer.setStatus(AppraisalConstants.REJECT);
            dealer.setValid(false);
            dlrRegRepo.save(dealer);
            response.setMessage("Dealer Has Been Rejected Successfully");
            response.setCode(HttpStatus.PARTIAL_CONTENT.value());
            response.setStatus(false);
            return response;
        }
    }


    /**
     * This method checks the old image and new image having same file name or not. If file name is not same then deleting the old image and returns
     * true else returns false
     *
     * @param newPic This is the file name of new image
     * @param oldPic This is the file name of old image
     * @return Boolean
     */
    public Boolean updatePics(String newPic, String oldPic) throws IOException {

        if (null == newPic && null != oldPic || (null != newPic && null == oldPic) || (null != newPic && null != oldPic && !newPic.equals(oldPic))) {
            Path filePath = Paths.get(imageFolderPath + oldPic);
            Files.delete(filePath);
            return true;
        }
        return false;
    }

    @Override
    public Response chkDlrShpName(String dealerShipName) {
        log.info("This method is used to check DealerShip name");
        Response response = new Response();
        List<EDealerRegistration> dlrShpName = dlrRegRepo.chkDlrShpName(dealerShipName.toLowerCase());
        if (null != dlrShpName && dlrShpName.size() != 0) {
            response.setCode(HttpStatus.CONFLICT.value());
            response.setStatus(false);
            response.setMessage("DealerShip name is already registered");
        } else {
            response.setCode(HttpStatus.OK.value());
            response.setStatus(true);
//            response.setMessage("DealerShip name is available ");
        }
        return response;
    }

    @Override
    public Response pendingDlr(Integer pageNo, Integer pageSize) throws AppraisalException {
        log.info("This method is used to send pending dealer list");
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(AppraisalConstants.CREATEDON).descending());
        List<EDealerRegistration> pendingDlrList = dlrRegRepo.getPendingDlrList(pageable);
        PendingDealerList pendingDealerList = new PendingDealerList();
        if (null != pendingDlrList && !pendingDlrList.isEmpty()) {

            List<DealerRegistration> dealerRegistrations = apprVehMapper.eDlrRegToDlrReg(pendingDlrList);
            pendingDealerList.setPendingDlr(dealerRegistrations);
            pendingDealerList.setCode(HttpStatus.OK.value());
            pendingDealerList.setMessage("Pending dealer list found");
            pendingDealerList.setStatus(true);
        } else throw new AppraisalException("No pending dealer found");
        return pendingDealerList;
    }

    @Override
    public String pdfUpload(MultipartFile file) throws AppraisalException, IOException {

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (null != extension && (extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx") || extension.equalsIgnoreCase("pdf"))) {
            String filename = UUID.randomUUID() + "." + extension;
            Path filePath = Paths.get(pdfpath + filename);
            Files.write(filePath, file.getBytes());
            return filename;
        }
        throw new AppraisalException("only .pdf, .doc, .docx file types are allowed");

    }



    @Override
    public List<UserRegistration> dlrListToDlrAdmin(UUID dealerAdminId, Integer pageNo, Integer pageSize) throws AppraisalException {
        log.info("This method is used to get dealer under dealer Admin");
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<ERoleMapping> byDealerAdmin = roleMapRepo.findByDealerAdmin(dealerAdminId, pageable);
        List<UserRegistration> users = new ArrayList<>();
        if (null != byDealerAdmin) {
            for (ERoleMapping user : byDealerAdmin) {
                users.add(apprVehMapper.eUserRegisToUserRegis(user.getUser()));
            }
            return users;
        } throw  new AppraisalException("Dealer List is empty");

    }



    String concatName(String first, String last){
        String name="";
        if(null!=first && null!=last){
            name=first+" "+last;
        }
        return name;
    }
    public Boolean compareValues(String oldValue, String newValue){
        if(null==oldValue && null==newValue){
            return true;
        }
        else if(null!=oldValue&& null!=newValue) {

            return oldValue.equals(newValue);
        }

        return false;
    }
}
