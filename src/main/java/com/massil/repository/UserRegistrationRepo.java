package com.massil.repository;
//Author:yudhister vijay

import com.massil.persistence.model.EUserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
@Transactional
public interface UserRegistrationRepo extends JpaRepository<EUserRegistration,Long>, JpaSpecificationExecutor<EUserRegistration> {

    /**
     * This method returns the valid  EUserRegistration object
     * @param userId This is the userId  field of EUserRegistration class
     * @author YogeshKumarV
     * @return EUserRegistration
     */
    @Query(value = "select e from EUserRegistration e where e.valid=true and e.id=:userId")
    EUserRegistration findUserById(UUID userId);

/*    @Query(value = "select e from EUserRegistration e where e.valid=true and e.dealer.id=:d2dealerId")
    EUserRegistration findDealerById(Long d2dealerId);*/

    /**
     * This method returns the list of user under the given dealer
     * @param dealerId this is the dealerId
     * @author YogeshKumarV
     * @return  List<EUserRegistration>
     */
    @Query(value = "select e from EUserRegistration e where e.valid=true and e.dealer.id=:dealerId")
    List<EUserRegistration> findUserByDealerId(Long dealerId);

    /**
     *  This method returns EUserRegistration
     * @param userName This is userName
     * @param pass This is Password
     * @return EUserRegistration
     */
    @Query(value = "SELECT u FROM EUserRegistration u WHERE u.userName =:userName AND u.password =:pass AND u.valid=true")
    EUserRegistration findByUserNameAndPassword(String userName,String pass);

    /**
     * this method check whether username is present or not
     * @param email receive from UI
     * @return EUsrRegistration
     */
    @Query(value = "select e from EUserRegistration e where e.valid=true and e.userName=:email")
    EUserRegistration checkUserNamePresent(String email);

    /**
     * This method provide list of users
     * @param userId
     * @return
     */
    @Query(value = "select e from EUserRegistration e where e.id in (:userId) and e.valid=true  ")
    List<EUserRegistration> findByIdFromList(List<UUID> userId);

    @Query(value = "SELECT u FROM EUserRegistration u JOIN ERoleMapping m ON u.id  = m.user.id " +
            "WHERE m.dealerAdmin = :userId AND u.valid = true")
    List<EUserRegistration> findDlrshipUsersByDlrAdmin(UUID userId);

    @Query(value = "select u from EUserRegistration u WHERE u.id =:userId AND u.valid=true")
    EUserRegistration findByUserName(UUID userId);

    @Query(value = "select u from EUserRegistration u WHERE u.id =:userId AND u.valid=true")
    List<EUserRegistration> findDlrSales(UUID userId);

    @Query(value=" SELECT u FROM EUserRegistration u WHERE u.valid=true AND u.email=:email")
    EUserRegistration findUserByEmailId(String email);
}
