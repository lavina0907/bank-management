# Bank Management System (BMS)

## Project Overview

The **Bank Management System (BMS)** is a **Spring Boot-based** application that provides banking services such as **customer account management, balance operations (credit, debit), balance check, exchange and notifications**. It follows a **microservices architecture** and uses **PostgreSQL** as the database. The system ensures **transaction safety, concurrency handling, and notification integration** for critical banking operations.

---

## Technology Stack

- **Backend:** Java 17+, Spring Boot, Spring Data JPA, Spring Validation
- **Database:** PostgreSQL
- **Messaging & Notifications:** Asynchronous Notifications via `NotificationService`
- **Containerization:** Docker, Docker Compose

---

## System Components

### Customer Management

- Allows customers to **register** with email, phone, and personal details.
- Ensures **uniqueness** by checking if a customer already exists.
- Notifies the customer upon successful registration.

### Account Management

- Every registered customer is assigned an **Account**.
- Customers can hold **multiple currency balances** in their account.
- Supports **transaction history tracking** for auditing purposes.

### Balance Operations

- **Credit Balance**: Adds money to a customer's account (supports retry logic in case of failures).
- **Debit Balance**: Deducts money, ensuring the account has sufficient funds.
- **Optimistic Locking**: Prevents race conditions when multiple transactions update the same account.

### Transaction Processing

- Supports **credit and debit transactions** with status tracking (`COMPLETED`).
- Ensures **data integrity** using transactions (`@Transactional`).
- Handles **concurrency** with a retry mechanism for deposits and synchronized debit processing.

### Notifications

- Sends **email notifications** to customers when:
    - An account is created.
    - A balance is credited or debited.
- Uses `ExecutorService` for **asynchronous execution** to avoid blocking operations.

---

## Deployment Setup

### Docker Compose Configuration

The application is containerized using **Docker Compose**, with PostgreSQL and PgAdmin as dependencies.

### Steps to Run the Application

1. **Start the Database**
   ```bash
   docker-compose up -d postgres
   ```
2. **Build & Run the Spring Boot Application**
   ```bash
   mvn clean package
   docker build -t bank-management .
   docker-compose down
   docker-compose up -d
   ```
---
## Local Port : 8081

## Note
Supported Currency : EUR, USD, SEK and RUB. if you want to onboard more currencies just add it in `Currency.java` enum


---

# API Documentation
## Base URL
```
http://localhost:8081/
```
## Prerequisite

**- Customer data required before execution of other APIs**
- To create customer data please use create customer API : API - 1

## Endpoints

### 1. Create Customer with Balance
**Endpoint:**
```
POST v1/customer/create
```
**Description:**
Creates a new customer along with an initial account balance and currency

**Request Body (JSON):**
```json
{
    "firstName": "string",
    "lastName": "string",
    "email": "string",
    "phone": "string",
    "balance": {
        "currency": "string",
        "amount": number
    }
}
```
**Request Body (JSON): Sample**
```json
{
  "firstName": "Lavina",
  "lastName": "Soni",
  "email": "l.s.v3@gmail.com",
  "phone": "8010716616",
  "balance": {
    "currency": "EUR",
    "amount": 1000
  }
}
```

**Response Body (JSON): Sample**
```json
{
  "status": "SUCCESS",
  "message": "Customer created successfully, Customer Id : 09d90077-51a5-40b3-a287-ee0d5df9b606"
}
```

---

### 2. Create Customer Account
**Endpoint:**
```
POST v1/account/create
```
**Description:**
Creates new currency balance with initial balance for an existing customer.

**Request Body (JSON):**
```json
{
    "email": "string",
    "balance": {
        "currency": "string",
        "amount": number
    }
}
```
**Request Body (JSON): Sample**
```json
{
  "email": "l.s.v3@gmail.com",
  "balance": {
    "currency": "SEK",
    "amount": 21200
  }
}
```
**Response Body (JSON): Sample**
```json
{
  "status": "SUCCESS",
  "message": "Customer account created successfully, account Id : 6e76d688-30aa-4224-8587-d5969210b2d0 and currency : SEK"
}
```
---

### 3. Credit Balance
**Endpoint:**
```
PUT v1/account/creditBalance
```
**Description:**
Adds a specified amount to a customer's account currency balance.

**Request Body (JSON):**
```json
{
    "email": "string",
    "balance": {
        "currency": "string",
        "amount": number
    }
}
```
**Request Body (JSON): Sample**
```json
{
  "email": "l.s.v3@gmail.com",
  "balance": {
    "currency": "SEK",
    "amount": 10000.12
  }
}
```
**Response Body (JSON): Sample**
```json
{
  "status": "SUCCESS",
  "message": "Amount credited successfully"
}
```
---

### 4. Debit Balance
**Endpoint:**
```
PUT v1/account/debitBalance
```
**Description:**
Deducts a specified amount from a customer's account balance.

**Request Body (JSON):**
```json
{
    "email": "string",
    "balance": {
        "currency": "string",
        "amount": number
    }
}
```
**Request Body (JSON): Sample**
```json
{
  "email": "l.s.v3@gmail.com",
  "balance": {
    "currency": "SEK",
    "amount": 10000.00
  }
}
```
**Response Body (JSON): Sample**
```json
{
  "status": "SUCCESS",
  "message": "Amount debited successfully"
}
```
---

### 5. Get Account Balance
**Endpoint:**
```
GET v1/account/getBalance
```
**Description:**
Retrieves the balance details of a customer's account.

**Request Body (JSON):**
```json
{
    "email": "string"
}
```
**Request Body (JSON): Sample**
```json
{
  "email": "l.s.v3@gmail.com"
}
```
**Response Body (JSON): Sample**
```json
{
  "currentBalance": [
    {
      "currency": "SEK",
      "amount": 21200.12
    },
    {
      "currency": "USD",
      "amount": 1000.00
    }
  ]
}
```
---

### 6. Currency Exchange Calculation
**Endpoint:**
```
GET v1/account-balance/getExchangeAmount
```
**Description:**
Calculates the exchange amount for a given currency conversion request.

**Request Body (JSON):**
```json
{
    "email": "string",
    "baseCurrency": "string",
    "targetCurrency": "string",
    "amount": number,
    "isExchangeFullBalance": boolean
}
```
**Request Body (JSON): Sample**
```json
{
  "email": "l.s.v3@gmail.com",
  "baseCurrency": "USD",
  "targetCurrency": "EUR",
  "amount": 12123923,
  "isExchangeFullBalance": false
}
```
**Note: `isExchangeFullBalance: boolean`** //if it true system will exchange complete balance in baseCurrency and if it is false system will exchange request amount only

**Response Body (JSON): Sample**
```json
{
  "baseCurrency": {
    "currency": "EUR",
    "amount": 1000.00
  },
  "targetCurrency": {
    "currency": "SEK",
    "amount": 12340.000000
  }
}
```
### 7. Currency Exchange create
**Endpoint:**
```
GET v1/exchange-rate/create
```
**Description:**
Calculates the exchange amount for a given currency conversion request.

**Request Body (JSON):**
```json
{
  "baseCurrency":"String",
  "targetCurrency":"String",
  "exchangeRate":90.00
}
```
**Request Body (JSON): Sample**
```json
{
  "baseCurrency":"USD",
  "targetCurrency":"EUR",
  "exchangeRate":90.00
}
```
**Response Body (JSON): Sample**
```json
{
  "status": "SUCCESS",
  "message": "Exchange rate added"
}
```
---






