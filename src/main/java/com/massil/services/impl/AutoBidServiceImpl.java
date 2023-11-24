package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.OfferException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AutoBidBumps;
import com.massil.dto.AutoBidMethodArgs;
import com.massil.dto.Offers;
import com.massil.persistence.mapper.AutoBidMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.services.AutoBidService;
import com.massil.services.OffersService;
import org.jobrunr.jobs.JobId;
import org.jobrunr.scheduling.JobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private AutoBidMapper autoBidMapper;
    Logger log = LoggerFactory.getLogger(AutoBidServiceImpl.class);

//    @Job(name = "The AutoBid job", retries = 1)
   @Scheduled(fixedDelay = 60 * 1000)
    @Override
    public void sellerAutoBid() throws OfferException, AppraisalException {
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
    void acceptHighBid(AutoBidMethodArgs args) throws OfferException, AppraisalException {
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

    @Override
    public String  addBumpsAndRange() {
       List<EAutoBidBump> list=new ArrayList<>();
       EAutoBidBump eAutoBidBump1=new EAutoBidBump();
       eAutoBidBump1.setStartPrice((double) 0);
       eAutoBidBump1.setEndPrice(1000.0);
       eAutoBidBump1.setBump(100.0);
       list.add(eAutoBidBump1);
        EAutoBidBump eAutoBidBump2=new EAutoBidBump();
        eAutoBidBump2.setStartPrice(1000.0);
        eAutoBidBump2.setEndPrice(3000.0);
        eAutoBidBump2.setBump(100.0);
        list.add(eAutoBidBump2);
        EAutoBidBump eAutoBidBump3=new EAutoBidBump();
        eAutoBidBump3.setStartPrice(3000.0);
        eAutoBidBump3.setEndPrice(6000.0);
        eAutoBidBump3.setBump(100.0);
        list.add(eAutoBidBump3);
        EAutoBidBump eAutoBidBump4=new EAutoBidBump();
        eAutoBidBump4.setStartPrice(6000.0);
        eAutoBidBump4.setEndPrice(10000.0);
        eAutoBidBump4.setBump(100.0);
        list.add(eAutoBidBump4);
        EAutoBidBump eAutoBidBump5=new EAutoBidBump();
        eAutoBidBump5.setStartPrice(10000.0);
        eAutoBidBump5.setEndPrice(15000.0);
        eAutoBidBump5.setBump(100.0);
        list.add(eAutoBidBump5);
        EAutoBidBump eAutoBidBump6=new EAutoBidBump();
        eAutoBidBump6.setStartPrice(15000.0);
        eAutoBidBump6.setEndPrice(20000.0);
        eAutoBidBump6.setBump(100.0);
        list.add(eAutoBidBump6);
        EAutoBidBump eAutoBidBump7=new EAutoBidBump();
        eAutoBidBump7.setStartPrice(20000.0);
        eAutoBidBump7.setEndPrice(30000.0);
        eAutoBidBump7.setBump(100.0);
        list.add(eAutoBidBump7);
        EAutoBidBump eAutoBidBump8=new EAutoBidBump();
        eAutoBidBump8.setStartPrice(30000.0);
        eAutoBidBump8.setEndPrice(40000.0);
        eAutoBidBump8.setBump(100.0);
        list.add(eAutoBidBump8);
        EAutoBidBump eAutoBidBump9=new EAutoBidBump();
        eAutoBidBump9.setStartPrice(40000.0);
        eAutoBidBump9.setEndPrice(50000.0);
        eAutoBidBump9.setBump(100.0);
        list.add(eAutoBidBump9);
        EAutoBidBump eAutoBidBump10=new EAutoBidBump();
        eAutoBidBump10.setStartPrice(50000.0);
        eAutoBidBump10.setEndPrice(70000.0);
        eAutoBidBump10.setBump(100.0);
        list.add(eAutoBidBump10);
        EAutoBidBump eAutoBidBump11=new EAutoBidBump();
        eAutoBidBump11.setStartPrice(70000.0);
        eAutoBidBump11.setEndPrice(90000.0);
        eAutoBidBump11.setBump(100.0);
        list.add(eAutoBidBump11);
        EAutoBidBump eAutoBidBump12=new EAutoBidBump();
        eAutoBidBump12.setStartPrice(90000.0);
        eAutoBidBump12.setEndPrice(130000.0);
        eAutoBidBump12.setBump(100.0);
        list.add(eAutoBidBump12);
        EAutoBidBump eAutoBidBump13=new EAutoBidBump();
        eAutoBidBump13.setStartPrice(130000.0);
        eAutoBidBump13.setEndPrice(200000.0);
        eAutoBidBump13.setBump(100.0);
        list.add(eAutoBidBump13);
        EAutoBidBump eAutoBidBump14=new EAutoBidBump();
        eAutoBidBump14.setStartPrice(200000.0);
        eAutoBidBump14.setEndPrice(500000.0);
        eAutoBidBump14.setBump(100.0);
        list.add(eAutoBidBump14);
        EAutoBidBump eAutoBidBump15=new EAutoBidBump();
        eAutoBidBump15.setStartPrice(500000.0);
        eAutoBidBump15.setEndPrice(1000000.0);
        eAutoBidBump15.setBump(100.0);
        list.add(eAutoBidBump15);
        List<EAutoBidBump> saveSAutoBidBumps = autoBidBumpRepo.saveAll(list);
        return !saveSAutoBidBumps.isEmpty() ? "Saved":  "Failed";


    }

    @Override
    public void updateBumpsAndRange() {
        List<EAutoBidBump> all = autoBidBumpRepo.findAll();
        for (EAutoBidBump bidBump: all) {
            if (bidBump.getEndPrice() == 1000.0) {
                bidBump.setBump(250.0);
            }
            if (bidBump.getEndPrice() == 3000.0) {
                bidBump.setBump(400.0);
            }
            if (bidBump.getEndPrice() == 6000.0) {
                bidBump.setBump(500.0);
            }
            if (bidBump.getEndPrice() == 10000.0) {
                bidBump.setBump(750.0);
            }
            if (bidBump.getEndPrice() == 15000.0) {
                bidBump.setBump(1000.0);
            }
            if (bidBump.getEndPrice() == 20000.0) {
                bidBump.setBump(1200.0);
            }
            if (bidBump.getEndPrice() == 30000.0) {
                bidBump.setBump(1500.0);
            }
            if (bidBump.getEndPrice() == 40000.0) {
                bidBump.setBump(2000.0);
            }
            if (bidBump.getEndPrice() == 50000.0) {
                bidBump.setBump(2500.0);
            }
            if (bidBump.getEndPrice() == 70000.0) {
                bidBump.setBump(3000.0);
            }
            if (bidBump.getEndPrice() == 90000.0) {
                bidBump.setBump(4000.0);
            }
            if (bidBump.getEndPrice() == 130000.0) {
                bidBump.setBump(6000.0);
            }
            if (bidBump.getEndPrice() == 200000.0) {
                bidBump.setBump(8000.0);
            }
            if (bidBump.getEndPrice() == 500000.0) {
                bidBump.setBump(10000.0);
            }
            if (bidBump.getEndPrice() == 1000000.0) {
                bidBump.setBump(15000.0);
            }

        }

        autoBidBumpRepo.saveAll(all);
        log.info("bumps values updated");


    }

    @Override
    public void deleteBumpsAndRange() {

    }

    @Override
    public List<AutoBidBumps> getAllBumpsAndRange() {
        return autoBidMapper.lEAutoBidToLAutoBid(autoBidBumpRepo.findAll());
    }
}
