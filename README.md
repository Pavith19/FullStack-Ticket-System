# Concurrent Ticket Management System - Backend

## 1. Introduction

This project implements the backend service for a concurrent Ticket Management System designed to simulate a real-world scenario where multiple vendors can add tickets and numerous customers can concurrently purchase them. The system uses Spring Boot to provide RESTful APIs for managing events, tickets, system configurations, and transactions. A key feature of this system is its real-time update capability, powered by Spring WebSocket, which allows instant communication between the server and clients, providing live updates on ticket availability, system status, and other critical events.

**Key Objectives:**

*   Simulate concurrent ticket addition by multiple vendors.
*   Facilitate concurrent ticket purchases by multiple customers.
*   Ensure thread safety and data consistency during concurrent operations.
*   Provide real-time updates to clients about ticket availability and system status.
*   Offer a robust and scalable backend solution for a ticket management system.

## 2. Technology Stack

-   **Java:** Primary programming language (version 8 or higher).
-   **Spring Boot:** Framework for creating stand-alone, production-grade Spring-based applications.
-   **Spring Data JPA:** Simplifies database access and operations.
-   **Spring WebSocket:** Enables real-time, bidirectional communication between the server and clients.
-   **H2 Database:** An in-memory relational database, ideal for development and testing.
-   **SLF4J (with Logback):** Logging framework for recording application events.
-   **Maven:** Dependency management and build tool.

## 3. Features

-   **System Configuration:** Allows administrators to configure the system's parameters, such as maximum ticket capacity, ticket release and retrieval rates, and event details.
-   **Event Management:** Supports the addition of multiple events, each with a unique name and price.
-   **Ticket Management:**
    -   Enables multiple vendors to concurrently add tickets to the system.
    -   Allows customers to purchase tickets concurrently. The system supports partial purchases if the requested number of tickets exceeds availability.
    -   Ensures thread safety during ticket operations to maintain data consistency.
-   **Real-time Updates:**
    -   Utilizes WebSocket technology to provide real-time notifications for ticket additions, purchases, and system status changes.
    -   Keeps clients informed about the current ticket availability for each event.
-   **System Control:**
    -   Provides endpoints to start, stop, and reset the ticket handling system.
    -   Logs all system activities and state changes for monitoring and debugging.
-   **Transaction Tracking:**
    -   Records every ticket purchase transaction, including details like event name, ticket price, vendor and customer IDs, ticket count, and timestamp.
-   **Error Handling:**
    -   Implements robust mechanisms to handle concurrency issues, invalid requests, and unexpected errors gracefully.

## 4. Project Structure

```
com.ticketSystem/
├── config/             # WebSocket configuration
│   └── WebSocketConfig.java
├── controller/         # REST controllers for handling API requests
│   ├── SystemConfigurationController.java
│   ├── SystemStatusController.java
│   ├── TicketAvailabilityController.java
│   └── TicketSystemControlController.java
├── model/              # Domain models (Event, Ticket, Transaction, SystemConfiguration)
│   ├── Event.java
│   ├── Ticket.java
│   ├── Transaction.java
│   └── SystemConfiguration.java
├── repository/         # Spring Data JPA repositories for database interactions
│   ├── EventRepository.java
│   ├── SystemConfigurationRepository.java
│   └── TransactionRepository.java
├── service/            # Services for business logic (TicketPoolService, LogService)
│   ├── TicketPoolService.java
│   └── LogService.java
└── TicketSystemApplication.java  # Main application class
```

## 5. Setup Instructions

### 5.1 Prerequisites

-   **Java Development Kit (JDK):** Version 8 or higher. You can check your Java version by running `java -version` in your terminal.
-   **Maven:** Version 3.2 or higher. Verify your Maven installation with `mvn -v`.
-   **IDE (Optional but Recommended):** IntelliJ IDEA, Eclipse, or any other Java IDE for easier development and code management.
-   **Git:** For cloning the repository.

### 5.2 Cloning the Repository

1. Open your terminal or command prompt.
2. Navigate to the directory where you want to clone the project.
3. Clone the repository using the following command:

    ```bash
    git clone [repository URL]
    ```
4. Change to the newly cloned project directory:

    ```bash
    cd [repository directory]
    ```

### 5.3 Building the Application

1. Make sure you are in the project's root directory (where the `pom.xml` file is located).
2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

    This command will compile the code, run tests, and package the application into a JAR file.

### 5.4 Running the Application

You can run the application in several ways:

**a) Using the Spring Boot Maven Plugin:**

