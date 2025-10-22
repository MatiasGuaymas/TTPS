package io.github.vicen621.volveacasa.persistence;

import io.github.vicen621.volveacasa.entities.Usuario;
import io.github.vicen621.volveacasa.persistence.dao.GenericDAO;
import io.github.vicen621.volveacasa.persistence.factory.DAOFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class GenericDaoHibernateTest extends BaseDAOTest {

    GenericDAO<Usuario> usuarioDAO;

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

    @BeforeEach
    void setUp() {
        cleanDatabase();
        usuarioDAO = DAOFactory.getUsuarioDAO();
    }

    @Test
    void testPersist() {
        Usuario nuevoUsuario = buildTestUser("persist@test.com", true);
        assertNull(nuevoUsuario.getId(), "El ID debe ser nulo antes de persistir");

        usuarioDAO.persist(nuevoUsuario);

        assertNotNull(nuevoUsuario.getId(), "El ID debe ser asignado por la BD después de persistir");
    }

    @Test
    void testGet() {
        Usuario usuario = buildTestUser("get@test.com", true);
        usuarioDAO.persist(usuario);
        Long id = usuario.getId();

        Usuario recuperado = usuarioDAO.get(id);

        assertNotNull(recuperado, "No se encontró el usuario que se acaba de guardar");
        assertEquals(id, recuperado.getId(), "El ID recuperado no coincide");
        assertEquals("get@test.com", recuperado.getEmail(), "El email recuperado no coincide");
    }

    @Test
    void testGetNonExistent() {
        Usuario recuperado = usuarioDAO.get(1L);

        assertNull(recuperado, "Se encontró un usuario que no debería existir");
    }

    @Test
    void testUpdate() {
        Usuario usuario = buildTestUser("update@test.com", true);
        usuarioDAO.persist(usuario);
        Long id = usuario.getId();

        // modifico el usuario
        usuario.setNombre("NombreCambiado");
        usuario.setTelefono("987654");

        usuarioDAO.update(usuario);

        Usuario actualizado = usuarioDAO.get(id);

        assertNotNull(actualizado);
        assertEquals("NombreCambiado", actualizado.getNombre());
        assertEquals("987654", actualizado.getTelefono());
    }

    @Test
    void testDeleteByEntity() {
        Usuario usuario = buildTestUser("delete@test.com", true);
        usuarioDAO.persist(usuario);
        Long id = usuario.getId();

        assertNotNull(usuarioDAO.get(id), "El usuario no se guardó correctamente");

        usuarioDAO.delete(usuario);

        assertNull(usuarioDAO.get(id), "El usuario no fue borrado de la BD");
    }

    @Test
    void testDeleteById() {
        Usuario usuario = buildTestUser("deleteid@test.com", true);
        usuarioDAO.persist(usuario);
        Long id = usuario.getId();

        assertNotNull(usuarioDAO.get(id), "El usuario no se guardó correctamente");

        usuarioDAO.delete(id);

        assertNull(usuarioDAO.get(id), "El usuario no fue borrado de la BD");
    }

    @Test
    void testGetAll() {
        Usuario u1 = buildTestUser("user1@test.com", true);
        Usuario u2 = buildTestUser("user2@test.com", true);
        usuarioDAO.persist(u1);
        usuarioDAO.persist(u2);

        List<Usuario> lista = usuarioDAO.getAll(null); // Probar sin orden

        assertNotNull(lista);
        assertEquals(2, lista.size(), "La cantidad de usuarios recuperados no es 2");
    }

    @Test
    void testGetAllOrderBy() {
        // usuarios con nombres en desorden alfabético
        Usuario usuarioB = buildTestUser("beto@test.com", true);
        usuarioB.setNombre("Beto");
        usuarioDAO.persist(usuarioB);

        Usuario usuarioA = buildTestUser("ana@test.com", true);
        usuarioA.setNombre("Ana");
        usuarioDAO.persist(usuarioA);

        // Lista ordenada por campo nombre
        List<Usuario> listaOrdenada = usuarioDAO.getAll("nombre");

        assertNotNull(listaOrdenada);
        assertEquals(2, listaOrdenada.size());

        // Verifico el orden
        assertEquals("Ana", listaOrdenada.get(0).getNombre(), "El primer elemento debería ser Ana");
        assertEquals("Beto", listaOrdenada.get(1).getNombre(), "El segundo elemento debería ser Beto");
    }
}