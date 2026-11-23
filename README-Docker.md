# Capitec Branch Booking — Docker deployment

This repository runs the Angular frontend, Spring Boot API, and MySQL database as three Docker Compose services.

## Architecture and URLs

| Service | Container/service name | Host URL / port |
|---|---|---|
| Angular frontend (Nginx) | `frontend` | http://localhost:4200 |
| Spring Boot API | `backend` | http://localhost:8087 |
| MySQL 8.4 | `mysql` | `127.0.0.1:3307` |

Inside the Compose network, the backend connects to MySQL at `mysql:3306`. Nginx forwards browser requests under `/api/` to `backend:8087`; therefore, the browser uses the frontend origin and the Angular app keeps its relative `/api/...` paths. MySQL data and backend logs are stored in named volumes (`mysql_data` and `backend_logs`).

## Prerequisites

- Docker Desktop with Docker Compose, or Docker Engine with the Compose plugin.
- At least 4 GB RAM available to Docker.
- Host ports `4200`, `8087`, and `3307` available (or change the corresponding variables in `.env`).

Check your installation:

```bash
docker --version
docker compose version
```

## Configure environment

The project archive includes a `.env` file with example values. Before starting:

1. Open `.env` in the repository root.
2. Replace `DB_PASSWORD` and `MYSQL_ROOT_PASSWORD` with strong, unique passwords.
3. Set `JWT_SECRET` to a cryptographically random secret of at least 32 characters.
4. Keep `.env` private and out of source control. Do not use the example values in a public or production deployment.

The variables and defaults used by Compose are:

| Variable | Default | Purpose |
|---|---:|---|
| `DB_USERNAME` | `capitec` | MySQL application user |
| `DB_PASSWORD` | example value in `.env` | MySQL application password |
| `MYSQL_ROOT_PASSWORD` | example value in `.env` | MySQL root password |
| `JWT_SECRET` | placeholder in `.env` | JWT signing secret |
| `JWT_EXPIRATION` | `3600000` | JWT lifetime in milliseconds |
| `FRONTEND_PORT` | `4200` | Host port mapped to Nginx port 80 |
| `BACKEND_PORT` | `8087` | Host port mapped to API port 8087 |
| `MYSQL_PORT` | `3307` | Host port mapped to MySQL port 3306 |

Compose can fall back to the listed defaults if a variable is absent. Configure the `.env` file rather than relying on those defaults.

## Build and start

Run these commands from the directory containing `docker-compose.yml`:

```bash
docker compose config
docker compose up --build -d
docker compose ps
```

The first build may take several minutes while Docker downloads the Maven, Node.js, Nginx, and MySQL images and builds the application images. The backend is configured to wait until the MySQL health check reports healthy.

Open the application:

- Frontend: http://localhost:4200
- API base URL (direct host access): http://localhost:8087/api

The API endpoints used by the frontend include `/api/auth/login` and `/api/appointments`. When using the browser UI, Nginx proxies those paths to the backend container.

## Database initialization and persistence

On the first initialization of an empty MySQL data directory, scripts mounted from `database/init` are executed by the MySQL image. The database is named `capitec`.

The database contents persist in the `mysql_data` named volume across container restarts and `docker compose down`. Initialization scripts are not re-run against an already initialized volume. To deliberately recreate the database from scratch, remove the volume (this deletes its data):

```bash
docker compose down -v
docker compose up --build -d
```

The backend uses the Spring `docker` profile and `application-docker.properties`. It connects to the Compose DNS name `mysql`, not `localhost`, and Hibernate is configured with `ddl-auto=update`. Review schema migration and backup requirements before production use.

For a database client running on the host, use host `127.0.0.1`, port `3307` (or your configured `MYSQL_PORT`), database `capitec`, and the credentials from `.env`.

## Common operations

```bash
# View service status
docker compose ps

# Follow all service logs
docker compose logs -f

# Follow one service
docker compose logs -f frontend
docker compose logs -f backend
docker compose logs -f mysql

# Restart services
docker compose restart

# Stop containers; named volumes are retained
docker compose down

# Rebuild images after changing application source or Dockerfiles
docker compose up --build -d

# Stop containers and delete persistent volumes (DESTRUCTIVE)
docker compose down -v
```

## Troubleshooting

**Frontend loads but API requests fail**
- Check `docker compose ps` and `docker compose logs backend`.
- Check `docker compose logs frontend` for Nginx errors.
- Confirm the backend completed startup and MySQL is healthy.
- Confirm the frontend's Nginx configuration proxies `/api/` to `backend:8087`.

**Backend cannot connect to MySQL**
- Check `docker compose logs mysql` and `docker compose logs backend`.
- Confirm the database credentials in `.env` match the values used to initialize the MySQL volume.
- If credentials were changed after the volume was created, changing `.env` alone does not update the existing MySQL user's password. Update the database user or intentionally recreate the volume (data loss).

**A host port is already in use**
- Change `FRONTEND_PORT`, `BACKEND_PORT`, or `MYSQL_PORT` in `.env`.
- Recreate the services with `docker compose up -d`.

**The database does not contain expected initial records**
- The Compose setup mounts `database/init` for first-time initialization.
- No application seed users or sample booking data should be assumed unless included in those SQL scripts.
- Existing named volumes are not reinitialized automatically.

## Development versus Docker networking

- `Branch_Booking_FrontEnd/proxy.conf.json` is for running Angular's development server on the host. Its target is `http://localhost:8087`.
- The Docker frontend is a production Angular build served by Nginx. Docker API routing is configured in `Branch_Booking_FrontEnd/nginx.conf`; it routes to `http://backend:8087`.
- The backend's Docker profile uses the Compose service hostname `mysql`. Do not change it to `localhost` inside a container.

## Security and production notes

This Compose configuration is suitable as a local deployment baseline, not a complete production-hardening package.

- Replace every example password and secret before starting; never commit a real `.env`.
- Use a secret manager or deployment platform secrets for production credentials.
- Put public traffic behind HTTPS and an appropriately configured reverse proxy/load balancer.
- Restrict database exposure. The current Compose file publishes MySQL on the host port configured by `MYSQL_PORT`; remove that port mapping if host access is not required.
- Back up the MySQL volume and test restore procedures.
- Review JWT lifetime/key rotation, access control, CORS, logging, database migrations, and container image update policies.
- The backend currently uses Hibernate `ddl-auto=update`; use a controlled migration strategy for production.
- Review `application.properties` separately before running the backend outside Docker; the Docker profile overrides it only when `SPRING_PROFILES_ACTIVE=docker` is active.

## Repository layout

```text
.
├── docker-compose.yml
├── .env
├── README.md
├── README-Docker.md
├── database/
│   └── init/
├── Branch_Booking_Backend/
│   ├── Dockerfile
│   └── src/main/resources/application-docker.properties
└── Branch_Booking_FrontEnd/
    ├── Dockerfile
    └── nginx.conf
```
