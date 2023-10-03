package com.massil.repository.elasticRepo;

import com.massil.constants.AppraisalConstants;
import com.massil.dto.CardsPage;
import com.massil.dto.FilterParameters;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.repository.ConfigCodesRepo;
import com.massil.util.CompareUtils;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
public class AppraisalVehicleERepo {
    Logger log = LoggerFactory.getLogger(AppraisalVehicleERepo.class);
    @Autowired
    private AppraisalVehicleMapper mapper;
    @Autowired
    private SearchSession searchSession;
    @Autowired
    private ConfigCodesRepo configCodesRepo;
    @Autowired
    private CompareUtils compareUtils;


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
    public CardsPage filterAppraisalCards(FilterParameters filter, UUID userId, Integer pageNo, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNo,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( (f,root)->{
                    if(null!=filter) {
                        root.add(f.match().field("user.id").matching(userId ));
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



    public CardsPage inventoryCards(UUID userId, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.bool()
                        .must( f.match().field( "user.id" )
                                .matching( userId ) )
                        .must( f.match().field( "invntrySts" )
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

    public CardsPage filterInventoryCards(FilterParameters filter, UUID userId, Integer pageNo, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNo,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( (f,root)->{
                            if(null!=filter) {
                                root.add(f.match().field("user.id").matching(userId ));
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

}
