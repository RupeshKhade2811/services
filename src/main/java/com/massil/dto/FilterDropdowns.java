package com.massil.dto;

import com.massil.ExceptionHandle.Response;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FilterDropdowns extends Response {
    private List<String> make;
    private List<String> model;
    private List<String> series;
    private List<Long> year;
    private List<String> engine;
    private List<String> transmission;

}
