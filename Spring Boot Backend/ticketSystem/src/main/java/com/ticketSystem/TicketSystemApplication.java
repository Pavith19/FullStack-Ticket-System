package com.ticketSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

/**
 * Main application class for the Ticket Management System.
 * Serves as the entry point for the Spring Boot application and configures
 * the application's core thread execution capabilities.
 * Key Responsibilities:
 * - Bootstrap the Spring Boot application
 * - Configure a thread pool executor for concurrent task processing
 */
@SpringBootApplication
public class TicketSystemApplication {

	/**
	 * Main method to start the Spring Boot application.
	 * Launches the ticket management system application with default configurations.
	 * @param args Command-line arguments passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(TicketSystemApplication.class, args);
	}

	/**
	 * Configures a thread pool executor for managing concurrent tasks.
	 * Creates a ThreadPoolTaskExecutor with specific configuration:
	 * - Core pool size of 20 threads
	 * - Maximum pool size of 50 threads
	 * - Queue capacity of 500 tasks
	 * - Custom thread name prefix "TicketSystem-"
	 * @return Configured Executor for managing concurrent tasks
	 */
	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(20);
		executor.setMaxPoolSize(50);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("TicketSystem-");
		executor.initialize();
		return executor;
	}

}