1. Navigate to the project's root directory in your terminal.
2. Run the following command:

    ```bash
    mvn spring-boot:run
    ```

**b) Running the JAR file directly:**

1. After building the project (using `mvn clean install`), you'll find the JAR file in the `target` directory.
2. Run the JAR file using the `java -jar` command:

    ```bash
    java -jar target/[your-application-name].jar
    ```

    Replace `[your-application-name].jar` with the actual name of the generated JAR file.

The application will start on the default port 8080. You can access it in your browser or through an API client at `http://localhost:8080`.

## 6. Usage Instructions

### 6.1 Configuring the System

Before starting the ticket system, you need to configure its parameters. This is done through the `/api/system-configuration/configure` endpoint.

**Endpoint:** `/api/system-configuration/configure`
**Method:** `POST`

**Request Body:**

```json
{
  "maxCapacity": 1000,
  "totalTickets": 500,
  "releaseRate": 5,
  "retrievalRate": 3,
  "events": [
    {
      "name": "Concert A",
      "price": 50.0
    },
    {
      "name": "Movie Premiere",
      "price": 25.5
    }
  ]
}
```

**Explanation of Parameters:**

*   **`maxCapacity`:** The maximum number of tickets the system can hold at any given time.
*   **`totalTickets`:** The total number of tickets that will be added to the system. This should not exceed `maxCapacity`.
*   **`releaseRate`:** The maximum number of tickets a vendor can add in a single operation (simulates a burst of ticket additions).
*   **`retrievalRate`:** The maximum number of tickets a customer can buy in a single transaction (simulates a burst of ticket purchases).
*   **`events`:** An array of event objects, each with:
    *   **`name`:** The name of the event (must be unique).
    *   **`price`:** The price of a ticket for that event.

**Response:**

A successful configuration will return a 200 OK status with a message indicating success and details of the configuration:

```json
{
  "message": "System configured successfully",
  "maxCapacity": 1000,
  "totalTickets": 500,
  "releaseRate": 5,
  "retrievalRate": 3,
  "events": ["Concert A", "Movie Premiere"]
}
```

**Error Handling:**

If there are validation errors (e.g., invalid values, duplicate event names), the API will return a 400 Bad Request status with details of the errors.

### 6.2 Starting the System

After configuring the system, you can start the ticket handling process using the following endpoint:

**Endpoint:** `/api/ticket-system-control/start`
**Method:** `POST`

**Response:**

A successful start will return a 200 OK status with the message:

```
Ticket system started
```

### 6.3 Stopping the System

To stop the ticket handling process:

**Endpoint:** `/api/ticket-system-control/stop`
**Method:** `POST`

**Response:**

```
Ticket system stopped successfully
```

### 6.4 Resetting the System

To reset the system to its initial state (clearing all data):

**Endpoint:** `/api/ticket-system-control/reset`
**Method:** `POST`

**Response:**

```
Ticket system reset successfully
```

### 6.5 Getting System Status

You can retrieve the current system status and configuration:

**Endpoint:** `/api/system-status`
**Method:** `GET`

**Response:**

```json
{
  "totalTickets": 500,
  "releaseRate": 5,
  "retrievalRate": 3,
  "maxCapacity": 1000,
  "events": [
    {
      "name": "Concert A",
      "price": 50.0
    },
    {
      "name": "Movie Premiere",
      "price": 25.5
    }
  ]
}
```

### 6.6 Getting Ticket Availability

To check the current ticket availability:

**Endpoint:** `/api/ticket-availability`
**Method:** `GET`

**Response:**

```json
{
    "availability": {
        "Concert A": 43,
        "Movie Premiere": 46
    },
    "ticketsAdded": 500,
    "currentTickets": 89,
    "ticketsSold": 411
}
```

## 7. WebSocket

The application uses WebSocket at the endpoint `/ws-ticket-system` with SockJS fallback for real-time communication. The following topics are used:

-   `/topic/system-updates`: For system-related updates (start, stop, reset, errors).
-   `/topic/ticket-updates`: For ticket addition and purchase updates.

## 8. Testing

The project is set up to use Spring's testing framework. You can run the tests using:

```bash
mvn test
```

## 9. Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a new branch for your feature
3. Commit your changes
4. Push to your branch
5. Submit a pull request

Please ensure your code follows the project's coding standards and includes appropriate tests.

## 10. License

[Specify your license here, e.g., MIT License]

## 11. Acknowledgments

-   Thanks to all the contributors who have helped with this project.
-   Special thanks to the open-source community for providing invaluable tools and libraries.

## 12. Contact

[Add contact information or project maintainer details]
