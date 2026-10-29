package com.clinic.app.data.services;

import com.clinic.app.entities.Patient;
import com.clinic.app.data.repositories.PatientRepository;
import java.util.List;
import java.util.Optional;

public class PatientService {
    private static PatientService instance;
    private PatientRepository patientRepository;

    private PatientService() {
        this.patientRepository = new PatientRepository();
    }

    public static synchronized PatientService getInstance() {
        if (instance == null) {
            instance = new PatientService();
        }
        return instance;
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public boolean addPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deletePatient(Integer patientId) {
        try {
            patientRepository.deleteById(patientId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Patient> searchPatients(String searchText) {
        return patientRepository.findByName(searchText);
    }

    public Patient getPatientById(Integer id) {
        Optional<Patient> patient = patientRepository.findById(id);
        return patient.orElse(null);
    }

    public Optional<Patient> getPatientByIdOptional(Integer id) {
        return patientRepository.findById(id);
    }

    public List<Patient> getPatientsByName(String name) {
        return patientRepository.findByName(name);
    }

    public List<Patient> getPatientsByEmail(String email) {
        return patientRepository.findByEmail(email);
    }

    public List<Patient> getPatientsByPhone(String phone) {
        return patientRepository.findByPhone(phone);
    }
}