package com.massil.dto;


import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
public class TestDriveMes {

    private String dataInterval;
    private String engineTempTest;
    private String engineRpm;
    private String driveGear;
    private String vehicleSpeed;
    private String voltage;
    private String upstbk1;
    private String upstbk2;
    private String dwnstbk1;
    private String dwnstbk2;
}
