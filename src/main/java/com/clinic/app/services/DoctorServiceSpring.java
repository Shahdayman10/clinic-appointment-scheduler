package com.clinic.app.services;

import com.clinic.app.entities.Doctor;
import com.clinic.app.data.services.DoctorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceSpring {
    private final DoctorService doctorService = DoctorService.getInstance();

    public List<Doctor> getAllDoctors() {
        return doctorService.getAllDoctors();
    }

    public boolean addDoctor(Doctor doctor) {
        return doctorService.addDoctor(doctor);
    }

    public boolean updateDoctor(Doctor doctor) {
        return doctorService.updateDoctor(doctor);
    }

    public boolean deleteDoctor(Integer doctorId) {
        return doctorService.deleteDoctor(doctorId);
    }

    public List<Doctor> searchDoctors(String searchText) {
        return doctorService.searchDoctors(searchText);
    }

    public Doctor getDoctorById(Integer id) {
        return doctorService.getDoctorById(id);
    }

    public Optional<Doctor> getDoctorByIdOptional(Integer id) {
        return doctorService.getDoctorByIdOptional(id);
    }

    public List<Doctor> getDoctorsByName(String name) {
        return doctorService.getDoctorsByName(name);
    }

    public List<Doctor> getDoctorsBySpecialty(String specialty) {
        return doctorService.getDoctorsBySpecialty(specialty);
    }
}