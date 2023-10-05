package com.massil.services;

import com.factory.appraisal.factoryService.ExceptionHandle.AppraisalException;
import com.factory.appraisal.factoryService.dto.TokenSrvc;



public interface TokenService {

    TokenSrvc getAcessTkn() throws AppraisalException;
}
