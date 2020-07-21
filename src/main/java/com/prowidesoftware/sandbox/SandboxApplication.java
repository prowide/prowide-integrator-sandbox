package com.prowidesoftware.sandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@EnableAutoConfiguration
@SpringBootApplication
@EntityScan( basePackages = {"com.prowidesoftware.swift"} )
public class SandboxApplication extends SpringBootServletInitializer {

	/**
	 * Used when the application is run in the container, deployed as a war file
	 */
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(SandboxApplication.class);
	}

	/**
	 * Used when the application is run from command line as a jar
	 */
	public static void main(String[] args) {
		SpringApplication.run(SandboxApplication.class, args);
	}

}
