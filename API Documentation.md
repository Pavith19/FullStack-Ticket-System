# 🎟️ Ticket System API Documentation

## 🚀 System Overview
A robust, feature-rich ticket management API designed for efficient ticket sales and management across multiple events.

## 2.1. Ticket System Control Endpoints

### 2.1.1. 🟢 Start Ticket System
**Endpoint:** `POST /api/ticket-system-control/start`

**🔍 Detailed Description:**
- Initializes the entire ticket handling system
- Performs critical pre-start checks
- Ensures system is not already running
- Clears existing transactions
- Validates ticket availability

**🛠️ Technical Specifications:**
- **Request Type:** POST
- **Authentication:** Required
- **Thread Safety:** Fully implemented

**📤 Request Parameters:** None required

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** System successfully started
  - **Example Response:** `"Ticket system started"`
- 🚫 400 Bad Request
  - **Conditions:** 
    - System already running
    - No tickets available for sale
  - **Example Response:** `"Ticket system is already running. Please reset first."`
- ⚠️ 500 Internal Server Error
  - **Meaning:** Unexpected system initialization failure
  - **Example Response:** `"Failed to start ticket system: [detailed error message]"`

### 2.1.2. 🛑 Stop Ticket System
**Endpoint:** `POST /api/ticket-system-control/stop`

**🔍 Detailed Description:**
- Gracefully halts the ticket handling system
- Prevents new transactions
- Preserves current system state
- Ensures clean system shutdown

**🛠️ Technical Specifications:**
- **Request Type:** POST
- **Authentication:** Required
- **System Impact:** Immediate cessation of ticket operations

**📤 Request Parameters:** None required

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** System successfully stopped
  - **Example Response:** `"Ticket system stopped successfully."`
- 🚫 400 Bad Request
  - **Meaning:** System was not running
  - **Example Response:** `"Ticket system is not running."`
- ⚠️ 500 Internal Server Error
  - **Meaning:** Unexpected shutdown issues
  - **Example Response:** `"Failed to stop ticket system: [detailed error message]"`

### 2.1.3. 🔄 Reset Ticket System
**Endpoint:** `POST /api/ticket-system-control/reset`

**🔍 Detailed Description:**
- Completely resets ticket handling system
- Forcibly stops running system
- Clears all internal states
- Resets all counters and transaction history
- Prepares system for fresh start

**🛠️ Technical Specifications:**
- **Request Type:** POST
- **Authentication:** Required
- **Data Preservation:** All existing data will be cleared

**📤 Request Parameters:** None required

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** System successfully reset
  - **Example Response:** `"Ticket system reset successfully"`
- ⚠️ 500 Internal Server Error
  - **Meaning:** Unable to complete reset process
  - **Example Response:** `"Failed to reset ticket system: [detailed error message]"`

## 2.2. 🎫 Ticket System Information Endpoints

### 2.2.1. 📊 Get Current Tickets by Event
**Endpoint:** `GET /api/ticket-system-info/current-tickets`

**🔍 Detailed Description:**
- Retrieves real-time ticket availability
- Provides comprehensive overview of ticket pool
- Supports multiple event tracking

**🛠️ Technical Specifications:**
- **Request Type:** GET
- **Caching:** Real-time data
- **Performance:** Optimized for quick retrieval

**📤 Request Parameters:** None required

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** Successfully retrieved ticket counts
  - **Example Response:** 
    ```json
    {
        "Cars Movie": 20,
        "Girls": 15
    }
    ```
- ⚠️ 500 Internal Server Error
  - **Meaning:** Failed to retrieve ticket information
  - **Example Response:** `"Failed to retrieve current tickets"`
 
 ## 2.3. 💰 Vendor and Customer Interaction Endpoints

### 2.3.1. 🛒 Purchase Tickets
**Endpoint:** `POST /api/ticket-system/purchase`

**🔍 Detailed Description:**
- Enables customer ticket purchasing
- Asynchronous processing
- Thread-safe transaction handling
- Real-time inventory management

**📥 Request Parameters:**
- `customerId` (int): Unique customer identifier
- `eventName` (string): Target event for ticket purchase

**🛠️ Technical Specifications:**
- **Request Type:** POST
- **Concurrency:** Fully thread-safe
- **Validation:** Strict ticket availability checks

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** Successful ticket purchase
  - **Example Response:** `"Customer purchased 2 tickets for Tennis Match"`
- 🚫 400 Bad Request
  - **Conditions:** 
    - Insufficient ticket inventory
    - Invalid event selection
  - **Example Response:** `"Not enough tickets available for Tennis Match."`
