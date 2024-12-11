package com.ticketSystem.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocket configuration for real-time communication in the ticket system.
 * Configures WebSocket messaging capabilities to enable:
 * - Real-time ticket availability updates
 * - Live event status notifications
 * - Instant communication between server and client
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configures the message broker for WebSocket communication.
     * This method sets up:
     * - Simple message broker with '/topic' destination prefix
     * - Application destination prefix for message routing
     * Destination Prefixes:
     * - '/topic/': Used for broadcasting messages to multiple subscribers
     * - '/app/': Used for routing messages to specific message-handling methods
     * @param config The message broker configuration registry
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // Enable the simple message broker with the destination '/topic'
        // which will allow the server to broadcast messages to clients.
        config.enableSimpleBroker("/topic");

        // Set the application destination prefix '/app' to route messages
        // to the appropriate message-handling methods on the server.
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registers WebSocket endpoints for client connections.
     * Configures:
     * - WebSocket endpoint at '/ws-ticket-system'
     * - SockJS fallback for browsers that don't support WebSockets
     * - Allows connections from all origin patterns
     * @param registry The STOMP endpoint registry
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        // Create WebSocket endpoint with SockJS support
        registry.addEndpoint("/ws-ticket-system")
                // Allow connections from any origin
                .setAllowedOriginPatterns("*")
                // Enable SockJS fallback for older browsers
                .withSockJS();
    }

}
