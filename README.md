# Concurrent Ticket Management System

## Overview

This is a sophisticated, concurrent ticket management system designed to simulate a real-world ticket sales platform with multiple vendors, customers, and events. The system provides a robust, thread-safe implementation of ticket sales, featuring:

- Concurrent ticket addition by multiple vendors
- Real-time ticket purchasing
- WebSocket-based live updates
- Configurable system parameters
- Comprehensive logging and monitoring

## System Architecture

The application is built using:
- Backend: Spring Boot
- Messaging: WebSocket
- Database: JPA/Hibernate
- Logging: SLF4J
- Concurrency: Java Concurrent Utilities

### Key Components

#### Model Classes
- **Event:** Represents event details with unique name and pricing
- **SystemConfiguration:** Manages global ticket system parameters
- **Ticket:** Immutable ticket representation
- **Transaction:** Captures ticket purchase details

#### Repository Interfaces
- **EventRepository:** 
  - Provides database operations for Event entities
  - Supports CRUD operations
  - Enables custom query methods for event management
  - Extends JpaRepository for standard database interactions

- **SystemConfigurationRepository:**
  - Manages system-wide configuration settings
  - Allows retrieval and update of global system parameters
  - Provides methods to fetch and modify configuration

- **TransactionRepository:**
  - Handles transaction-related database operations
  - Supports logging and tracking of ticket purchases
  - Enables complex querying of transaction history
  - Provides methods for aggregating transaction data

#### Key Services
- **TicketPoolService:** Core service managing ticket sales and distribution
- **TicketAvailabilityController:** Provides real-time ticket availability information
- **TicketSystemControlController:** Manages ticket system lifecycle (start, stop, reset)
- **WebSocketConfig:** Configures WebSocket communication for real-time updates
- **LogService:** Handles system logging and WebSocket notifications

## Backend Endpoints

### Ticket Availability Endpoint
- **URL:** `/api/ticket-availability`
- **Method:** GET
- **Description:** Retrieves current ticket system status
- **Returns:**
  - Ticket availability by event
  - Total tickets added
  - Current available tickets
  - Total tickets sold

### Ticket System Control Endpoints
- **Base URL:** `/api/ticket-system-control`

1. **Start Ticket System**
   - **URL:** `/start`
   - **Method:** POST
   - **Description:** Initializes the ticket handling system
   - **Checks:**
     - System is not already running
     - Not all tickets have been sold
     - Clears existing transactions

2. **Stop Ticket System**
   - **URL:** `/stop`
   - **Method:** POST
   - **Description:** Halts the ongoing ticket system

3. **Reset Ticket System**
   - **URL:** `/reset`
   - **Method:** POST
   - **Description:** Resets the ticket system to its initial state

### WebSocket Configuration

#### WebSocket Endpoint
- **Endpoint:** `/ws-ticket-system`
- **Features:**
  - SockJS fallback support
  - Supports connections from all origins

#### Message Broker Destinations
- **Broadcast Prefix:** `/topic/`
  - Used for broadcasting messages to multiple subscribers
- **Application Prefix:** `/app/`
  - Used for routing messages to specific handling methods

## Concurrency and Performance

### Thread Pool Configuration
- **Core Pool Size:** 20 threads
- **Maximum Pool Size:** 50 threads
- **Queue Capacity:** 500 tasks
- **Thread Name Prefix:** `TicketSystem-`

## Prerequisites

### Backend
- Java 17 or higher
- Maven 3.8+
- Spring Boot 3.x
- Spring Data JPA
- Hibernate

### Database
- MySQL/PostgreSQL recommended
- Hibernate ORM for database interactions

### Frontend (Recommended)
- Node.js 16+
- React 18+

## Installation

### Backend Setup

1. Clone the repository:
```bash
git clone https://github.com/yourusername/ticket-management-system.git
cd ticket-management-system
```

2. Configure Database
   - Create a database for the application
   - Update `application.properties` with database connection details
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/ticketsystem
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
mvn spring-boot:run
```

### Frontend Setup (Separate Repository)

1. Clone the frontend repository:
```bash
git clone https://github.com/yourusername/ticket-management-frontend.git
cd ticket-management-frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

## System Configuration

The system requires configuration before starting ticket sales. Use the `/api/system-configuration/configure` endpoint with the following JSON structure:

```json
{
  "maxCapacity": 1000,
  "totalTickets": 500,
  "releaseRate": 5,
  "retrievalRate": 3,
  "events": [
    {
      "name": "Concert A",
      "price": 50.00
    },
    {
      "name": "Sports Event B", 
      "price": 75.00
    }
  ]
}
```

### Configuration Parameters

- `maxCapacity`: Maximum number of tickets the system can handle
- `totalTickets`: Total tickets to be released
- `releaseRate`: Speed of ticket addition by vendors
- `retrievalRate`: Speed of ticket purchases by customers
- `events`: List of events with names and prices

## Database Schema

### Events Table
- `event_id` (Primary Key)
- `event_name` (Unique)
- `event_price`

### System Configuration Table
- `id` (Primary Key)
- `total_tickets`
- `release_rate`
- `retrieval_rate`
- `max_capacity`

### Transactions Table
- `id` (Primary Key)
- `event_name`
- `ticket_price`
- `vendor_id`
- `customer_id`
- `ticket_count`
- `transaction_timestamp`

## System Workflow

1. Configure the system using the configuration endpoint
2. Start ticket handling via appropriate API call
3. Vendors automatically add tickets
4. Customers purchase tickets concurrently
5. System tracks and logs all activities

## Monitoring & Logging

- Backend logs are written to console and log files
- WebSocket updates provide real-time system status
- Endpoints available for checking current system state

## WebSocket Endpoints

- `/topic/system-updates`: General system events
- `/topic/ticket-updates`: Ticket addition and purchase notifications

## Error Handling and Logging

- Comprehensive error logging using SLF4J
- Detailed error responses for API endpoints
- Graceful handling of system state transitions

## Performance Characteristics

- Concurrent ticket handling
- Thread-safe operations
- Configurable ticket release and retrieval rates
- Real-time monitoring and logging

## Potential Improvements

- Implement persistent storage for transactions
- Add more comprehensive error handling
- Create admin dashboard for system monitoring
- Implement more advanced concurrency controls
- Add unit and integration tests for repositories
- Implement caching mechanisms
- Add more detailed transaction reporting

## Troubleshooting

- Ensure all prerequisites are met
- Check log files for detailed error information
- Verify network configuration for WebSocket connections
- Validate database connection and configuration
- Check thread pool and concurrency settings

## Security Considerations

- Implement authentication and authorization
- Use prepared statements to prevent SQL injection
- Validate and sanitize input data
- Implement rate limiting on API endpoints

## Logging and Monitoring

- Configure log levels appropriately
- Use structured logging
- Implement performance monitoring
- Set up alerts for critical system events

## License

Apache License 2.0

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

