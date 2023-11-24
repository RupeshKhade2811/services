package com.massil.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Action {

    @JacksonXmlProperty(localName = "amount")
    private Double amount;
    @JacksonXmlProperty(localName = "date")
    private String date;

}
