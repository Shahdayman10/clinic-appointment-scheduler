package com.clinic.app.data.repositories;

import com.clinic.app.entities.Doctor;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class DoctorRepository extends AbstractRepository<Doctor, Integer> {

    public DoctorRepository() {
        super(Doctor.class);
    }

    public List<Doctor> findByName(String name) {
        EntityManager em = getEntityManager();
        TypedQuery<Doctor> query = em.createQuery(
            "SELECT d FROM Doctor d WHERE d.name LIKE :name", Doctor.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public List<Doctor> findBySpecialty(String specialty) {
        EntityManager em = getEntityManager();
        TypedQuery<Doctor> query = em.createQuery(
            "SELECT d FROM Doctor d WHERE d.specialty = :specialty", Doctor.class);
        query.setParameter("specialty", specialty);
        return query.getResultList();
    }

    public List<Doctor> findByNameAndSpecialty(String name, String specialty) {
        EntityManager em = getEntityManager();
        TypedQuery<Doctor> query = em.createQuery(
            "SELECT d FROM Doctor d WHERE d.name LIKE :name AND d.specialty = :specialty", Doctor.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("specialty", specialty);
        return query.getResultList();
    }
}