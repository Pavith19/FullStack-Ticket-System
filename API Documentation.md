# 🎟️ Ticket System API Documentation

## 🚀 System Overview
A robust, feature-rich ticket management API designed for efficient ticket sales and management across multiple events.

## 2.1. 🎛️ Ticket System Control Endpoints

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
