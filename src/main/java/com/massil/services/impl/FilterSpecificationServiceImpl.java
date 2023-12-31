package com.massil.services.impl;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.AppraisalVehicleERepo;
import com.massil.services.FilterSpecificationService;
import com.massil.util.AppraisalSpecification;
import com.massil.util.DealersUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
public class FilterSpecificationServiceImpl implements FilterSpecificationService {

    @Autowired
    private AppraiseVehicleRepo eAppraiseVehicleRepo;
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;
    @Autowired
    private RoleMappingRepo roleMappingRepo;

    @Autowired
    private UserRegistrationRepo userRegistrationRepo;
    @Autowired
    private DlrInvntryViewRepo dlrInvntryViewRepo;
    @Autowired
    private OffersMapper offersMapper;

    @Autowired
    private TransReportRepo transReportRepo;
    @Autowired
    private DealersUser dealersUser;
    @Autowired
    private CompanyRepo companyRepo;
    @Autowired
    private MembersByFactoryManagerRepo ftryManagerRepo;
    @Autowired
    private MembersByFactorySalesmenRepo ftrySalesmenRepo;
    @Autowired
    private DealerRegistrationRepo dlrRegRepo;
    @Autowired
    private AllDealersViewRepo allDealersViewRepo;

    @Autowired
    private CorporateAdminViewRepo corAdminViewRepo;

    @Autowired
    private UserlistViewRepo userListViewRepo;

    @Autowired
    private D2DlrListRepo d2DlrListRepo;
    @Autowired
    private AppraisalVehicleERepo eRepo;
    @Autowired
    private ConfigCodesRepo configCodesRepo;
	 @Autowired
    private FactoryPersonnelRepo fctryPrsnnelRepo;


    Logger log = LoggerFactory.getLogger(FilterSpecificationServiceImpl.class);
    @Override
    public CardsPage filterAppraisalVehicle(FilterParameters filter, UUID userId, Integer pageNo, Integer pageSize) throws AppraisalException {
        Page<EAppraiseVehicle> resultPage=null;
        CardsPage cardsPage=null;
        CardsPage pageInfo = new CardsPage();
        EUserRegistration userById = userRegistrationRepo.findUserById(userId);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);

            if (null != userById) {
                Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(AppraisalConstants.CREATEDON).descending());

                if (null!=filter) {
                    if(Boolean.TRUE.equals(configCodesRepo.isElasticActive())){
                        cardsPage = eRepo.filterAppraisalCards(filter, allUsersUnderDealer, pageNo, pageSize);
                    }else {
                        resultPage = eAppraiseVehicleRepo.findAll(AppraisalSpecification.getEApprSpecification(filter, allUsersUnderDealer), pageable);
                    }
                }else {
                    if(Boolean.TRUE.equals(configCodesRepo.isElasticActive())){
                        cardsPage = eRepo.appraisalCards(allUsersUnderDealer, pageNo, pageSize);
                    }else {
                        resultPage = eAppraiseVehicleRepo.appraisalCards(allUsersUnderDealer, true, pageable);
                    }
                }

