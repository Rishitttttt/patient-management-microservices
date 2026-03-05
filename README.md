# Patient Management Microservices System

A distributed backend system built using **Spring Boot microservices**, **Kafka (KRaft mode)**, **gRPC**, **JWT authentication**, **PostgreSQL**, and **Docker Compose**.

This project demonstrates a **production-style microservices architecture** where services communicate through:

* REST APIs
* gRPC service calls
* Event-driven messaging using Kafka

The system is fully containerized and can be started with a single command.

---

# Architecture Overview

The system consists of multiple independent services communicating through an **API Gateway**.

```
Client
   │
   ▼
API Gateway
   │
   ├── Auth Service ── PostgreSQL
   │
   ├── Patient Service ── PostgreSQL
   │        │
   │        ▼
   │   Billing Service (gRPC)
   │
   ▼
Kafka Event
   │
   ▼
Analytics Service
```

---

# Microservices

## API Gateway

Entry point for all client requests.

Responsibilities:

* Routing requests to internal services
* JWT token validation
* Centralized authentication
* Service communication abstraction

Port

```
4004
```

---

## Auth Service

Handles user authentication and JWT token generation.

Features

* Login authentication
* JWT token creation
* Token validation
* User persistence

Database

```
PostgreSQL
```

Port

```
4003
```

---

## Patient Service

Responsible for managing patient records.

Features

* Create patient
* Update patient
* Delete patient
* Retrieve patient records
* Publish Kafka events when patients are created

Additional integrations

* Calls **Billing Service using gRPC**
* Sends **Kafka events** for analytics

Port

```
4000
```

---

## Billing Service

Handles billing account creation.

Communication

```
gRPC
```

Flow

```
Patient Service → Billing Service
```

Port

```
4001
```

---

## Analytics Service

Consumes events from Kafka and processes analytics.

Example events

```
PatientCreatedEvent
```

Port

```
4002
```

---

# Event Driven Architecture

When a patient is created:

```
Patient Service
     │
     ▼
Kafka Event
     │
     ▼
Analytics Service
```

This allows services to remain loosely coupled.

---

# Technologies Used

Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Cloud Gateway

Communication

* REST APIs
* gRPC
* Apache Kafka (KRaft mode)

Database

* PostgreSQL

Infrastructure

* Docker
* Docker Compose

---

# Running the Project

Clone the repository

```
git clone https://github.com/Rishitttttt/patient-management-microservices.git
cd patient-management-microservices
```

Start the system

```
docker compose up --build
```

All services will start automatically.

---

# Stopping the System

```
docker compose down
```

---

# Service Ports

| Service           | Port |
| ----------------- | ---- |
| API Gateway       | 4004 |
| Auth Service      | 4003 |
| Patient Service   | 4000 |
| Billing Service   | 4001 |
| Analytics Service | 4002 |
| Kafka             | 9092 |

---

# Project Structure

```
api-gateway
auth-service
patient
billing-service
analytics-service
docker-compose.yml
```

---

# Example Flow

1. Client logs in through **Auth Service**
2. JWT token is returned
3. Client sends requests through **API Gateway**
4. Patient Service processes patient operations
5. Billing account is created using **gRPC**
6. Patient event is published to **Kafka**
7. Analytics Service consumes the event

---

# Author

Rishit
