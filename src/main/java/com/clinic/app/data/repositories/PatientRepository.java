package com.clinic.app.data.repositories;

import com.clinic.app.entities.Patient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

public class PatientRepository extends AbstractRepository<Patient, Integer> {

    public PatientRepository() {
        super(Patient.class);
    }

    public List<Patient> findByName(String name) {
        EntityManager em = getEntityManager();
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE p.name LIKE :name", Patient.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    public List<Patient> findByEmail(String email) {
        EntityManager em = getEntityManager();
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE p.email = :email", Patient.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    public List<Patient> findByPhone(String phone) {
        EntityManager em = getEntityManager();
        TypedQuery<Patient> query = em.createQuery(
            "SELECT p FROM Patient p WHERE p.phone = :phone", Patient.class);
        query.setParameter("phone", phone);
        return query.getResultList();
    }
}