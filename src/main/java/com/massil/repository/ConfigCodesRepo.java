package com.massil.repository;

import com.massil.persistence.model.EConfigCodes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
@Transactional
public interface ConfigCodesRepo extends JpaRepository<EConfigCodes,Long> {

    /**
     * Returns the List< EConfigCodes> base on codeType
     * @param codeType This is codeType filed of EConfigCodes
     * @return  List< EConfigCodes>
     */
    List<EConfigCodes> findByCodeType(String codeType);

    /**
     * This method returns the List<EConfigCodes> base on List<Long>
     * @param id This is  object of List<Long>
     * @return List<EConfigCodes>
     */
    @Query(value = "select e from EConfigCodes e where e.id in (:id) and e.valid=true  ")
    List<EConfigCodes> findByCodeId(List<Long> id);

    /**
     * This method returns List of valid EConfigCodes base on  configGroup
     * @param configGroup This is the field of EConfigCodes class
     * @return List<EConfigCodes>
     */
    @Query(value="select c from EConfigCodes c where c.configGroup=:configGroup and c.valid=true")
    List<EConfigCodes> getCodesByConfigGroup(String configGroup);

    @Query(value="select e from EConfigCodes e where e.valid=true and e.id=:codeId")
    EConfigCodes configByCodeId(Long codeId);
    @Query(value="select e from EConfigCodes e where e.valid=true and e.codeType=:codeType and e.shortCode=:shortCode")
    EConfigCodes findByCodeTypeAndShortCode(String codeType,String shortCode);

    @Query(value="select intValue from EConfigCodes e where e.valid=true and e.shortCode=:shortCode")
    Integer getFee(String shortCode);

    @Query(value="select c.id from EConfigCodes c where c.shortDescrip=:exterior and c.codeType='EXTERIOR_COLOR' and c.valid=true")
    List<Long> getCodeForExtColor(String exterior);
    @Query(value="select c.id from EConfigCodes c where c.shortDescrip=:interior and c.codeType='INTERIOR_COLOR' and c.valid=true")
    List<Long> getCodeForIntColor(String interior);
    @Query(value="select c.valid from EConfigCodes c where c.shortDescrip='ElasticSearch'")
    Boolean isElasticActive();



}
