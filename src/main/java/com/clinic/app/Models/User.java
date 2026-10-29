package com.clinic.app.Models;
import javafx.beans.property.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**
 * User model class representing a system user
 */
public class User {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty firstName = new SimpleStringProperty();
    private final StringProperty lastName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty passwordHash = new SimpleStringProperty();
    private final StringProperty role = new SimpleStringProperty();
    private final BooleanProperty isActive = new SimpleBooleanProperty();
    private final ObjectProperty<LocalDateTime> createdAt = new SimpleObjectProperty<>();
    private final ObjectProperty<LocalDateTime> updatedAt = new SimpleObjectProperty<>();
    // Constructors
    public User() {
        this.isActive.set(true);
        this.role.set("USER");
        this.createdAt.set(LocalDateTime.now());
        this.updatedAt.set(LocalDateTime.now());
    }
    public User(int id, String firstName, String lastName, String email, String passwordHash) {
        this();
        this.id.set(id);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.email.set(email);
        this.passwordHash.set(passwordHash);
    }
    public User(int id, String firstName, String lastName, String email, String passwordHash,
                String role, boolean isActive, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id.set(id);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.email.set(email);
        this.passwordHash.set(passwordHash);
        this.role.set(role);
        this.isActive.set(isActive);
        this.createdAt.set(createdAt);
        this.updatedAt.set(updatedAt);
    }
    // ID Property
    public IntegerProperty idProperty() { return id; }
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    // First Name Property
    public StringProperty firstNameProperty() { return firstName; }
    public String getFirstName() { return firstName.get(); }
    public void setFirstName(String firstName) { this.firstName.set(firstName); }
    // Last Name Property
    public StringProperty lastNameProperty() { return lastName; }
    public String getLastName() { return lastName.get(); }
    public void setLastName(String lastName) { this.lastName.set(lastName); }
    // Email Property
    public StringProperty emailProperty() { return email; }
    public String getEmail() { return email.get(); }
    public void setEmail(String email) { this.email.set(email); }
    // Password Hash Property
    public StringProperty passwordHashProperty() { return passwordHash; }
    public String getPasswordHash() { return passwordHash.get(); }
    public void setPasswordHash(String passwordHash) { this.passwordHash.set(passwordHash); }
    // Role Property
    public StringProperty roleProperty() { return role; }
    public String getRole() { return role.get(); }
    public void setRole(String role) { this.role.set(role); }
    // IsActive Property
    public BooleanProperty isActiveProperty() { return isActive; }
    public boolean isActive() { return isActive.get(); }
    public void setActive(boolean isActive) { this.isActive.set(isActive); }
    // Created At Property
    public ObjectProperty<LocalDateTime> createdAtProperty() { return createdAt; }
    public LocalDateTime getCreatedAt() { return createdAt.get(); }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt.set(createdAt); }
    // Updated At Property
    public ObjectProperty<LocalDateTime> updatedAtProperty() { return updatedAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt.get(); }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt.set(updatedAt); }
    // Utility Methods
    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(getRole());
    }
    public boolean isDoctor() {
        return "DOCTOR".equalsIgnoreCase(getRole());
    }
    public boolean isUser() {
        return "USER".equalsIgnoreCase(getRole());
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + getId() +
                ", firstName='" + getFirstName() + '\'' +
                ", lastName='" + getLastName() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", role='" + getRole() + '\'' +
                ", isActive=" + isActive() +
                ", createdAt=" + getCreatedAt() +
                '}';
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return getId() == user.getId();
    }
    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }
}