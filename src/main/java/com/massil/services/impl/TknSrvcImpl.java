package com.massil.services.impl;


import com.massil.ExceptionHandle.AppraisalException;
import com.massil.dto.TokenSrvc;
import com.massil.services.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Service
public class TknSrvcImpl implements TokenService {


    @Value("${market_check_access_token_url}")
    private String mktChkAcsTknUrl;
    @Value("${oauth_client_credentials}")
    private String clientCredentials;



        public TokenSrvc getAcessTkn() throws AppraisalException {

            String input = clientCredentials;

            // Encode the string to Base64
            String encodedString = Base64.getEncoder().encodeToString(input.getBytes());

                    RestTemplate restTemplate = new RestTemplate();

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    headers.set("Authorization", "Basic"+" "+encodedString);

                    MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
                    map.add("grant_type", "client_credentials");
                    map.add("expires_in", "600000");

                    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

                    ResponseEntity<TokenSrvc> response = restTemplate.exchange(
                            mktChkAcsTknUrl,
                            HttpMethod.POST,
                            request,
                            TokenSrvc.class
                    );

                    if (response.getStatusCode() == HttpStatus.OK) {
                        return response.getBody();
                    } else {
                        // Handle error here
                        throw new RuntimeException("Failed to obtain access token");
                    }


        }
    }

