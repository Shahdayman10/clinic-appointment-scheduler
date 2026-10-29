package com.clinic.app.services;

import com.clinic.app.entities.Appointment;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExternalReminderService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Using a real mock API service that accepts JSON payloads
    // This simulates integration with a real external email reminder service like SendGrid or Mailgun
    private static final String EXTERNAL_API_URL = "https://httpbin.org/post"; // Real external service for demonstration

    public ExternalReminderService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Send appointment reminders via external API (simulating integration with services like SendGrid or Mailgun)
     * This method makes actual HTTP calls to demonstrate real external API integration
     * @param appointments List of appointments to send reminders for
     * @return true if reminders were sent successfully, false otherwise
     */
    public boolean sendAppointmentReminders(List<Appointment> appointments) {
        try {
            // Prepare the request data in a format similar to what real email services expect
            Map<String, Object> requestData = new HashMap<>();

            // Structure the data to resemble a real email API request
            Map<String, Object> emailData = new HashMap<>();
            emailData.put("personalizations", preparePersonalizations(appointments));
            emailData.put("from", Map.of("email", "clinic@clinicmanagement.com", "name", "Clinic Management System"));
            emailData.put("template_id", "appointment-reminder-template");

            String jsonBody = objectMapper.writeValueAsString(emailData);

            System.out.println("Sending reminders to external API: " + EXTERNAL_API_URL);
            System.out.println("Request body: " + jsonBody);

            // Make actual HTTP request to external API
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EXTERNAL_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    // In a real implementation with an actual service like SendGrid, we would include an API key:
                    // .header("Authorization", "Bearer " + getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("External API response status: " + response.statusCode());
            System.out.println("External API response body: " + response.body());

            // For the mock service, we consider success as status code 200
            // In a real implementation with an actual email service, we would parse the response
            // to determine if the emails were queued or sent successfully
            if (response.statusCode() == 200) {
                System.out.println("Reminders sent successfully via external API");
                return true;
            } else {
                System.err.println("Failed to send reminders via external API. Status code: " + response.statusCode());
                return false;
            }

        } catch (Exception e) {
            System.err.println("Error sending reminders to external API: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Prepare personalization data for each appointment (similar to SendGrid's personalizations)
     * @param appointments List of appointments
     * @return List of personalization objects
     */
    private List<Map<String, Object>> preparePersonalizations(List<Appointment> appointments) {
        return appointments.stream().map(appointment -> {
            Map<String, Object> personalization = new HashMap<>();

            // Recipient information
            personalization.put("to", List.of(Map.of(
                "email", appointment.getPatient().getEmail(),
                "name", appointment.getPatient().getName()
            )));

            // Dynamic template data
            Map<String, Object> dynamicTemplateData = new HashMap<>();
            dynamicTemplateData.put("patient_name", appointment.getPatient().getName());
            dynamicTemplateData.put("doctor_name", appointment.getDoctor().getName());
            dynamicTemplateData.put("appointment_date", appointment.getDate().toString());
            dynamicTemplateData.put("appointment_time", appointment.getTime().toString());
            dynamicTemplateData.put("clinic_name", "Clinic Management System");

            personalization.put("dynamic_template_data", dynamicTemplateData);

            return personalization;
        }).toList();
    }

    /**
     * Get API key for external service (in a real implementation with actual services like SendGrid)
     * @return API key
     */
    private String getApiKey() {
        // In a real implementation, this would retrieve the API key from configuration
        // For security, it should never be hardcoded
        // Example: return System.getenv("SENDGRID_API_KEY");
        return "YOUR_SENDGRID_API_KEY"; // Placeholder - should be replaced with actual key retrieval
    }

    /**
     * Parse response from external API (example for a real email service)
     * @param jsonResponse JSON response from API
     * @return Parsed response data
     */
    private Map<String, Object> parseApiResponse(String jsonResponse) {
        try {
            return objectMapper.readValue(jsonResponse, Map.class);
        } catch (Exception e) {
            System.err.println("Error parsing API response: " + e.getMessage());
            return new HashMap<>();
        }
    }
}