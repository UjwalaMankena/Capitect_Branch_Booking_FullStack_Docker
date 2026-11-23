
# Booking Frontend

## Docker deployment (repository-level Compose)

The recommended full-stack deployment builds Angular for production and serves it
with Nginx. Run the following commands from the repository root (the directory
containing `docker-compose.yml`), not from this frontend directory:

```bash
docker compose up --build -d
docker compose ps
docker compose logs -f frontend
```

Open the application at `http://localhost:4200` by default. The Nginx configuration
in `nginx.conf` serves the Angular app and forwards `/api/` requests to
`http://backend:8087/` over the Docker Compose network. The browser should use
relative `/api/...` URLs; it should not call the Docker-only hostname `backend`.

Change `FRONTEND_PORT` in the root `.env` file to change the host port. See the
repository's [Docker deployment guide](../README-Docker.md) for full-stack setup,
environment configuration, logs, and troubleshooting.

An Angular-based banking appointment booking application that allows customers to authenticate, select banking services, and schedule appointments at their preferred bank branch.

## Technology Stack

| Technology | Version |
|---|---|
| Angular | 20.3.35 |
| Angular CLI | 20.3.43 |
| TypeScript | ~5.8.0 |
| RxJS | ~7.8.1 |
| Bootstrap | 5.3.3 |
| Bootstrap Icons | 1.11.3 |
| ng-bootstrap | 19.0.1 |
| Node.js | Compatible with Angular 20 |
| npm | Compatible with Node.js |

## Prerequisites

Ensure the following are installed:

- Node.js (Angular 20 compatible version)
- npm
- Angular CLI 20
- Java Spring Boot backend (for API integration)

Install Angular CLI:

```bash
npm install -g @angular/cli@20.3.43
```

Verify installations:

```bash
node -v
npm -v
ng version
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
```

### 2. Navigate to Project

```bash
cd booking-frontend
```

### 3. Install Dependencies

```bash
npm install
```

### 4. Start Development Server

```bash
npm start
```

Alternatively:

```bash
ng serve
```

Open the application:

http://localhost:4200/

## Available NPM Commands

| Command | Description |
|---|---|
| `npm start` | Starts Angular development server |
| `npm run build` | Creates production build |
| `npm run watch` | Builds continuously in development mode |
| `npm run ng` | Executes Angular CLI commands |

### Production Build

```bash
npm run build
```

Build output is generated under:

```text
dist/
```

## Application Architecture

The application uses Angular services to separate API communication and business logic from UI components.

```text
src/
 └── app/
     ├── services/
     │   ├── auth.service.ts
     │   └── booking.service.ts
     │
     ├── components/
     │   ├── login/
     │   └── booking/
     │
     ├── app.component.ts
     ├── app.config.ts
     └── app.routes.ts
```

## Authentication

The application uses JWT-based authentication.

### Login API

```http
POST /api/auth/login
```

Request:

```json
{
  "email": "customer@example.com",
  "password": "password"
}
```

The authentication service stores the JWT token and username in browser local storage.

Storage keys:

```text
access_token
username
```

The token is used for authenticated API requests.

### Authorization Header

Protected API requests use:

```http
Authorization: Bearer <JWT_TOKEN>
```

## Appointment Booking

Customers can schedule appointments by selecting:

- Banking service / purpose
- Preferred branch
- Appointment date
- Appointment time
- Customer contact details

### Create Appointment API

```http
POST /api/appointments
```

Authorization:

```http
Authorization: Bearer <JWT_TOKEN>
```

Content-Type:

```http
Content-Type: application/json
```

The booking service handles API communication and returns the appointment confirmation or an error.

## API Configuration

### Docker / production container

The Docker image is a production Angular build served by Nginx. The Nginx
configuration in `nginx.conf` forwards requests matching `/api/` to
`http://backend:8087/api/` on the Compose network. Angular services should use
relative paths such as:

```typescript
private apiUrl = '/api/auth';
```

```typescript
private apiUrl = '/api/appointments';
```

Do not configure the production browser app to call `http://backend:8087`
directly; `backend` is a Docker-internal service name and is resolved by Nginx
inside the Compose network.

### Local Angular development server

`proxy.conf.json` is only for the host-based Angular development server. It
forwards `/api` to `http://localhost:8087`. Start the backend on the host at
port `8087`, then run:

```bash
npm start
```

The development server is normally available at `http://localhost:4200`.
The Docker deployment does not use Angular's development proxy; it uses Nginx.

## Error Handling

The application handles common HTTP errors:

| HTTP Status | Description |
|---|---|
| 0 | Network or connectivity issue |
| 400 | Invalid request |
| 401 | Unauthorized / Invalid JWT |
| 403 | Forbidden |
| 404 | API endpoint not found |
| 500 | Internal server error |

Users receive an appropriate error message when API requests fail.

## Running the Application

### Docker deployment

Run from the repository root:

```bash
docker compose up --build -d
docker compose logs -f frontend
```

Open `http://localhost:4200`. The frontend is served by Nginx on container port
80; Compose maps the host port configured by `FRONTEND_PORT` (default `4200`).

### Local development (without Docker)

Ensure the backend is running at `http://localhost:8087`, then from this
frontend directory run:

```bash
npm ci
npm start
```

Open `http://localhost:4200`. The Angular development proxy forwards API calls
to the host backend.

## Troubleshooting

### 1. CORS Error

If you encounter CORS errors:

- Verify that `proxy.conf.json` exists.
- Verify that Angular is configured to use the proxy.
- Restart the Angular development server.
- Ensure the Spring Boot backend is running.

### 2. 401 Unauthorized

- Verify that the user is logged in.
- Check whether `access_token` exists in local storage.
- Verify that the JWT is being sent in the Authorization header.
- Check token expiration.

### 3. API Connection Failed

Verify that the Spring Boot backend is accessible:

```text
http://localhost:8087
```

Check the browser Network tab and backend logs for additional details.

## Security Considerations

- Do not hardcode credentials or JWT secrets in Angular.
- Use HTTPS in production.
- Validate JWT tokens on the backend.
- Protect sensitive APIs using Spring Security.
- Avoid storing sensitive customer information in local storage.
- Consider a more secure token storage strategy for production.

## Development Notes

- Angular services manage API communication.
- Reactive Forms handle customer input and validation.
- JWT authentication protects secured API endpoints.
- Angular signals maintain authentication state.
- RxJS handles asynchronous HTTP operations.

---

**Project:** Booking Frontend  
**Framework:** Angular 20  
**Backend:** Spring Boot REST API  
**Authentication:** JWT
