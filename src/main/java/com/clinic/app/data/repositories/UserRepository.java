package com.clinic.app.data.repositories;

import com.clinic.app.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class UserRepository extends AbstractRepository<User, Integer> {

    public UserRepository() {
        super(User.class);
    }

    public Optional<User> findByEmail(String email) {
        if (!databaseAvailable) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    public List<User> findByRole(String role) {
        if (!databaseAvailable) {
            return List.of();
        }

        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.role = :role", User.class);
        query.setParameter("role", role);
        return query.getResultList();
    }

    public List<User> findByActiveStatus(boolean isActive) {
        if (!databaseAvailable) {
            return List.of();
        }

        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.active = :isActive", User.class);
        query.setParameter("isActive", isActive);
        return query.getResultList();
    }

    public Optional<User> findByEmailAndPassword(String email, String passwordHash) {
        if (!databaseAvailable) {
            return Optional.empty();
        }

        EntityManager em = getEntityManager();
        TypedQuery<User> query = em.createQuery(
            "SELECT u FROM User u WHERE u.email = :email AND u.passwordHash = :passwordHash", User.class);
        query.setParameter("email", email);
        query.setParameter("passwordHash", passwordHash);
        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
}