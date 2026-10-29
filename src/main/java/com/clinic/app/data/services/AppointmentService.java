package com.clinic.app.data.services;

import com.clinic.app.entities.Appointment;
import com.clinic.app.entities.Doctor;
import com.clinic.app.entities.Patient;
import com.clinic.app.data.repositories.AppointmentRepository;
import com.clinic.app.data.repositories.DoctorRepository;
import com.clinic.app.data.repositories.PatientRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AppointmentService {
    private static AppointmentService instance;
    private AppointmentRepository appointmentRepository;
    private DoctorRepository doctorRepository;
    private PatientRepository patientRepository;

    private AppointmentService() {
        this.appointmentRepository = new AppointmentRepository();
        this.doctorRepository = new DoctorRepository();
        this.patientRepository = new PatientRepository();
    }

    public static synchronized AppointmentService getInstance() {
        if (instance == null) {
            instance = new AppointmentService();
        }
        return instance;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public boolean addAppointment(Appointment appointment) {
        try {
            // Validate that required fields are present
            if (appointment.getDoctor() == null) {
                System.err.println("Error adding appointment: Doctor is null");
                return false;
            }
            if (appointment.getPatient() == null) {
                System.err.println("Error adding appointment: Patient is null");
                return false;
            }
            if (appointment.getDate() == null) {
                System.err.println("Error adding appointment: Date is null");
                return false;
            }
            if (appointment.getTime() == null) {
                System.err.println("Error adding appointment: Time is null");
                return false;
            }

            // Check for conflicts before adding
            if (hasAppointmentConflict(appointment.getDoctor(), appointment.getDate(), appointment.getTime())) {
                System.err.println("Conflict detected: Doctor " + appointment.getDoctor().getName() +
                    " already has an appointment at " + appointment.getTime() + " on " + appointment.getDate());
                return false;
            }

            System.out.println("Adding appointment: " + appointment);
            appointmentRepository.save(appointment);
            System.out.println("Appointment added successfully with ID: " + appointment.getId());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAppointment(Appointment appointment) {
        try {
            // Check for conflicts before updating (excluding the current appointment)
            if (hasAppointmentConflictExcludingCurrent(appointment)) {
                System.err.println("Conflict detected: Doctor " + appointment.getDoctor().getName() +
                    " already has an appointment at " + appointment.getTime() + " on " + appointment.getDate());
                return false;
            }

            appointmentRepository.save(appointment);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteAppointment(Integer appointmentId) {
        try {
            appointmentRepository.deleteById(appointmentId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting appointment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Appointment> searchAppointments(String searchText) {
        // For simplicity, we'll search by status
        return appointmentRepository.findByStatus(searchText);
    }

    public Appointment getAppointmentById(Integer id) {
        Optional<Appointment> appointment = appointmentRepository.findById(id);
        return appointment.orElse(null);
    }

    public Optional<Appointment> getAppointmentByIdOptional(Integer id) {
        return appointmentRepository.findById(id);
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointmentRepository.findByPatient(patient);
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointmentRepository.findByDoctor(doctor);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentRepository.findByDate(date);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDateRange(startDate, endDate);
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(Doctor doctor, LocalDate date) {
        return appointmentRepository.findByDoctorAndDate(doctor, date);
    }

    public List<Appointment> getAppointmentsByDoctorAndDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDoctorAndDateRange(doctor, startDate, endDate);
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    // Method to check for appointment conflicts
    public boolean hasAppointmentConflict(Doctor doctor, LocalDate date, LocalTime time) {
        return appointmentRepository.existsConflict(doctor, date, time);
    }

    // Method to check for appointment conflicts excluding the current appointment (for updates)
    public boolean hasAppointmentConflictExcludingCurrent(Appointment appointment) {
        // First check if there's a conflict
        boolean hasConflict = appointmentRepository.existsConflict(
            appointment.getDoctor(),
            appointment.getDate(),
            appointment.getTime()
        );

        // If there's a conflict, check if it's with the same appointment (updating existing)
        if (hasConflict && appointment.getId() != null) {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
            if (existing.isPresent()) {
                Appointment existingAppointment = existing.get();
                // If it's the same appointment, it's not a real conflict
                if (existingAppointment.getDoctor().getId().equals(appointment.getDoctor().getId()) &&
                    existingAppointment.getDate().equals(appointment.getDate()) &&
                    existingAppointment.getTime().equals(appointment.getTime())) {
                    return false; // Not a conflict, it's the same appointment
                }
            }
        }

        return hasConflict;
    }

    // Method to get upcoming appointments (within 48 hours)
    public List<Appointment> getUpcomingAppointments() {
        return appointmentRepository.findUpcomingAppointments();
    }

    // Method to get text-based schedule for a doctor and date range
    public List<Appointment> getDoctorSchedule(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return appointmentRepository.findByDoctorAndDateRangeOrdered(doctor, startDate, endDate);
    }
}