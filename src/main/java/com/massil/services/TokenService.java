package com.massil.services;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.dto.TokenSrvc;

public interface TokenService {

    TokenSrvc getAcessTkn() throws AppraisalException;
}
