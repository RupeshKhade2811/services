package com.massil.repository;




import com.massil.persistence.model.ECompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface CompanyRepo extends JpaRepository<ECompany, Long>, JpaSpecificationExecutor<ECompany> {

    /**
     * This method returns the valid  ECompany object
     * @param id This is the compId  field of ECompany class
     * @author YudhisterVijay
     * @return ECompany
     */
    @Query(value = "select e from ECompany e where e.id=:id and e.valid=true ")
    ECompany findByCompanyId(Long id);


}
