package com.massil.repository;
//Author:Rupesh khade

import com.massil.persistence.model.EAppraiseVehicle;
import org.hibernate.search.backend.elasticsearch.ElasticsearchBackend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface AppraiseVehicleRepo extends JpaRepository<EAppraiseVehicle,Long>, JpaSpecificationExecutor<EAppraiseVehicle>{

   // @Query("select e from EAppraiseVehicle e where e.vinNumber=:vin and e.dealer.id=:dealerId and e.valid=true")
    @Query("select e from EAppraiseVehicle e where e.vinNumber=:vin and valid=true and e.dealer.id=(select u.dealer.id from EUserRegistration" +
            " u where u.id=:userId and u.valid=true)")
    List< EAppraiseVehicle> findByVinAndDlrId(String vin, UUID userId);

    @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.user.id = :userId AND " +
            "(e.invntrySts = 'created' OR e.invntrySts = 'Draft') AND e.valid = :valid ORDER BY e.createdOn DESC")
    Page<EAppraiseVehicle> appraisalCards(UUID userId, boolean valid, Pageable pageable);

    @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.user.id in (:userId) AND " +
         "(e.invntrySts = 'created' OR e.invntrySts = 'Draft') AND e.valid = :valid ORDER BY e.createdOn DESC")
    Page<EAppraiseVehicle> allAppraisalCards(List<Long> userId, boolean valid, Pageable pageable);

    /**
     * Returns the valid EAppraiseVehicle object base on EAppraiseVehicle id
     * @param id This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */

    @Query(value = "select e from EAppraiseVehicle e where e.id=:id and e.valid=true ")
    EAppraiseVehicle getAppraisalById(Long id);

    /**
     * returns the vinNumber
     * @param id this is the Appraisal id of EAppraiseVehicle
     * @return String
     */
    @Query(value = "select e.vinNumber from EAppraiseVehicle e where e.id=:id and e.valid=true ")
    String getVin(Long id);

    /**
     * Returns the valid EAppraiseVehicle object base on EAppraiseVehicle id
     * @param id This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */
    @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.user.id =:id AND e.valid = true AND e.createdOn = ("
            + "SELECT MAX(e2.createdOn) FROM EAppraiseVehicle e2 WHERE e2.user.id =:id)")
    EAppraiseVehicle getAppraisalDetails(UUID id);



    /**
     * Returns the valid EAppraiseVehicle object base on EAppraiseVehicle id
     * @param apprRefId This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */
    @Query(value = "select e from EAppraiseVehicle e where e.id=:apprRefId AND e.valid=true AND e.invntrySts=:invntry")
    EAppraiseVehicle findVehicleByInventorySts(Long apprRefId, String invntry);

    /**
     * Returns the valid List of EAppraiseVehicle object base on userId
     * @param userId This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */
    @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.user.id = :userId AND "+
            "e.invntrySts = :inventory AND e.valid = :valid ORDER BY e.modifiedOn DESC")
    Page<EAppraiseVehicle> findUserAndInvntrySts(@Param("userId") UUID userId,@Param("inventory") String inventory, boolean valid, Pageable pageable);



    /**
     * Returns the valid List of EAppraiseVehicle object base on userId
     * @param userId This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */

    @Query(value = "SELECT av FROM EAppraiseVehicle av " +
            "WHERE av.user.id <> :userId AND av.invntrySts = :inventory " +
            "AND av.valid =:valid AND av.field1<>true AND av.field2<>true " +
            "AND av.id NOT IN (SELECT o.appRef.id FROM EOffers o WHERE o.status.id in (4,5) AND o.appRef IN (SELECT av.id FROM EAppraiseVehicle av WHERE av.user.id <> :userId AND av.invntrySts = :inventory AND av.valid = true)) " +
            "ORDER BY av.modifiedOn DESC")
    Page<EAppraiseVehicle> findByUserIdNot(@Param("userId") UUID userId, @Param("inventory") String inventory,Boolean valid, Pageable pageable);

    /**
     * Returns the valid List of EAppraiseVehicle object base on DealerId
     * @param dealerId This is the id of EAppraiseVehicle
     * @return EAppraiseVehicle
     */

/*
    @Query(value = "SELECT av FROM EAppraiseVehicle av " +
            "WHERE av.dealer.id <> :dealerId AND av.invntrySts = :inventory " +
            "AND av.valid =:valid  " +
            "AND av.id NOT IN (SELECT o.appRef FROM EOffers o WHERE o.status in (4,5) AND o.appRef IN (SELECT av.id FROM EAppraiseVehicle av WHERE av.user.id <> :dealerId AND av.invntrySts = :inventory AND av.valid = true)) " +
            "ORDER BY av.modifiedOn DESC")
    Page<EAppraiseVehicle> findByDealerIdNot(@Param("dealerId") Long dealerId,@Param("inventory") String inventory, Boolean valid, Pageable pageable);

*/

    /**
     * this method used to find inventoryStatus as created and pushForBuyFigure is true, of not same userid
     * @param userId this is the userId or DealerId
     * @param invntrySts this is inventoryStatus field checking for created
     * @param pushForBuyFig  checking pushForBuyFig is true
     * @param valid checking is_Valid is true
     * @param pageable This is object of Pageable
     * @return EAppraiseVehicle
     */
    @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.user.id <> :userId AND e.invntrySts = :invntrySts "
            +"AND e.tdStatus.pushForBuyFig = :pushForBuyFig And e.valid=:valid ORDER BY e.modifiedOn DESC")
    Page<EAppraiseVehicle> findNotUserIdInAvbleTrdCrds(UUID userId, String invntrySts, boolean pushForBuyFig, boolean valid ,Pageable pageable);



    /**
     * this method used to find inventoryStatus as created and pushForBuyFigure is true, of not same dealer id
     * @param dealerId this is the DealerId
     * @param invntrySts this is inventoryStatus field checking for created
     * @param pushForBuyFig checking pushForBuyFig is true
     * @param valid checking is_Valid is true
     * @param pageable This is object of Pageable
     * @return EAppraiseVehicle
     */
      @Query(value = "SELECT e FROM EAppraiseVehicle e WHERE e.dealer.id <> :dealerId AND e.invntrySts = :invntrySts "
            +"AND e.tdStatus.pushForBuyFig = :pushForBuyFig And e.valid=:valid ORDER BY e.modifiedOn DESC")
      Page<EAppraiseVehicle> findNotDlrIdInAvbleTrdCrds(Long dealerId, String invntrySts, boolean pushForBuyFig, boolean valid ,Pageable pageable);


    @Query("SELECT count(*) FROM EAppraiseVehicle e WHERE e.invntrySts <> 'Draft' AND valid=true")
    Long getTotalVehiclesInSystem();
    @Query("(SELECT o.appRef.id FROM EOffers o WHERE o.status.id in (4,5) AND o.appRef IN (SELECT av.id FROM EAppraiseVehicle av WHERE av.user.id <> :userId AND av.invntrySts ='inventory' AND av.valid = true))")
   List<Long> apprIdOfUnsoldVehicles(UUID userId);

}
