package com.clinic.app.utils;

import com.clinic.app.data.services.PatientService;
import com.clinic.app.data.services.DoctorService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class EntityModelConverter {

    // Convert JPA Doctor entity to JavaFX Doctor model
    public static com.clinic.app.Models.Doctor toDoctorModel(com.clinic.app.entities.Doctor doctorEntity) {
        if (doctorEntity == null) {
            return null;
        }

        com.clinic.app.Models.Doctor model = new com.clinic.app.Models.Doctor(
            doctorEntity.getId() != null ? doctorEntity.getId() : 0,
            doctorEntity.getName() != null ? doctorEntity.getName() : "",
            doctorEntity.getSpecialty() != null ? doctorEntity.getSpecialty() : "",
            doctorEntity.getPhone() != null ? doctorEntity.getPhone() : "",
            doctorEntity.getEmail() != null ? doctorEntity.getEmail() : "",
            doctorEntity.getAvailable() != null ? doctorEntity.getAvailable() : true
        );

        return model;
    }

    // Convert JavaFX Doctor model to JPA Doctor entity
    public static com.clinic.app.entities.Doctor toDoctorEntity(com.clinic.app.Models.Doctor doctorModel) {
        if (doctorModel == null) {
            return null;
        }

        com.clinic.app.entities.Doctor entity = new com.clinic.app.entities.Doctor();
        if (doctorModel.getId() > 0) {
            entity.setId(doctorModel.getId());
        }
        entity.setName(doctorModel.getName());
        entity.setSpecialty(doctorModel.getSpecialty());
        entity.setPhone(doctorModel.getPhone());
        entity.setEmail(doctorModel.getEmail());
        entity.setAvailable(doctorModel.isAvailable());

        return entity;
    }

    // Convert JPA Patient entity to JavaFX Patient model
    public static com.clinic.app.Models.Patient toPatientModel(com.clinic.app.entities.Patient patientEntity) {
        if (patientEntity == null) {
            return null;
        }

        com.clinic.app.Models.Patient model = new com.clinic.app.Models.Patient();
        model.setId(patientEntity.getId() != null ? patientEntity.getId() : 0);
        model.setName(patientEntity.getName() != null ? patientEntity.getName() : "");
        model.setPhone(patientEntity.getPhone() != null ? patientEntity.getPhone() : "");
        model.setEmail(patientEntity.getEmail() != null ? patientEntity.getEmail() : "");
        model.setCreatedAt(patientEntity.getCreatedAt());

        return model;
    }

    // Convert JavaFX Patient model to JPA Patient entity
    public static com.clinic.app.entities.Patient toPatientEntity(com.clinic.app.Models.Patient patientModel) {
        if (patientModel == null) {
            return null;
        }

        com.clinic.app.entities.Patient entity = new com.clinic.app.entities.Patient();
        if (patientModel.getId() > 0) {
            entity.setId(patientModel.getId());
        }
        entity.setName(patientModel.getName());
        entity.setPhone(patientModel.getPhone());
        entity.setEmail(patientModel.getEmail());
        entity.setCreatedAt(patientModel.getCreatedAt());

        return entity;
    }

    // Convert JPA Appointment entity to JavaFX Appointment model
    public static com.clinic.app.Models.Appointment toAppointmentModel(com.clinic.app.entities.Appointment appointmentEntity) {
        if (appointmentEntity == null) {
            return null;
        }

        String patientName = appointmentEntity.getPatient() != null ?
            appointmentEntity.getPatient().getName() : "Unknown Patient";
        String doctorName = appointmentEntity.getDoctor() != null ?
            appointmentEntity.getDoctor().getName() : "Unknown Doctor";

        com.clinic.app.Models.Appointment model = new com.clinic.app.Models.Appointment(
            appointmentEntity.getId() != null ? appointmentEntity.getId() : 0,
            appointmentEntity.getPatient() != null ? appointmentEntity.getPatient().getId() : 0,
            appointmentEntity.getDoctor() != null ? appointmentEntity.getDoctor().getId() : 0,
            patientName,
            doctorName,
            appointmentEntity.getDate() != null ? appointmentEntity.getDate().toString() : "",
            appointmentEntity.getTime() != null ? appointmentEntity.getTime().toString() : "",
            appointmentEntity.getStatus() != null ? appointmentEntity.getStatus() : "scheduled",
            appointmentEntity.getNotes() != null ? appointmentEntity.getNotes() : ""
        );

        return model;
    }

    // Convert JavaFX Appointment model to JPA Appointment entity
    public static com.clinic.app.entities.Appointment toAppointmentEntity(com.clinic.app.Models.Appointment appointmentModel) {
        if (appointmentModel == null) {
            System.err.println("Appointment model is null");
            return null;
        }

        com.clinic.app.entities.Appointment entity = new com.clinic.app.entities.Appointment();
        if (appointmentModel.getId() > 0) {
            entity.setId(appointmentModel.getId());
        }

        // Set patient and doctor entities by fetching them from the database
        if (appointmentModel.getPatientId() > 0) {
            PatientService patientService = PatientService.getInstance();
            Optional<com.clinic.app.entities.Patient> patient = patientService.getPatientByIdOptional(appointmentModel.getPatientId());
            if (patient.isPresent()) {
                entity.setPatient(patient.get());
            } else {
                System.err.println("Patient not found with ID: " + appointmentModel.getPatientId());
            }
        } else {
            System.err.println("Invalid patient ID: " + appointmentModel.getPatientId());
        }

        if (appointmentModel.getDoctorId() > 0) {
            DoctorService doctorService = DoctorService.getInstance();
            Optional<com.clinic.app.entities.Doctor> doctor = doctorService.getDoctorByIdOptional(appointmentModel.getDoctorId());
            if (doctor.isPresent()) {
                entity.setDoctor(doctor.get());
            } else {
                System.err.println("Doctor not found with ID: " + appointmentModel.getDoctorId());
            }
        } else {
            System.err.println("Invalid doctor ID: " + appointmentModel.getDoctorId());
        }

        if (appointmentModel.getDate() != null && !appointmentModel.getDate().isEmpty()) {
            try {
                entity.setDate(LocalDate.parse(appointmentModel.getDate()));
            } catch (Exception e) {
                System.err.println("Error parsing date: " + appointmentModel.getDate());
            }
        }

        if (appointmentModel.getTime() != null && !appointmentModel.getTime().isEmpty()) {
            try {
                entity.setTime(LocalTime.parse(appointmentModel.getTime()));
            } catch (Exception e) {
                System.err.println("Error parsing time: " + appointmentModel.getTime());
            }
        }

        entity.setStatus(appointmentModel.getStatus());
        entity.setNotes(appointmentModel.getNotes());

        System.out.println("Converted appointment entity: " + entity);
        return entity;
    }
}