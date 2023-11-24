package com.massil.util;

import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.model.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.Date;
import java.util.List;
import java.util.UUID;


public class AppraisalSpecification {
    private AppraisalSpecification(){

    }
    public static Specification<EAppraiseVehicle> getEApprSpecification(FilterParameters filter, List<UUID> userId) {
        Specification<EAppraiseVehicle> spec = Specification.where((root, query, criteriaBuilder) -> null);

        spec = spec.and((root, query, criteriaBuilder) -> root.get(AppraisalConstants.USER).get(AppraisalConstants.ID).in(userId));
        spec = spec.and((root, query, criteriaBuilder) -> root.get(AppraisalConstants.INVENTORYSTS).in( AppraisalConstants.CREATED,AppraisalConstants.DRAFT));
        spec = spec.and(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VALID),Boolean.TRUE)));
        spec = getEAppraiseVehicleSpecification(filter, spec);
        return spec;
    }
    public static Specification<AppraisalFilterParamView> getAppFilParaSpec(FilterParameters filter, List<UUID> userId) {
        Specification<AppraisalFilterParamView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        spec = spec.and((root, query, criteriaBuilder) -> root.get(AppraisalConstants.USERID).in(userId));
        spec = getAppFilParamSpec(filter, spec);
        return spec;
    }


    public static Specification<EAppraiseVehicle> getInventrySpecification(FilterParameters filter, List<UUID> userId) {
        Specification<EAppraiseVehicle> spec = Specification.where((root, query, criteriaBuilder) -> null);
        spec = spec.and((root, query, criteriaBuilder) -> root.get(AppraisalConstants.USER).get(AppraisalConstants.ID).in(userId));
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.INVENTORYSTS), AppraisalConstants.INVENTORY));
        spec = spec.and(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VALID),Boolean.TRUE)));
        spec = getEAppraiseVehicleSpecification(filter, spec);
        return spec;
    }
    public static Specification<InventoryFilterParamView> getInvFilParaSpec(FilterParameters filter, List<UUID> userId) {
        Specification<InventoryFilterParamView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        spec = spec.and((root, query, criteriaBuilder) -> root.get(AppraisalConstants.USERID).in(userId));
        spec = getInvFilParamSpec(filter, spec);
        return spec;
    }
    public static Specification<EAppraiseVehicle> getSearchFactorySpecification(FilterParameters filter, List<UUID> userId, List<Long> soldVehicles) {
        Specification<EAppraiseVehicle> spec = Specification.where((root, query, criteriaBuilder) -> null);

        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.not(root.get(AppraisalConstants.USER).get(AppraisalConstants.ID).in(userId)));
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.INVENTORYSTS), AppraisalConstants.INVENTORY));
        spec = spec.and(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VALID),Boolean.TRUE)));
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("field1"), true));
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.notEqual(root.get("field2"), true));
        if(null!=soldVehicles && !soldVehicles.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.not(root.get(AppraisalConstants.ID).in(soldVehicles)));
        }
        spec = getEAppraiseVehicleSpecification(filter, spec);
        return spec;
    }
    public static Specification<SearchFactoryView> getSearchFactSpec(FilterParameters filter, List<UUID> userId) {
        Specification<SearchFactoryView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.not(root.get(AppraisalConstants.USERID).in(userId)));
        spec = getSearchFactSpec(filter, spec);
        return spec;
    }

    public static Specification<TransactionReport> displayBySellingDealer(SellingDealer filter) {
        Specification<TransactionReport> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.FIRSTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.LASTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        return spec;
    }

    public static Specification<MembersByFactoryManager> displayByFtryManager(SellingDealer filter) {
        Specification<MembersByFactoryManager> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("fmFirstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("fmLastName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        return spec;
    }


    public static Specification<MembersByFactorySalesmen> displayByFtrySales(SellingDealer filter) {
        Specification<MembersByFactorySalesmen> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("fsFirstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("fsLastName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        return spec;
    }


    private static Specification<EAppraiseVehicle> getEAppraiseVehicleSpecification(FilterParameters filter, Specification<EAppraiseVehicle> spec) {
        if (null!= filter.getYear()){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VEH_YEAR), filter.getYear()));
        }
        if (null!= filter.getMinDistance()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(AppraisalConstants.VEH_MILES), filter.getMinDistance()));
        }
        if (null!= filter.getMaxDistance()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(AppraisalConstants.VEH_MILES), filter.getMaxDistance()));
        }
        if (null!=filter.getMinYear()) {
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get(AppraisalConstants.VEH_YEAR), filter.getMinYear()));
        }
        if (null!=filter.getMaxYear()) {
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(AppraisalConstants.VEH_YEAR), filter.getMaxYear()));
        }
        if (null!= filter.getMake()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MAKE)), filter.getMake().toLowerCase()));
        }
        if (null!= filter.getModel()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MODEL)),"%" + filter.getModel().toLowerCase() + "%"));
        }

        if (null != filter.getSeries()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_SERIES)),"%" + filter.getSeries().toLowerCase() + "%"));
        }

        if (null!= filter.getEngine()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.TD_STATUS).get(AppraisalConstants.ENG_TYPE)), filter.getEngine().toLowerCase()));
        }
        if (null!= filter.getTransmission()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.TD_STATUS).get(AppraisalConstants.TRANS_TYPE)), filter.getTransmission().toLowerCase()));
        }

        return spec;
    }


    private static Specification<AppraisalFilterParamView> getAppFilParamSpec(FilterParameters filter, Specification<AppraisalFilterParamView> spec) {
        if (null!= filter.getYear()){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VEH_YEAR), filter.getYear()));
        }
        if (null!= filter.getMake()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MAKE)), filter.getMake().toLowerCase()));
        }
        if (null!= filter.getModel()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MODEL)),"%" + filter.getModel().toLowerCase() + "%"));
        }
        return spec;
    }
    private static Specification<InventoryFilterParamView> getInvFilParamSpec(FilterParameters filter, Specification<InventoryFilterParamView> spec) {
        if (null!= filter.getYear()){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VEH_YEAR), filter.getYear()));
        }
        if (null!= filter.getMake()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MAKE)), filter.getMake().toLowerCase()));
        }
        if (null!= filter.getModel()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MODEL)),"%" + filter.getModel().toLowerCase() + "%"));
        }
        return spec;
    }
    private static Specification<SearchFactoryView> getSearchFactSpec(FilterParameters filter, Specification<SearchFactoryView> spec) {
        if (null!= filter.getYear()){
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.VEH_YEAR), filter.getYear()));
        }
        if (null!= filter.getMake()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MAKE)), filter.getMake().toLowerCase()));
        }
        if (null!= filter.getModel()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MODEL)),"%" + filter.getModel().toLowerCase() + "%"));
        }
        return spec;
    }



    public static Specification<DlrInvntryView> getDlrInvntryViewSpecification(DlrInvntryPdfFilter filter) {

        Specification<DlrInvntryView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(AppraisalConstants.INVENTORYSTS), AppraisalConstants.INVENTORY));
        spec = spec.and(((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("valid"),Boolean.TRUE)));

        if (null!= filter.getConsumerAskPrice()&& filter.getConsumerAskPrice()!=0.0) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(AppraisalConstants.CONSUMER_ASKING_PRICE), filter.getConsumerAskPrice()));
        }
        if (null!= filter.getVehicleMake() && !filter.getVehicleMake().equals("")) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get(AppraisalConstants.VEH_MAKE)), filter.getVehicleMake().toLowerCase()));
        }
        if (null!= filter.getDelrRetlAskPrice() && filter.getDelrRetlAskPrice()!=0.0) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get(AppraisalConstants.Dealer_ASKING_PRICE), filter.getDelrRetlAskPrice()));
        }
        if (null!= filter.getDaysSinceInventory() && filter.getDaysSinceInventory()!=0.0) {
            Date daysAgo = new Date(System.currentTimeMillis() - (filter.getDaysSinceInventory() * 24 * 60 * 60 * 1000));
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("invntryDate"), daysAgo));
        }
        if(null!=filter.getUsers()){
            spec = spec.and((root, query, criteriaBuilder) -> root.get("user").in(filter.getUsers()));
        }
        return spec;
    }


    public static Specification<ECompany> searchByCompany(String companyName){
        Specification<ECompany> spec = Specification.where((root, query, criteriaBuilder) -> null);
            spec=spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%"+companyName.toLowerCase()+"%"));
        return spec;
    }


    public static Specification<EDealerRegistration> dsplyDlrList(DealerRegistration filter) {
        Specification<EDealerRegistration> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.FIRSTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.LASTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }

        return spec;
    }
    public static Specification<AllDealersView> dsplyDlrs(DealerRegistration filter) {
        Specification<AllDealersView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.FIRSTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.LASTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        return spec;
    }

    public static Specification<UserListView> usrLst(String roleGroup, String name) {
        Specification<UserListView> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (name != null) {
            spec = spec.or((root, query, criteriaBuilder) ->criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + name.toLowerCase() + "%"));
        }
        if (name != null) {
            spec = spec.or((root, query, criteriaBuilder) ->criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + name.toLowerCase() + "%"));
        }
        if (roleGroup != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(criteriaBuilder.lower(root.get("roleGroup")), roleGroup.toLowerCase()));
        }

        return spec;
    }


    public static Specification<D2DlrList> d2List(SellingDealer filter) {
        Specification<D2DlrList> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if(null!=filter.getUserId()){
            spec= spec.and((root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("dealerAdmin"),filter.getUserId()));
        }
        return spec;
    }
	 public static Specification<FactoryPersonnel> fctryPrsnl(SellingDealer filter) {
        Specification<FactoryPersonnel> spec = Specification.where((root, query, criteriaBuilder) -> null);
        if (null!=filter.getFirstName()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.FIRSTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        if (null!=filter.getFirstName()) {
            spec = spec.or((root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get(AppraisalConstants.LASTNAME)), "%" + filter.getFirstName().toLowerCase() + "%"));
        }
        return spec;
    }
}
