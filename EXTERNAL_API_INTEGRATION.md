# External API Integration Documentation

## Overview
This document describes the implementation of external API integration for sending appointment reminders in the Clinic Appointment Scheduler application. The integration simulates connecting to real external email services like SendGrid or Mailgun.

## Implementation Details

### 1. ExternalReminderService
The [ExternalReminderService](file:///c:/Users/01/Documents/NetBeansProjects/clinic-appointment-scheduler/src/main/java/com/clinic/app/services/ExternalReminderService.java#L13-L104) class handles communication with external email services:

- **Endpoint**: Uses `https://httpbin.org/post` as a mock external service for demonstration
- **Data Format**: Structures data similar to SendGrid's API format with personalizations
- **Authentication**: Includes framework for API key authentication (commented out for demo)
- **Error Handling**: Comprehensive error handling for network and API issues

### 2. API Endpoint
A new REST endpoint has been added to [AppointmentRestController](file:///c:/Users/01/Documents/NetBeansProjects/clinic-appointment-scheduler/src/main/java/com/clinic/app/controllers/api/AppointmentRestController.java#L15-L157):

```
POST /api/send-reminders
```

This endpoint:
- Retrieves upcoming appointments (next 48 hours)
- Sends them to the external reminder service
- Returns success/failure status

### 3. JavaFX Integration
The JavaFX application includes a new button "Send External Reminders" that:
- Calls the REST API endpoint
- Displays success/error messages
- Shows response details in the reminders text area

### 4. Data Structure
The service structures data to match real email service APIs:

```json
{
  "personalizations": [
    {
      "to": [
        {
          "email": "patient@example.com",
          "name": "Patient Name"
        }
      ],
      "dynamic_template_data": {
        "patient_name": "Patient Name",
        "doctor_name": "Doctor Name",
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

## How to Integrate with Real Services

### SendGrid Integration
To integrate with SendGrid:

1. Replace the `EXTERNAL_API_URL` with SendGrid's API endpoint:
   ```java
   private static final String EXTERNAL_API_URL = "https://api.sendgrid.com/v3/mail/send";
   ```

2. Uncomment and update the API key retrieval:
   ```java
   .header("Authorization", "Bearer " + getApiKey())
   ```

3. Set the `SENDGRID_API_KEY` environment variable

### Mailgun Integration
To integrate with Mailgun:

1. Replace the `EXTERNAL_API_URL` with Mailgun's API endpoint:
   ```java
   private static final String EXTERNAL_API_URL = "https://api.mailgun.net/v3/YOUR_DOMAIN/messages";
   ```

2. Update the request to use Mailgun's data format
3. Set the `MAILGUN_API_KEY` environment variable

## Testing
The integration can be tested by:
1. Running the Spring Boot application
2. Clicking "Send External Reminders" in the JavaFX application
3. Checking the console output for API request/response details

## Security Considerations
- API keys are retrieved from environment variables, not hardcoded
- HTTPS is used for all external communications
- Error responses are logged but not exposed to end users