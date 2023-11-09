package com.massil;



import com.massil.config.JobRunrConfig;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.jobrunr.jobs.JobId;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.scheduling.JobScheduler;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.xml.crypto.Data;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


@SpringBootApplication
@EnableScheduling
public class ServicesApplication {
	Logger log = LoggerFactory.getLogger(ServicesApplication.class);

;

/*
	@Bean
	public StorageProvider storageProvider(JobMapper jobMapper) {
		InMemoryStorageProvider storageProvider = new InMemoryStorageProvider();
		storageProvider.setJobMapper(jobMapper);
		return storageProvider;
	}
*/

	@Bean
	public OpenAPI springFactoryOpenAPI() {
		return new OpenAPI()
				.info(new Info().title("Factory KeyAssure LLC")
						.description("")
						.version("v1.0.0")
						.license(new License().name("Massil 2.0").url("https://www.massiltechnologies.com/")));

	}


	public static void main(String[] args) {
		SpringApplication.run(ServicesApplication.class, args);


	}

}
