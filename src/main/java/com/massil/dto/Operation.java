package com.massil.dto;


import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Operation {

    private String op;
    private String path;
    List<UserValue> value;
}
