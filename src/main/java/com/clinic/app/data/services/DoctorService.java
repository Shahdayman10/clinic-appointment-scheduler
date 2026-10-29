package com.clinic.app.data.services;

import com.clinic.app.entities.Doctor;
import com.clinic.app.data.repositories.DoctorRepository;
import java.util.List;
import java.util.Optional;

public class DoctorService {
    private static DoctorService instance;
    private DoctorRepository doctorRepository;

    private DoctorService() {
        this.doctorRepository = new DoctorRepository();
    }

    public static synchronized DoctorService getInstance() {
        if (instance == null) {
            instance = new DoctorService();
        }
        return instance;
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public boolean addDoctor(Doctor doctor) {
        try {
            doctorRepository.save(doctor);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateDoctor(Doctor doctor) {
        try {
            doctorRepository.save(doctor);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteDoctor(Integer doctorId) {
        try {
            doctorRepository.deleteById(doctorId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Doctor> searchDoctors(String searchText) {
        return doctorRepository.findByName(searchText);
    }

    public Doctor getDoctorById(Integer id) {
        Optional<Doctor> doctor = doctorRepository.findById(id);
        return doctor.orElse(null);
    }

    public Optional<Doctor> getDoctorByIdOptional(Integer id) {
        return doctorRepository.findById(id);
    }

    public List<Doctor> getDoctorsByName(String name) {
        return doctorRepository.findByName(name);
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorRepository.findBySpecialty(specialty);
    }
}