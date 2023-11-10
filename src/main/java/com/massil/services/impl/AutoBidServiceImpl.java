package com.massil.services.impl;

import com.massil.ExceptionHandle.OfferException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AutoBidMethodArgs;
import com.massil.dto.Offers;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.AutoBidService;
import com.massil.services.OffersService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Autowired
    private OffersRepo offersRepo;
    Logger log = LoggerFactory.getLogger(AutoBidServiceImpl.class);

//    @Job(name = "The AutoBid job", retries = 1)
   @Scheduled(fixedDelay = 60 * 1000)
    @Override
    public void sellerAutoBid() throws OfferException {
        log.info("*****Auto bid start******");

        List<EAutoBidJobs> pendingJobs = autoBidJobsRepo.findPendingJobs();
        if(null!=pendingJobs && !pendingJobs.isEmpty()) {
            for (EAutoBidJobs job : pendingJobs) {
                EOfferQuotes runningOfferQuotes = null;    // buyer's running quotes
                Long runningOfferQuotesId = offerQuotesRepo.getLatestQuotID(job.getOfferId().getId());
                Optional<EOfferQuotes> runningLatestQuo = offerQuotesRepo.findById(runningOfferQuotesId);
                EAppraiseVehicle appraisalRef = job.getAppraisalRef();
                EOffers dbOffer = offersRepo.findOfferByOfferId(job.getOfferId().getId());
                String statusCode=dbOffer.getStatus().getStatusCode();


                if(!(statusCode.equals(AppraisalConstants.SELLERACCEPTED)||statusCode.equals(AppraisalConstants.BUYERACCEPTED) ||statusCode.equals(AppraisalConstants.SOLD)) && (runningLatestQuo.isPresent())) {
                        runningOfferQuotes = runningLatestQuo.get();
                        ECountdownClockHighBid oldClockHighBid = clockHighBidRepo.findByAppRefId(appraisalRef.getId());
                        Double highestBidByBuyer = offerQuotesRepo.highestBidForAppraisalByBuyer(appraisalRef.getId()); //don't compare with this compare with ECountdownClockHighBid
                        highestBidByBuyer= null!=highestBidByBuyer ? highestBidByBuyer:0;
                    AutoBidMethodArgs args=new AutoBidMethodArgs();
                    args.setRunningOfferQuotes(runningOfferQuotes);
                    args.setDealerReserve(job.getAppraisalRef().getDealerReserve());
                    args.setHighestBidByBuyer(highestBidByBuyer);
                    args.setAppraisalRef(appraisalRef);
                    args.setJob(job);
                    args.setOldClockHighBid(oldClockHighBid);

                        if (null == oldClockHighBid) {
                            processFirstHighBid(args);
                        } else {
                            if(Objects.equals(oldClockHighBid.getOffersQuotes().getId(), runningOfferQuotes.getId())) {
                                acceptHighBid(args);
                            }else { //this is the new incoming bid
                                processSecNewHighBid(args) ;
                            }
                        }
                }

            }

        }
    }

    private Boolean isBuyerCounterHigher(Double highBid,Double runningOfferQuotes) {
        return null != highBid && null != runningOfferQuotes && (runningOfferQuotes >=highBid);
    }

    public void setCountDownClock(Long appraisalId){
       ECountdownClockHighBid clockHighBid = clockHighBidRepo.findByAppRefId(appraisalId);
       clockHighBid.setTimer(0);
       clockHighBidRepo.save(clockHighBid);
       log.info("CountDownClock stopped after finishing the task");

    }

    void processFirstHighBid(AutoBidMethodArgs args) throws OfferException {
        ECountdownClockHighBid clockHighBid = new ECountdownClockHighBid();
        if (args.getRunningOfferQuotes().getBuyerQuote() > args.getDealerReserve() && args.getRunningOfferQuotes().getBuyerQuote() >=args.getHighestBidByBuyer()) {
            clockHighBid.setHighBid(args.getRunningOfferQuotes().getBuyerQuote());

            //start timerClock
            JobId jobId = jobScheduler.schedule(LocalDateTime.now().plusMinutes(5), x -> setCountDownClock(args.getAppraisalRef().getId()));//send here apprId using that apprId find EClockhighbid
            clockHighBid.setJobRunRTimerId(jobId.asUUID());
            clockHighBid.setOffersQuotes(args.getRunningOfferQuotes());
            clockHighBid.setAutoBidJobs(args.getJob());
            clockHighBid.setAppraisalRef(args.getAppraisalRef());
            clockHighBidRepo.save(clockHighBid);
            log.info("countdown start for first time for an appraisal");

        }else {
            giveBump(args.getOldClockHighBid(),args.getDealerReserve(), args.getJob());

        }

    }
    void processSecNewHighBid(AutoBidMethodArgs args) throws OfferException {

        if (args.getRunningOfferQuotes().getBuyerQuote() > args.getDealerReserve() && args.getRunningOfferQuotes().getBuyerQuote() > args.getOldClockHighBid().getHighBid()) {
            args.getOldClockHighBid().setHighBid(args.getRunningOfferQuotes().getBuyerQuote());
            //stop old timer
            if (null != args.getOldClockHighBid().getJobRunRTimerId())
                jobScheduler.delete(args.getOldClockHighBid().getJobRunRTimerId(),"got a new HighBid ");
            //start new timerClock
            JobId jobId = jobScheduler.schedule(LocalDateTime.now().plusMinutes(5), x -> setCountDownClock(args.getAppraisalRef().getId()));
            args.getOldClockHighBid().setJobRunRTimerId(jobId.asUUID());

            //giving bump to oldHighBid
            Double newHighBid = args.getOldClockHighBid().getHighBid();
            Double counter = (null != newHighBid && newHighBid > 0) ? newHighBid : args.getDealerReserve();
            Double bump= autoBidBumpRepo.findBump(args.getOldClockHighBid().getOffersQuotes().getBuyerQuote()).getBump();
            offersService.sellerCounter(args.getOldClockHighBid().getOffersQuotes().getOffers().getId(), new Offers(counter + bump));
            //job completed for oldHighBid
            autoBidJobsRepo.updatePendingJobStatus(args.getOldClockHighBid().getAutoBidJobs().getId());

            //setting new highBid
            args.getOldClockHighBid().setOffersQuotes(args.getRunningOfferQuotes());
            args.getOldClockHighBid().setAutoBidJobs(args.getJob());
            args.getOldClockHighBid().setAppraisalRef(args.getAppraisalRef());
            clockHighBidRepo.save(args.getOldClockHighBid());
            log.info("bump given to old highBid and new highBid saved");

        } else {
            giveBump(args.getOldClockHighBid(),args.getDealerReserve(), args.getJob());

        }

    }
    void acceptHighBid(AutoBidMethodArgs args) throws OfferException {
        if (Boolean.TRUE.equals(isBuyerCounterHigher(args.getHighestBidByBuyer(), args.getRunningOfferQuotes().getBuyerQuote())) && args.getRunningOfferQuotes().getBuyerQuote() > args.getDealerReserve() && args.getOldClockHighBid().getTimer() == 0) {
            offersService.sellerAccept(args.getJob().getOfferId().getId());
            //job completed
            autoBidJobsRepo.updatePendingJobStatus(args.getJob().getId());
            log.info("seller Accepted the Buyer quote");
        }

    }
    void giveBump(ECountdownClockHighBid oldClockHighBid,Double dealerReserve,EAutoBidJobs job) throws OfferException {
        //giving bump
        Double highBid = oldClockHighBid.getHighBid();
        Double counter = (null != highBid && highBid > 0) ? highBid : dealerReserve;
        offersService.sellerCounter(job.getOfferId().getId(), new Offers(counter + job.getBump().getBump()));
        //job completed
        autoBidJobsRepo.updatePendingJobStatus(job.getId());
        log.info("Seller countered");
    }



}
