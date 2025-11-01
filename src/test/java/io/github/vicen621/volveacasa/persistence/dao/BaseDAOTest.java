package io.github.vicen621.volveacasa.persistence.dao;

import io.github.vicen621.volveacasa.persistence.TestPersistenceConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public abstract class BaseDAOTest {
    static AnnotationConfigApplicationContext ctx;
    static EntityManager em;

    public static void createContext() {
        ctx = new AnnotationConfigApplicationContext();
        // Registra la clase de configuration (PersistenceConfig)
        ctx.register(TestPersistenceConfig.class);
        // Refresca para actualizar la creacion de beans
        ctx.refresh();
        BaseDAOTest.em = ctx.getBean(EntityManagerFactory.class).createEntityManager();
    }

    public void cleanDatabase() {
        // Hay que borrar en el orden correcto para no violar Foreign Keys
        // Tablas "hijas" primero
        em.getTransaction().begin();
        em.createQuery("DELETE FROM Avistamiento").executeUpdate();
        em.createNativeQuery("DELETE FROM mascota_fotos").executeUpdate(); // La tabla de @ElementCollection
        em.createQuery("DELETE FROM Mascota").executeUpdate();

        // Tablas "raíz" al final
        em.createNativeQuery("DELETE FROM usuario_medallas").executeUpdate(); // La tabla de @ManyToMany
        em.createQuery("DELETE FROM Medalla").executeUpdate();
        em.createQuery("DELETE FROM Mensaje").executeUpdate();
        em.createQuery("DELETE FROM Usuario").executeUpdate();
        em.getTransaction().commit();
    }
}