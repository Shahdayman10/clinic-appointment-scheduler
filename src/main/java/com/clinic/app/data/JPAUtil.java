package com.clinic.app.data;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JPAUtil {
    private static final String PERSISTENCE_UNIT_NAME = "clinicPU";
    private static EntityManagerFactory entityManagerFactory;
    private static boolean databaseAvailable = true;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            try {
                entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
                System.out.println("Entity Manager Factory created successfully!");
            } catch (Throwable ex) {
                System.err.println("Failed to create EntityManagerFactory: " + ex);
                databaseAvailable = false;
                // We don't throw the exception here to allow the application to continue with mock data
                // throw new ExceptionInInitializerError(ex);
            }
        }
        return entityManagerFactory;
    }

    public static void shutdown() {
        if (entityManagerFactory != null) {
            entityManagerFactory.close();
            System.out.println("Entity Manager Factory closed.");
        }
    }

    public static boolean isDatabaseAvailable() {
        return databaseAvailable && entityManagerFactory != null;
    }
}