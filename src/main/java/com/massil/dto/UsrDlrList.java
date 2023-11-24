package com.massil.dto;



import com.massil.ExceptionHandle.Response;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class UsrDlrList extends Response {
    private  List<UserRegistration> userList;
    private UserRegistration details;

}
