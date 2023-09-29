package com.massil.repository.elasticRepo;

import com.massil.controller.AppraisalVehicleController;
import com.massil.dto.CardsPage;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EConfigCodes;
import com.massil.repository.ConfigCodesRepo;
import jakarta.persistence.TypedQuery;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.engine.search.query.SearchResultTotal;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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


    public CardsPage appraisalCards(UUID userId, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EAppraiseVehicle> searchResult = searchSession.search(EAppraiseVehicle.class)
                .where( f -> f.and(
                        f.match().field( "title" )
                                .matching( "robot" ),
                        f.match().field( "description" )
                                .matching( "crime" ),
                        f.match().field( "description" )
                                .matching( "crime" ),
                        f.match().field( "description" )
                                .matching( "crime" )
                ) )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EAppraiseVehicle> appraiseVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setAppraiseVehicleList(appraiseVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(totalRecords/pageSize);
        return cardsPage;
    }



}
