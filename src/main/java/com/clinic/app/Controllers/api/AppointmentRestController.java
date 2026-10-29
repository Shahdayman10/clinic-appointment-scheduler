package com.clinic.app.controllers.api;

import com.clinic.app.entities.Appointment;
import com.clinic.app.entities.Doctor;
import com.clinic.app.dto.AppointmentDTO;
import com.clinic.app.services.AppointmentServiceSpring;
import com.clinic.app.services.DoctorServiceSpring;
import com.clinic.app.services.ExternalReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // Allow requests from any origin (for development)
public class AppointmentRestController {

    @Autowired
    private AppointmentServiceSpring appointmentService;

    @Autowired
    private DoctorServiceSpring doctorService;

    @Autowired
    private ExternalReminderService externalReminderService;

    // GET /appointments/{id} - Get appointment by ID
    @GetMapping("/appointments/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable Integer id) {
        try {
            Optional<Appointment> appointment = appointmentService.getAppointmentByIdOptional(id);
            if (appointment.isPresent()) {
                AppointmentDTO dto = convertToDTO(appointment.get());
                return new ResponseEntity<>(dto, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST /appointments - Create a new appointment
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        try {
            // Convert DTO to entity
            Appointment appointment = convertToEntity(appointmentDTO);

            // Validate required fields
            if (appointment.getDoctor() == null || appointment.getPatient() == null ||
                appointment.getDate() == null || appointment.getTime() == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            boolean success = appointmentService.addAppointment(appointment);
            if (success) {
                AppointmentDTO createdDTO = convertToDTO(appointment);
                return new ResponseEntity<>(createdDTO, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT); // Conflict if appointment already exists
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // GET /doctor-availability - Get doctor availability for a specific date
    @GetMapping("/doctor-availability")
    public ResponseEntity<List<AppointmentDTO>> getDoctorAvailability(
            @RequestParam Integer doctorId,
            @RequestParam String date) {
        try {
            Doctor doctor = doctorService.getDoctorById(doctorId);
            if (doctor == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

            LocalDate localDate = LocalDate.parse(date);
            List<Appointment> appointments = appointmentService.getAppointmentsByDoctorAndDate(doctor, localDate);

            // Convert to DTOs
            List<AppointmentDTO> appointmentDTOs = appointments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(appointmentDTOs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // POST /send-reminders - Send appointment reminders via external API
    @PostMapping("/send-reminders")
    public ResponseEntity<Map<String, Object>> sendAppointmentReminders() {
        try {
            // Get upcoming appointments (next 24-48 hours)
            List<Appointment> upcomingAppointments = appointmentService.getUpcomingAppointments();

            // Send reminders via external API
            boolean success = externalReminderService.sendAppointmentReminders(upcomingAppointments);

            // Prepare response
            Map<String, Object> response = Map.of(
                "success", success,
                "message", success ? "Reminders sent successfully" : "Failed to send reminders",
                "appointmentsProcessed", upcomingAppointments.size()
            );

            return new ResponseEntity<>(response, success ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            Map<String, Object> errorResponse = Map.of(
                "success", false,
                "message", "Error sending reminders: " + e.getMessage()
            );
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Helper method to convert entity to DTO
    private AppointmentDTO convertToDTO(Appointment appointment) {
        return new AppointmentDTO(
                appointment.getId(),
                appointment.getPatient() != null ? appointment.getPatient().getId() : null,
                appointment.getPatient() != null ? appointment.getPatient().getName() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getId() : null,
                appointment.getDoctor() != null ? appointment.getDoctor().getName() : null,
                appointment.getDate(),
                appointment.getTime(),
                appointment.getStatus(),
                appointment.getNotes(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt()
        );
    }

    // Helper method to convert DTO to entity
    private Appointment convertToEntity(AppointmentDTO dto) {
        Appointment appointment = new Appointment();
        appointment.setId(dto.getId());
        appointment.setDate(dto.getDate());
        appointment.setTime(dto.getTime());
        appointment.setStatus(dto.getStatus());
        appointment.setNotes(dto.getNotes());
        appointment.setCreatedAt(dto.getCreatedAt());
        appointment.setUpdatedAt(dto.getUpdatedAt());
        // Note: Patient and Doctor entities would need to be fetched from the database
        // based on the IDs in a real implementation
        return appointment;
    }
}