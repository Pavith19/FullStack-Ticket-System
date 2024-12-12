# 🎫 Concurrent Ticket Management System - Full Stack Application

## 🚀 Introduction

This project is a full-stack, concurrent Ticket Management System that simulates a real-world scenario where multiple vendors can add tickets and numerous customers can concurrently purchase them. The system consists of a **Spring Boot backend** that provides RESTful APIs for managing events, tickets, system configurations, and transactions, and a **React frontend** that provides a user-friendly interface to interact with the system.

### 🎯 Key Objectives

- **Concurrent Ticket Management:** Simulate ticket addition by multiple vendors
- **Concurrent Ticket Purchasing:** Enable multiple customers to buy tickets simultaneously
- **Thread Safety:** Ensure data consistency during concurrent operations
- **Real-time Updates:** Provide live updates about ticket availability using WebSocket
- **Scalable Solution:** Develop a robust ticket management system with an intuitive user interface

## 🏗️ System Architecture

The application follows a client-server architecture:

- **Backend:** Spring Boot application handling core business logic, data persistence, and real-time WebSocket communication
- **Frontend:** React application consuming backend APIs, providing system configuration and monitoring interface
- **Database:** H2 in-memory database for development and testing purposes

## 💻 Technology Stack

### Backend Technologies
- **Language:** Java (version 8+)
- **Framework:** Spring Boot
- **Data Access:** Spring Data JPA
- **Real-time Communication:** Spring WebSocket
- **Database:** H2 Database
- **Logging:** SLF4J (with Logback)
- **Build Tool:** Maven

### Frontend Technologies
- **Library:** React
- **UI Components:** Material UI (MUI)
- **HTTP Client:** axios
- **WebSocket:** SockJS, @stomp/stompjs
- **Notifications:** react-hot-toast
- **Runtime Environment:** Node.js
- **Package Managers:** npm or yarn

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
│           │       └── TicketSystemApplication.java  # Main application class
│           └── resources/
│               └── application.properties  # Application properties
└── frontend/                 # React frontend
    └── public/
    │   └── index.html          # Main HTML file
    └── src/
        ├── Components/         # React components
        ├── systemConfigService.js   # API service for backend communication
        ├── App.js              # Main React component
        ├── index.js            # Entry point for React
        └── webpack.config.js   # Webpack configuration
    └── package.json            # Frontend dependencies and scripts
```

## 🛠️ Setup Instructions

### Prerequisites

- **Java Development Kit (JDK):** Version 8 or higher
- **Maven:** Version 3.2 or higher
- **Node.js:** Version 14 or higher (LTS recommended)
- **npm or yarn**
- **IDE:** IntelliJ IDEA or Eclipse (Java), VS Code or WebStorm (React)
- **Git**

### Installation Steps

1. **Clone the Repository**
   ```bash
   git clone [repository URL]
   cd [repository directory]
   ```

2. **Build and Run Backend**
   ```bash
   cd backend
   mvn clean install
   mvn spring-boot:run
   ```

3. **Build and Run Frontend**
   ```bash
   cd ../frontend
   npm install  # or yarn install
   npm start    # or yarn start
   ```

## 🔧 System Configuration API

### Configure System
**Endpoint:** `/api/system-configuration/configure`
**Method:** `POST`

**Example Request Body:**
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

## 🌐 WebSocket Communication

**WebSocket Endpoint:** `/ws-ticket-system`

**Topics:**
- `/topic/system-updates`: System-related updates
- `/topic/ticket-updates`: Real-time ticket updates

## 🧪 Testing

- **Backend:** Use Spring's testing framework
  ```bash
  mvn test
  ```
- **Frontend:** Use Jest and React Testing Library
  ```bash
  npm test
  ```

## 🤝 Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Submit a pull request

## 📄 License

[Specify your license here]

## 🙏 Acknowledgments

Thanks to all contributors and the amazing open-source libraries that made this project possible!
