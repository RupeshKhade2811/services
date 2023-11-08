package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.OfferException;
import com.massil.ExceptionHandle.Response;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.*;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.OffersERepo;
import com.massil.services.OffersService;
import com.massil.util.CompareUtils;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.jobrunr.jobs.annotations.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class OffersServiceImpl implements OffersService {
    Logger log = LoggerFactory.getLogger(OffersServiceImpl.class);
    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;
    @Autowired
    private OffersMapper offersMapper;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private AppraiseVehicleRepo eAppraiseVehicleRepo;
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private UserRegistrationRepo userRepo;
    @Autowired
    private DealerRegistrationRepo dealerRepo;
    @Autowired
    private JavaMailSender sender;

    @Autowired
    private StatusRepo statusRepo;

    @Autowired
    private OfferQuotesRepo offerQuotesRepo;
    @Autowired
    private UserRegistrationRepo userRegistrationRepo;
    @Autowired
    private AppraiseVehicleRepo appraiseVehicleRepo;
    @Autowired
    private CompareUtils comUtl;
    @Autowired
    private Configuration config;
    @Autowired
    private NotificationTbRepo notifyRepo;
    @Autowired
    private ShipmentRepo shipmentRepo;
    @Autowired
    private AutoBidProcessRepo autoBidProRepo;
    @Autowired
    private AutoBidJobsRepo autoBidJobsRepo;
    @Autowired
    private AutoBidBumpRepo autoBidBumpRepo;
    @Autowired
    private OffersERepo offersERepo;
    @Autowired
    private ConfigCodesRepo configCodesRepo;
    @Autowired
    private ECountdownClockHighBidRepo clockHighBidRepo;


    @Override
    public CardsPage procurementCards(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException{
        CardsPage cardsPage =null;
        CardsPage pageInfo = new CardsPage();
        Page<EOffers> pageResult = null;

            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());
            EUserRegistration userById = userRepo.findUserById(id);
            if (null != userById) {
                if(Boolean.FALSE.equals(configCodesRepo.isElasticActive())) {
                    pageResult = offersRepo.findByBuyerUserIdJPQL(id, AppraisalConstants.INVENTORY, true, false, pageable);
                }else {
                    cardsPage = offersERepo.procurementCards(id, pageNumber, pageSize);
                }

            }

        if (null!= pageResult && pageResult.getTotalElements() != 0) {

            pageInfo.setTotalRecords(pageResult.getTotalElements());
            pageInfo.setTotalPages((long) pageResult.getTotalPages());

            List<EOffers> apv = pageResult.toList();

            List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
            pageInfo.setCards(appraiseVehicleDtos);
        } else if (null!=cardsPage && !cardsPage.getEOffersList().isEmpty()) {
            pageInfo.setTotalRecords(cardsPage.getTotalRecords());
            pageInfo.setTotalPages( cardsPage.getTotalPages());

            List<EOffers> apv = cardsPage.getEOffersList();

            List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
            pageInfo.setCards(appraiseVehicleDtos);
        } else throw new AppraisalException("AppraisalCards not available");


        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setMessage("Getting all procurement cards in offers page");
        pageInfo.setStatus(true);
        return pageInfo;

    }

    @Override
    public CardsPage liquidationCards(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException {

        CardsPage pageInfo = new CardsPage();
        Page<EOffers> pageResult = null;

            Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.MODIFIEDON).descending());
            EUserRegistration userById = userRepo.findUserById(id);
            if (null != userById) {

                    pageResult = offersRepo.findBySellerUserIdJPQL(id, false, pageable);

            }

            if (null!= pageResult&&pageResult.getTotalElements() != 0) {

                pageInfo.setTotalRecords(pageResult.getTotalElements());
                pageInfo.setTotalPages((long) pageResult.getTotalPages());

                List<EOffers> apv = pageResult.toList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);
                pageInfo.setCards(appraiseVehicleDtos);
            }
            else throw new AppraisalException("AppraisalCards not available");


        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setMessage("Getting all liquidation cards in offers page");
        pageInfo.setStatus(true);
        return pageInfo;

    }

    @Override
    @Transactional
    public Response makeOfferByBuyer(Long appraisalId, Offers offers, UUID buyerUserId) throws OfferException {

        EOffers offers1 = appraisalVehicleMapper.offersToEOffers(offers);
        Response response = new Response();
        String res="";

        if(Boolean.TRUE.equals(checkvalue(offers.getBuyerQuote()))){
            EAppraiseVehicle byApprId = eAppraiseVehicleRepo.getAppraisalById(appraisalId);
            if (!byApprId.getIsHold()) {
                EOffers offer = offersRepo.findOffer(appraisalId, buyerUserId);

                if (null != offer) {
                    String statusCode = offer.getStatus().getStatusCode();
                    if (statusCode.equals(AppraisalConstants.SELLERREJECTED) || statusCode.equals(AppraisalConstants.BUYERREJECTED)) {
                        res = updatPrevOffer(appraisalId, offers1, offer, offers);

                    } else {
                        response.setMessage("Previous offer is still in progress");
                        response.setStatus(Boolean.TRUE);
                        response.setCode(HttpStatus.FOUND.value());
                        return response;
                    }

                } else {
                    res = offerInsertedToDb(byApprId, offers1, appraisalId, offers, buyerUserId);
                }
            }else{
                response.setMessage("This appraisal is on HOLD");
                response.setCode(HttpStatus.FORBIDDEN.value());
                response.setStatus(Boolean.TRUE);
                return response;
            }
        }else throw new OfferException("provide quote more then 0");


        response.setMessage(res);
        response.setStatus(Boolean.TRUE);
        response.setCode(HttpStatus.OK.value());
        return response;

    }

    @Override
    @Transactional
    public Response sellerCounter(Long offerId, Offers offers) throws OfferException {


        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();
        EOfferQuotes selQuo = offerQuotesRepo.getLatestQuo(offerId);


        if(Boolean.TRUE.equals(checkvalue(offers.getSellerQuote()))){
            if (null != offerById) {
                if (selQuo.getSellerQuote()==null||((offers.getSellerQuote()<=selQuo.getSellerQuote())&&
                        (offers.getSellerQuote()>selQuo.getBuyerQuote()))) {
                    offerById.setPrice(offers.getSellerQuote());
                    offerById.setStatus(statusRepo.findByStatusCode(AppraisalConstants.SELLERCOUNTER));
                    EOffers saveOffer = offersRepo.save(offerById);

                    EOfferQuotes offerQuotesById = offerQuotesRepo.findTopByOffersIdOrderByCreatedOnDesc(offerId);
                    offerQuotesById.setSellerQuote(offers.getSellerQuote());
                    offerQuotesRepo.save(offerQuotesById);
                }else throw new OfferException("Seller cannot quote less than buyer quoted value and " +
                        "more than his/her previous quoted value");
            } else throw new OfferException("Offer not available");
        }else throw new OfferException("give quote more then 0");

        response.setCode(HttpStatus.OK.value());
        response.setMessage("Seller countered and seller quote updated");
        response.setStatus(Boolean.TRUE);
        return response;

    }

    @Override
    @Transactional
    public Response buyerCounter(Long offerId, Offers offers) throws OfferException {


        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();
        EOfferQuotes latestQuo = offerQuotesRepo.getLatestQuo(offerId);

        if(Boolean.TRUE.equals(checkvalue(offers.getBuyerQuote()))){
            if (null != offerById) {
                if (offers.getBuyerQuote()>latestQuo.getBuyerQuote()) {
                offerById.setPrice(offers.getBuyerQuote());
                offerById.setStatus(statusRepo.findByStatusCode(AppraisalConstants.BUYERCOUNTER));
                EOffers saveOffer = offersRepo.save(offerById);

                EOfferQuotes offerQuotes = new EOfferQuotes();
                offerQuotes.setBuyerQuote(offers.getBuyerQuote());
                offerQuotes.setAppRef(offerById.getAppRef());
                offerQuotes.setOffers(saveOffer);
                offerQuotesRepo.save(offerQuotes);

                    if(null!= saveOffer.getAppRef().getDealerReserve() && saveOffer.getAppRef().getDealerReserve()>0) {
                        EAutoBidJobs autoBidJobs = new EAutoBidJobs();
                        autoBidJobs.setAppraisalRef(offerById.getAppRef());
                        autoBidJobs.setStatus(0);
                        EAutoBidBump bump = autoBidBumpRepo.findBump(offers.getBuyerQuote());
                        autoBidJobs.setBump(bump);
                        autoBidJobs.setOfferId(saveOffer);
                        autoBidJobsRepo.save(autoBidJobs);
                    }
                }else throw new OfferException("Buyer cannot quote less than previous quoted value");
            } else throw new OfferException("Offer not available");
        }else throw new OfferException("give quote more then 0");


        response.setStatus(Boolean.TRUE);
        response.setMessage("Buyer countered and buyer quote updated");
        response.setCode(HttpStatus.OK.value());

        return response;

    }

    @Override
    @Transactional
    public Response sellerAccept(Long offerId) throws OfferException {

        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();

        if (null != offerById) {
            int rowUpdated = offersRepo.updateOfferSetPurchasedSellerAccept(offerId, offerById.getAppRef().getId());
            int rowsUpdated = offersRepo.updateOfferSetSold(offerId, offerById.getAppRef().getId());
            EShipment shipment=new EShipment();
            shipment.setSellerAgreed(true);
            insertShipmentDtls(offerById,shipment);

            } else throw new OfferException("Invalid offer id Send..");




        response.setCode(HttpStatus.OK.value());
        response.setMessage("Seller accepted and status updated");
        response.setStatus(Boolean.TRUE);
        return response;
    }

    @Override
    @Transactional
    public Response buyerAccept(Long offerId) throws OfferException {

        EOffers offers = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();

            if (null != offers) {

                int rowUpdated = offersRepo.updateOfferSetPurchasedBuyerAccept(offerId, offers.getAppRef().getId());
                int rowsUpdated = offersRepo.updateOfferSetSold(offerId, offers.getAppRef().getId());
                EShipment shipment=new EShipment();
                shipment.setBuyerAgreed(true);
                insertShipmentDtls(offers,shipment);

            } else throw new OfferException("Invalid offer id Send..");

        response.setStatus(Boolean.TRUE);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Buyer accepted the offer");
        return response;
    }

    @Override
    @Transactional
    public Response sellerRejected(Long offerId) throws OfferException {

        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();


            if (null != offerById) {
                offerById.setStatus(statusRepo.findByStatusCode(AppraisalConstants.SELLERREJECTED));
                EAppraiseVehicle byApprId = eAppraiseVehicleRepo.getAppraisalById(offerById.getAppRef().getId());
                offersRepo.save(offerById);
            } else throw new OfferException("Invalid offer id Send..");

        response.setStatus(Boolean.TRUE);
        response.setCode(HttpStatus.OK.value());
        response.setMessage("Seller Rejected the offer");
        return response;
    }

    @Override
    public Response buyerRejected(Long offerId) throws OfferException {

        EOffers offerById = offersRepo.findById(offerId).orElse(null);
        Response response = new Response();

            if (null != offerById) {
                offerById.setStatus(statusRepo.findByStatusCode(AppraisalConstants.BUYERREJECTED));
                EAppraiseVehicle byApprId = eAppraiseVehicleRepo.getAppraisalById(offerById.getAppRef().getId());

                offersRepo.save(offerById);
            } else throw new OfferException("Invalid offer id Send..");

        response.setMessage("Buyer rejected the offer");
        response.setCode(HttpStatus.OK.value());
        response.setStatus(Boolean.TRUE);
        return response;
    }

    @Override
    @Transactional
    public OfferInfo procurementOfferInfo(Long offerId) throws OfferException {
        OfferInfo offerInfo=null;
        EOffers info = offersRepo.findById(offerId).orElse(null);

        if(null!=info){
            offerInfo=new OfferInfo();
            AppraisalVehicleCard card = appraisalVehicleMapper.eApprVehiToApprVehiCard(info.getAppRef());

            offerInfo.setCard(card);
            offerInfo.setStatusInfo(offersMapper.eStatusToStatus(info.getStatus()));
            List< EOfferQuotes> quotes = offerQuotesRepo.findByOffersIdOrderByCreatedOnDesc(offerId);
            List<Quotes> quotesList=new ArrayList<>();
            int i=0;
            for (EOfferQuotes offer: quotes) {
                Quotes quotes1 = new Quotes();
                if(i==0){
                    quotes1.setStatus(offerInfo.getStatusInfo().getStatus());
                    i=1;
                }
                    quotes1.setBuyerQuote(offer.getBuyerQuote());
                    quotes1.setSellerQuote(offer.getSellerQuote());
                    quotes1.setAppraisedValue(offerInfo.getCard().getAppraisedValue());

                quotesList.add(quotes1);
            }
            offerInfo.setQuotesList(quotesList);
            offerInfo.setOfferId(offerId);
        }
        else throw new OfferException("Invalid id");

        offerInfo.setCode(HttpStatus.OK.value());
        offerInfo.setMessage("Procurement offer information");
        offerInfo.setStatus(true);
        return offerInfo;
    }
    @Override
    @Transactional
    public OfferInfo liquidationOfferInfo(Long offerId) throws OfferException {
        OfferInfo offerInfo = null;
        EOffers offerById = offersRepo.findById(offerId).orElse(null);

        if (null != offerById) {
            offerInfo = new OfferInfo();
            AppraisalVehicleCard card = appraisalVehicleMapper.eApprVehiToApprVehiCard(offerById.getAppRef());
            offerInfo.setCard(card);
            offerInfo.setStatusInfo(offersMapper.eStatusToStatus(offerById.getStatus()));
            List<EOfferQuotes> quotes = offerQuotesRepo.findByOffersIdOrderByCreatedOnDesc(offerId);
            List<Quotes> quotesList=new ArrayList<>();
            int i=0;
            for (EOfferQuotes qts: quotes) {
                Quotes quotes1= new Quotes();
                if(i==0){
                    quotes1.setStatus(offerInfo.getStatusInfo().getStatus());
                    i=1;
                }
                quotes1.setBuyerQuote(qts.getBuyerQuote());
                quotes1.setSellerQuote(qts.getSellerQuote());
                quotes1.setAppraisedValue(offerInfo.getCard().getAppraisedValue());
                quotesList.add(quotes1);
            }
         /*       offerInfo.setBuyerQuote(quotes.getBuyerQuote());
                offerInfo.setSellerQuote(quotes.getSellerQuote());*/
            offerInfo.setQuotesList(quotesList);
            offerInfo.setOfferId(offerId);

        } else throw new OfferException("invalid id");

        offerInfo.setMessage("Liquidation offer information");

        offerInfo.setCode(HttpStatus.OK.value());
        offerInfo.setStatus(true);

        return offerInfo;
    }


    @Job(name = "The sample job", retries = 2)
    @Override
    public Response myScheduledTask() throws IOException, TemplateException, MessagingException {
        log.info("myScheduledTask() method started..");
        log.info("myScheduledTask() method started..");
        Logger log = LoggerFactory.getLogger(OffersServiceImpl.class);
        Response response = new Response();

        List<EOffers> mdfonLess24hrs = offersRepo.listOfMakeOfferLessThn24hrs();

        for(int i=0;i<mdfonLess24hrs.size();i++){
            if(null==notifyRepo.GetNotificationFreq(mdfonLess24hrs.get(i).getId())) {
            EUserRegistration buyerById = userRegistrationRepo.findUserById(mdfonLess24hrs.get(i).getBuyerUserId().getId());
            EUserRegistration sellerById = userRegistrationRepo.findUserById(mdfonLess24hrs.get(i).getSellerUserId().getId());

            EStatus stsById = statusRepo.findStsById(mdfonLess24hrs.get(i).getStatus().getId());

            if(stsById.getStatusCode().equals(AppraisalConstants.MAKEOFFER) || stsById.getStatusCode().equals(AppraisalConstants.BUYERCOUNTER)
            || stsById.getStatusCode().equals(AppraisalConstants.SELLERCOUNTER)){

                    MimeMessage message = sender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

                    String apprVeh = appraiseVehicleRepo.getVin(mdfonLess24hrs.get(i).getAppRef().getId());


                    String email = sellerById.getEmail();
                    String message1 = "SELLER, YOUR OFFER RECEIVED WILL EXPIRE SOON, PLEASE ACCEPT/DECLINE";
                    String price = String.valueOf(mdfonLess24hrs.get(i).getPrice());
                    String name1 = comUtl.checkDbVariable(sellerById.getFirstName()) + " " + comUtl.checkDbVariable(sellerById.getLastName());
                    String name2 = comUtl.checkDbVariable(buyerById.getFirstName()) + " " + comUtl.checkDbVariable(buyerById.getLastName());
                    String vin = apprVeh;

                    Template t = config.getTemplate("delayAccept.ftl");

                    Map<String, Object> model1 = new HashMap<>();
                    model1.put(AppraisalConstants.SELLERNAME, name1);
                    model1.put(AppraisalConstants.BUYERNAME, name2);
                    model1.put(AppraisalConstants.VIN, vin);
                    model1.put(AppraisalConstants.MESSAGE, message1);
                    model1.put(AppraisalConstants.VALUE, price);

                    String html = FreeMarkerTemplateUtils.processTemplateIntoString(t, model1);

                    helper.setTo(email);
                    helper.setText(html, true);
                    helper.setSubject(AppraisalConstants.SUBJECT);
                    sender.send(message);

                MimeMessage msg = sender.createMimeMessage();
                MimeMessageHelper helper1 = new MimeMessageHelper(msg, StandardCharsets.UTF_8.name());

                String email2 = buyerById.getEmail();
                String message2 = "BUYER, YOUR MAKE OFFER WILL EXPIRE SOON";
                String price2 = String.valueOf(mdfonLess24hrs.get(i).getPrice());
                String name3 = comUtl.checkDbVariable(sellerById.getFirstName()) + " " + comUtl.checkDbVariable(sellerById.getLastName());
                String name4 = comUtl.checkDbVariable(buyerById.getFirstName()) + " " + comUtl.checkDbVariable(buyerById.getLastName());
                String vin1 = apprVeh;

                Template t1 = config.getTemplate("delayAccept.ftl");

                Map<String, Object> model2 = new HashMap<>();
                model2.put(AppraisalConstants.SELLERNAME, name3);
                model2.put(AppraisalConstants.BUYERNAME, name4);
                model2.put(AppraisalConstants.VIN, vin1);
                model2.put(AppraisalConstants.MESSAGE, message2);
                model2.put(AppraisalConstants.VALUE, price2);

                String html1 = FreeMarkerTemplateUtils.processTemplateIntoString(t1, model2);

                helper1.setTo(email2);
                helper1.setText(html1, true);
                helper1.setSubject(AppraisalConstants.SUBJECT);
                sender.send(msg);

                    ENotificationTable notify = new ENotificationTable();
                    notify.setToBuyerMail(email2);
                    notify.setToSellerMail(email);
                    notify.setOffers(mdfonLess24hrs.get(i));
                    notify.setStatus(stsById);
                    notify.setNotifyFrequency(1);
                    notifyRepo.save(notify);
                }

                }
        }
        log.info("myScheduledTask() method end..");
        log.info("myScheduledTask() method end..");

        return response;

    }

    @Override
    public ListedOffer getOfferList(Long appraisalId) throws OfferException {
       ListedOffer list=new ListedOffer() ;
        List<OfferList> offerLists=null;
       List<EOffers> offerList = offersRepo.getOfferList(appraisalId,true);

           if (null != offerList && 0 != offerList.size()) {
               offerLists = new ArrayList<>();
               for (EOffers offers : offerList) {
                   OfferList obj = new OfferList();
                   obj.setFirstName(offers.getBuyerUserId().getFirstName());
                   obj.setLastName(offers.getBuyerUserId().getLastName());
                   obj.setOfferId(offers.getId());
                   offerLists.add(obj);
               }
           } else throw new OfferException("invalid id");

        list.setOffersCards(offerLists);
        list.setCode(HttpStatus.OK.value());
        list.setStatus(true);
        list.setMessage("getting offer");

        return list;
    }

    public String offerInsertedToDb(EAppraiseVehicle byApprId,EOffers offers1,Long appraisalId, Offers offers, UUID buyerUserId){
        EUserRegistration sellerUserid = byApprId.getUser();
        EDealerRegistration sellerDealerid = byApprId.getDealer();
        EUserRegistration userById = userRepo.findUserById(buyerUserId);

        Boolean pushForBuyFig = byApprId.getTdStatus().getPushForBuyFig();

        offers1.setIsTradeBuy(pushForBuyFig);
        offers1.setSellerUserId(sellerUserid);
        if(null!=sellerDealerid){
            offers1.setSellerDealerId(sellerDealerid);
        }else{
            offers1.setSellerDealerId(null);
        }
        offers1.setMakeNewOffer(0);
        offers1.setPrice(offers.getBuyerQuote());
        offers1.setBuyerUserId(userById);
        offers1.setStatus(statusRepo.findByStatusCode(AppraisalConstants.MAKEOFFER));


        EDealerRegistration dealerId = userById.getDealer();
        if (null != dealerId) {
            offers1.setBuyerDealerId(dealerId);
        } else
            offers1.setBuyerDealerId(null);

        byApprId.setIsOfferMade(true);
        offers1.setAppRef(byApprId);

        EOffers save = offersRepo.save(offers1);

        EOfferQuotes offerQuotes = new EOfferQuotes();
        offerQuotes.setOffers(save);
//        offerQuotes.setSellerQuote(0.0);
        offerQuotes.setAppRef(eAppraiseVehicleRepo.getAppraisalById(appraisalId));
        offerQuotes.setBuyerQuote(offers.getBuyerQuote());
        offerQuotesRepo.save(offerQuotes);

        setBidProcess(byApprId, offers, save);


        return "Offer Made and quotes saved successfully";
    }

    private void setBidProcess(EAppraiseVehicle byApprId, Offers offers, EOffers save) {

        if(null!= byApprId.getDealerReserve() && byApprId.getDealerReserve()>0) {
            EAutoBidProcess autoBidProcess = new EAutoBidProcess();
            autoBidProcess.setAppraisalRef(byApprId);
            autoBidProcess.setLastOfferID(save);
            autoBidProcess.setRcvHighestBid(offers.getBuyerQuote());//no use
            autoBidProcess.setCounterdHighestBid(0.0); //no use
            autoBidProcess.setReservePrice(byApprId.getDealerReserve());
            autoBidProcess.setAutoBidStatus(true);
            autoBidProRepo.save(autoBidProcess);

            EAutoBidJobs autoBidJobs = new EAutoBidJobs();
            autoBidJobs.setAppraisalRef(byApprId);
            autoBidJobs.setStatus(0);
            EAutoBidBump bump = autoBidBumpRepo.findBump(offers.getBuyerQuote());
            autoBidJobs.setBump(bump);
            autoBidJobs.setOfferId(save);
            autoBidJobsRepo.save(autoBidJobs);
        }
    }

    public String updatPrevOffer(Long appraisalId,EOffers offers1, EOffers offer,Offers offers){
        Double price = offers.getBuyerQuote();
        offer.setPrice(price);
        offer.setStatus(statusRepo.findByStatusCode(AppraisalConstants.MAKEOFFER));
        EOffers saveOffer = offersRepo.save(offer);

        EOfferQuotes offerQuotes = new EOfferQuotes();
        offerQuotes.setOffers(saveOffer);
        offerQuotes.setSellerQuote(0.0);
        offerQuotes.setAppRef(eAppraiseVehicleRepo.getAppraisalById(appraisalId));
        offerQuotes.setBuyerQuote(offers.getBuyerQuote());
        offerQuotesRepo.save(offerQuotes);
        return "Offer again saved ";
    }

    Boolean checkvalue(Double quote){
        return quote > 0;
    }


    private void insertShipmentDtls(EOffers eOffers,EShipment shipment ){
        shipment.setAppraisalRef(eOffers.getAppRef());
        shipment.setOffers(eOffers);
        shipment.setPushForBuyFig(eOffers.getIsTradeBuy());
        shipmentRepo.save(shipment);
    }


    @Override
    @Transactional
    public HighReserveValuePop isReserveHigh(Long appraisalId, Double newDealerReserve) throws AppraisalException {
        EAppraiseVehicle appraisalById = eAppraiseVehicleRepo.getAppraisalById(appraisalId);
        HighReserveValuePop reserveValuePop = new HighReserveValuePop();
        if(null!= appraisalById) {
            if (null != appraisalById.getDealerReserve()){
                reserveValuePop.setIsNewReserveHigh(appraisalById.getDealerReserve() < newDealerReserve);
            }else {
                reserveValuePop.setIsNewReserveHigh(true);
            }
            if(Boolean.TRUE.equals(reserveValuePop.getIsNewReserveHigh())) {
                Integer rows = offersRepo.anyOfferLowerThanReserve(appraisalId, newDealerReserve);
                reserveValuePop.setAreBidsLTNewReserve(rows != 0);
            }
            reserveValuePop.setStatus(true);
            reserveValuePop.setCode(HttpStatus.OK.value());
        }else throw new AppraisalException("Appraisal not found");
        return reserveValuePop;
    }
}
