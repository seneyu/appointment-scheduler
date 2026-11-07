# Appointment Scheduler
A Spring Boot REST API for scheduling dental appointments, built on Spring Boot fundamentals: layered architecture, JPA relationships, DTOs, validation, and centralized error handling.

Status: work in progress. Create, update, and delete flows work. Overlapping appointments conflict detection works. Unit tests cover the appointment service layer and the mapper.

## Tech Stack
- Java 21, Maven
- Spring Boot, Spring Web, Spring Data JPA, Validation
- PostgreSQL (Docker Compose)
- Testing: JUnit 5, Mockito

## Quickstart
1. Git clone this repo. 
2. Copy `.env.example` to `.env`. Set `DB_USER` and `DB_PASSWORD` - local development credentials only.
3. Start the database: `docker compose up -d`
4. Run the app: `./mvnw spring-boot:run`

## API
Base path: `/api/v1`. Example requests for every endpoint are in [requests.http](requests.http).

| Method | Path | Description |
|--------|------|-------------|
| GET | `/patients` | List all patients |
| GET | `/patients/{id}` | Get a patient |
| POST | `/patients` | Create a patient |
| PUT | `/patients/{id}` | Update a patient |
| DELETE | `/patients/{id}` | Delete a patient |
| GET | `/appointments` | List all appointments |
| GET | `/appointments/{id}` | Get an appointment |
| POST | `/appointments` | Create an appointment (status set to `SCHEDULED`) |
| PUT | `/appointments/{id}` | Update an appointment |
| PATCH | `/appointments/{id}/cancel` | Cancel an appointment |
| PATCH | `/appointments/{id}/confirm` | Confirm an appointment |
| PATCH | `/appointments/{id}/complete` | Complete an appointment |
| DELETE | `/appointments/{id}` | Delete an appointment |

## Possible Improvements
- Safe concurrent bookings
- Authentication and role-based access