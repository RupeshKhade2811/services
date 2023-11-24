package com.massil.repository.elasticRepo;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.CardsPage;
import com.massil.dto.FilterDropdowns;
import com.massil.dto.FilterParameters;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.ERoleMapping;
import com.massil.repository.ConfigCodesRepo;
import com.massil.repository.RoleMappingRepo;
import com.massil.util.CompareUtils;
import com.massil.util.DealersUser;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class AppraisalVehicleERepo {
    Logger log = LoggerFactory.getLogger(AppraisalVehicleERepo.class);
    @Autowired
    private SearchSession searchSession;

    @Autowired
    private CompareUtils compareUtils;
    @Autowired
    private RoleMappingRepo mappingRepo;
    @Autowired
    private DealersUser dealersUser;


    public CardsPage appraisalCards(UUID userId, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.bool()
                        .must( f.match().field( "user.id" )
                                .matching( userId ) )
                        .mustNot( f.match().field( "invntrySts" )
                                .matching( AppraisalConstants.INVENTORY ))
                        .must(f.match().field("valid")
                                .matching(true))
                ).sort( f -> f.field( "createdOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }
    public CardsPage filterAppraisalCards(FilterParameters filter, List<UUID> userId, Integer pageNo, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNo,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( (f,root)->{
                    if(null!=filter) {
                        root.add(f.terms().field("user.id").matchingAny(userId ));
                        root.add(f.bool().mustNot(f.match().field("invntrySts").matching(AppraisalConstants.INVENTORY)));
                        root.add(f.match().field("valid").matching(true ));

                        if (null != filter.getMake()) {
                            root.add(f.match().field("vehicleMake").matching(filter.getMake()));
                        }
                        if (null != filter.getModel()) {
                            root.add(f.match().field("vehicleModel").matching(filter.getModel()));
                        }
                        if (null != filter.getYear()) {
                            root.add(f.match().field("vehicleYear").matching(filter.getYear()));
                        }
                    }

                }
                ).sort( f -> f.field( "createdOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }

    public FilterDropdowns filterAppraisalParams(FilterParameters filter, UUID userId) throws AppraisalException {
        log.info("From ElasticSearchRepo");
        ERoleMapping byUserId = mappingRepo.findByUserId(userId);
        FilterDropdowns dropdowns = new FilterDropdowns();
        if (null != byUserId) {
            if (byUserId.getRole().getRole().equals("D2") || byUserId.getRole().getRole().equals("D3") || byUserId.getRole().getRole().equals("S1") || byUserId.getRole().getRole().equals("M1")) {
                userId = byUserId.getDealerAdmin();
            }
            List<UUID> allUsersUnderDealer = dealersUser.getAllUsersUnderDealer(userId);


            SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                    .where((f, root) -> {
                                if (null != filter) {

                                    root.add(f.terms().field("user.id").matchingAny(allUsersUnderDealer));
                                    root.add(f.bool().mustNot(f.match().field("invntrySts").matching(AppraisalConstants.INVENTORY)));
                                    root.add(f.match().field("valid").matching(true));

                                    if (null != filter.getMake()) {
                                        root.add(f.match().field("vehicleMake").matching(filter.getMake()));
                                    }
                                    if (null != filter.getModel()) {
                                        root.add(f.match().field("vehicleModel").matching(filter.getModel()));
                                    }
                                    if (null != filter.getYear()) {
                                        root.add(f.match().field("vehicleYear").matching(filter.getYear()));
                                    }
                                }

                            }
                    ).sort(f -> f.field("createdOn").desc()).fetchAll();

            List<EAppraiseVehicle> appVehicles = searchResult.hits();
            List<Long> yearList = null;
            List<String> makeList = null;
            List<String> modelList = null;
            yearList = appVehicles.stream().map(EAppraiseVehicle::getVehicleYear).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            yearList.sort(Collections.reverseOrder());
            makeList = appVehicles.stream().map(EAppraiseVehicle::getVehicleMake).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            Collections.sort(makeList);
            modelList = appVehicles.stream().map(EAppraiseVehicle::getVehicleModel).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            Collections.sort(modelList);
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


    public CardsPage searchFactory(UUID id, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.bool()
                        .mustNot( f.match().field( "user.id" )
                                .matching( id ) )
                        .must( f.match().field( "invntrySts" )
                                .matching( AppraisalConstants.INVENTORY ))
                        .must(f.match().field("valid")
                                .matching(true))
                        .must(f.match().field("field1")
                                .matching(false))
                        .must(f.match().field("field2")
                                .matching(false))
                        .mustNot(f.match().field("offers.status.id").matching(4L))
                        .mustNot(f.match().field("offers.status.id").matching(5L))
                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }

    public CardsPage filterSearchFactoryVehicle(FilterParameters filter, UUID userId, Integer pageNo,Integer pageSize) {
        log.info("From ElasticSearchRepo filterSearchFactoryVehicle");

        Integer offset=Math.multiplyExact(pageNo,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( (f,root)->{
                            if(null!=filter) {
                                root.add(f.bool().mustNot(f.match().field("user.id").matching(userId )));
                                root.add(f.bool().must(f.match().field("invntrySts").matching(AppraisalConstants.INVENTORY)));
                                root.add(f.match().field("valid").matching(true ));
                                root.add(f.match().field("field1").matching(false ));
                                root.add(f.match().field("field2").matching(false ));
                                root.add(f.bool().mustNot(f.match().field("offers.status.id").matching(4L)));
                                root.add(f.bool().mustNot(f.match().field("offers.status.id").matching(5L)));

                                if (null != filter.getMake()) {
                                    root.add(f.match().field("vehicleMake").matching(filter.getMake()));
                                }
                                if (null != filter.getModel()) {
                                    root.add(f.match().field("vehicleModel").matching(filter.getModel()));
                                }
                                if (null != filter.getYear()) {
                                    root.add(f.match().field("vehicleYear").matching(filter.getYear()));
                                }
                            }
                }
                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }



    public CardsPage inventoryCards(List<UUID> userId, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.bool()
                        .must( f.terms().field( "user.id" )
                                .matchingAny( userId ) )
                        .must( f.match().field( "invntrySts" )
                                .matching( AppraisalConstants.INVENTORY ))
                        .must(f.match().field("valid")
                                .matching(true))
                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }

    public CardsPage filterInventoryCards(FilterParameters filter, List<UUID> userId, Integer pageNo, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNo,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( (f,root)->{
                            if(null!=filter) {
                                root.add(f.terms().field("user.id").matchingAny(userId ));
                                root.add(f.bool().must(f.match().field("invntrySts").matching(AppraisalConstants.INVENTORY)));
                                root.add(f.match().field("valid").matching(true ));

                                if (null != filter.getMake()) {
                                    root.add(f.match().field("vehicleMake").matching(filter.getMake()));
                                }
                                if (null != filter.getModel()) {
                                    root.add(f.match().field("vehicleModel").matching(filter.getModel()));
                                }
                                if (null != filter.getYear()) {
                                    root.add(f.match().field("vehicleYear").matching(filter.getYear()));
                                }
                            }

                        }
                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }

    public CardsPage availableTradeCards(List<UUID> userId, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.bool()
                        .mustNot( f.terms().field( "user.id" ).matchingAny( userId ) )
                        .must( f.match().field( "invntrySts" )
                                .matching( AppraisalConstants.CREATED ))
                        .must(f.match().field("valid")
                                .matching(true))
                        .must(f.match().field("tdStatus.pushForBuyFig")
                                .matching(true))
                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }

}