                if(null!=resultPage && !resultPage.isEmpty()) {
                    pageInfo.setTotalRecords(resultPage.getTotalElements());
                    List<AppraisalVehicleCard> cards = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(resultPage.getContent());
                    pageInfo.setCards(cards);
                    pageInfo.setTotalPages((long) resultPage.getTotalPages());
                }else if(null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()){
                    pageInfo.setTotalRecords(cardsPage.getTotalRecords());
                    pageInfo.setTotalPages((long) cardsPage.getTotalPages());
                    List<EAppraiseVehicle> apv = cardsPage.getAppraiseVehicleList();
                    List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(apv);
                    pageInfo.setCards(appraiseVehicleDtos);

                }
                else throw new AppraisalException("AppraisalCards not available");
                pageInfo.setCode(HttpStatus.OK.value());
                pageInfo.setMessage("Cards showing successfully");
                pageInfo.setStatus(true);
                return pageInfo;
            } else throw new AppraisalException(AppraisalConstants.INVALID_USER_ID);
    }
    public CardsPage filterInventoryVehicle(FilterParameters filter, UUID userId, Integer pageNo,Integer pageSize) throws AppraisalException {
        Page<EAppraiseVehicle> resultPage=null;
        CardsPage cardsPage = null;
        CardsPage pageInfo = new CardsPage();
        ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);
        if(byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")){
            userId=byUserId.getDealerAdmin();
        }
        List<UUID> usersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());

         if (null != filter) {
                    if (Boolean.TRUE.equals(configCodesRepo.isElasticActive())) {
                        cardsPage = eRepo.filterInventoryCards(filter, usersUnderDealer, pageNo, pageSize);
                    } else {
                        resultPage = eAppraiseVehicleRepo.findAll(AppraisalSpecification.getInventrySpecification(filter, usersUnderDealer), pageable);
                    }
         } else {
                    if (Boolean.TRUE.equals(configCodesRepo.isElasticActive())) {
                        cardsPage = eRepo.inventoryCards(usersUnderDealer, pageNo, pageSize);
                    } else {
                        resultPage = eAppraiseVehicleRepo.findUserAndInvntrySts(usersUnderDealer, AppraisalConstants.INVENTORY, true, pageable);
                    }
         }
         if (null != resultPage && !resultPage.isEmpty()) {
                    pageInfo.setTotalRecords(resultPage.getTotalElements());
                    List<AppraisalVehicleCard> cards = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(resultPage.getContent());
                    pageInfo.setCards(cards);
                    pageInfo.setTotalPages((long) resultPage.getTotalPages());
         }
         else if (null != cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()) {
                    pageInfo.setTotalRecords(cardsPage.getTotalRecords());
                    pageInfo.setTotalPages((long) cardsPage.getTotalPages());
                    List<EAppraiseVehicle> invtry = cardsPage.getAppraiseVehicleList();
                    List<AppraisalVehicleCard> appraiseVehicleDtos = appraisalVehicleMapper.lEApprVehiToLApprVehiCard(invtry);
                    pageInfo.setCards(appraiseVehicleDtos);
         }
         else throw new AppraisalException("InventoryCards not available");

         pageInfo.setCode(HttpStatus.OK.value());
         pageInfo.setMessage("Cards showing successfully");
         pageInfo.setStatus(true);
         return pageInfo;
    }


    @Override
    public CardsPage filterSearchFactoryVehicle(FilterParameters filter, UUID userId, Integer pageNo, Integer pageSize) throws AppraisalException {
        Page<EAppraiseVehicle>resultPage=null;
        CardsPage cardsPage=null;
        CardsPage pageInfo = new CardsPage();
        List<AppraisalVehicleCard> offersCards=new ArrayList<>();
        ERoleMapping byUserId = roleMappingRepo.findByUserId(userId);
        if(byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")){
            userId=byUserId.getDealerAdmin();
        }
        List<UUID> usersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());

        if (null!=filter) {
                    if(Boolean.FALSE.equals(configCodesRepo.isElasticActive())) {
                        List<Long> unsoldVehicles = eAppraiseVehicleRepo.apprIdOfUnsoldVehicles(usersUnderDealer);
                        resultPage = eAppraiseVehicleRepo.findAll(AppraisalSpecification.getSearchFactorySpecification(filter, usersUnderDealer,unsoldVehicles), pageable);
                    }else {
                        cardsPage = eRepo.filterSearchFactoryVehicle(filter, usersUnderDealer, pageNo, pageSize);
                    }
        }
        if (null!=resultPage && resultPage.getTotalElements() != 0) {
                    pageInfo.setTotalRecords(resultPage.getTotalElements());
                    pageInfo.setTotalPages((long) resultPage.getTotalPages());

                    List<EAppraiseVehicle> invtry = resultPage.toList();
                    for (EAppraiseVehicle vehicle:invtry) {
                        ERoleMapping byUserId1 = roleMappingRepo.findByUserId(vehicle.getUser().getId());
                        AppraisalVehicleCard appraisalVehicleCard = offersMapper.eApprVehiToOffersCards(vehicle, userId,usersUnderDealer);
                        appraisalVehicleCard.setRole(appraisalVehicleMapper.eRoleToRole(byUserId1.getRole()));
                        offersCards.add(appraisalVehicleCard);
                    }
                    pageInfo.setCode(HttpStatus.OK.value());
                    pageInfo.setMessage("SearchFactory vehicle cards are visible");
                    pageInfo.setStatus(Boolean.TRUE);
                    pageInfo.setCards(offersCards);
                    pageInfo.setRoleType(byUserId.getRole().getRole());
                    pageInfo.setRoleGroup(byUserId.getRole().getRoleGroup());
        }else if (null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()){

                    pageInfo.setTotalRecords(cardsPage.getTotalRecords());
                    pageInfo.setTotalPages(cardsPage.getTotalPages());

                    List<EAppraiseVehicle> invtry = cardsPage.getAppraiseVehicleList();
                    for (EAppraiseVehicle vehicle:invtry) {
                        ERoleMapping byUserId1 = roleMappingRepo.findByUserId(vehicle.getUser().getId());
                        AppraisalVehicleCard appraisalVehicleCard = offersMapper.eApprVehiToOffersCards(vehicle, userId,usersUnderDealer);
                        appraisalVehicleCard.setRole(appraisalVehicleMapper.eRoleToRole(byUserId1.getRole()));
                        offersCards.add(appraisalVehicleCard);
                    }
                    pageInfo.setCode(HttpStatus.OK.value());
                    pageInfo.setMessage("SearchFactory vehicle cards are visible");
                    pageInfo.setStatus(Boolean.TRUE);
                    pageInfo.setCards(offersCards);
                    pageInfo.setRoleType(byUserId.getRole().getRole());
                    pageInfo.setRoleGroup(byUserId.getRole().getRoleGroup());
        } else throw new AppraisalException("No Cards available");
        return pageInfo;
    }


    @Override
    public SellingDealerList sendSlrDlrList(SellingDealer filter) throws AppraisalException {

        List<TransactionReport> all = transReportRepo.findAll( AppraisalSpecification.displayBySellingDealer(filter)) ;
        SellingDealerList dealerList= new SellingDealerList();
        if(null!=all){
            List<SellingDealer> sellingDealers = appraisalVehicleMapper.lTransaRepToSellingDealer(all);

            List<SellingDealer> filterList = sellingDealers.stream()
                    .collect(Collectors.toMap(SellingDealer::getUserId, Function.identity(), (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            dealerList.setSlrDlrList(filterList);
            log.info(dealerList.getSlrDlrList().toString());
        }else throw new AppraisalException("No such Dealer Found");
        dealerList.setCode(HttpStatus.OK.value());
        dealerList.setMessage("dealerList found");
        dealerList.setStatus(true);
        return dealerList;
    }



    @Override
    public SellingDealerList displayByFtryManagerList(SellingDealer filter) throws AppraisalException {
        List<MembersByFactoryManager> all = ftryManagerRepo.findAll(AppraisalSpecification.displayByFtryManager(filter));
        SellingDealerList dealerList= new SellingDealerList();
        if(null!=all){
            List<SellingDealer> sellingDealers = appraisalVehicleMapper.lMangerRepToSellingDealer(all);
            List<SellingDealer> filterList = sellingDealers.stream()
                    .collect(Collectors.toMap(SellingDealer::getUserId, Function.identity(), (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            dealerList.setSlrDlrList(filterList);
        }else throw new AppraisalException("No such Dealer Found");
        dealerList.setCode(HttpStatus.OK.value());
        dealerList.setMessage("dealerList found");
        dealerList.setStatus(true);
        return dealerList;
    }

    @Override
    public SellingDealerList displayByFtrySalesList(SellingDealer filter) throws AppraisalException {
        List<MembersByFactorySalesmen> all = ftrySalesmenRepo.findAll( AppraisalSpecification.displayByFtrySales(filter));
        SellingDealerList dealerList= new SellingDealerList();
        if(null!=all){
            List<SellingDealer> sellingDealers = appraisalVehicleMapper.lSaleToSellingDealer(all);
            List<SellingDealer> filterList = sellingDealers.stream()
                    .collect(Collectors.toMap(SellingDealer::getUserId, Function.identity(), (existing, replacement) -> existing))
                    .values()
                    .stream()
                    .collect(Collectors.toList());

            dealerList.setSlrDlrList(filterList);
        }else throw new AppraisalException("No such Dealer Found");
        dealerList.setCode(HttpStatus.OK.value());
        dealerList.setMessage("dealerList found");
        dealerList.setStatus(true);
        return dealerList;
    }

    @Override
    public DlrList srchByDlrList(DealerRegistration filter) throws AppraisalException {
        List<EDealerRegistration> all = dlrRegRepo.findAll( AppraisalSpecification.dsplyDlrList(filter));
        DlrList dealerList= new DlrList();
        if(!all.isEmpty()){
            List<DealerRegistration> sellingDealers = appraisalVehicleMapper.eDlrRegToDlrReg(all);
            dealerList.setDlrWithNoCmp(sellingDealers);
        }else throw new AppraisalException("No such Dealer Found");
        dealerList.setCode(HttpStatus.OK.value());
        dealerList.setMessage("dealerList found");
        dealerList.setStatus(true);
        return dealerList;
    }
    @Override
    public DlrList searchDealers(DealerRegistration filter) throws AppraisalException {
        Pageable pageable = PageRequest.of(0, 8);
        Page<AllDealersView> all = allDealersViewRepo.findAll( AppraisalSpecification.dsplyDlrs(filter),pageable);

        DlrList dealerList= new DlrList();
        if(null!=all){
           dealerList.setTotalPages(all.getTotalPages());
            dealerList.setTotalRecords(all.getTotalElements());
            List<DealerRegistration> sellingDealers= new ArrayList<>();
            List<AllDealersView> dealersViews = all.toList();
            for (AllDealersView view: dealersViews) {
                sellingDealers.add(appraisalVehicleMapper.allDealersViewToDealerReg(view,userRegistrationRepo,companyRepo));
            }
            dealerList.setDlrWithNoCmp(sellingDealers);
        }else throw new AppraisalException("No such Dealer Found");
        dealerList.setCode(HttpStatus.OK.value());
        dealerList.setMessage("dealerList found");
        dealerList.setStatus(true);
        return dealerList;
    }



    @Override
    public TableList sendDlrFilterList(DlrInvntryPdfFilter filter, UUID userId, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        filter.setUsers(allUsersUnderDealer);
        Specification<DlrInvntryView> dlrInvntryViewSpecification = AppraisalSpecification.getDlrInvntryViewSpecification(filter);
        Page<DlrInvntryView> all1 = dlrInvntryViewRepo.findAll(dlrInvntryViewSpecification, pageable);
        List<PdfDataDto> dlrInvntryFilter = offersMapper.lDlrInvViewToPdfDataDto(all1.toList());
        TableList tableList= new TableList();
        tableList.setDlrInvntryList(dlrInvntryFilter);
        tableList.setCode(HttpStatus.OK.value());
        tableList.setMessage("dealerList found");
        tableList.setStatus(true);
        tableList.setTotalPages(all1.getTotalPages());
        tableList.setTotalRecords(all1.getTotalElements());
        return  tableList;
    }

    @Override
    public FilterDropdowns makeDropDown(UUID userId) {
        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        List<String> makeDropDown =dlrInvntryViewRepo.dlrInvntryVehMakeDropDown(allUsersUnderDealer);
        FilterDropdowns filterDropdowns = new FilterDropdowns();
        filterDropdowns.setMake(makeDropDown);
        filterDropdowns.setMessage("vehicle make dropdown list");
        filterDropdowns.setStatus(true);
        filterDropdowns.setCode(HttpStatus.OK.value());
        return filterDropdowns;
    }

    @Override
    public List<Company> searchCompany(String name) {
        int pageSize = 5; // Number of items to return per page
        int startIndex = 0; // Calculate the start index for the current page

        List<Company> companies = null;
        List<ECompany> companyList = companyRepo.findAll(AppraisalSpecification.searchByCompany(name));

        if (!companyList.isEmpty()) {
            companies = appraisalVehicleMapper.leCompDetailsTolCompDetails(companyList);

            // Check if there are enough elements for the requested page
            if (startIndex < companies.size()) {
                int endIndex = Math.min(startIndex + pageSize, companies.size());
                companies = companies.subList(startIndex, endIndex);
            } else {
                // If there are no more elements for the requested page, return an empty list
                companies = Collections.emptyList();
            }
        }
        return companies;
    }

    @Override
    public TableList sendDlrFilterListPdf(DlrInvntryPdfFilter filter, UUID userId) {

        List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);
        filter.setUsers(allUsersUnderDealer);
        Specification<DlrInvntryView> dlrInvntryViewSpecification = AppraisalSpecification.getDlrInvntryViewSpecification(filter);
        List<DlrInvntryView> all1 = dlrInvntryViewRepo.findAll(dlrInvntryViewSpecification);
        List<PdfDataDto> dlrInvntryFilter = offersMapper.lDlrInvViewToPdfDataDto(all1);
        TableList tableList= new TableList();
        tableList.setDlrInvntryList(dlrInvntryFilter);
        tableList.setCode(HttpStatus.OK.value());
        tableList.setMessage("dealerList found");
        tableList.setStatus(true);
        log.info("inventoryPdfData send to jasper");
        return  tableList;
    }

    @Override
    public CorporateAdminList sendCorporateList() throws AppraisalException {
//        Specification<CorporateAdminView> corAdminViewSpec = AppraisalSpecification.corporateAdminList(filter);
        List<CorporateAdminView> all = corAdminViewRepo.findAll();
        CorporateAdminList corporateAdminList= new CorporateAdminList();
        if(!all.isEmpty()){
            corporateAdminList.setCorAdminList(all);
        }else throw new AppraisalException("No such Corporate Admin Found");
        corporateAdminList.setCode(HttpStatus.OK.value());
        corporateAdminList.setMessage("Corporate Admin List found");
        corporateAdminList.setStatus(true);
        return corporateAdminList;
    }

    @Override
    public List<UserRegistration> getUserList(String roleGroup) {
//       Pageable pageable=PageRequest.of(0,8);
       List<UserRegistration> user = null;
//       Page<UserListView> all1 = userListViewRepo.findAll(AppraisalSpecification.usrLst(roleGroup, name),pageable);
        List<UserListView> all1 = userListViewRepo.findByRoleGroup(roleGroup);
        if ( null!=all1) {
            user = appraisalVehicleMapper.lUserListViewToUserReg(all1);
        }
        return user;
    }

    @Override
    public List<D2DlrList> sendDlrD2(UUID userId) throws AppraisalException {
        log.info("searching of dealer d2 started");
//        Specification<D2DlrList> dlrD2ListSpec = AppraisalSpecification.d2List(filter);
        List<D2DlrList> all = d2DlrListRepo.findByDealerAdmin(userId);
        if(!all.isEmpty()){
            return all;
        }else throw  new AppraisalException("No record available");

    }
    @Override
    public List<FactoryPersonnel> sendFctryPrsnl(SellingDealer filter) throws AppraisalException {
        log.info("searching of factory personnel started");
        Specification<FactoryPersonnel> fctryPrsnl = AppraisalSpecification.fctryPrsnl(filter);
        List<FactoryPersonnel> all = fctryPrsnnelRepo.findAll(fctryPrsnl);
        if(!all.isEmpty()){
            return all;
        }else throw  new AppraisalException("No record available");

    }




}
