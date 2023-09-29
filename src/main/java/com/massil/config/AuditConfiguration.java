package com.massil.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditConfiguration implements AuditorAware<String> {


    private static final String SYSTEM = "System";

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    private String auditorName ;

    @Override
    public Optional<String> getCurrentAuditor() {


       return Optional.of(Optional.ofNullable(auditorName).orElse(SYSTEM));


    }


}
