# java-shareit

A multi-module Spring Boot app for items sharing, booking and request management.

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.4-green.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.6-red.svg)](https://maven.apache.org)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.4-blue.svg)](https://www.postgresql.org)
[![Docker](https://img.shields.io/badge/Docker-27.4-2496ED.svg?logo=docker)](https://www.docker.com)

## Features

### Core Functionality
- **User Management**
    - CRUD operations
    - User validation and error handling
- **Item Sharing**
    - Add items with availability status
    - Search items by text query
    - Manage item ownership
- **Booking System**
    - Create bookings with time slots
    - Approve/reject booking requests
    - View booking history
- **Request Management**
    - Create item requests
    - View requests with answers
    - Track request status

### Storage Options
- **Persistent Storage**
    - PostgreSQL database for production
    - JPA repositories for data access
- **In-Memory Storage**
    - Optional in-memory implementations

### REST API
- Comprehensive endpoints for all entities
- JSON request/response format
- Proper HTTP status codes, and validation

### Additional Features
- Extensive error handling
- Logging for all operations
- Layered architecture

## Getting Started

### Prerequisites
- Java 21 or later
- Maven 3.6 or later
- PostgreSQL 17.4 or later (optional, for production)
- Docker 27.4.0 or later (optional, for containerized deployment)

### Clone the Repository
```sh
git clone git@github.com:DawydowGerman/java-shareit.git
  ```
  ```sh
cd java-shareit
  ```

### Build with Maven
  ```sh
  mvn clean package
  ```

### Run the Application

- Option 1: With PostgreSQL (default)
  ```sh
  mvn spring-boot:run
  ```

- Option 2: With Docker
  ```sh
  docker-compose up --build
  ```