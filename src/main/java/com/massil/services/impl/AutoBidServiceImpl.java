package com.massil.services.impl;

import com.massil.ExceptionHandle.OfferException;
import com.massil.dto.Offers;
import com.massil.persistence.model.EAutoBidJobs;
import com.massil.persistence.model.EOfferQuotes;
import com.massil.persistence.model.EOffers;
import com.massil.repository.AutoBidBumpRepo;
import com.massil.repository.AutoBidJobsRepo;
import com.massil.repository.OfferQuotesRepo;
import com.massil.repository.OffersRepo;
import com.massil.services.AutoBidService;
import com.massil.services.OffersService;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@Service
public class AutoBidServiceImpl implements AutoBidService {
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private OffersService offersService;
    @Autowired
    private OfferQuotesRepo offerQuotesRepo;
    @Autowired
    private AutoBidBumpRepo autoBidBumpRepo;
    @Autowired
    private AutoBidJobsRepo autoBidJobsRepo;
    Logger log = LoggerFactory.getLogger(AutoBidServiceImpl.class);

    @Job(name = "The AutoBid job", retries = 1)
    @Override
    public void sellerAutoBid() throws OfferException {
        log.info("*****Auto bid start******");
        List<EAutoBidJobs> pendingJobs = autoBidJobsRepo.findPendingJobs();
        if(null!=pendingJobs) {


            for (EAutoBidJobs job : pendingJobs) {

                EOfferQuotes runningOfferQuotes = null;
                EOfferQuotes eOfferQuotes=null;
                List<EOfferQuotes> listOfferQuotes=new ArrayList<>();
                Long runningOfferQuotesId = offerQuotesRepo.getLatestQuotID(job.getOfferId().getId());
                Optional<EOfferQuotes> latestQuo = offerQuotesRepo.findById(runningOfferQuotesId);

                if (latestQuo.isPresent()) {
                    runningOfferQuotes = latestQuo.get();
                    List< EOffers> offersByApprId = offersRepo.findOffersPendingForSellerCounter(job.getAppraisalRef().getId());

                    if(!offersByApprId.isEmpty()) {
                        for (EOffers offers : offersByApprId) {
                            Long id = offerQuotesRepo.getLatestQuotID(offers.getId());
                            Optional<EOfferQuotes> quotesRepoById = offerQuotesRepo.findById(id);
                            if (quotesRepoById.isPresent()) eOfferQuotes = quotesRepoById.get();
                            assert eOfferQuotes != null;

                            if (!Objects.equals(eOfferQuotes.getId(), runningOfferQuotes.getId())) {
                                listOfferQuotes.add(eOfferQuotes);
                            }
                        }
                    }

                    if(Boolean.TRUE.equals(isBuyerCounterHigher(listOfferQuotes,runningOfferQuotes)) && runningOfferQuotes.getBuyerQuote()>runningOfferQuotes.getAppRef().getDealerReserve()){
                        offersService.sellerAccept(job.getOfferId().getId());
                        log.info("seller Accepted the Buyer quote");
                    }
                    else {
                        offersService.sellerCounter(job.getOfferId().getId(), new Offers(runningOfferQuotes.getBuyerQuote() + job.getBump().getBump()));
                        log.info("seller quote value {}", runningOfferQuotes.getSellerQuote() + job.getBump().getBump());
                    }
                }

            }
            autoBidJobsRepo.updatePendingJobStatus(pendingJobs.stream().map(EAutoBidJobs::getId).collect(Collectors.toList()));

        }
    }

    private Boolean isBuyerCounterHigher(List<EOfferQuotes> listOfferQuotes,EOfferQuotes runningOfferQuotes) {
       if(!listOfferQuotes.isEmpty()) {

           for (EOfferQuotes quotes : listOfferQuotes) {
               if (runningOfferQuotes.getBuyerQuote() < quotes.getBuyerQuote()) {
                   return false;
               }
           }
       }

        return true;
    }
}
