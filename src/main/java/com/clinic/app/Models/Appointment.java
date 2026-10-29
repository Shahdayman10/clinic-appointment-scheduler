package com.clinic.app.Models;
import javafx.beans.property.*;
public class Appointment {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final IntegerProperty patientId = new SimpleIntegerProperty();
    private final IntegerProperty doctorId = new SimpleIntegerProperty();
    private final StringProperty patientName = new SimpleStringProperty();
    private final StringProperty doctorName = new SimpleStringProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty time = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty notes = new SimpleStringProperty();
    // Constructor with names (for display)
    public Appointment(int id, String patientName, String doctorName, String date,
                      String time, String status, String notes) {
        setId(id);
        setPatientName(patientName);
        setDoctorName(doctorName);
        setDate(date);
        setTime(time);
        setStatus(status);
        setNotes(notes);
    }
    // Constructor with IDs (for database operations)
    public Appointment(int id, int patientId, int doctorId, String patientName, String doctorName,
                      String date, String time, String status, String notes) {
        setId(id);
        setPatientId(patientId);
        setDoctorId(doctorId);
        setPatientName(patientName);
        setDoctorName(doctorName);
        setDate(date);
        setTime(time);
        setStatus(status);
        setNotes(notes);
    }
    // Getters and Setters for properties
    public int getId() {
        return id.get();
    }
    public void setId(int id) {
        this.id.set(id);
    }
    public IntegerProperty idProperty() {
        return id;
    }
    public int getPatientId() {
        return patientId.get();
    }
    public void setPatientId(int patientId) {
        this.patientId.set(patientId);
    }
    public IntegerProperty patientIdProperty() {
        return patientId;
    }
    public int getDoctorId() {
        return doctorId.get();
    }
    public void setDoctorId(int doctorId) {
        this.doctorId.set(doctorId);
    }
    public IntegerProperty doctorIdProperty() {
        return doctorId;
    }
    public String getPatientName() {
        return patientName.get();
    }
    public void setPatientName(String patientName) {
        this.patientName.set(patientName);
    }
    public StringProperty patientNameProperty() {
        return patientName;
    }
    public String getDoctorName() {
        return doctorName.get();
    }
    public void setDoctorName(String doctorName) {
        this.doctorName.set(doctorName);
    }
    public StringProperty doctorNameProperty() {
        return doctorName;
    }
    public String getDate() {
        return date.get();
    }
    public void setDate(String date) {
        this.date.set(date);
    }
    public StringProperty dateProperty() {
        return date;
    }
    public String getTime() {
        return time.get();
    }
    public void setTime(String time) {
        this.time.set(time);
    }
    public StringProperty timeProperty() {
        return time;
    }
    public String getStatus() {
        return status.get();
    }
    public void setStatus(String status) {
        this.status.set(status);
    }
    public StringProperty statusProperty() {
        return status;
    }
    public String getNotes() {
        return notes.get();
    }
    public void setNotes(String notes) {
        this.notes.set(notes);
    }
    public StringProperty notesProperty() {
        return notes;
    }
    // toString method for debugging and display
    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + getId() +
                ", patientName='" + getPatientName() + '\'' +
                ", doctorName='" + getDoctorName() + '\'' +
                ", date='" + getDate() + '\'' +
                ", time='" + getTime() + '\'' +
                ", status='" + getStatus() + '\'' +
                ", notes='" + getNotes() + '\'' +
                '}';
    }
    // Optional: equals and hashCode methods for proper comparison
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Appointment that = (Appointment) o;
        return getId() == that.getId();
    }
    @Override
    public int hashCode() {
        return getId();
    }
}