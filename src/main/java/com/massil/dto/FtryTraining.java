package com.massil.dto;


import lombok.*;



@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@ToString
////@ApiModel(description = "Factory Training Portal")
public class FtryTraining  {

    private Long factTranId;
    private String title;
    private String description;
    private String video;
    private String viewer;
    private String category;




}
