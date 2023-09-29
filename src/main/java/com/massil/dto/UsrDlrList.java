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
    List<UserRegistration> userList;
    public UserRegistration details;

}
