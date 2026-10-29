package com.clinic.app.data.repositories;

import com.clinic.app.data.JPAUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements BaseRepository<T, ID> {
    protected final Class<T> entityClass;
    protected EntityManager entityManager;
    protected boolean databaseAvailable = true;

    public AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
        try {
            this.entityManager = JPAUtil.getEntityManagerFactory().createEntityManager();
            // Test the connection
            this.entityManager.getTransaction();
        } catch (Exception e) {
            System.err.println("Database not available for " + entityClass.getSimpleName() + ": " + e.getMessage());
            this.databaseAvailable = false;
            this.entityManager = null;
        }
    }

    @Override
    public T save(T entity) {
        if (!databaseAvailable) {
            throw new RuntimeException("Database not available");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            System.out.println("Saving entity: " + entity);

            // Check if entity has an ID and if it's already managed
            boolean isManaged = entityManager.contains(entity);
            System.out.println("Entity manager contains entity: " + isManaged);

            T result;
            if (isManaged) {
                System.out.println("Merging entity (already managed)");
                result = entityManager.merge(entity);
            } else {
                // For entities with ID, we might need to merge instead of persist
                // This is especially important for updates
                System.out.println("Checking if entity should be merged or persisted");

                // Try to determine if this is an update or insert by checking if the entity has an ID
                // and if it exists in the database
                result = entityManager.merge(entity);
                System.out.println("Merged entity");
            }

            transaction.commit();
            System.out.println("Entity saved successfully: " + result);
            return result;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            System.err.println("Error saving entity: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error saving entity: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        if (!databaseAvailable) {
            return Optional.empty();
        }

        T entity = entityManager.find(entityClass, id);
        return Optional.ofNullable(entity);
    }

    @Override
    public List<T> findAll() {
        if (!databaseAvailable) {
            return List.of();
        }

        String jpql = "SELECT e FROM " + entityClass.getSimpleName() + " e";
        return entityManager.createQuery(jpql, entityClass).getResultList();
    }

    @Override
    public void delete(T entity) {
        if (!databaseAvailable) {
            throw new RuntimeException("Database not available");
        }

        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            if (!entityManager.contains(entity)) {
                entity = entityManager.merge(entity);
            }
            entityManager.remove(entity);
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Error deleting entity: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(ID id) {
        if (!databaseAvailable) {
            throw new RuntimeException("Database not available");
        }

        Optional<T> entity = findById(id);
        if (entity.isPresent()) {
            delete(entity.get());
        }
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public boolean isDatabaseAvailable() {
        return databaseAvailable;
    }
}