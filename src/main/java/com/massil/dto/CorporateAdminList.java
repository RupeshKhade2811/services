package com.massil.dto;



import com.massil.ExceptionHandle.Response;
import com.massil.persistence.model.CorporateAdminView;
import lombok.*;

import java.util.List;

@Getter
@Setter

@ToString
public class CorporateAdminList extends Response {
    List<CorporateAdminView> CorAdminList;
}
