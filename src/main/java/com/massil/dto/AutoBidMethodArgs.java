package com.massil.dto;

import com.massil.persistence.model.EAppraiseVehicle;
import com.massil.persistence.model.EAutoBidJobs;
import com.massil.persistence.model.ECountdownClockHighBid;
import com.massil.persistence.model.EOfferQuotes;
import lombok.Data;

@Data
public class AutoBidMethodArgs {
    private EOfferQuotes runningOfferQuotes;
    private Double dealerReserve;
    private Double highestBidByBuyer;
    private EAppraiseVehicle appraisalRef;
    private EAutoBidJobs job;
    private ECountdownClockHighBid oldClockHighBid;

    }
