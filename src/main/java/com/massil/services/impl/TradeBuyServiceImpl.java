
package com.massil.services.impl;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.OfferException;
import com.massil.constants.AppraisalConstants;
import com.massil.dto.AppraisalVehicleCard;
import com.massil.dto.CardsPage;
import com.massil.dto.OfferInfo;
import com.massil.dto.Quotes;
import com.massil.persistence.mapper.AppraisalVehicleMapper;
import com.massil.persistence.mapper.OffersMapper;
import com.massil.persistence.model.*;
import com.massil.repository.*;
import com.massil.repository.elasticRepo.AppraisalVehicleERepo;
import com.massil.services.TradeBuyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TradeBuyServiceImpl implements TradeBuyService {

    Logger log = LoggerFactory.getLogger(TradeBuyServiceImpl.class);


    @Autowired
    private AppraiseVehicleRepo eAppraiseVehicleRepo;

    @Autowired
    private AppraisalVehicleMapper appraisalVehicleMapper;

    @Autowired
    private UserRegistrationRepo userRepo;

    @Autowired
    private DealerRegistrationRepo dealerRepo;
    @Autowired
    private OffersRepo offersRepo;
    @Autowired
    private OffersMapper offersMapper;
    @Autowired
    private OfferQuotesRepo offerQuotesRepo;
    @Autowired
    private StatusRepo statusRepo;
    @Autowired
    private ConfigCodesRepo configurationCodesRepo;
    @Autowired
    private AppraisalVehicleERepo appraisalVehicleERepo;



    @Override
    public CardsPage availableTradesCards(UUID userId, Integer pageNumber, Integer pageSize) throws AppraisalException {
        CardsPage cardsPage = null;
        CardsPage pageInfo = new CardsPage();
        Page<EAppraiseVehicle> pageResult = null;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(AppraisalConstants.CREATEDON).descending());

        if(Boolean.FALSE.equals(configurationCodesRepo.isElasticActive())) {
            pageResult = eAppraiseVehicleRepo.findNotUserIdInAvbleTrdCrds(userId, AppraisalConstants.CREATED, true, true, pageable);
        }else {
            cardsPage = appraisalVehicleERepo.availableTradeCards(userId, pageNumber, pageSize);
        }


        if (null != pageResult && pageResult.getTotalElements() != 0) {
            pageInfo.setTotalRecords(pageResult.getTotalElements());
            pageInfo.setTotalPages((long) pageResult.getTotalPages());
            List<EAppraiseVehicle> apv = pageResult.toList();
            List<AppraisalVehicleCard> invenVehDtos = new ArrayList<>();
            for (EAppraiseVehicle eAppraiseVehicle : apv) {
                invenVehDtos.add(appraisalVehicleMapper.lEApprVehiToLApprVehiCard1(eAppraiseVehicle, userId));
            }

            pageInfo.setCards(invenVehDtos);

        }
        else if(null!=cardsPage && !cardsPage.getAppraiseVehicleList().isEmpty()){
            pageInfo.setTotalRecords(cardsPage.getTotalRecords());
            pageInfo.setTotalPages((long) cardsPage.getTotalPages());
            List<EAppraiseVehicle> apv = cardsPage.getAppraiseVehicleList();
            List<AppraisalVehicleCard> invenVehDtos = new ArrayList<>();
            for (EAppraiseVehicle eAppraiseVehicle : apv) {
                invenVehDtos.add(appraisalVehicleMapper.lEApprVehiToLApprVehiCard1(eAppraiseVehicle, userId));
            }
            pageInfo.setCards(invenVehDtos);
        }
        else throw new AppraisalException("Available TardeBuy Cards not available");
        pageInfo.setMessage("Available trade buy cards found");
        pageInfo.setCode(HttpStatus.OK.value());
        pageInfo.setStatus(true);
        return pageInfo;
    }


    @Override
    public CardsPage factoryOffersCards(UUID id, Integer pageNumber, Integer pageSize) throws AppraisalException {
        CardsPage pageInfo = new CardsPage();
        Page<EOffers> pageResult = null;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        EUserRegistration userById = userRepo.findUserById(id);
        if (null != userById) {
            pageResult = offersRepo.findBySlrUserIdInGetFctryOfrsJPQL(id, true, pageable);
        }

            if (null != pageResult && pageResult.getTotalElements() != 0) {
                pageInfo.setTotalRecords(pageResult.getTotalElements());
                pageInfo.setTotalPages((long) pageResult.getTotalPages());

                List<EOffers> apv = pageResult.toList();
                List<AppraisalVehicleCard> appraiseVehicleDtos = offersMapper.lEoffersToOffersCards(apv);

                pageInfo.setCards(appraiseVehicleDtos);
            } else throw new AppraisalException("Factory Offers Cards not available");


            pageInfo.setMessage("Factory Offers cards found");
            pageInfo.setCode(HttpStatus.OK.value());
            pageInfo.setStatus(true);
            return pageInfo;
        }
        @Override
        public OfferInfo factoryOffersInfo (Long offerId) throws OfferException {
            OfferInfo offerInfo = null;
            EOffers offerById = offersRepo.findById(offerId).orElse(null);

            if (null != offerById) {
                offerInfo = new OfferInfo();
                AppraisalVehicleCard card = appraisalVehicleMapper.eApprVehiToApprVehiCard(offerById.getAppRef());
                offerInfo.setCard(card);
                offerInfo.setStatusInfo(offersMapper.eStatusToStatus(offerById.getStatus()));
                List<EOfferQuotes> quotes = offerQuotesRepo.findByOffersIdOrderByCreatedOnDesc(offerId);
                List<Quotes> quotesList = new ArrayList<>();
                int i = 0;
                for (EOfferQuotes offer : quotes) {
                    Quotes quotes1 = new Quotes();
                    if (i == 0) {
                        quotes1.setStatus(offerInfo.getStatusInfo().getStatus());
                        i = 1;
                    }
                    quotes1.setBuyerQuote(offer.getBuyerQuote());
                    quotes1.setSellerQuote(offer.getSellerQuote());
                    quotes1.setAppraisedValue(offerInfo.getCard().getAppraisedValue());
                    quotesList.add(quotes1);
                }
                /*offerInfo.setBuyerQuote(quotes.getBuyerQuote());
                offerInfo.setSellerQuote(quotes.getSellerQuote());*/
                offerInfo.setQuotesList(quotesList);
                offerInfo.setOfferId(offerId);
            } else throw new OfferException("invalid id");

            offerInfo.setMessage("Factoryoffers offer information");

            offerInfo.setCode(HttpStatus.OK.value());
            offerInfo.setStatus(true);
            return offerInfo;
        }


        @Override
        public OfferInfo availableTradeInfo (Long offerId) throws OfferException {
            OfferInfo offerInfo = null;
            EOffers offerById = offersRepo.findById(offerId).orElse(null);


            if (null != offerById) {
                offerInfo = new OfferInfo();
                AppraisalVehicleCard card = appraisalVehicleMapper.eApprVehiToApprVehiCard(offerById.getAppRef());
                offerInfo.setCard(card);
                offerInfo.setStatusInfo(offersMapper.eStatusToStatus(offerById.getStatus()));
                List<EOfferQuotes> quotes = offerQuotesRepo.findByOffersIdOrderByCreatedOnDesc(offerId);
                List<Quotes> quotesList = new ArrayList<>();
                int i = 0;
                for (EOfferQuotes offer : quotes) {
                    Quotes quotes1 = new Quotes();
                    if (i == 0) {
                        quotes1.setStatus(offerInfo.getStatusInfo().getStatus());
                        i = 1;
                    }
                    quotes1.setBuyerQuote(offer.getBuyerQuote());
                    quotes1.setSellerQuote(offer.getSellerQuote());
                    quotes1.setAppraisedValue(offerInfo.getCard().getAppraisedValue());
                    quotesList.add(quotes1);
                }
                offerInfo.setQuotesList(quotesList);
                offerInfo.setOfferId(offerById.getId());
            } else throw new OfferException("invalid id");

            offerInfo.setMessage("available trade offer information");
            offerInfo.setCode(HttpStatus.OK.value());
            offerInfo.setStatus(true);
            return offerInfo;
        }
    }


