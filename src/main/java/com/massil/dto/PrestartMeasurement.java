package com.massil.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class PrestartMeasurement {
    private String engineTemp;
    private String batteryVoltage;
    private String fuelPressure;
    private String warmUps;
    private String timeSince;
    private String mileSince;
    private String currentTroubleCodes;
    private String pendingTroubleCodes;
    private String odometer;
    private String scannedVin;
}
