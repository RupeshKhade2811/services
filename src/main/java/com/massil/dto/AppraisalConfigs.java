package com.massil.dto;


import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;

/**
 * This class is a DTO which will give DROPDOWNS
 */

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AppraisalConfigs extends Response {
    private List<ConfigDropDown> vehicleExtrColor;
    private List<ConfigDropDown> vehicleIntrColor;
    private List<ConfigDropDown> dashWarnLights;
    private List<ConfigDropDown> acCond;
    private List<ConfigDropDown> doorLocks;
    private List<ConfigDropDown> roofType;
    private List<ConfigDropDown> brakingSysSts;
    private List<ConfigDropDown> enginePerformance;
    private List<ConfigDropDown> transmissionStatus;
    private List<ConfigDropDown> stereoSts;
    private List<ConfigDropDown> interiorCond;
    private List<ConfigDropDown> frontLeftWinSts;
    private List<ConfigDropDown> frontRightWinSts;
    private List<ConfigDropDown> rearLeftWinSts;
    private List<ConfigDropDown> rearRightWinSts;
    private List<ConfigDropDown> oilCond;
    private List<ConfigDropDown> frontWindShieldDamage;
    private List<UserDropDown> dealershipUserNames;
    private List<ConfigDropDown> steeringFeelSts;
    private List<ConfigDropDown> bookAndKeys;
    private List<ConfigDropDown> titleSts;
    private List<ConfigDropDown> tireCondition;
    private List<ConfigDropDown> rearWindowDamage;
    private List<ConfigDropDown> make;
    private List<ConfigDropDown> model;
    private List<ConfigDropDown> series;
    private List<ConfigDropDown> engine;
    private List<ConfigDropDown> transmission;

}
