package com.massil.controller;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.ExceptionHandle.Response;
import com.massil.dto.MailTeamSprt;
import com.massil.services.EmailService;
import freemarker.template.TemplateException;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/emailSender")
@Tag(name = "Email sender", description = "email sent/receive")
public class EmailController {

    Logger log = LoggerFactory.getLogger(EmailController.class);
    @Autowired
    private EmailService emailService;


    @PostMapping("/sentMailToSupportTeam")
    public ResponseEntity<Response> sentMailSuprtTeam(@RequestBody MailTeamSprt mailTeamSprt , @RequestHeader("userId") UUID userId) throws AppraisalException, TemplateException, MessagingException, IOException, AppraisalException, MessagingException {
        Response response=emailService.mailToSprtTeam(mailTeamSprt, userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }



}
