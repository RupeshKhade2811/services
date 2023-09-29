package com.massil.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchRes {
    private Integer totalResults;
    private Integer itemsPerPage;
    @JsonProperty("Resources")
    private List<UserResponse> resources;
}
