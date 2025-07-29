package com.project.arebbus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the Arebbus backend service.
 * This is the entry point for the Spring Boot application that provides
 * bus tracking and transportation management services.
 */
@SpringBootApplication
public class ArebbusApplication {
	
	/**
	 * Main method to start the Spring Boot application.
	 * 
	 * @param args Command line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(ArebbusApplication.class, args);
	}
}
