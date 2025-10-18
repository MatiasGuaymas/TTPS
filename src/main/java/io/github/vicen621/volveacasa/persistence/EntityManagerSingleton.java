package io.github.vicen621.volveacasa.persistence;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class EntityManagerSingleton {
    private static final EntityManagerFactory INSTANCE;

    static {
        try {
            INSTANCE = Persistence.createEntityManagerFactory("io.github.vicen621.volveacasa.jpa");
        } catch (Exception e) {
            System.err.println("Initial EntityManagerFactory creation failed: " + e.getMessage());
            throw new ExceptionInInitializerError(e);
        }
    }

    private EntityManagerSingleton() {
    }

    public static EntityManagerFactory getInstance() {
        return INSTANCE;
    }
}
