package org.deus.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
public class ApiApplication {
	public static final Logger logger = LoggerFactory.getLogger("org.deus.api");

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}
}
