package com.clinic.app.data.repositories;

import com.clinic.app.entities.Appointment;
import com.clinic.app.entities.Doctor;
import com.clinic.app.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public class AppointmentRepository extends AbstractRepository<Appointment, Integer> {

    public AppointmentRepository() {
        super(Appointment.class);
    }

    public List<Appointment> findByDoctor(Doctor doctor) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.doctor = :doctor ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("doctor", doctor);
        return query.getResultList();
    }

    public List<Appointment> findByPatient(Patient patient) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.patient = :patient ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("patient", patient);
        return query.getResultList();
    }

    public List<Appointment> findByDate(LocalDate date) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.date = :date ORDER BY a.time", Appointment.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.date BETWEEN :startDate AND :endDate ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public List<Appointment> findByDoctorAndDate(Doctor doctor, LocalDate date) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.date = :date ORDER BY a.time", Appointment.class);
        query.setParameter("doctor", doctor);
        query.setParameter("date", date);
        return query.getResultList();
    }

    public List<Appointment> findByDoctorAndDateRange(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.date BETWEEN :startDate AND :endDate ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("doctor", doctor);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    public List<Appointment> findByStatus(String status) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.status = :status ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    // New method for conflict detection (exact slot)
    public boolean existsConflict(Doctor doctor, LocalDate date, LocalTime time) {
        EntityManager em = getEntityManager();
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.date = :date AND a.time = :time", Long.class);
        query.setParameter("doctor", doctor);
        query.setParameter("date", date);
        query.setParameter("time", time);
        return query.getSingleResult() > 0;
    }

    // New method for schedule generation in range
    public List<Appointment> findByDoctorAndDateRangeOrdered(Doctor doctor, LocalDate startDate, LocalDate endDate) {
        EntityManager em = getEntityManager();
        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.doctor.id = :docId AND a.date BETWEEN :from AND :to ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("docId", doctor.getId());
        query.setParameter("from", startDate);
        query.setParameter("to", endDate);
        return query.getResultList();
    }

    // New method for reminders (next 48 hours)
    public List<Appointment> findUpcomingAppointments() {
        EntityManager em = getEntityManager();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nowPlus48h = now.plusHours(48);

        TypedQuery<Appointment> query = em.createQuery(
            "SELECT a FROM Appointment a WHERE a.date >= :today AND a.date <= :endDate AND a.status = 'scheduled' ORDER BY a.date, a.time", Appointment.class);
        query.setParameter("today", now.toLocalDate());
        query.setParameter("endDate", nowPlus48h.toLocalDate());
        return query.getResultList();
    }
}