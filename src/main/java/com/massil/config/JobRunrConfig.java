package com.massil.config;



import com.massil.services.AutoBidService;
import com.massil.services.OffersService;
import com.massil.services.impl.AutoBidServiceImpl;
import jakarta.annotation.PostConstruct;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.scheduling.cron.Cron;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JobRunrConfig {
    @Autowired
    private JobScheduler jobScheduler;
    @Value("${cron.schedule.expression}")
    private String cronExpression;
   @Value("${cron.schedule.expression.autoBid}")
    private String autoBidCronExpression;
    Logger log = LoggerFactory.getLogger(JobRunrConfig.class);


    @PostConstruct
    public void scheduleRecurrently() throws InterruptedException {
        jobScheduler.<OffersService>scheduleRecurrently(cronExpression, x -> x.myScheduledTask());

        //jobScheduler.<AutoBidService>scheduleRecurrently(Cron.minutely(), x -> x.sellerAutoBid());

   /*@Bean
    public StorageProvider storageProvider(JobMapper jobMapper) {
        InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
        storageProvider.setJobMapper(jobMapper);
        return storageProvider;
    }*/
    }

}
