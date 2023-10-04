package com.massil.repository.elasticRepo;

import com.google.gson.JsonObject;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.CardsPage;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EOffers;
import com.massil.util.CompareUtils;
import org.hibernate.search.backend.elasticsearch.ElasticsearchExtension;
import org.hibernate.search.backend.elasticsearch.search.aggregation.impl.ElasticsearchTermsAggregation;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.aggregation.SearchAggregation;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.scope.SearchScope;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
public class OffersERepo {
    Logger log = LoggerFactory.getLogger(OffersERepo.class);
    @Autowired
    private SearchSession searchSession;

    @Autowired
    private CompareUtils compareUtils;

    public CardsPage procurementCards(UUID id, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo procurementCards");
        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchResult<EOffers> searchResult = searchSession.search(EOffers.class)
                .where( f -> f.bool()
                        .must( f.match().field( "buyerUserId.id" )
                                .matching( id ) )
                        .must( f.match().field( "appRef.invntrySts" )
                                .matching( AppraisalConstants.INVENTORY ))
                        .must(f.match().field("valid")
                                .matching(true))
                        .must(f.match().field("appRef.valid")
                                .matching(true))
                        .must(f.match().field("isTradeBuy")
                                .matching(false))

                ).sort( f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        long totalRecords = searchResult.total().hitCount();
        List<EOffers> procureVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setEOffersList(procureVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));
        return cardsPage;
    }
    public CardsPage liquidationCards(UUID id, Integer pageNumber, Integer pageSize){
        log.info("From ElasticSearchRepo liquidationCards");

        Integer offset=Math.multiplyExact(pageNumber,pageSize);
        SearchScope<EOffers> scope = searchSession.scope( EOffers.class );
        AggregationKey<Map<Object, Long>> countsByGenreKey = AggregationKey.of( "appRef.id" );

        SearchResult<EOffers> searchResult = searchSession.search(EOffers.class)
                .where( f -> f.bool()
                        .must( f.match().field( "sellerUserId.id" )
                                .matching( id ) )
                        .must( f.match().field( "appRef.invntrySts" )
                                .matching( AppraisalConstants.INVENTORY ))
                        .must(f.match().field("valid")
                                .matching(true))
                        .must(f.match().field("appRef.valid")
                                .matching(true))
                        .must(f.match().field("isTradeBuy")
                                .matching(false))
                              )
                .aggregation( countsByGenreKey, scope.aggregation().withRoot("appRef").terms()
                        .field( "id", Object.class )
                        .orderByTermDescending().toAggregation() )
                .sort(f -> f.field( "modifiedOn" ).desc() )
                .fetch(offset,pageSize);
        Map<Object, Long> countsByGenre = searchResult.aggregation( countsByGenreKey );
        List<Object> objectList = countsByGenre.keySet().stream().toList();
        long totalRecords = searchResult.total().hitCount();
        List<EOffers> liquidVehicles = searchResult.hits();
        CardsPage cardsPage=new CardsPage();
        cardsPage.setEOffersList(liquidVehicles);
        cardsPage.setTotalRecords(totalRecords);
        cardsPage.setTotalPages(compareUtils.calTotalPages(totalRecords, Long.valueOf(pageSize)));

        return cardsPage;
    }



}
