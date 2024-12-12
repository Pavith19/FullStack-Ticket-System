# 🎫 Concurrent Ticket Management System

## 🌟 Introduction

This project is a full-stack, concurrent Ticket Management System that simulates a real-world scenario where multiple vendors can add tickets and numerous customers can concurrently purchase them. The system consists of a **Spring Boot backend** that provides RESTful APIs for managing events, tickets, system configurations, and transactions, and a **React frontend** that provides a user-friendly interface to interact with the system.

### 🚀 Key Objectives

- **Concurrent Ticket Management**: Simulate simultaneous ticket addition by multiple vendors
- **Real-time Purchasing**: Enable concurrent ticket purchases by multiple customers
- **Thread Safety**: Ensure data consistency during concurrent operations
- **WebSocket Integration**: Provide real-time updates about ticket availability and system status
- **Scalable Solution**: Offer a robust ticket management system with an intuitive user interface

## 🏗️ System Architecture

### Backend
- **Technology**: Spring Boot
- **Core Functions**: 
  - Business logic processing
  - Data persistence
  - Real-time WebSocket communication

### Frontend
- **Technology**: React
- **Core Functions**:
  - Backend API consumption
  - User interface for system configuration
  - Real-time system monitoring and updates

## 💻 Technology Stack

### Backend Technologies
- Java
- Spring Boot
- Spring Data JPA
- Spring WebSocket
- H2 Database
- SLF4J (Logback)
- Maven

### Frontend Technologies
- React
- Material UI
- axios
- SockJS
- @stomp/stompjs
- react-hot-toast
- Node.js

## 📂 Project Structure

```
ticket-management-system/
├── backend/                  # Spring Boot backend
│   └── src/
│       └── main/
│           ├── java/
│           │   └── com/ticketSystem/
│           │       ├── config/             # WebSocket configuration
│           │       ├── controller/         # REST controllers
│           │       ├── model/              # Domain models
│           │       ├── repository/         # Spring Data JPA repositories
│           │       ├── service/            # Business logic services
│           │       └── TicketSystemApplication.java
│           └── resources/
│               └── application.properties
└── frontend/                 # React frontend
    ├── public/
    │   └── index.html
    └── src/
        ├── Components/
        ├── systemConfigService.js
        ├── App.js
        ├── index.js
        └── webpack.config.js
```

## 🛠️ Setup Instructions

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher. Check with `java -version`
- **Maven:** Version 3.2 or higher. Verify with `mvn -v`
- **Node.js:** Version 14 or higher (LTS recommended). Check with `node -v`
- **npm:** Usually comes with Node.js. Verify with `npm -v`
- **IDE (Recommended):** 
  - IntelliJ IDEA or Eclipse for Java
  - VS Code or WebStorm for React
- **Git:** For cloning the repository

### Quick Start

1. **Clone the Repository**

- Open your terminal or command prompt
- Navigate to the directory where you want to clone the project
- Clone the repository:

    ```bash
    git clone [repository URL]
    cd [repository directory]
    ```

2. **Backend Setup**
```bash
cd backend
mvn clean install
mvn spring-boot:run  # Runs on port 8080
```

3. **Frontend Setup**
```bash
cd ../frontend
npm install  # or yarn install
npm start    # Runs on port 3000
```

## 🌐 Key Endpoints

### System Configuration
`POST /api/system-configuration/configure`

**Request Body Example:**
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

### System Control
- `POST /api/ticket-system-control/start`
- `POST /api/ticket-system-control/stop`
- `POST /api/ticket-system-control/reset`

### System Information
- `GET /api/system-status`
- `GET /api/ticket-availability`

## 🖥️ Frontend UI Components

### 1. Configuration Form
- Configure system parameters
- Add/Remove events
- Validate input dynamically

### 2. Control Panel
- Start/Stop/Reset system
- Display system configuration

### 3. Ticket Display
- Show total, current, and sold tickets
- Event-wise ticket breakdown

### 4. Event Board
- List configured events
- Event names and prices

### 5. Log Display
- Real-time system logs
- Color-coded log levels
- Log filtering

## 🔌 WebSocket Communication

**Endpoint:** `/ws-ticket-system`

**Topics:**
- `/topic/system-updates`
- `/topic/ticket-updates`

**Message Format Example:**
```json
{
  "type": "VENDOR_TICKET_ADD",
  "message": "Vendor 1 added 5 tickets for event Concert A at price $50.00",
  "details": {
    "vendorId": 1,
    "eventName": "Concert A",
    "ticketsAdded": 5,
    "price": 50.0,
    "currentTickets": 100
  },
  "timestamp": "2023-11-20T10:00:00"
}
```

## 💡 Program Flow

1. **System Startup**
   - Backend initializes H2 database
   - Frontend connects via WebSocket

2. **Configuration**
   - User configures system via frontend
   - Backend validates and saves configuration
   - WebSocket broadcasts updates

3. **System Operations**
   - Simulate vendor ticket additions
   - Simulate customer ticket purchases
   - Real-time UI updates via WebSocket


## 9. 🧪 Testing

### Backend Testing
- Framework: Spring's testing framework
- Command: `mvn test`

### Frontend Testing
- Framework: Jest and React Testing Library
- Command: `npm test` or `yarn test`


## 📊 Sample SQL Queries

### Event Table
```sql
INSERT INTO events (event_name, event_price) VALUES (?, ?);
SELECT * FROM events WHERE event_name = ?;
```

### Transaction Table
```sql
INSERT INTO transactions 
  (event_name, ticket_price, vendor_id, customer_id, ticket_count, transaction_timestamp) 
  VALUES (?, ?, ?, ?, ?, ?);
```

## 📄 License

[Specify your license here, e.g., MIT License]


