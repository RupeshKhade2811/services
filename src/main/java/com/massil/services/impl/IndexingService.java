package com.massil.services.impl;

import com.massil.dto.ConfigDropDown;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.model.EConfigCodes;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.backend.elasticsearch.work.execution.impl.ElasticsearchIndexWorkspace;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.hibernate.search.mapper.orm.work.SearchWorkspace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexingService {
    @Autowired
    private AppraisalVehicleMapper mapper;

    @Autowired
    private SearchSession searchSession;


    @Transactional
    public void initiateIndexing() throws InterruptedException {
        log.info("Initiating indexing...");
        searchSession.massIndexer().idFetchSize(150).batchSizeToLoadObjects(25).threadsToLoadObjects(2)
                .startAndWait();
        log.info("All entities indexed");
    }

}