package com.ticketSystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration class for configuring Cross-Origin Resource Sharing (CORS) settings.
 * This configuration enables cross-origin requests for the ticket system's API endpoints,
 * allowing the frontend application to interact with the backend securely.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures Cross-Origin Resource Sharing (CORS) mappings for the application.
     * This method sets up CORS policies to:
     * - Allow requests from the frontend application (localhost:3000)
     * - Enable standard HTTP methods (GET, POST, PUT, DELETE, OPTIONS)
     * - Accept all headers
     * - Support credentials passing
     * @param registry The CORS configuration registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {

        // Configure CORS for all API endpoints
        registry.addMapping("/api/**")
                // Specify allowed frontend origin
                .allowedOrigins("http://localhost:3000")
                // Define allowed HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Allow all headers to pass through
                .allowedHeaders("*")
                // Enable credential sharing
                .allowCredentials(true);
    }
}
