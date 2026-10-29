package com.clinic.app.entities;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

@Column(name = "date", nullable = false)
private LocalDate date;

@Column(name = "time", nullable = false)
private LocalTime time;

@Column(name = "status")
private String status;

@Column(name = "notes")
private String notes;

@Column(name = "created_at")
private LocalDateTime createdAt;

@Column(name = "updated_at")
private LocalDateTime updatedAt;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "patient_id", nullable = false)
private Patient patient;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "doctor_id", nullable = false)
private Doctor doctor;

// Constructors
public Appointment() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
    this.status = "scheduled"; // Default status
}

public Appointment(Patient patient, Doctor doctor, LocalDate date, LocalTime time) {
    this();
    this.patient = patient;
    this.doctor = doctor;
    this.date = date;
    this.time = time;
}

// Getters and Setters
public Integer getId() {
    return id;
}

public void setId(Integer id) {
    this.id = id;
}

public LocalDate getDate() {
    return date;
}

public void setDate(LocalDate date) {
    this.date = date;
}

public LocalTime getTime() {
    return time;
}

public void setTime(LocalTime time) {
    this.time = time;
}

public String getStatus() {
    return status;
}

public void setStatus(String status) {
    this.status = status;
}

public String getNotes() {
    return notes;
}

public void setNotes(String notes) {
    this.notes = notes;
}

public LocalDateTime getCreatedAt() {
    return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
}

public LocalDateTime getUpdatedAt() {
    return updatedAt;
}

public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
}

public Patient getPatient() {
    return patient;
}

public void setPatient(Patient patient) {
    this.patient = patient;
}

public Doctor getDoctor() {
    return doctor;
}

public void setDoctor(Doctor doctor) {
    this.doctor = doctor;
}

@PreUpdate
public void preUpdate() {
    this.updatedAt = LocalDateTime.now();
}

@Override
public String toString() {
    return "Appointment{" +
            "id=" + id +
            ", date=" + date +
            ", time=" + time +
            ", status='" + status + '\'' +
            ", notes='" + notes + '\'' +
            ", patient=" + (patient != null ? patient.getName() : "null") +
            ", doctor=" + (doctor != null ? doctor.getName() : "null") +
            '}';
}
}