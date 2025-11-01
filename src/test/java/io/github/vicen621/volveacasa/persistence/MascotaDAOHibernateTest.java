package io.github.vicen621.volveacasa.persistence;

import io.github.vicen621.volveacasa.persistence.dao.MascotaDAO;
import io.github.vicen621.volveacasa.persistence.dao.UsuarioDAO;
import io.github.vicen621.volveacasa.persistence.dao.filtros.MascotaFilter;
import io.github.vicen621.volveacasa.persistence.entities.Mascota;
import io.github.vicen621.volveacasa.persistence.entities.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class MascotaDAOHibernateTest extends BaseDAOTest {
	static MascotaDAO mascotaDAO;
    static UsuarioDAO usuarioDAO;
	private Usuario usuarioTest;

	@BeforeAll
	static void init() {
        createContext();
		mascotaDAO = ctx.getBean(MascotaDAO.class);
        usuarioDAO = ctx.getBean(UsuarioDAO.class);
	}

    @BeforeEach
    void setUp() {
        cleanDatabase();
        usuarioTest = Usuario.builder()
                .nombre("Juan")
                .apellidos("Perez")
                .email("juan.perez@test.com")
                .contrasena("1234")
                .telefono("123456789")
                .ciudad("CiudadTest")
                .barrio("BarrioTest")
                .latitud(-34.6037f)
                .longitud(-58.3816f)
                .build();
        usuarioDAO.persist(usuarioTest);
    }

	private Mascota buildTestMascota(String nombre, Mascota.Tipo tipo, Mascota.Estado estado, String raza, String color) {
		return Mascota.builder()
				.nombre(nombre)
				.tamano("Mediano")
				.descripcion("Descripcion de prueba")
				.color(color)
				.raza(raza)
				.peso(10.0f)
				.latitud(-34.6037f)
				.longitud(-58.3816f)
				.fechaPerdida(LocalDate.now())
				.estado(estado)
				.tipo(tipo)
				.agregarFoto("fotoBase64Test")
				.creador(usuarioTest)
				.build();
	}

	@Test
	void testGetByTipo() {
		Mascota perro = buildTestMascota("Perro1", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Negro");
		Mascota gato = buildTestMascota("Gato1", Mascota.Tipo.GATO, Mascota.Estado.PERDIDO_PROPIO, "Siames", "Blanco");
		mascotaDAO.persist(perro);
		mascotaDAO.persist(gato);

        mascotaDAO.get(perro.getId());

		List<Mascota> resultados = mascotaDAO.getFiltered(MascotaFilter.builder().conTipo(Mascota.Tipo.PERRO).build());
		Assertions.assertNotNull(resultados);
		Assertions.assertEquals(1, resultados.size());
		Assertions.assertEquals("Perro1", resultados.get(0).getNombre());
	}

	@Test
	void testGetByEstado() {
		Mascota m1 = buildTestMascota("Mascota1", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Negro");
		Mascota m2 = buildTestMascota("Mascota2", Mascota.Tipo.PERRO, Mascota.Estado.ADOPTADO, "Mestizo", "Negro");
		mascotaDAO.persist(m1);
		mascotaDAO.persist(m2);

		List<Mascota> resultados = mascotaDAO.getFiltered(MascotaFilter.builder().conEstado(Mascota.Estado.ADOPTADO).build());
		Assertions.assertNotNull(resultados);
		Assertions.assertEquals(1, resultados.size());
		Assertions.assertEquals(Mascota.Estado.ADOPTADO, resultados.get(0).getEstado());
	}

	@Test
	void testGetByRaza() {
		Mascota m1 = buildTestMascota("Mascota1", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Negro");
		Mascota m2 = buildTestMascota("Mascota2", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Caniche", "Negro");
		mascotaDAO.persist(m1);
		mascotaDAO.persist(m2);

		List<Mascota> resultados = mascotaDAO.getFiltered(MascotaFilter.builder().conRaza("Caniche").build());
		Assertions.assertNotNull(resultados);
		Assertions.assertEquals(1, resultados.size());
		Assertions.assertEquals("Caniche", resultados.get(0).getRaza());
	}

	@Test
	void testGetByColor() {
		Mascota m1 = buildTestMascota("Mascota1", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Negro");
		Mascota m2 = buildTestMascota("Mascota2", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Blanco");
		mascotaDAO.persist(m1);
		mascotaDAO.persist(m2);

		List<Mascota> resultados = mascotaDAO.getFiltered(MascotaFilter.builder().conColor("Blanco").build());
		Assertions.assertNotNull(resultados);
		Assertions.assertEquals(1, resultados.size());
		Assertions.assertEquals("Blanco", resultados.get(0).getColor());
	}

	@Test
	void testGetByTipoSinResultados() {
		Mascota perro = buildTestMascota("Perro1", Mascota.Tipo.PERRO, Mascota.Estado.PERDIDO_PROPIO, "Mestizo", "Negro");
		mascotaDAO.persist(perro);

		List<Mascota> resultados = mascotaDAO.getFiltered(MascotaFilter.builder().conTipo(Mascota.Tipo.GATO).build());
		Assertions.assertNotNull(resultados);
		Assertions.assertEquals(0, resultados.size());
	}
}

