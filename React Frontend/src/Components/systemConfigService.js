import axios from 'axios';
import SockJS from 'sockjs-client';
import { Stomp } from '@stomp/stompjs';

const BASE_URL = 'http://localhost:8080/api'; // Base URL for the API

// Service for system configuration management
export const systemConfigService = {

  // Configure the system with the provided config object
  configure: async (config) => {
    try {
      // Send POST request to configure the system
      const response = await axios.post(`${BASE_URL}/system-configuration/configure`, config);
      return response.data; // Return the response data if successful
    } catch (error) {

      // Handle errors and throw a specific error message
      throw error.response ? error.response.data : new Error('Configuration failed');
    }
  },
  
  // Get the current system status
  getSystemStatus: async () => {
    try {
      // Send GET request to fetch system status
      const response = await axios.get(`${BASE_URL}/system-status`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : new Error('Could not fetch system status');
    }
  }
};

// Service for controlling the ticketing system (start, stop, reset)
export const ticketControlService = {

  // Start the ticketing system
  startSystem: async () => {
    try {
      const response = await axios.post(`${BASE_URL}/ticket-system-control/start`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : new Error('Could not start ticket system');
    }
  },
  
  // Stop the ticketing system
  stopSystem: async () => {
    try {
      const response = await axios.post(`${BASE_URL}/ticket-system-control/stop`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : new Error('Could not stop ticket system');
    }
  },
  
  // Reset the ticketing system
  resetSystem: async () => {
    try {
      // Send POST request to reset the ticket system
      const response = await axios.post(`${BASE_URL}/ticket-system-control/reset`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : new Error('Could not reset ticket system');
    }
  }
};

// Service for checking the availability of tickets
export const ticketAvailabilityService = {
  getAvailability: async () => {
    try {
      // Send GET request to fetch ticket availability
      const response = await axios.get(`${BASE_URL}/ticket-availability`);
      return response.data;
    } catch (error) {
      throw error.response ? error.response.data : new Error('Could not fetch ticket availability');
    }
  }
};

// WebSocket service for connecting to a ticket system WebSocket
export const WebSocketService = {

  // Connect to the WebSocket server and listen for messages
  connect: (onMessageReceived) => {
    const socket = new SockJS('http://localhost:8080/ws-ticket-system');
    const stompClient = Stomp.over(socket);
    
    // Establish the WebSocket connection
    stompClient.connect({}, () => {
      console.log('WebSocket connected');
      
      // Subscribe to the '/topic/system-updates' WebSocket topic
      stompClient.subscribe('/topic/system-updates', (message) => {
        try {
          const parsedMessage = JSON.parse(message.body);
          console.log('Received WebSocket message:', parsedMessage);
          onMessageReceived(parsedMessage);
        } catch (error) {
          console.error('Error parsing WebSocket message:', error);
        }
      });
    }, (error) => {
      console.error('WebSocket connection error:', error);
    });
    
    // Return the WebSocket client object for further use
    return stompClient;
  }
};