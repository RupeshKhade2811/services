package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.Shipment;
import jakarta.xml.bind.JAXBException;

import java.text.ParseException;
import java.util.UUID;

public interface PaymentGatewayService {
     Response paymentInfo(UUID userId, String token);
     public void feePaymentService(Shipment shipment, Long shipId) throws Exception;

     public void onceInADay() throws AppraisalException, JAXBException;
     public void onceInAMonth() throws AppraisalException, JAXBException, ParseException;
}
