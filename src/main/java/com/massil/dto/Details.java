package com.massil.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Details {
    private String op;
    @JsonProperty("value")
    private Map<String,Object> value;
}
