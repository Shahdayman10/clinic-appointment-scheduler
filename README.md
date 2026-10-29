# Clinic Appointment Scheduler

A Java desktop application for managing clinic appointments, built with JavaFX and integrated with a Spring Boot REST API and MySQL database.

This project demonstrates desktop application development, database integration, appointment management, and REST API concepts.

## Application Screenshots

### Welcome Screen
![Welcome Screen](screenshots/welcome-screen.jfif)

### Login Screen
![Login Screen](screenshots/login-screen.jfif)

### Sign Up Screen
![Sign Up Screen](screenshots/signup-screen.jfif)

### Dashboard Screen
![Dashboard Screen](screenshots/dashboard.jfif)

### Patients Screen
![Patients Screen](screenshots/5.jfif)

### Doctors Screen
![Doctors Screen](screenshots/6.jfif)

### Appointments Screen
![Appointments Screen](screenshots/7.jfif)

## Features

- **Patient Management:** Manage patient information.
- **Doctor Management:** Organize doctor information.
- **Appointment Management:** Create and manage clinic appointments.
- **Doctor Availability:** Retrieve doctor availability through the API.
- **Desktop Interface:** JavaFX screens for interacting with the application.
- **REST API:** HTTP endpoints for appointment-related operations.
- **External Reminder Integration:** Example integration structure for appointment reminders.

*Available features depend on the application configuration, database setup, and external service configuration.*

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 21 | Application development |
| JavaFX | Desktop user interface |
| Spring Boot | REST API |
| MySQL | Database |
| Hibernate / JPA | Database persistence |
| Maven | Dependency and build management |

## Application Screens

The JavaFX interface includes the following screens:

- Welcome
- Login
- Sign Up
- Dashboard
- Patients
- Doctors
- Appointments
- Schedule

The corresponding FXML files are located in `src/main/resources`.

## Project Structure

```text
clinic-appointment-scheduler/
├── src/
│   └── main/
│       ├── java/              # Java source code
│       └── resources/         # FXML, properties, and resources
├── .gitignore
├── pom.xml
├── README.md
├── API_DOCUMENTATION.md
├── EXTERNAL_API_INTEGRATION.md
├── update_doctors_table.sql
├── test_api.bat
├── test_api.sh
├── test_external_api.bat
└── test_external_api.sh
```

## Prerequisites

Before running the project, make sure you have:

- JDK 21 installed.
- Apache Maven installed.
- MySQL Server installed and running.
- A JavaFX-compatible environment.
- A MySQL database configured for the application.

## Database Configuration

The application uses a MySQL database named `clinic_db` in its local development configuration.

1. Start MySQL.
2. Create the `clinic_db` database if it does not exist.
3. Review `src/main/resources/application.properties`.
4. Review `src/main/resources/META-INF/persistence.xml`.
5. Configure the database URL, username, and password for your local environment.
6. Ensure the database tables required by the application exist before running it.

**Important:** The configuration files currently contain local development settings. Update them as needed for your own environment. Do not use real patient information when testing or demonstrating the project.

The database setup SQL file is not included in this public repository version. You may need to create the required schema separately.

## Build the Project

From the project root directory, run:

```bash
mvn clean package -DskipTests
```

This builds the project while skipping automated tests. A successful build does not guarantee that every application feature has been tested at runtime.

## Run the Desktop Application

After configuring the database, run:

```bash
mvn javafx:run
```

The JavaFX Maven plugin is configured to launch:

```text
com.clinic.app.App
```

Make sure your local Java and database environments are configured before starting the application.

## REST API Documentation

The project includes a Spring Boot REST API.

For additional endpoint details, request formats, and parameters, see [API_DOCUMENTATION.md](API_DOCUMENTATION.md).

### Example Endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| GET | `/api/appointments/{id}` | Retrieve an appointment by ID |
| POST | `/api/appointments` | Submit an appointment request |
| GET | `/api/doctor-availability` | Retrieve doctor availability |
| POST | `/api/send-reminders` | Trigger the appointment reminder workflow |

The examples below assume the API server is running at `http://localhost:8080`. The exact API startup procedure depends on the project's Spring Boot configuration.

### Example API Requests

**Get an appointment by ID**

```bash
curl http://localhost:8080/api/appointments/1
```

**Check doctor availability**

```bash
curl "http://localhost:8080/api/doctor-availability?doctorId=1&date=2025-10-15"
```

**Trigger appointment reminders**

```bash
curl -X POST http://localhost:8080/api/send-reminders
```

These commands demonstrate request formats. Successful responses depend on the API implementation, server status, database configuration, and available records. You may need to replace the sample appointment ID or date with values that exist in your database.

## External Reminder Integration

The project includes documentation about its external reminder integration approach.

See [EXTERNAL_API_INTEGRATION.md](EXTERNAL_API_INTEGRATION.md) for more information.

External reminder delivery may require additional configuration and valid credentials from the relevant service provider. Placeholder values are not real credentials and do not confirm that messages can be delivered.

**Security note:** Never publish API keys, passwords, private tokens, or other credentials in source code or public repositories.

## Testing

The repository includes helper scripts for API-related checks:

- `test_api.bat`
- `test_api.sh`
- `test_external_api.bat`
- `test_external_api.sh`

Their results depend on the API server, database, and external service configuration. Review each script before running it.

No automated test files were found in `src/test` during the repository review.

## Current Scope and Possible Improvements

Possible future improvements include:

- Adding automated unit and integration tests.
- Improving validation and error handling.
- Using environment variables for database credentials and external API keys.
- Adding screenshots and a demonstration video.
- Expanding API documentation with example responses.
- Improving deployment and setup instructions.

## Documentation

- [API Documentation](API_DOCUMENTATION.md)
- [External API Integration](EXTERNAL_API_INTEGRATION.md)

## Notes

This repository is intended to showcase a JavaFX desktop application and its related API and database integration. Setup requirements may vary depending on the local environment.
