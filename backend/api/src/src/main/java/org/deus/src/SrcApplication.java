package org.deus.src;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class SrcApplication {
	private static final Logger logger = LoggerFactory.getLogger(SrcApplication.class);
	public static void main(String[] args) {
		SpringApplication.run(SrcApplication.class, args);
	}

}
