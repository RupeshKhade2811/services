package com.massil;



import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.jobrunr.jobs.mappers.JobMapper;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.storage.StorageProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;


@SpringBootApplication
public class ServicesApplication {

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
