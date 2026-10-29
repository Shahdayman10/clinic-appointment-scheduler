# Clinic Appointment Scheduler API Documentation

## Overview
This document provides detailed information about the RESTful API endpoints implemented in the Clinic Appointment Scheduler application. The API enables integration with external systems and provides data access for the JavaFX frontend.

## Base URL
```
http://localhost:8080/api
```

## API Endpoints

### 1. Get Appointment by ID
```
GET /api/appointments/{id}
```

**Description**: Retrieve a specific appointment by its ID.

**Parameters**:
- `id` (path parameter): The unique identifier of the appointment

**Responses**:
- `200 OK`: Successfully retrieved the appointment
- `404 Not Found`: Appointment with the specified ID does not exist
- `500 Internal Server Error`: An error occurred while processing the request

**Example Response**:
```json
{
  "id": 1,
  "patientId": 101,
  "patientName": "John Doe",
  "doctorId": 201,
  "doctorName": "Dr. Smith",
  "date": "2023-10-15",
  "time": "14:30",
  "status": "scheduled",
  "notes": "Regular checkup",
  "createdAt": "2023-10-01T10:30:00",
  "updatedAt": "2023-10-01T10:30:00"
}
```

### 2. Create Appointment
```
POST /api/appointments
```

**Description**: Create a new appointment.

**Request Body**:
```json
{
  "patientId": 101,
  "patientName": "John Doe",
  "doctorId": 201,
  "doctorName": "Dr. Smith",
  "date": "2023-10-15",
  "time": "14:30",
  "status": "scheduled",
  "notes": "Regular checkup"
}
```

**Responses**:
- `201 Created`: Appointment successfully created
- `400 Bad Request`: Invalid request data
- `409 Conflict`: Appointment already exists
- `500 Internal Server Error`: An error occurred while processing the request

### 3. Get Doctor Availability
```
GET /api/doctor-availability
```

**Description**: Retrieve a doctor's appointments for a specific date.

**Parameters**:
- `doctorId` (query parameter): The unique identifier of the doctor
- `date` (query parameter): The date in YYYY-MM-DD format

**Responses**:
- `200 OK`: Successfully retrieved the doctor's appointments
- `404 Not Found`: Doctor with the specified ID does not exist
- `500 Internal Server Error`: An error occurred while processing the request

**Example Response**:
```json
[
  {
    "id": 1,
    "patientId": 101,
    "patientName": "John Doe",
    "doctorId": 201,
    "doctorName": "Dr. Smith",
    "date": "2023-10-15",
    "time": "14:30",
    "status": "scheduled",
    "notes": "Regular checkup",
    "createdAt": "2023-10-01T10:30:00",
    "updatedAt": "2023-10-01T10:30:00"
  }
]
```

### 4. Send Appointment Reminders (External API Integration)
```
POST /api/send-reminders
```

**Description**: Send appointment reminders via an external email service. This endpoint demonstrates integration with external APIs like SendGrid or Mailgun.

**Request Body**: None required

**Responses**:
- `200 OK`: Reminders sent successfully
- `500 Internal Server Error`: Failed to send reminders or an error occurred

**Example Response**:
```json
{
  "success": true,
  "message": "Reminders sent successfully",
  "appointmentsProcessed": 5
}
```

## External API Integration

### Overview
The application integrates with external email services to send appointment reminders. The implementation is designed to work with services like SendGrid or Mailgun.

### Implementation Details
- **Service Class**: [ExternalReminderService](file:///c:/Users/01/Documents/NetBeansProjects/clinic-appointment-scheduler/src/main/java/com/clinic/app/services/ExternalReminderService.java#L13-L104)
- **Endpoint**: `POST /api/send-reminders`
- **Data Format**: JSON structured similar to SendGrid's API
- **Security**: API keys retrieved from environment variables

### Sample External API Request
```json
{
  "personalizations": [
    {
      "to": [
        {
          "email": "patient@example.com",
          "name": "John Doe"
        }
      ],
      "dynamic_template_data": {
        "patient_name": "John Doe",
        "doctor_name": "Dr. Smith",
        "appointment_date": "2023-10-15",
        "appointment_time": "14:30",
        "clinic_name": "Clinic Management System"
      }
    }
  ],
  "from": {
    "email": "clinic@clinicmanagement.com",
    "name": "Clinic Management System"
  },
  "template_id": "appointment-reminder-template"
}
```

## Error Handling
All endpoints follow standard HTTP status codes:
- `200`: Success
- `201`: Created
- `400`: Bad Request
- `404`: Not Found
- `409`: Conflict
- `500`: Internal Server Error

Error responses include a JSON body with error details:
```json
{
  "timestamp": "2023-10-01T10:30:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error processing request",
  "path": "/api/appointments"
}
```

## Testing
To test the API endpoints:

1. Start the Spring Boot application
2. Use tools like curl, Postman, or the JavaFX application to make requests
3. Example curl command:
   ```bash
   curl -X GET http://localhost:8080/api/appointments/1
   ```

## Integration with JavaFX Application
The JavaFX application uses the [ApiClient](file:///c:/Users/01/Documents/NetBeansProjects/clinic-appointment-scheduler/src/main/java/com/clinic/app/utils/ApiClient.java#L15-L155) class to communicate with these endpoints:
- Fetching appointment data
- Creating new appointments
- Sending appointment reminders via external services

The "Send External Reminders" button in the JavaFX UI triggers the `POST /api/send-reminders` endpoint, which in turn calls the external email service.