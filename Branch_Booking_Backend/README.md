
# Capitec Appointment Booking – Backend

## Docker deployment (repository-level Compose)

For the full-stack Docker deployment, run Compose from the repository root (the directory containing `docker-compose.yml`), not from this backend directory.

```bash
# From repository root
docker compose up --build -d
docker compose ps
docker compose logs -f backend
```

The backend service is exposed on `http://localhost:8087` by default. The Docker profile is activated with `SPRING_PROFILES_ACTIVE=docker`; it reads database and JWT settings from environment variables and connects to MySQL at `mysql:3306` on the Compose network. Do not use `localhost` as the database hostname from inside the backend container.

The root `.env` file supplies `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, and `JWT_EXPIRATION`. Replace example values before deployment and keep real secrets out of source control. See the repository's [Docker deployment guide](../README-Docker.md) for complete setup, operations, persistence, and troubleshooting.

## 1. Project Overview

Capitec Appointment Booking is a Spring Boot REST API application that provides authentication and appointment management functionality for customers.

The application uses JWT-based authentication to secure REST APIs and MySQL as the relational database.

### Key Features

- Customer authentication using email and password.
- JWT token generation and validation.
- Secure REST APIs using Spring Security.
- Appointment booking and management.
- Customer information management.
- Department information management.
- MySQL database integration.
- Spring Data JPA for database operations.

## 2. Technology Stack

| Technology | Version |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.2 |
| Spring Security | Spring Boot managed |
| Spring Data JPA | Spring Boot managed |
| Spring Web MVC | Spring Boot managed |
| MySQL | 8.x |
| Maven | 3.9+ |
| JWT (JJWT) | 0.12.6 |

### Dependencies

The application uses the following Maven dependencies:

- spring-boot-starter-data-jpa
- spring-boot-starter-security
- spring-boot-starter-webmvc
- mysql-connector-j
- spring-security-crypto
- jjwt-api
- jjwt-impl
- jjwt-jackson

## 3. Prerequisites

Ensure the following software is installed:

- JDK 21
- Apache Maven 3.9+
- MySQL Server 8.x
- IDE (IntelliJ IDEA / Eclipse / Spring Tool Suite)

Verify installations:

```bash
java -version
mvn -version
mysql --version
```

## 4. Database Configuration

### Database Details

| Property | Value |
|---|---|
| Database | MySQL |
| Schema Name | capitec |
| Database Engine | InnoDB |
| Character Set | utf8mb4 |
| Collation | utf8mb4_0900_ai_ci |

### Database Schema

The application currently uses three database tables.

#### 1. user

Stores customer authentication and profile information.

| Column | Data Type | Description |
|---|---|---|
| ID | INT | Primary Key |
| name | VARCHAR(255) | Customer Name |
| PASSWORD | VARCHAR(255) | Encrypted Password |
| email | VARCHAR(255) | Customer Email |
| mobile_number | INT | Mobile Number |
| CREATED_DATE | DATETIME | Record Creation Date |
| created_by | VARCHAR(255) | Created By |
| UPDATED_DATE | DATETIME | Last Updated Date |
| updated_by | VARCHAR(255) | Updated By |

Constraints:
- ID is the primary key.
- Email must be unique.
- Mobile number must be unique.

#### 2. appointment

Stores customer appointment information.

| Column | Data Type | Description |
|---|---|---|
| ID | INT | Primary Key |
| appointment_type | VARCHAR(255) | Appointment Purpose |
| USER_ID | INT | Customer Reference |
| APPOINTMENT_DATE_TIME | DATETIME | Appointment Date and Time |
| CREATED_DATE | DATETIME | Record Creation Date |
| created_by | VARCHAR(255) | Created By |
| updated_by | VARCHAR(255) | Updated By |
| UPDATED_DATE | DATETIME | Last Updated Date |

Constraints:
- ID is the primary key.
- USER_ID references user.ID.

#### 3. department

Stores department information.

| Column | Data Type | Description |
|---|---|---|
| ID | INT | Primary Key |
| NAME | VARCHAR(45) | Department Name |
| ADDRESS | VARCHAR(45) | Department Address |
| CREATED_DATE | DATETIME | Record Creation Date |
| CREATED_BY | VARCHAR(45) | Created By |
| UPDATED_DATE | DATETIME | Last Updated Date |
| UPDATED_BY | VARCHAR(45) | Updated By |

## 5. Database Setup

### Step 1: Create Database

Execute the following SQL:

```sql
CREATE DATABASE capitec;