- ⚠️ 500 Internal Server Error
  - **Meaning:** Transaction processing failure
  - **Example Response:** `"Failed to process ticket purchase: [error details]"`

## 2.4. 🏪 Event and Ticket Management Endpoints

### 2.4.1 ➕ Add Tickets to Pool
**Endpoint:** `POST /api/ticket-system/vendor/add-tickets`

**🔍 Detailed Description:**
- Enables vendors to expand ticket inventory
- Respects system-wide capacity constraints
- Supports dynamic ticket pool management

**📥 Request Parameters:**
- `vendorId` (int): Vendor's unique identifier
- `eventName` (string): Event for ticket allocation
- `ticketsToAdd` (int): Number of tickets to introduce
- `price` (double): Individual ticket price

**🛠️ Technical Specifications:**
- **Request Type:** POST
- **Capacity Management:** Hard limit enforcement
- **Pricing Flexibility:** Per-event price setting

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** Tickets successfully added
  - **Example Response:** `"Vendor added 5 tickets for Tennis Match"`
- 🚫 400 Bad Request
  - **Conditions:** 
    - Maximum capacity reached
    - Invalid ticket count
  - **Example Response:** `"Cannot add more tickets. Total capacity reached."`
- ⚠️ 500 Internal Server Error
  - **Meaning:** Ticket addition failure
  - **Example Response:** `"Failed to add tickets for Tennis Match: [error details]"`

## 2.5. ⚙️ System Configuration Endpoints

### 2.5.1 📋 Get System Configuration
**Endpoint:** `GET /api/system/config`

**🔍 Detailed Description:**
- Retrieves comprehensive system settings
- Provides insights into system operational parameters
- Supports dynamic configuration monitoring

**🛠️ Technical Specifications:**
- **Request Type:** GET
- **Refresh Rate:** Real-time updates
- **Security:** Restricted access

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** Configuration successfully retrieved
  - **Example Response:** 
    ```json
    {
        "totalTickets": 100,
        "releaseRate": 5,
        "retrievalRate": 2
    }
    ```
- ⚠️ 500 Internal Server Error
  - **Meaning:** Configuration retrieval failed
  - **Example Response:** `"Failed to retrieve system configuration"`

## 2.6. 📚 Transaction Management Endpoints

### 2.6.1. 📖 Get All Transactions
**Endpoint:** `GET /api/transactions`

**🔍 Detailed Description:**
- Comprehensive transaction history retrieval
- Supports financial and sales analysis
- Provides complete sales tracking

**🛠️ Technical Specifications:**
- **Request Type:** GET
- **Data Scope:** Full transaction history
- **Sorting:** Chronological order

**🔢 Possible Responses:**
- ✅ 200 OK
  - **Meaning:** Transactions successfully retrieved
  - **Example Response:**
    ```json
    [
        {
            "transactionId": 1,
            "customerId": 3,
            "eventName": "Tennis Match",
            "ticketCount": 2,
            "totalPrice": 50.00
        },
        {
            "transactionId": 2,
            "customerId": 5,
            "eventName": "Pickleball Championship",
            "ticketCount": 1,
            "totalPrice": 25.00
        }
    ]
    ```
- ⚠️ 500 Internal Server Error
  - **Meaning:** Transaction retrieval failed
  - **Example Response:** `"Failed to retrieve transactions"`

## 2.7. 🌐 WebSocket Real-Time Updates

### 2.7.1. 🎫 Ticket Updates WebSocket
**Endpoint:** `ws://localhost:8080/topic/ticket-updates`

**🔍 Detailed Description:**
- Real-time ticket pool change notifications
- Instant updates for all connected clients
- Supports multiple event tracking

**📡 Message Format:**
```json
{
    "action": "add" | "purchase" | "reset",
    "vendor": 1,     // Vendor ID (add action)
    "customer": 2,   // Customer ID (purchase action)
    "tickets": 5,    // Number of tickets
    "event": "Tennis Match"
}
```

### 2.7.2 🚦 System Status Updates WebSocket
**Endpoint:** `ws://localhost:8080/topic/system-status`

**🔍 Detailed Description:**
- Provides real-time system state monitoring
- Instant notifications on system changes
- Comprehensive status tracking

**📡 Message Format:**
```json
{
    "status": "running" | "stopped" | "reset",
    "message": "System is running",
    "ticketsSold": 50,
    "ticketsAvailable": 50
}
```

---


