package io.github.vicen621.volveacasa.persistence;

import jakarta.persistence.EntityManager;

public abstract class BaseDAOTest {

    public void cleanDatabase() {
        // Limpio la base de datos
        try (EntityManager em = EntityManagerSingleton.getInstance().createEntityManager()) {
            em.getTransaction().begin();

            // Hay que borrar en el orden correcto para no violar Foreign Keys
            // Tablas "hijas" primero
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
}