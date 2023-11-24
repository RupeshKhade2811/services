package com.massil.services;

import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.GlobalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.DealerRegistration;
import com.massil.dto.MailTeamSprt;
import freemarker.template.TemplateException;
import jakarta.mail.MessagingException;
import net.sf.jasperreports.engine.JRException;
import org.jdom2.JDOMException;


import java.io.IOException;
import java.util.UUID;


public interface EmailService {

    /**
     * This method sends Email to user who creates appraisal
     * @param userId receiving user id from UI in header
     * @return Response
     */
    Response sendCreationEmail(UUID userId) throws AppraisalException, TemplateException, IOException, MessagingException;

    /**
     * This method sends updates in Email to buyer
     * @param offerId receiving user id from UI in header
     * @return Response
     */

    Response offerUpdateEmail(Long offerId) throws AppraisalException, MessagingException, TemplateException, IOException, MessagingException;

    Response mailToSprtTeam(MailTeamSprt mailTeamSprt, UUID userId) throws AppraisalException, TemplateException, MessagingException, IOException;

    public void sendMailWithAttachment(Long offerId) throws MessagingException, IOException, AppraisalException, JRException, JDOMException, GlobalException, TemplateException;

    Response sendToDealer(DealerRegistration dealerRegistration) throws MessagingException, IOException, TemplateException;
}
