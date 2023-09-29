package com.massil.Mapper;


import com.massil.constants.AppraisalConstants;
import com.massil.persistence.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class AppraisalConfigMapper {

    Logger log = LoggerFactory.getLogger(AppraisalConfigMapper.class);

    /**
     * This method updates the EVehDWarnLightStatus by getting configCodes from list and setting into the EVehDWarnLightStatus object
     * and then EVehDWarnLightStatus object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of EApprTestDrSts
     * @param wrnLightList This is the object of List<EConfigurationCodes>
     */
    public void updateDrWarnLight(EApprTestDrSts testDrSts, List<EConfigCodes> wrnLightList){
        log.info("This method updates vehicle warn Lights **MAPPER CLASS**");
        EVehDWarnLightStatus dashWrnLight=testDrSts.getVehDrWarnLightSts();
        Map<String, EConfigCodes> wrnLightMap = chkconfig(wrnLightList);
        if(null==dashWrnLight){
            dashWrnLight=new EVehDWarnLightStatus();
        }
        if(isUpdateApprConfReq(dashWrnLight.getNoFaults(),wrnLightMap, AppraisalConstants.NOFAULTS)){
            dashWrnLight.setNoFaults(wrnLightMap.get(AppraisalConstants.NOFAULTS));
        }
        if(isUpdateApprConfReq(dashWrnLight.getAbsFault(),wrnLightMap,AppraisalConstants.ABSFAULT)){
            dashWrnLight.setAbsFault(wrnLightMap.get(AppraisalConstants.ABSFAULT));
        }

        if(isUpdateApprConfReq(dashWrnLight.getAirBagFault(),wrnLightMap,AppraisalConstants.AIRBAGFAULT)){
            dashWrnLight.setAirBagFault(wrnLightMap.get(AppraisalConstants.AIRBAGFAULT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getBatteryLight(),wrnLightMap,AppraisalConstants.BATTERYLIGHT)){
            dashWrnLight.setBatteryLight(wrnLightMap.get(AppraisalConstants.BATTERYLIGHT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getBrakeSystemFault(),wrnLightMap,AppraisalConstants.BRAKESYSTEM)){
            dashWrnLight.setBrakeSystemFault(wrnLightMap.get(AppraisalConstants.BRAKESYSTEM));
        }
        if(isUpdateApprConfReq(dashWrnLight.getBrakePadWearLight(),wrnLightMap,AppraisalConstants.BRAKEPADWEAR)){
            dashWrnLight.setBrakePadWearLight(wrnLightMap.get(AppraisalConstants.BRAKEPADWEAR));
        }
        if(isUpdateApprConfReq(dashWrnLight.getChargingSystemFault(),wrnLightMap,AppraisalConstants.CHARGINGSYSTEM)){
            dashWrnLight.setChargingSystemFault(wrnLightMap.get(AppraisalConstants.CHARGINGSYSTEM));
        }
        if(isUpdateApprConfReq(dashWrnLight.getClngSysFault(),wrnLightMap,AppraisalConstants.COOLINGSYSTEMFAULT)){
            dashWrnLight.setClngSysFault(wrnLightMap.get(AppraisalConstants.COOLINGSYSTEMFAULT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getCoolantLow(),wrnLightMap,AppraisalConstants.COOLANTLOW)){
            dashWrnLight.setCoolantLow(wrnLightMap.get(AppraisalConstants.COOLANTLOW));
        }
        if(isUpdateApprConfReq(dashWrnLight.getCheckEngineLight(),wrnLightMap,AppraisalConstants.CHECKENGINELIGHT)){
            dashWrnLight.setCheckEngineLight(wrnLightMap.get(AppraisalConstants.CHECKENGINELIGHT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getOilPressureLow(),wrnLightMap,AppraisalConstants.OILPRESSURELOW)){
            dashWrnLight.setOilPressureLow(wrnLightMap.get(AppraisalConstants.OILPRESSURELOW));
        }
        if(isUpdateApprConfReq(dashWrnLight.getServiceSoon(),wrnLightMap,AppraisalConstants.SERVICESOON)){
            dashWrnLight.setServiceSoon(wrnLightMap.get(AppraisalConstants.SERVICESOON));
        }
        if(isUpdateApprConfReq(dashWrnLight.getChngOilIndctr(),wrnLightMap,AppraisalConstants.CHANGEOILINDICATOR)){
            dashWrnLight.setChngOilIndctr(wrnLightMap.get(AppraisalConstants.CHANGEOILINDICATOR));
        }
        if(isUpdateApprConfReq(dashWrnLight.getLowOilIndctr(),wrnLightMap,AppraisalConstants.LOWOILINDICATOR)){
            dashWrnLight.setLowOilIndctr(wrnLightMap.get(AppraisalConstants.LOWOILINDICATOR));
        }
        if(isUpdateApprConfReq(dashWrnLight.getTractionControl(),wrnLightMap,AppraisalConstants.TRACTIONCONTROL)){
            dashWrnLight.setTractionControl(wrnLightMap.get(AppraisalConstants.TRACTIONCONTROL));
        }
        if(isUpdateApprConfReq(dashWrnLight.getTransmiFault(),wrnLightMap,AppraisalConstants.TRANSMIFAULT)){
            dashWrnLight.setTransmiFault(wrnLightMap.get(AppraisalConstants.TRANSMIFAULT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getTpms(),wrnLightMap,AppraisalConstants.TPMS)){
            dashWrnLight.setTpms(wrnLightMap.get(AppraisalConstants.TPMS));
        }
        if(isUpdateApprConfReq(dashWrnLight.getDislParticulateFilt(),wrnLightMap,AppraisalConstants.DISLPARTICULATEFILT)){
            dashWrnLight.setDislParticulateFilt(wrnLightMap.get(AppraisalConstants.DISLPARTICULATEFILT));
        }
        if(isUpdateApprConfReq(dashWrnLight.getMainBtryFault(),wrnLightMap,AppraisalConstants.MAINBATTERYFAULT)){
            dashWrnLight.setMainBtryFault(wrnLightMap.get(AppraisalConstants.MAINBATTERYFAULT));
        }

        dashWrnLight.setTdStatus(testDrSts);
        testDrSts.setVehDrWarnLightSts(dashWrnLight);
        log.debug("UPDATING WARN LIGHT{}",testDrSts);
    }


    /**
     * This method updates the EApprVehOilCondn by getting configCodes from list and setting into the EApprVehOilCondn object
     *   and then EApprVehOilCondn object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     *
     */

    public void updateOilCondn (EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATING OIL CONDITION **MAPPER CLASS**");
        EApprVehOilCondn oilCondn=testDrSts.getApprVehOilCondn();
        Map<String, EConfigCodes> oilCondnMap =chkconfig(configList);
        if(null==oilCondn){
            oilCondn=new EApprVehOilCondn();
        }
        if(isUpdateApprConfReq(oilCondn.getCleanOil(),oilCondnMap,AppraisalConstants.CLEANOIL)){
            oilCondn.setCleanOil(oilCondnMap.get(AppraisalConstants.CLEANOIL));
        }
        if(isUpdateApprConfReq(oilCondn.getDirtyOil(),oilCondnMap,AppraisalConstants.DIRTYOIL)){
            oilCondn.setDirtyOil(oilCondnMap.get(AppraisalConstants.DIRTYOIL));
        }
        if(isUpdateApprConfReq(oilCondn.getWaterInOil(),oilCondnMap,AppraisalConstants.WATERINOIL)){
            oilCondn.setWaterInOil(oilCondnMap.get(AppraisalConstants.WATERINOIL));
        }
        if(isUpdateApprConfReq(oilCondn.getOneQuartLow(),oilCondnMap,AppraisalConstants.ONEQUARTLOW)){
            oilCondn.setOneQuartLow(oilCondnMap.get(AppraisalConstants.ONEQUARTLOW));
        }
        if(isUpdateApprConfReq(oilCondn.getGreaterThanAQuartLow(),oilCondnMap,AppraisalConstants.GREATERTHANAQUARTLOW)){
            oilCondn.setGreaterThanAQuartLow(oilCondnMap.get(AppraisalConstants.GREATERTHANAQUARTLOW));
        }
        if(isUpdateApprConfReq(oilCondn.getElectronicGauge(),oilCondnMap,AppraisalConstants.ELECTRONICGAUGE)){
            oilCondn.setElectronicGauge(oilCondnMap.get(AppraisalConstants.ELECTRONICGAUGE));
        }
        if(isUpdateApprConfReq(oilCondn.getCorrectLevel(),oilCondnMap,AppraisalConstants.CORRECTLEVEL)){
            oilCondn.setCorrectLevel(oilCondnMap.get(AppraisalConstants.CORRECTLEVEL));
        }
        if(isUpdateApprConfReq(oilCondn.getElectricVeh(),oilCondnMap,AppraisalConstants.ELECTRIC_VEH)){
            oilCondn.setElectricVeh(oilCondnMap.get(AppraisalConstants.ELECTRIC_VEH));
        }
        oilCondn.setTdStatus(testDrSts);
        testDrSts.setApprVehOilCondn(oilCondn);
        log.debug("Updating Oil Condition",testDrSts);
    }


    /**
     * This method updates the EApprVehInteriCondn by getting configCodes from list and setting into the EApprVehInteriCondn object
     *   and then EApprVehInteriCondn object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of EApprTestDrSts
     * @param intrCondnList This is the object of List<EConfigurationCodes>
     */
    public void updateInteriCondn (EApprTestDrSts testDrSts,List<EConfigCodes> intrCondnList){
        log.info("UPDATING INTERIOR CONDITION **MAPPER CLASS**");
        EApprVehInteriCondn intrCondn=testDrSts.getApprVehInteriCondn();
        Map<String, EConfigCodes> intrCondnMap= chkconfig(intrCondnList);
        if(null==intrCondn){
            intrCondn=new EApprVehInteriCondn();
        }
        if(isUpdateApprConfReq(intrCondn.getCleanFL(),intrCondnMap,AppraisalConstants.CLEANFL)){
            intrCondn.setCleanFL(intrCondnMap.get(AppraisalConstants.CLEANFL));
        }
        if(isUpdateApprConfReq(intrCondn.getGoodMnrRepaisNeed(),intrCondnMap,AppraisalConstants.GOODMNRREPAISNEED)){
            intrCondn.setGoodMnrRepaisNeed(intrCondnMap.get(AppraisalConstants.GOODMNRREPAISNEED));
        }
        if(isUpdateApprConfReq(intrCondn.getSmokersCar(),intrCondnMap,AppraisalConstants.SMOKERSCAR)){
            intrCondn.setSmokersCar(intrCondnMap.get(AppraisalConstants.SMOKERSCAR));
        }
        if(isUpdateApprConfReq(intrCondn.getOddSmelling(),intrCondnMap,AppraisalConstants.ODDSMELLING)){
            intrCondn.setOddSmelling(intrCondnMap.get(AppraisalConstants.ODDSMELLING));
        }
        if(isUpdateApprConfReq(intrCondn.getVeryDirty(),intrCondnMap,AppraisalConstants.VERYDIRTY)){
            intrCondn.setVeryDirty(intrCondnMap.get(AppraisalConstants.VERYDIRTY));
        }
        if(isUpdateApprConfReq(intrCondn.getStrongPetSmell(),intrCondnMap,AppraisalConstants.STRONGPETSMELL)){
            intrCondn.setStrongPetSmell(intrCondnMap.get(AppraisalConstants.STRONGPETSMELL));
        }
        if(isUpdateApprConfReq(intrCondn.getDriversSeatWear(),intrCondnMap,AppraisalConstants.DRIVERSSEATWEAR)){
            intrCondn.setDriversSeatWear(intrCondnMap.get(AppraisalConstants.DRIVERSSEATWEAR));
        }
        if(isUpdateApprConfReq(intrCondn.getHeadlineNeedRplc(),intrCondnMap,AppraisalConstants.HEADLINENEEDRPLC)){
            intrCondn.setHeadlineNeedRplc(intrCondnMap.get(AppraisalConstants.HEADLINENEEDRPLC));
        }
        if(isUpdateApprConfReq(intrCondn.getDriverSeatRipped(),intrCondnMap,AppraisalConstants.DRIVERSEATRIPPED)){
            intrCondn.setDriverSeatRipped(intrCondnMap.get(AppraisalConstants.DRIVERSEATRIPPED));
        }
        if(isUpdateApprConfReq(intrCondn.getDashCrackedMinor(),intrCondnMap,AppraisalConstants.DASHCRACKEDMINOR)){
            intrCondn.setDashCrackedMinor(intrCondnMap.get(AppraisalConstants.DASHCRACKEDMINOR));
        }
        if(isUpdateApprConfReq(intrCondn.getDashCrackBrknMaj(),intrCondnMap,AppraisalConstants.DASHCRACKBRKNMAJ)){
            intrCondn.setDashCrackBrknMaj(intrCondnMap.get(AppraisalConstants.DASHCRACKBRKNMAJ));
        }
        if(isUpdateApprConfReq(intrCondn.getPassenSeatRipped(),intrCondnMap,AppraisalConstants.PASSENSEATRIPPED)){
            intrCondn.setPassenSeatRipped(intrCondnMap.get(AppraisalConstants.PASSENSEATRIPPED));
        }
        if(isUpdateApprConfReq(intrCondn.getCarpetBadlyWorn(),intrCondnMap,AppraisalConstants.CARPETBADLYWORN)){
            intrCondn.setCarpetBadlyWorn(intrCondnMap.get(AppraisalConstants.CARPETBADLYWORN));
        }
        if(isUpdateApprConfReq(intrCondn.getInterTrimBrknnMiss(),intrCondnMap,AppraisalConstants.INTERTRIMBRKNNMISS)){
            intrCondn.setInterTrimBrknnMiss(intrCondnMap.get(AppraisalConstants.INTERTRIMBRKNNMISS));
        }
        if(isUpdateApprConfReq(intrCondn.getPoorNeedsRepair(),intrCondnMap,AppraisalConstants.POORNEEDSREPAIR)){
            intrCondn.setPoorNeedsRepair(intrCondnMap.get(AppraisalConstants.POORNEEDSREPAIR));
        }
        intrCondn.setTdStatus(testDrSts);
        testDrSts.setApprVehInteriCondn(intrCondn);
        log.debug("Updating Interior condition",testDrSts);
    }


    /**
     * This method updates the EApprVehStereoSts by getting configCodes from list and setting into the EApprVehStereoSts object
     *   and then EApprVehStereoSts object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     */

    public void updateStereoSts (EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATING STEREO STATUS **MAPPER CLASS**");
        EApprVehStereoSts stereoStatus=testDrSts.getApprVehStereoSts();
        Map<String, EConfigCodes>  stereoStatusMap= chkconfig(configList);
        if(null==stereoStatus){
            stereoStatus=new EApprVehStereoSts();
        }
        if(isUpdateApprConfReq(stereoStatus.getFactoryEquptOperat(),stereoStatusMap,AppraisalConstants.FACTORYEQUPTOPERAT)){
            stereoStatus.setFactoryEquptOperat(stereoStatusMap.get(AppraisalConstants.FACTORYEQUPTOPERAT));
        }
        if(isUpdateApprConfReq(stereoStatus.getFactoryEquptNotOperat(),stereoStatusMap,AppraisalConstants.FACTORYEQUPTNOTOPERAT)){
            stereoStatus.setFactoryEquptNotOperat(stereoStatusMap.get(AppraisalConstants.FACTORYEQUPTNOTOPERAT));
        }
        if(isUpdateApprConfReq(stereoStatus.getMissingButtons(),stereoStatusMap,AppraisalConstants.MISSINGBUTTONS)){
            stereoStatus.setMissingButtons(stereoStatusMap.get(AppraisalConstants.MISSINGBUTTONS));
        }
        if(isUpdateApprConfReq(stereoStatus.getAftMktNavigaSys(),stereoStatusMap,AppraisalConstants.AFTMKTNAVIGASYS)){
            stereoStatus.setAftMktNavigaSys(stereoStatusMap.get(AppraisalConstants.AFTMKTNAVIGASYS));
        }
        if(isUpdateApprConfReq(stereoStatus.getAftMktSoundSys(),stereoStatusMap,AppraisalConstants.AFTRMKTSNDSYS)){
            stereoStatus.setAftMktSoundSys(stereoStatusMap.get(AppraisalConstants.AFTRMKTSNDSYS));
        }
        if(isUpdateApprConfReq(stereoStatus.getAftMktRearEntertainSys(),stereoStatusMap,AppraisalConstants.AFTMKTREARENTERTAINSYS)){
            stereoStatus.setAftMktRearEntertainSys(stereoStatusMap.get(AppraisalConstants.AFTMKTREARENTERTAINSYS));
        }
        if(isUpdateApprConfReq(stereoStatus.getFactoryRearEntertainSys(),stereoStatusMap,AppraisalConstants.FACTORYREARENTERTAINSYS)){
            stereoStatus.setFactoryRearEntertainSys(stereoStatusMap.get(AppraisalConstants.FACTORYREARENTERTAINSYS));
        }
        if(isUpdateApprConfReq(stereoStatus.getCd(),stereoStatusMap,AppraisalConstants.CD)){
            stereoStatus.setCd(stereoStatusMap.get(AppraisalConstants.CD));
        }
        if(isUpdateApprConfReq(stereoStatus.getCrckdBrknScreen(),stereoStatusMap,AppraisalConstants.CRKDBROKENSCREEN)){
            stereoStatus.setCrckdBrknScreen(stereoStatusMap.get(AppraisalConstants.CRKDBROKENSCREEN));
        }
        if(isUpdateApprConfReq(stereoStatus.getFadedDisBtn(),stereoStatusMap,AppraisalConstants.FADEDDISBTN)){
            stereoStatus.setFadedDisBtn(stereoStatusMap.get(AppraisalConstants.FADEDDISBTN));
        }
        if(isUpdateApprConfReq(stereoStatus.getPrmSoundSys(),stereoStatusMap,AppraisalConstants.PRMSOUNDSYS)){
            stereoStatus.setPrmSoundSys(stereoStatusMap.get(AppraisalConstants.PRMSOUNDSYS));
        }
        if(isUpdateApprConfReq(stereoStatus.getNvgSys(),stereoStatusMap,AppraisalConstants.NVGSYS)){
            stereoStatus.setNvgSys(stereoStatusMap.get(AppraisalConstants.NVGSYS));
        }
        stereoStatus.setTdStatus(testDrSts);
        testDrSts.setApprVehStereoSts(stereoStatus);
    }


    /**
     * This method updates the EApprVehAcCondn by getting configCodes from list and setting into the EApprVehAcCondn object
     *   and then EApprVehAcCondn object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateAcCondn(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE AC CONDITION **MAPPER CLASS**");
        EApprVehAcCondn eApprVehAcCondn=testDrSts.getApprVehAcCondn();
        Map<String, EConfigCodes> map =chkconfig(configList);
        if(null==eApprVehAcCondn){
            eApprVehAcCondn=new EApprVehAcCondn();
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getColdAir(),map,AppraisalConstants.COLD_AIR)){
            eApprVehAcCondn.setColdAir(map.get(AppraisalConstants.COLD_AIR));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getBadDisplay(),map,AppraisalConstants.BAD_DISPLAY)){
            eApprVehAcCondn.setBadDisplay(map.get(AppraisalConstants.BAD_DISPLAY));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getFadedDisOrBtn(),map,AppraisalConstants.FADED_DISORBTN)){
            eApprVehAcCondn.setFadedDisOrBtn(map.get(AppraisalConstants.FADED_DISORBTN));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getFanSpeedMalfun(),map,AppraisalConstants.FANSPEEDMALFUN)){
            eApprVehAcCondn.setFanSpeedMalfun(map.get(AppraisalConstants.FANSPEEDMALFUN));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getClimateCtrlMalfun(),map,AppraisalConstants.CLIMATECTRLMALFUN)){
            eApprVehAcCondn.setClimateCtrlMalfun(map.get(AppraisalConstants.CLIMATECTRLMALFUN));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getHotOrWarmAir(),map,AppraisalConstants.HOTORWARMAIR)){
            eApprVehAcCondn.setHotOrWarmAir(map.get(AppraisalConstants.HOTORWARMAIR));
        }
        if(isUpdateApprConfReq(eApprVehAcCondn.getNotOperational(),map,AppraisalConstants.NOTOPERATIONAL)){
            eApprVehAcCondn.setNotOperational(map.get(AppraisalConstants.NOTOPERATIONAL));
        }
        eApprVehAcCondn.setTdStatus(testDrSts);
        testDrSts.setApprVehAcCondn(eApprVehAcCondn);
    }



     /**
     * This method updates the ESteeringFeelStatus by getting configCodes from list and setting into the ESteeringFeelStatus object
     *   and then ESteeringFeelStatus object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateSteeringFeel(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE STEERING FEEL  **MAPPER CLASS**");
        ESteeringFeelStatus eSteeringFeelStatus =testDrSts.getSteeringFeel();
        Map<String, EConfigCodes> map=chkconfig(configList);
        if(null== eSteeringFeelStatus){
            eSteeringFeelStatus =new ESteeringFeelStatus();
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getNormalOperation(),map,AppraisalConstants.NORML_OP)){
            eSteeringFeelStatus.setNormalOperation(map.get(AppraisalConstants.NORML_OP));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getPowerSearingPumpWhine(),map,AppraisalConstants.PWR_SEAR_PUMP_WHINE)){
            eSteeringFeelStatus.setPowerSearingPumpWhine(map.get(AppraisalConstants.PWR_SEAR_PUMP_WHINE));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getSloppySteeringWheel(),map,AppraisalConstants.SLOPY_STERNG_WHEEL)){
            eSteeringFeelStatus.setSloppySteeringWheel(map.get(AppraisalConstants.SLOPY_STERNG_WHEEL));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getNoPowerAssist(),map,AppraisalConstants.NO_PWR_ASS)){
            eSteeringFeelStatus.setNoPowerAssist(map.get(AppraisalConstants.NO_PWR_ASS));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getPullsToLeft(),map,AppraisalConstants.PUL_LEFT)){
            eSteeringFeelStatus.setPullsToLeft(map.get(AppraisalConstants.PUL_LEFT));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getPullsToRight(),map,AppraisalConstants.PUL_RIGHT)){
            eSteeringFeelStatus.setPullsToRight(map.get(AppraisalConstants.PUL_RIGHT));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getBadTieRods(),map,AppraisalConstants.BAD_TIE_RODS)){
            eSteeringFeelStatus.setBadTieRods(map.get(AppraisalConstants.BAD_TIE_RODS));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getSteeringWheelWobble(),map,AppraisalConstants.STERNG_WHEEL_WOBBLE)){
            eSteeringFeelStatus.setSteeringWheelWobble(map.get(AppraisalConstants.STERNG_WHEEL_WOBBLE));
        }
        if(isUpdateApprConfReq(eSteeringFeelStatus.getBadSteeringRack(),map,AppraisalConstants.BAD_STERNG_RACK)){
            eSteeringFeelStatus.setBadSteeringRack(map.get(AppraisalConstants.BAD_STERNG_RACK));
        }
        eSteeringFeelStatus.setTdStatus(testDrSts);
        testDrSts.setSteeringFeel(eSteeringFeelStatus);
    }


    /**
     * This method updates the EBookAndKeys by getting configCodes from list and setting into the EBookAndKeys object
     *   and then EBookAndKeys object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateBookAndKeys(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE BOOK AND KEYS  **MAPPER CLASS**");
        EBookAndKeys eBookAndKeys=testDrSts.getBookAndKeys();
        Map<String, EConfigCodes> map=chkconfig(configList);
        if(null==eBookAndKeys){
            eBookAndKeys=new EBookAndKeys();
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHaveBothKeysAndAllBooks(),map,AppraisalConstants.BOTH_KEYS_AND_BOOKS)){
            eBookAndKeys.setHaveBothKeysAndAllBooks(map.get(AppraisalConstants.BOTH_KEYS_AND_BOOKS));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHave_1_Key(),map,AppraisalConstants.ONE_KEY)){
            eBookAndKeys.setHave_1_Key(map.get(AppraisalConstants.ONE_KEY));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHave_1_KeyAndBooks(),map,AppraisalConstants.ONE_KEY_AND_BOOKS)){
            eBookAndKeys.setHave_1_KeyAndBooks(map.get(AppraisalConstants.ONE_KEY_AND_BOOKS));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getNoBooks(),map,AppraisalConstants.NO_BOOKS)){
            eBookAndKeys.setNoBooks(map.get(AppraisalConstants.NO_BOOKS));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHave_2Keys(),map,AppraisalConstants.TWO_KEYS)){
            eBookAndKeys.setHave_2Keys(map.get(AppraisalConstants.TWO_KEYS));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHaveFullSetOfKeys(),map,AppraisalConstants.SET_OF_KEYS)){
            eBookAndKeys.setHaveFullSetOfKeys(map.get(AppraisalConstants.SET_OF_KEYS));
        }
        if(isUpdateApprConfReq(eBookAndKeys.getHaveBooks(),map,AppraisalConstants.BOOKS)){
            eBookAndKeys.setHaveBooks(map.get(AppraisalConstants.BOOKS));
        }

        eBookAndKeys.setTdStatus(testDrSts);
        testDrSts.setBookAndKeys(eBookAndKeys);
    }


    /**
     * This method updates the EApprBrakingSysSts by getting configCodes from list and setting into the EApprBrakingSysSts object
     *   and then EApprBrakingSysSts object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */

    public void updateBrakingSys(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE Braking Sys **MAPPER CLASS**");
        EApprBrakingSysSts eApprBrakingSysSts=testDrSts.getApprBrakingSysSts();
        Map<String, EConfigCodes> map=chkconfig(configList);
        if(null==eApprBrakingSysSts){
            eApprBrakingSysSts=new EApprBrakingSysSts();
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getNoIssuesGoodFeelStopsWell(),map,AppraisalConstants.NO_ISSUES_GOOD_FEEL_STOPS_WELL)){
            eApprBrakingSysSts.setNoIssuesGoodFeelStopsWell(map.get(AppraisalConstants.NO_ISSUES_GOOD_FEEL_STOPS_WELL));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getSoftPedalHardStopping(),map,AppraisalConstants.SOFT_PEDAL_HARD_STOPPING)){
            eApprBrakingSysSts.setSoftPedalHardStopping(map.get(AppraisalConstants.SOFT_PEDAL_HARD_STOPPING));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getVibrationsInPedalAndSteeringWheel(),map,AppraisalConstants.VIBRATIONS_IN_PEDAL_AND_STEERING_WHEEL)){
            eApprBrakingSysSts.setVibrationsInPedalAndSteeringWheel(map.get(AppraisalConstants.VIBRATIONS_IN_PEDAL_AND_STEERING_WHEEL));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getPullsRight(),map,AppraisalConstants.PULLS_RIGHT)){
            eApprBrakingSysSts.setPullsRight(map.get(AppraisalConstants.PULLS_RIGHT));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getPullsLeft(),map,AppraisalConstants.PULLS_LEFT)){
            eApprBrakingSysSts.setPullsLeft(map.get(AppraisalConstants.PULLS_LEFT));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getGrindingNoise(),map,AppraisalConstants.GRINDING_NOISE)){
            eApprBrakingSysSts.setGrindingNoise(map.get(AppraisalConstants.GRINDING_NOISE));
        }
        if(isUpdateApprConfReq(eApprBrakingSysSts.getHardPedalHardToStop(),map,AppraisalConstants.HARD_PEDAL_HARD_TO_STOP)){
            eApprBrakingSysSts.setHardPedalHardToStop(map.get(AppraisalConstants.HARD_PEDAL_HARD_TO_STOP));
        }
        eApprBrakingSysSts.setTdStatus(testDrSts);
        testDrSts.setApprBrakingSysSts(eApprBrakingSysSts);
    }

    /**
     * This method updates the EApprVehTransSts by getting configCodes from list and setting into the EApprVehTransSts object
     *   and then EApprVehTransSts object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateTransmnSys(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE Transmission Sys **MAPPER CLASS**");
        EApprVehTransSts eApprVehTransSts =testDrSts.getApprTransmissionSts();
        Map<String, EConfigCodes> map =chkconfig(configList);
        if(null== eApprVehTransSts){
            eApprVehTransSts =new EApprVehTransSts();
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getShiftsNormally(),map,AppraisalConstants.SHIFTS_NORMALLY)){
            eApprVehTransSts.setShiftsNormally(map.get(AppraisalConstants.SHIFTS_NORMALLY));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getHardShifting(),map,AppraisalConstants.HARD_SHIFTING)){
            eApprVehTransSts.setHardShifting(map.get(AppraisalConstants.HARD_SHIFTING));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getSlippingInGear(),map,AppraisalConstants.SLIPPING_IN_GEAR)){
            eApprVehTransSts.setSlippingInGear(map.get(AppraisalConstants.SLIPPING_IN_GEAR));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getNeedsReplacing(),map,AppraisalConstants.NEEDS_REPLACING)){
            eApprVehTransSts.setNeedsReplacing(map.get(AppraisalConstants.NEEDS_REPLACING));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getKnocksGoingIntoGear(),map,AppraisalConstants.KNOCKS_GOING_INTO_GEAR)){
            eApprVehTransSts.setKnocksGoingIntoGear(map.get(AppraisalConstants.KNOCKS_GOING_INTO_GEAR));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getHasAWhine(),map,AppraisalConstants.HAS_A_WHINE)){
            eApprVehTransSts.setHasAWhine(map.get(AppraisalConstants.HAS_A_WHINE));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getClutchPedalLow(),map,AppraisalConstants.CLUTCH_PEDAL_LOW)){
            eApprVehTransSts.setClutchPedalLow(map.get(AppraisalConstants.CLUTCH_PEDAL_LOW));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getNoReverse(),map,AppraisalConstants.NO_REVERSE)){
            eApprVehTransSts.setNoReverse(map.get(AppraisalConstants.NO_REVERSE));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getOddSound(),map,AppraisalConstants.ODD_SOUND)){
            eApprVehTransSts.setOddSound(map.get(AppraisalConstants.ODD_SOUND));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getNewClutch(),map,AppraisalConstants.NEW_CLUTCH)){
            eApprVehTransSts.setNewClutch(map.get(AppraisalConstants.NEW_CLUTCH));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getClutchNeedsReplacement(),map,AppraisalConstants.CLUTCH_NEEDS_REPLACEMENT)){
            eApprVehTransSts.setClutchNeedsReplacement(map.get(AppraisalConstants.CLUTCH_NEEDS_REPLACEMENT));
        }
        if(isUpdateApprConfReq(eApprVehTransSts.getDoesNotShiftProperly(),map,AppraisalConstants.DOES_NOT_SHIFT_PROPERLY)){
            eApprVehTransSts.setDoesNotShiftProperly(map.get(AppraisalConstants.DOES_NOT_SHIFT_PROPERLY));
        }
        eApprVehTransSts.setTdStatus(testDrSts);
        testDrSts.setApprTransmissionSts(eApprVehTransSts);
    }



    /**
     * This method updates the EApprEnginePer by getting configCodes from list and setting into the EApprEnginePer object
     *   and then EApprEnginePer object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateEngPerformance(EApprTestDrSts testDrSts, List<EConfigCodes> configList){
        log.info("UPDATE Engine performance **MAPPER CLASS**");
        EApprEnginePer eApprEnginePer=testDrSts.getApprEnginePer();
        Map<String, EConfigCodes> map=chkconfig(configList);
        if(null==eApprEnginePer){
            eApprEnginePer=new EApprEnginePer();
        }
        if(isUpdateApprConfReq(eApprEnginePer.getStrongRunningNoIssues(),map,AppraisalConstants.STRONG_RUNNING_NO_ISSUES)){
            eApprEnginePer.setStrongRunningNoIssues(map.get(AppraisalConstants.STRONG_RUNNING_NO_ISSUES));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getIdlesRoughDrivesWell(),map,AppraisalConstants.IDLES_ROUGH_DRIVES_WELL)){
            eApprEnginePer.setIdlesRoughDrivesWell(map.get(AppraisalConstants.IDLES_ROUGH_DRIVES_WELL));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getStalledOnTestDrive(),map,AppraisalConstants.STALLED_ON_TEST_DRIVE)){
            eApprEnginePer.setStalledOnTestDrive(map.get(AppraisalConstants.STALLED_ON_TEST_DRIVE));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getClearExhaust(),map,AppraisalConstants.CLEAR_EXHAUST)){
            eApprEnginePer.setClearExhaust(map.get(AppraisalConstants.CLEAR_EXHAUST));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getRoughIdleLowPower(),map,AppraisalConstants.ROUGH_IDLE_LOW_POWER)){
            eApprEnginePer.setRoughIdleLowPower(map.get(AppraisalConstants.ROUGH_IDLE_LOW_POWER));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getSluggishPerformance(),map,AppraisalConstants.SLUGGISH_PERFORMANCE)){
            eApprEnginePer.setSluggishPerformance(map.get(AppraisalConstants.SLUGGISH_PERFORMANCE));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getSmokeFromExhaust(),map,AppraisalConstants.SMOKE_FROM_EXHAUST)){
            eApprEnginePer.setSmokeFromExhaust(map.get(AppraisalConstants.SMOKE_FROM_EXHAUST));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getHasATickSound(),map,AppraisalConstants.HAS_A_TICK_SOUND)){
            eApprEnginePer.setHasATickSound(map.get(AppraisalConstants.HAS_A_TICK_SOUND));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getHasAKnockSound(),map,AppraisalConstants.HAS_A_KNOCK_SOUND)){
            eApprEnginePer.setHasAKnockSound(map.get(AppraisalConstants.HAS_A_KNOCK_SOUND));
        }
        if(isUpdateApprConfReq(eApprEnginePer.getPoorAccelerationForTheModel(),map,AppraisalConstants.POOR_ACCELERATION_FOR_THE_MODEL)){
            eApprEnginePer.setPoorAccelerationForTheModel(map.get(AppraisalConstants.POOR_ACCELERATION_FOR_THE_MODEL));
        }
        eApprEnginePer.setTdStatus(testDrSts);
        testDrSts.setApprEnginePer(eApprEnginePer);
    }

    /**
     * This method updates the EApprVehTireCondn by getting configCodes from list and setting into the EApprVehTireCondn object
     *   and then EApprVehTireCondn object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateTireCondition(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        log.info("UPDATE TIRE CONDITION  **MAPPER CLASS**");
        EApprVehTireCondn eApprVehTireCondn = testDrSts.getApprVehTireCondn();
        Map<String, EConfigCodes> map=chkconfig(configList);
        if(null==eApprVehTireCondn){
            eApprVehTireCondn=new EApprVehTireCondn();
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getAllMatchingSizeAndMake(),map,AppraisalConstants.ALL_MATCHING)){
            eApprVehTireCondn.setAllMatchingSizeAndMake(map.get(AppraisalConstants.ALL_MATCHING));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getMismatched(),map,AppraisalConstants.MIS_MATCH)){
            eApprVehTireCondn.setMismatched(map.get(AppraisalConstants.MIS_MATCH));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getTreadDepth10_32New(),map,AppraisalConstants.TREAD_DEPTH_10_32_NEW)){
            eApprVehTireCondn.setTreadDepth10_32New(map.get(AppraisalConstants.TREAD_DEPTH_10_32_NEW));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getTreadDepth6_32orhigherGood(),map,AppraisalConstants.TREAD_DEPTH_6_32_OR_HIGH_GOOD)){
            eApprVehTireCondn.setTreadDepth6_32orhigherGood(map.get(AppraisalConstants.TREAD_DEPTH_6_32_OR_HIGH_GOOD));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getTreadDepth4_32Poor(),map,AppraisalConstants.TREAD_DEPTH_4_32_POOR)){
            eApprVehTireCondn.setTreadDepth4_32Poor(map.get(AppraisalConstants.TREAD_DEPTH_4_32_POOR));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getFrontsWornUneven(),map,AppraisalConstants.FRONTS_WORN_UNEVEN)){
            eApprVehTireCondn.setFrontsWornUneven(map.get(AppraisalConstants.FRONTS_WORN_UNEVEN));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getRearsWorn(),map,AppraisalConstants.REARS_WORN)){
            eApprVehTireCondn.setRearsWorn(map.get(AppraisalConstants.REARS_WORN));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getNoSpare(),map,AppraisalConstants.NO_SPARE)){
            eApprVehTireCondn.setNoSpare(map.get(AppraisalConstants.NO_SPARE));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getGoodSpare(),map,AppraisalConstants.GOOD_SPARE)){
            eApprVehTireCondn.setGoodSpare(map.get(AppraisalConstants.GOOD_SPARE));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getRimDamage(),map,AppraisalConstants.RIM_DAMAGE)){
            eApprVehTireCondn.setRimDamage(map.get(AppraisalConstants.RIM_DAMAGE));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getSdWallsChkd(),map,AppraisalConstants.SDWALLCHKD)){
            eApprVehTireCondn.setSdWallsChkd(map.get(AppraisalConstants.SDWALLCHKD));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getSpareOnCar(),map,AppraisalConstants.SPARE_ON_CAR)){
            eApprVehTireCondn.setSpareOnCar(map.get(AppraisalConstants.SPARE_ON_CAR));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getStockOffset(),map,AppraisalConstants.STOCKOFFSET)){
            eApprVehTireCondn.setStockOffset(map.get(AppraisalConstants.STOCKOFFSET));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getAllSameMake(),map,AppraisalConstants.ALLSAMEMAKE)){
            eApprVehTireCondn.setAllSameMake(map.get(AppraisalConstants.ALLSAMEMAKE));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getNeedRplcmt(),map,AppraisalConstants.NEEDREPLACEMENT)){
            eApprVehTireCondn.setNeedRplcmt(map.get(AppraisalConstants.NEEDREPLACEMENT));
        }
        if(isUpdateApprConfReq(eApprVehTireCondn.getRunFlats(),map,AppraisalConstants.RUNSFLAT)){
            eApprVehTireCondn.setRunFlats(map.get(AppraisalConstants.RUNSFLAT));
        }


        eApprVehTireCondn.setTdStatus(testDrSts);
        testDrSts.setApprVehTireCondn(eApprVehTireCondn);
    }

    private Map<String, EConfigCodes> chkconfig(List<EConfigCodes> configList) {
        Map<String, EConfigCodes> map=null;
        if(null!=configList && !configList.isEmpty()){
            map=configList.stream().collect(
                    Collectors.toMap(EConfigCodes::getShortCode, c->c));
        }
        else{
            map=new HashMap<>();
        }
        return map;
    }


    /**
     * This method updates the ERearWndwDmg by getting configCodes from list and setting into the ERearWndwDmg object
     *   and then ERearWndwDmg object is set into the EApprTestDrSts object
     * @param testDrSts This is the object of  EApprTestDrSts
     * @param configList This is the object of List<EConfigurationCodes>
     * @return
     */
    public void updateRearWindowDamage(EApprTestDrSts testDrSts,List<EConfigCodes> configList){
        ERearWndwDmg rearWindow = testDrSts.getRearWindow();
        Map<String, EConfigCodes> map= chkconfig(configList);
        if(null==rearWindow){
            rearWindow=new ERearWndwDmg();
        }
        if (isUpdateApprConfReq(rearWindow.getBrokenWindow(),map,AppraisalConstants.BROKEN_WINDOW)){
            rearWindow.setBrokenWindow(map.get(AppraisalConstants.BROKEN_WINDOW));
        }
        if (isUpdateApprConfReq(rearWindow.getTintPealing(),map,AppraisalConstants.TINT_PEALING)){
            rearWindow.setTintPealing(map.get(AppraisalConstants.TINT_PEALING));
        }
        if (isUpdateApprConfReq(rearWindow.getDicolored(),map,AppraisalConstants.DISCOLORED)){
            rearWindow.setDicolored(map.get(AppraisalConstants.DISCOLORED));
        }
        if (isUpdateApprConfReq(rearWindow.getDefrosterNotWorking(),map,AppraisalConstants.DEFROSTER_NOT_WORKING)){
            rearWindow.setDefrosterNotWorking(map.get(AppraisalConstants.DEFROSTER_NOT_WORKING));
        }
        if (isUpdateApprConfReq(rearWindow.getTrimMissing(),map,AppraisalConstants.TRIM_MISSING)){
            rearWindow.setTrimMissing(map.get(AppraisalConstants.TRIM_MISSING));
        }
        if (isUpdateApprConfReq(rearWindow.getTrimRustedOrPealing(),map,AppraisalConstants.TRIM_RUSTED_OR_PEALING)){
            rearWindow.setTrimRustedOrPealing(map.get(AppraisalConstants.TRIM_RUSTED_OR_PEALING));
        }
        if (isUpdateApprConfReq(rearWindow.getNoDamage(),map,AppraisalConstants.NO_DAMAGE)){
            rearWindow.setNoDamage(map.get(AppraisalConstants.NO_DAMAGE));
        }
        rearWindow.setTdStatus(testDrSts);
        testDrSts.setRearWindow(rearWindow);
    }



    /**
     * This method returns true if EConfigurationCodes is null and Map<String,EConfigurationCodes> is not null or
     * EConfigurationCodes is not null and Map<String,EConfigurationCodes> is null
     * @param configCode This is the object of EConfigurationCodes
     * @param configMap This is the object of Map<String,EConfigurationCodes>
     * @param shortCode This is the filed shortCode
     * @return Boolean
     */
    private Boolean isUpdateApprConfReq(EConfigCodes configCode, Map<String, EConfigCodes> configMap, String shortCode){
        return (null == configCode && null != configMap.get(shortCode)) || (null != configCode && null == configMap.get(shortCode));
    }



}
