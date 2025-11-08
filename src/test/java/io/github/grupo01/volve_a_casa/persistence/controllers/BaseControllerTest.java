package io.github.grupo01.volve_a_casa.persistence.controllers;

import io.github.grupo01.volve_a_casa.persistence.TestPersistenceConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class BaseControllerTest {
    static AnnotationConfigApplicationContext ctx;
    static EntityManager em;

    public static void createContext() {
        ctx = new AnnotationConfigApplicationContext();
        // Registra la clase de configuration (PersistenceConfig)
        ctx.register(TestPersistenceConfig.class);
        // Refresca para actualizar la creacion de beans
        ctx.refresh();
        em = ctx.getBean(EntityManagerFactory.class).createEntityManager();
    }

    /*public void cleanDatabase() {
        // Hay que borrar en el orden correcto para no violar Foreign Keys
        // Tablas "hijas" primero
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Sighting").executeUpdate();
        em.createNativeQuery("DELETE FROM mascota_fotos").executeUpdate(); // La tabla de @ElementCollection
        em.createQuery("DELETE FROM Pet").executeUpdate();

        // Tablas "raíz" al final
        em.createNativeQuery("DELETE FROM usuario_medallas").executeUpdate(); // La tabla de @ManyToMany
        em.createQuery("DELETE FROM Medal").executeUpdate();
        em.createQuery("DELETE FROM Message").executeUpdate();
        em.createQuery("DELETE FROM User").executeUpdate();
        em.getTransaction().commit();
    }*/
}
