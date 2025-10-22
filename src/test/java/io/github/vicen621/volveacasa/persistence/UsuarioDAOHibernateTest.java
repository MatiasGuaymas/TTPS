package io.github.vicen621.volveacasa.persistence;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.factory.DAOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioDAOHibernateTest extends BaseDAOTest {

    private UsuarioDAO usuarioDAO;

    @BeforeEach
    public void setUp() {
        cleanDatabase();
        usuarioDAO = DAOFactory.getUsuarioDAO();
    }

    private Usuario buildTestUser(String email, boolean habilitado) {
        Usuario.Builder builder = Usuario.builder()
                .nombre("Test")
                .apellidos("User")
                .email(email)
                .contrasena("pass123")
                .telefono("12345")
                .ciudad("TestCity")
                .barrio("TestBarrio")
                .latitud(10f)
                .longitud(10f)
                .habilitado(habilitado);

        return builder.build();
    }

    private Usuario buildTestUser(String email, String ciudad, String barrio, Usuario.Rol rol) {
        Usuario.Builder builder = Usuario.builder()
                .nombre("Test")
                .apellidos("User")
                .email(email)
                .contrasena("pass123")
                .telefono("12345")
                .latitud(10f)
                .longitud(10f)
                .ciudad(ciudad)
                .barrio(barrio)
                .rol(rol);

        return builder.build();
    }

    @Test
    void testSaveAndGetByEmail() {
        Usuario nuevoUsuario = buildTestUser("test@user.com", true);
        usuarioDAO.persist(nuevoUsuario);

        Usuario encontrado = usuarioDAO.getByEmail("test@user.com");

        assertNotNull(encontrado);
        assertEquals("test@user.com", encontrado.getEmail());
        assertTrue(encontrado.isHabilitado());


        Usuario noEncontrado = usuarioDAO.getByEmail("no@existe.com");
        assertNull(noEncontrado);
    }

    @Test
    void testDisableUserById() {
        Usuario usuario = buildTestUser("enabled@user.com", true);
        usuarioDAO.persist(usuario);

        Long id = usuario.getId();
        assertNotNull(id); // Si fuera null el usuario no se guardó bien.

        usuarioDAO.disableUser(id);

        Usuario usuarioDeLaBD = usuarioDAO.get(id);
        assertFalse(usuarioDeLaBD.isHabilitado());
    }

    @Test
    void testEnableUserWithObject() {
        Usuario usuario = buildTestUser("disabled@user.com", false);
        usuarioDAO.persist(usuario);
        Long id = usuario.getId();
        assertNotNull(id); // Si fuera null el usuario no se guardó bien.

        usuarioDAO.enableUser(usuario);

        Usuario usuarioDeLaBD = usuarioDAO.get(id);
        assertTrue(usuarioDeLaBD.isHabilitado());
    }

    @Test
    void testGetByBarrio() {
        Usuario u1_Centro = buildTestUser("u1@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);
        Usuario u2_Tolosa = buildTestUser("u2@test.com", "La Plata", "Tolosa", Usuario.Rol.USUARIO);
        Usuario u3_Centro = buildTestUser("u3@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);

        usuarioDAO.persist(u1_Centro);
        usuarioDAO.persist(u2_Tolosa);
        usuarioDAO.persist(u3_Centro);

        List<Usuario> resultados = usuarioDAO.getByBarrio("Centro");

        assertNotNull(resultados, "La lista no debe ser nula");
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios en 'Centro'");

        List<String> emails = resultados.stream()
                .map(Usuario::getEmail)
                .toList();

        assertTrue(emails.contains("u1@test.com"), "Falta el usuario u1");
        assertTrue(emails.contains("u3@test.com"), "Falta el usuario u3");
        assertFalse(emails.contains("u2@test.com"), "El usuario u2 (Tolosa) no debería estar");
    }

    @Test
    void testGetByCiudad() {
        // usuarios en diferentes ciudades
        Usuario u1_LaPlata = buildTestUser("u1@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);
        Usuario u2_Berisso = buildTestUser("u2@test.com", "Berisso", "Centro", Usuario.Rol.USUARIO);
        Usuario u3_LaPlata = buildTestUser("u3@test.com", "La Plata", "Tolosa", Usuario.Rol.USUARIO);

        usuarioDAO.persist(u1_LaPlata);
        usuarioDAO.persist(u2_Berisso);
        usuarioDAO.persist(u3_LaPlata);

        List<Usuario> resultados = usuarioDAO.getByCiudad("La Plata");

        assertNotNull(resultados);
        assertEquals(2, resultados.size(), "Debería encontrar 2 usuarios en 'La Plata'");

        List<String> emails = resultados.stream()
                .map(Usuario::getEmail)
                .toList();

        assertTrue(emails.contains("u1@test.com"));
        assertTrue(emails.contains("u3@test.com"));
        assertFalse(emails.contains("u2@test.com"), "El usuario u2 (Berisso) no debería estar");
    }

    @Test
    void testGetByRol() {
        // Usuarios con diferentes roles
        Usuario u1_User = buildTestUser("u1@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);
        Usuario u2_Admin = buildTestUser("u2@test.com", "La Plata", "Centro", Usuario.Rol.ADMIN);
        Usuario u3_User = buildTestUser("u3@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);

        usuarioDAO.persist(u1_User);
        usuarioDAO.persist(u2_Admin);
        usuarioDAO.persist(u3_User);

        List<Usuario> usuarios = usuarioDAO.getByRol(Usuario.Rol.USUARIO);
        List<Usuario> admins = usuarioDAO.getByRol(Usuario.Rol.ADMIN);

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size(), "Debería encontrar 2 USUARIOs");

        assertNotNull(admins);
        assertEquals(1, admins.size(), "Debería encontrar 1 ADMIN");

        assertEquals("u2@test.com", admins.get(0).getEmail());
    }

    @Test
    void testGetByBarrioSinResultados() {
        Usuario u1_Centro = buildTestUser("u1@test.com", "La Plata", "Centro", Usuario.Rol.USUARIO);
        usuarioDAO.persist(u1_Centro);

        List<Usuario> resultados = usuarioDAO.getByBarrio("BarrioInexistente");

        assertNotNull(resultados, "La lista no debe ser nula, debe estar vacía");
        assertEquals(0, resultados.size(), "No debería encontrar usuarios en un barrio inexistente");
    }
}