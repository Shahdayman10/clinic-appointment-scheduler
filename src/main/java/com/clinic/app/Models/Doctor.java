package com.clinic.app.Models;
import javafx.beans.property.*;
public class Doctor {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final StringProperty specialty = new SimpleStringProperty();
    private final StringProperty phone = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final BooleanProperty available = new SimpleBooleanProperty(true);
    public Doctor(int id, String name, String specialty) {
        setId(id);
        setName(name);
        setSpecialty(specialty);
    }
    public Doctor(int id, String name, String specialty, String phone, String email, boolean available) {
        setId(id);
        setName(name);
        setSpecialty(specialty);
        setPhone(phone);
        setEmail(email);
        setAvailable(available);
    }
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }
    public String getName() { return name.get(); }
    public void setName(String name) { this.name.set(name); }
    public StringProperty nameProperty() { return name; }
    public String getSpecialty() { return specialty.get(); }
    public void setSpecialty(String specialty) { this.specialty.set(specialty); }
    public StringProperty specialtyProperty() { return specialty; }
    public String getPhone() { return phone.get(); }
    public void setPhone(String phone) { this.phone.set(phone); }
    public StringProperty phoneProperty() { return phone; }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    public StringProperty emailProperty() { return email; }
    public boolean isAvailable() { return available.get(); }
    public void setAvailable(boolean available) { this.available.set(available); }
    public BooleanProperty availableProperty() { return available; }
    @Override
    public String toString() {
        return getName() + " - " + getSpecialty();
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Doctor doctor = (Doctor) obj;
        return getId() == doctor.getId();
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}