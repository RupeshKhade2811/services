package com.massil.persistence.mapper;

import com.massil.dto.AutoBidBumps;
import com.massil.persistence.model.EAutoBidBump;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AutoBidMapper {
    Logger log = LoggerFactory.getLogger(AutoBidMapper.class);
    EAutoBidBump autoBidToEAutoBid(AutoBidBumps autoBidBumps);
    List<EAutoBidBump> lAutoBidToLEAutoBid(List<AutoBidBumps> autoBidBumpsList);
    AutoBidBumps eAutoBidToAutoBid(EAutoBidBump autoBidBumps);
    List<AutoBidBumps> lEAutoBidToLAutoBid(List<EAutoBidBump> autoBidBumpsList);

}
