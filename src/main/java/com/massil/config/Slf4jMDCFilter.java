package com.massil.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//@Data
//@EqualsAndHashCode(callSuper = false)
//@Component
public class Slf4jMDCFilter extends OncePerRequestFilter {

    private final String responseHeader="Response_ID";
    private final String mdcTokenKey="correlation-ID";
    private final String requestHeader=null;
    private final String path="PATH";
    private final String httpHeaders="HTTP_HEADERS";

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws java.io.IOException, ServletException {
        try {
            final String token;
            if (!StringUtils.isEmpty(requestHeader) && !StringUtils.isEmpty(request.getHeader(requestHeader))) {
                token = request.getHeader(requestHeader);
            } else {
                token = UUID.randomUUID().toString().toUpperCase().replace("-", "");
            }
            Enumeration<String> headerNames = request.getHeaderNames();
            Map<String,String> headers =  new HashMap<>();

            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String hn=headerNames.nextElement();
                            headers.put(hn,request.getHeader(hn));
                }
            }
            MDC.put(mdcTokenKey, token);
            MDC.put(path, request.getRequestURI());
            MDC.put(httpHeaders, headers.toString());


            if (!StringUtils.isEmpty(responseHeader)) {
                response.addHeader(responseHeader, token);
            }
            chain.doFilter(request, response);
        } finally {
            MDC.remove(mdcTokenKey);
        }
    }

}
