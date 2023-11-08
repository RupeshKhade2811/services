package com.massil.services.impl;

import com.massil.ExceptionHandle.OfferException;
import com.massil.dto.Offers;
import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EAutoBidJobs;
import com.massil.persistence.model.ECountdownClockHighBid;
import com.massil.persistence.model.EOfferQuotes;
import com.massil.repository.*;
import com.massil.services.AutoBidService;
import com.massil.services.OffersService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
public class AutoBidServiceImpl implements AutoBidService {

    @Autowired
    private OffersService offersService;
    @Autowired
    private OfferQuotesRepo offerQuotesRepo;
    @Autowired
    private AutoBidBumpRepo autoBidBumpRepo;
    @Autowired
    private AutoBidJobsRepo autoBidJobsRepo;
    @Autowired
    private ECountdownClockHighBidRepo clockHighBidRepo;
    @Autowired
    private JobScheduler jobScheduler;
    Logger log = LoggerFactory.getLogger(AutoBidServiceImpl.class);

    @Job(name = "The AutoBid job", retries = 1)
    @Override
    public void sellerAutoBid() throws OfferException {
        log.info("*****Auto bid start******");

        List<EAutoBidJobs> pendingJobs = autoBidJobsRepo.findPendingJobs();
        if(null!=pendingJobs && !pendingJobs.isEmpty()) {

            for (EAutoBidJobs job : pendingJobs) {
                EOfferQuotes runningOfferQuotes = null;    // buyer's running quotes
                Double dealerReserve=job.getAppraisalRef().getDealerReserve();
                //List<EOfferQuotes> listOfferQuotes=new ArrayList<>();  //other buyer's quotes
                Long runningOfferQuotesId = offerQuotesRepo.getLatestQuotID(job.getOfferId().getId());
                Optional<EOfferQuotes> runningLatestQuo = offerQuotesRepo.findById(runningOfferQuotesId);
                String statusCode=job.getOfferId().getStatus().getStatusCode();
                EAppraiseVehicle appraisalRef = job.getAppraisalRef();

                if(!(statusCode.equals("s004")||statusCode.equals("s005") ||statusCode.equals("s008")) && (runningLatestQuo.isPresent())) {

                        runningOfferQuotes = runningLatestQuo.get();
                        ECountdownClockHighBid oldClockHighBid = clockHighBidRepo.findByAppRefId(appraisalRef.getId());
                        Double highestBidByBuyer = offerQuotesRepo.highestBidForAppraisalByBuyer(appraisalRef.getId()); //don't compare with this compare with ECountdownClockHighBid
                        highestBidByBuyer= null!=highestBidByBuyer ? highestBidByBuyer:0;

                        if (null == oldClockHighBid) {
                            ECountdownClockHighBid clockHighBid = new ECountdownClockHighBid();
                            if (runningOfferQuotes.getBuyerQuote() > dealerReserve && runningOfferQuotes.getBuyerQuote() >=highestBidByBuyer) {
                                clockHighBid.setHighBid(runningOfferQuotes.getBuyerQuote());

                                //start timerClock
                                JobId jobId = jobScheduler.schedule(LocalDateTime.now().plusMinutes(1), x -> setCountDownClock(clockHighBid));
                                clockHighBid.setJobRunRTimerId(jobId.asUUID());
                                clockHighBid.setOffersQuotes(runningOfferQuotes);
                                clockHighBid.setAutoBidJobs(job);
                                clockHighBidRepo.save(clockHighBid);
                                log.info("countdown start for first time for an appraisal");

                            }else {
                               //giving bump
                                offersService.sellerCounter(job.getOfferId().getId(), new Offers(dealerReserve + job.getBump().getBump()));
                                //job completed
                                autoBidJobsRepo.updatePendingJobStatus(job.getId());

                                log.info("seller countered");

                            }

                        } else {

                            if(Objects.equals(oldClockHighBid.getOffersQuotes().getId(), runningOfferQuotes.getId())) {

                                if (Boolean.TRUE.equals(isBuyerCounterHigher(highestBidByBuyer, runningOfferQuotes.getBuyerQuote())) && runningOfferQuotes.getBuyerQuote() > dealerReserve &&
                                        oldClockHighBid.getTimer() == 0) {
                                    offersService.sellerAccept(job.getOfferId().getId());
                                    //job completed
                                    autoBidJobsRepo.updatePendingJobStatus(job.getId());
                                    log.info("seller Accepted the Buyer quote");
                                }
                            }else { //this is the new incoming bid

                                if (runningOfferQuotes.getBuyerQuote() > dealerReserve && runningOfferQuotes.getBuyerQuote() > oldClockHighBid.getHighBid()) {
                                    oldClockHighBid.setHighBid(runningOfferQuotes.getBuyerQuote());
                                    //stop old timer
                                    if (null != oldClockHighBid.getJobRunRTimerId())
                                        jobScheduler.delete(oldClockHighBid.getJobRunRTimerId());
                                    //start new timerClock
                                    JobId jobId = jobScheduler.schedule(LocalDateTime.now().plusMinutes(1), x -> setCountDownClock(oldClockHighBid));
                                    oldClockHighBid.setJobRunRTimerId(jobId.asUUID());

                                    //giving bump to oldHighBid
                                    Double newHighBid = oldClockHighBid.getHighBid();
                                    Double counter = (null != newHighBid && newHighBid > 0) ? newHighBid : dealerReserve;
                                    Double bump= autoBidBumpRepo.findBump(oldClockHighBid.getOffersQuotes().getBuyerQuote()).getBump();
                                    offersService.sellerCounter(oldClockHighBid.getOffersQuotes().getOffers().getId(), new Offers(counter + bump));
                                    //job completed for oldHighBid
                                    autoBidJobsRepo.updatePendingJobStatus(oldClockHighBid.getAutoBidJobs().getId());


                                    //setting new highBid
                                    oldClockHighBid.setOffersQuotes(runningOfferQuotes);
                                    oldClockHighBid.setAutoBidJobs(job);
                                    clockHighBidRepo.save(oldClockHighBid);
                                    log.info("bump given to old highBid and new highBid saved");

                                } else {
                                    //giving bump
                                    Double highBid = oldClockHighBid.getHighBid();
                                    Double counter = (null != highBid && highBid > 0) ? highBid : dealerReserve;
                                    offersService.sellerCounter(job.getOfferId().getId(), new Offers(counter + job.getBump().getBump()));
                                    //job completed
                                    autoBidJobsRepo.updatePendingJobStatus(job.getId());
                                    log.info("Seller countered");

                                }
                            }

                        }
                }

            }

        }
    }

    private Boolean isBuyerCounterHigher(Double highBid,Double runningOfferQuotes) {
        return null != highBid && null != runningOfferQuotes && (runningOfferQuotes >=highBid);
    }

    public void setCountDownClock(ECountdownClockHighBid clockHighBid){
                clockHighBid.setTimer(0);
                clockHighBidRepo.save(clockHighBid);
                log.info("CountDownClock stopped after finishing the task");

    }

}
