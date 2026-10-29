# Clinic Appointment Scheduler

A comprehensive clinic management system with appointment scheduling capabilities, built with JavaFX for the frontend and Spring Boot for the REST API backend.

## Features

- Patient management
- Doctor management
- Appointment scheduling
- Doctor schedule management
- RESTful API integration
- Modern UI with responsive design

## Technologies Used

- **Frontend**: JavaFX
- **Backend**: Spring Boot
- **Database**: MySQL
- **ORM**: Hibernate (JPA)
- **Build Tool**: Maven

## Project Structure

```
clinic-appointment-scheduler/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/clinic/app/
│   │   │       ├── Controllers/          # JavaFX controllers
│   │   │       ├── Models/               # JavaFX models
│   │   │       ├── data/                 # Database access layer
│   │   │       │   ├── repositories/     # Repository classes
│   │   │       │   └── services/         # Service classes
│   │   │       ├── entities/             # JPA entities
│   │   │       ├── services/             # Spring services
│   │   │       ├── controllers/api/      # REST controllers
│   │   │       ├── utils/                # Utility classes
│   │   │       └── ClinicApiApplication.java  # Spring Boot main class
│   │   └── resources/
│   │       ├── css/                      # Stylesheets
│   │       ├── database/                 # Database scripts
│   │       ├── fxml/                     # FXML layout files
│   │       └── application.properties    # Spring Boot configuration
│   └── test/                             # Test classes
├── API_DOCUMENTATION.md                  # API documentation
├── EXTERNAL_API_INTEGRATION.md           # External API integration documentation
├── test_external_api.bat                 # Windows test script for external API
├── test_external_api.sh                  # Linux/Mac test script for external API
├── pom.xml                               # Maven configuration
└── README.md                             # This file
```

## REST API Endpoints

The application provides the following REST API endpoints:

1. `GET /api/appointments/{id}` - Get appointment by ID
2. `POST /api/appointments` - Create a new appointment
3. `GET /api/doctor-availability` - Get doctor availability for a specific date
4. `POST /api/send-reminders` - Send appointment reminders via external API

See [API_DOCUMENTATION.md](API_DOCUMENTATION.md) for detailed API documentation.

## Getting Started

### Prerequisites

- Java 21 or higher
- MySQL database
- Maven

### Database Setup

1. Create a MySQL database named `clinic_db`
2. Update the database credentials in `src/main/resources/application.properties` if needed
3. Run the database setup script in `src/main/resources/database/setup.sql`

### Installation

1. Clone the repository
2. Navigate to the project directory
3. Run `mvn clean install` to build the project

### Running the Application

#### Running the Spring Boot API Server

```bash
mvn spring-boot:run
```

The API server will start on port 8080.

#### Running the JavaFX Application

```bash
mvn javafx:run
```

The JavaFX application will start and connect to the API server.

## External API Integration

The application includes optional integration with external email services for sending appointment reminders. This feature demonstrates how the system can connect to real-world services like SendGrid or Mailgun.

- **Endpoint**: `POST /api/send-reminders`
- **Implementation**: [ExternalReminderService](src/main/java/com/clinic/app/services/ExternalReminderService.java)
- **Documentation**: See [EXTERNAL_API_INTEGRATION.md](EXTERNAL_API_INTEGRATION.md) for details on how to integrate with real email services

The JavaFX application includes a "Send External Reminders" button that triggers this functionality.

## Testing the API

You can test the API endpoints using the provided test scripts:

- On Windows: Run `test_api.bat`
- On Linux/Mac: Run `test_api.sh`

Or use curl directly:

```bash
# Get appointment by ID
curl -X GET http://localhost:8080/api/appointments/1

# Get doctor availability
curl -X GET "http://localhost:8080/api/doctor-availability?doctorId=1&date=2025-10-15"

# Send external reminders
curl -X POST http://localhost:8080/api/send-reminders
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a pull request

## License

This project is licensed under the MIT License.

## Acknowledgments

- Thanks to all contributors who have helped with this project
- Special thanks to the JavaFX and Spring Boot communities