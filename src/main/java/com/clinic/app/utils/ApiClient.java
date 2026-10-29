package com.clinic.app.utils;

import com.clinic.app.dto.AppointmentDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ApiClient {
    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Get appointment by ID
     * @param id Appointment ID
     * @return AppointmentDTO or null if not found
     * @throws IOException
     * @throws InterruptedException
     */
    public AppointmentDTO getAppointmentById(Integer id) throws IOException, InterruptedException {
        String url = BASE_URL + "/appointments/" + id;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return objectMapper.readValue(response.body(), AppointmentDTO.class);
        } else {
            System.err.println("Failed to get appointment. Status code: " + response.statusCode());
            return null;
        }
    }

    /**
     * Create a new appointment
     * @param appointment Appointment to create
     * @return Created appointment or null if failed
     * @throws IOException
     * @throws InterruptedException
     */
    public AppointmentDTO createAppointment(AppointmentDTO appointment) throws IOException, InterruptedException {
        String url = BASE_URL + "/appointments";

        String jsonBody = objectMapper.writeValueAsString(appointment);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), AppointmentDTO.class);
        } else {
            System.err.println("Failed to create appointment. Status code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            return null;
        }
    }

    /**
     * Get doctor availability for a specific date
     * @param doctorId Doctor ID
     * @param date Date in YYYY-MM-DD format
     * @return List of appointments or empty list if failed
     * @throws IOException
     * @throws InterruptedException
     */
    public List<AppointmentDTO> getDoctorAvailability(Integer doctorId, String date) throws IOException, InterruptedException {
        String url = BASE_URL + "/doctor-availability?" +
                    "doctorId=" + doctorId + "&date=" + URLEncoder.encode(date, StandardCharsets.UTF_8);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            String jsonArray = response.body();
            // Parse JSON array
            return objectMapper.readValue(jsonArray,
                objectMapper.getTypeFactory().constructCollectionType(List.class, AppointmentDTO.class));
        } else {
            System.err.println("Failed to get doctor availability. Status code: " + response.statusCode());
            return List.of();
        }
    }

    /**
     * Send appointment reminders via external API
     * @return true if successful, false otherwise
     * @throws IOException
     * @throws InterruptedException
     */
    public boolean sendAppointmentReminders() throws IOException, InterruptedException {
        String url = BASE_URL + "/send-reminders";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody()) // No body needed for this endpoint
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            // Parse the response to check if it was successful
            Map<String, Object> responseBody = objectMapper.readValue(response.body(), Map.class);
            Boolean success = (Boolean) responseBody.get("success");
            return success != null && success;
        } else {
            System.err.println("Failed to send appointment reminders. Status code: " + response.statusCode());
            System.err.println("Response body: " + response.body());
            return false;
        }
    }

    /**
     * Utility method to build query parameters
     * @param params Map of parameters
     * @return Query string
     */
    private String buildQueryString(Map<String, String> params) {
        return params.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}