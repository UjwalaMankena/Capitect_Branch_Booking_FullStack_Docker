# Capitec Branch Booking

Full-stack branch appointment booking application with an Angular frontend, Spring Boot REST API, and MySQL database.

## Run with Docker (recommended)

The repository root contains `docker-compose.yml` and the environment configuration file `.env`.

1. Edit `.env` and replace the example database passwords and JWT secret with strong values.
2. From this directory, run:

   ```bash
   docker compose up --build -d
   ```

3. Open http://localhost:4200.

The Compose deployment starts MySQL, the Spring Boot backend, and the Nginx-served Angular frontend. The frontend's Nginx reverse proxy forwards `/api/` requests to the backend service.

Useful commands:

```bash
docker compose ps
docker compose logs -f
docker compose down
```

**Warning:** `docker compose down -v` deletes the persistent MySQL volume and all database data.

See **[README-Docker.md](README-Docker.md)** for prerequisites, environment variables, service ports, database persistence, troubleshooting, and security notes.

## Services

| Component | Technology | Local URL |
|---|---|---|
| Frontend | Angular 20 + Nginx | http://localhost:4200 |
| Backend | Spring Boot 4 / Java 21 | http://localhost:8087 |
| Database | MySQL 8.4 | `127.0.0.1:3307` from host; `mysql:3306` from containers |

## Project directories

- `Branch_Booking_FrontEnd/` — Angular application and Nginx configuration.
- `Branch_Booking_Backend/` — Spring Boot API and Dockerfile.
- `database/init/` — SQL scripts mounted for first-time MySQL initialization.
- `docker-compose.yml` — multi-service deployment definition.
- `.env` — local deployment settings; keep real secrets private.

## Run components without Docker

For local development instructions, see:
- [Backend README](Branch_Booking_Backend/README.md)
- [Frontend README](Branch_Booking_FrontEnd/README.md)

The local development setup is separate from the Docker deployment. In Docker, the frontend is served by Nginx and API traffic is proxied to `backend:8087`.

##Mysql commands to connect from project root directory commandprompt

docker exec -it capitec-mysql mysql -u capitec -p

password : ChangeMe_db_12345

## data verification
select * from capitec.user;

select * from capitec.appointment;


