package com.clinic.app.services;

import com.clinic.app.entities.Appointment;
import com.clinic.app.entities.Doctor;
import com.clinic.app.entities.Patient;
import com.clinic.app.data.services.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceSpring {
    private final AppointmentService appointmentService = AppointmentService.getInstance();

    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    public boolean addAppointment(Appointment appointment) {
        return appointmentService.addAppointment(appointment);
    }

    public boolean updateAppointment(Appointment appointment) {
        return appointmentService.updateAppointment(appointment);
    }

    public boolean deleteAppointment(Integer appointmentId) {
        return appointmentService.deleteAppointment(appointmentId);
    }

    public List<Appointment> searchAppointments(String searchText) {
        return appointmentService.searchAppointments(searchText);
    }

    public Optional<Appointment> getAppointmentByIdOptional(Integer id) {
        return appointmentService.getAppointmentByIdOptional(id);
    }

    public Appointment getAppointmentById(Integer id) {
        return appointmentService.getAppointmentById(id);
    }

    public List<Appointment> getAppointmentsByPatient(Patient patient) {
        return appointmentService.getAppointmentsByPatient(patient);
    }

    public List<Appointment> getAppointmentsByDoctor(Doctor doctor) {
        return appointmentService.getAppointmentsByDoctor(doctor);
    }

    public List<Appointment> getAppointmentsByDate(LocalDate date) {
        return appointmentService.getAppointmentsByDate(date);
    }

    public List<Appointment> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return appointmentService.getAppointmentsByDateRange(startDate, endDate);
    }

    public List<Appointment> getAppointmentsByDoctorAndDate(Doctor doctor, LocalDate date) {
        return appointmentService.getAppointmentsByDoctorAndDate(doctor, date);
    }

    public List<Appointment> getAppointmentsByDoctorAndDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return appointmentService.getAppointmentsByDoctorAndDateRange(doctor, startDate, endDate);
    }

    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    public boolean hasAppointmentConflict(Doctor doctor, LocalDate date, java.time.LocalTime time) {
        return appointmentService.hasAppointmentConflict(doctor, date, time);
    }

    public boolean hasAppointmentConflictExcludingCurrent(Appointment appointment) {
        return appointmentService.hasAppointmentConflictExcludingCurrent(appointment);
    }

    public List<Appointment> getUpcomingAppointments() {
        return appointmentService.getUpcomingAppointments();
    }

    public List<Appointment> getDoctorSchedule(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        return appointmentService.getDoctorSchedule(doctor, startDate, endDate);
    }
}