package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.dto.*;
import com.massil.persistence.model.D2DlrList;

import java.util.List;
import java.util.UUID;

public interface FilterSpecificationService {
    /**
     * This method will show appraisal vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    CardsPage filterAppraisalVehicle(FilterParameters filter, UUID userId, Integer pageNo, Integer pageSize) throws AppraisalException;

    /**
     * This method will show Inventory vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    CardsPage filterInventoryVehicle(FilterParameters filter, UUID userId, Integer pageNo,Integer pageSize) throws AppraisalException;

    /**
     * This method will show search factory vehicles based on filer parameters
     * @param filter
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    CardsPage filterSearchFactoryVehicle(FilterParameters filter, UUID userId, Integer pageNo,Integer pageSize) throws AppraisalException;

    public SellingDealerList sendSlrDlrList(SellingDealer filter) throws AppraisalException;
    public SellingDealerList displayByFtryManagerList(SellingDealer filter) throws AppraisalException;
    public SellingDealerList displayByFtrySalesList(SellingDealer filter) throws AppraisalException;
    public DlrList srchByDlrList(DealerRegistration filter) throws AppraisalException;
    public TableList sendDlrFilterList(DlrInvntryPdfFilter filter, UUID userId, Integer pageNo, Integer pageSize);
    public TableList sendDlrFilterListPdf(DlrInvntryPdfFilter filter, UUID userId);

    public List<Company> searchCompany(String name);

    public CorporateAdminList sendCorporateList(SellingDealer filter) throws AppraisalException;

    public List<UserRegistration> getUserList(String roleGroup,String name);

    DlrList searchDealers(DealerRegistration filter) throws AppraisalException;

    List<D2DlrList> sendDlrD2(SellingDealer filter) throws AppraisalException;
}
