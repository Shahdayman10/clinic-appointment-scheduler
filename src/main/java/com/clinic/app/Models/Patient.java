package com.clinic.app.Models;
import javafx.beans.property.*;
import java.time.LocalDateTime;
import java.time.LocalDate;

public class Patient {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty address = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dateOfBirth = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();

    // Default constructor
    public Patient() {
        this.createdAt.set(LocalDateTime.now());
    }

    // Constructor with all fields
    public Patient(int id, String name, String phone, String email, String address, LocalDate dateOfBirth, LocalDateTime createdAt) {
        this.id.set(id);
        this.name.set(name);
        this.phone.set(phone);
        this.email.set(email);
        this.address.set(address);
        this.dateOfBirth.set(dateOfBirth);
        this.createdAt.set(createdAt);
    }

    // Constructor without ID (for new patients)
    public Patient(String name, String phone, String email) {
        this();
        this.name.set(name);
        this.phone.set(phone);
        this.email.set(email);
    }

    // Getters and Setters for properties
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }

    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }

    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }

    public String getAddress() { return address.get(); }
    public void setAddress(String address) { this.address.set(address); }
    public StringProperty addressProperty() { return address; }

    public LocalDate getDateOfBirth() { return dateOfBirth.get(); }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth.set(dateOfBirth); }
    public ObjectProperty<LocalDate> dateOfBirthProperty() { return dateOfBirth; }

    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Patient patient = (Patient) obj;
        return getId() == patient.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}