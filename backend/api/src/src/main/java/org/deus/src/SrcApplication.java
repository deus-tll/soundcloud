package org.deus.src;

import org.deus.datalayerstarter.models.PerformerModel;
import org.deus.datalayerstarter.models.SongModel;
import org.deus.datalayerstarter.models.auth.UserModel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableScheduling
@EntityScan(basePackages = "org.deus.datalayerstarter.models")
public class SrcApplication {
	public static final Logger logger = LoggerFactory.getLogger("org.deus.src");

	public static void main(String[] args) {
		SpringApplication.run(SrcApplication.class, args);
	}

}