USE capitec;
```

### Step 2: Create Tables

Execute the supplied SQL scripts for:

1. user
2. department
3. appointment

Ensure the tables are created in the above order because appointment references user.

### Step 3: Insert Initial Customer

```sql
INSERT INTO capitec.user
(
    ID,
    name,
    PASSWORD,
    email,
    mobile_number,
    CREATED_DATE,
    created_by
)
VALUES
(
    1,
    'customer',
    '$10$uB8BXBnCo40RUPhgMmIth./RUnc6LI65y6yzdLntSrDlcKRvtqZB.',
    'admin@gmail.com',
    9876544,
    '2026-11-18 18:16:52',
    'SYSTEM'
);
```

The password value is a pre-generated password hash. Use the corresponding original password when testing authentication.

## 6. Application Configuration

### Docker configuration

Docker Compose activates the `docker` Spring profile and uses
`src/main/resources/application-docker.properties`. Compose injects the datasource
URL, username, password, JWT secret, and expiration as environment variables.
The container listens on port `8087` and connects to the database service at
`mysql:3306`.

### Local (non-Docker) configuration

When running the backend directly on your host, configure
`src/main/resources/application.properties` for your local MySQL installation.
The following is an example only; adapt the port and credentials to your setup.

```properties
server.port=8087
spring.datasource.url=jdbc:mysql://localhost:3307/capitec
spring.datasource.username=${DB_USERNAME:capitec}
spring.datasource.password=${DB_PASSWORD}
```


Note:
- Update database credentials according to your local MySQL configuration.
- Keep database passwords outside source control.
- `ddl-auto=validate` verifies that entity mappings match the existing database schema without automatically modifying tables.

## 7. Authentication – JWT

The application uses Spring Security and JSON Web Tokens (JWT) for customer authentication.

### Login API

**Endpoint:**

```http
POST /api/auth/login
```

**Request:**

```json
{
  "email": "admin@gmail.com",
  "password": "your-password"
}
```

**Successful Response:**

```json
{
  "token": "<JWT_TOKEN>",
  "user": {
    "email": "admin@gmail.com"
  }
}
```

The JWT token is returned to the Angular frontend and stored in browser local storage.

### JWT Authorization

Protected APIs require the following HTTP header:

```http
Authorization: Bearer <JWT_TOKEN>
```

Spring Security validates the JWT before allowing access to protected endpoints.

## 8. Appointment Booking API

### Create Appointment

**Endpoint:**

```http
POST /api/appointments
```

**Authorization:**

```http
Authorization: Bearer <JWT_TOKEN>
```

**Content-Type:**

```http
Content-Type: application/json
```

The API accepts appointment information and associates the appointment with the authenticated customer.

The backend should derive the customer identity from the authenticated JWT rather than trusting a USER_ID supplied by the frontend.

## 9. Running the Application

### Docker deployment (recommended)

Run from the repository root:

```bash
docker compose up --build -d
docker compose logs -f backend
```

The backend is available directly at `http://localhost:8087`. In the full-stack
deployment, the frontend sends `/api/...` requests through Nginx, which proxies
them to the backend container on port `8087`.

To stop the stack without deleting database data:

```bash
docker compose down
```

### Run backend directly on the host (local development)

Start a local MySQL instance, create the `capitec` schema, configure
`application.properties`, then run:

```bash
mvn clean test
mvn spring-boot:run
```

The local backend listens on `http://localhost:8087` by default. This host-based
workflow is separate from the Docker Compose workflow.

## 10. Maven Commands

| Command | Description |
|---|---|
| `mvn clean` | Removes previous build output |
| `mvn compile` | Compiles source code |
| `mvn test` | Executes unit tests |
| `mvn clean install` | Builds and installs the application |
| `mvn spring-boot:run` | Starts Spring Boot application |
| `mvn package` | Generates executable JAR |

### Run Executable JAR

```bash
java -jar target/deportment-0.0.1-SNAPSHOT.jar
```

## 11. Frontend Integration

The backend is designed to integrate with the Angular 20 frontend.

| Application | URL |
|---|---|
| Angular Frontend | http://localhost:4200 |
| Spring Boot Backend | http://localhost:8087 |
| Login API | /api/auth/login |
| Appointment API | /api/appointments |

The Angular application communicates with the backend through HTTP REST APIs.

During local development, Angular Proxy Configuration can be used to forward `/api` requests to the Spring Boot backend.

## 12. Security Considerations

- Passwords must be stored using a secure password hashing algorithm such as BCrypt.
- Never store plain-text passwords.
- JWT signing secrets must be stored securely outside source control.
- Validate JWT signature and expiration.
- Protect appointment APIs using Spring Security.
- Derive customer identity from the authenticated principal.
- Use HTTPS in production.
- Never expose database credentials in Git repositories.
- Do not expose sensitive customer information in API responses.

## 13. Future Enhancements

Potential enhancements include:

- Appointment rescheduling and cancellation.
- Appointment history.
- Department and branch management.
- Email appointment confirmation.
- Role-based authorization.
- Refresh token implementation.
- Centralized exception handling.
- API documentation using OpenAPI / Swagger.
- Unit and integration testing.

---

**Project:** Capitec Appointment Booking  
**Artifact:** deportment  
**Framework:** Spring Boot 4.1.2  
**Language:** Java 21  
**Database:** MySQL  
**Security:** Spring Security + JWT  
**Frontend:** Angular 20
