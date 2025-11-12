package io.github.grupo01.volve_a_casa.persistence.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SightingTest {

    @Nested
    class BuilderTest {
        private User getUser(String nombre, String apellidos, String email) {
            return User.builder()
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .email(email)
                    .contrasena("1234")
                    .telefono("123456789")
                    .ciudad("La Plata")
                    .barrio("Centro")
                    .latitud(-34.6037f)
                    .longitud(-58.3816f)
                    .build();
        }

        private Pet getPet(User creador) {
            return Pet.builder()
                    .nombre("Firulais")
                    .tamano("Mediano")
                    .descripcion("Perro mestizo")
                    .color("Marrón")
                    .raza("Mestizo")
                    .peso(12.5f)
                    .latitud(-34.6037f)
                    .longitud(-58.3816f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("fotoBase64")
                    .creador(creador)
                    .build();
        }

        @Test
        @DisplayName("Construcción exitosa de Avistamiento")
        public void testConstruccionExitosa() {
            User owner = getUser("Rodolfo Alfredo", "Bertone", "bertone@info.unlp.edu.ar");
            User reporter = getUser("Laura Cristina", "De Giusti", "ldgiusti@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            float latitud = -34.9214f;
            float longitud = -57.9544f;
            String foto = "base64string";
            LocalDate fecha = LocalDate.of(2024, 5, 20);
            String comentario = "Lo vi cerca del parque";

            Sighting av = Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .latitud(latitud)
                    .longitud(longitud)
                    .fotoBase64(foto)
                    .fecha(fecha)
                    .comentario(comentario)
                    .build();

            assertNotNull(av);
            assertEquals(mascota, av.getPet());
            // Testeo que la relación sea bidireccional
            assertTrue(mascota.getSightings().contains(av));
            assertEquals(reporter, av.getReporter());
            // Testeo que la relación sea bidireccional
            assertTrue(reporter.getSightings().contains(av));
            assertEquals(latitud, av.getCoordinates().getLatitude());
            assertEquals(longitud, av.getCoordinates().getLongitude());
            assertEquals(foto, av.getPhotoBase64());
            assertEquals(fecha, av.getDate());
            assertEquals(comentario, av.getComment());
        }

        @Test
        @DisplayName("Test constructor sin comentario")
        public void testConstruccionExitosaSinComentario() {
            User owner = getUser("Laura Andrea", "Fava", "lfava@info.unlp.edu.ar");
            User reporter = getUser("Alejandro", "Fernandez", "fernandez@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            float latitud = -34.9214f;
            float longitud = -57.9544f;
            String foto = "base64string";
            LocalDate fecha = LocalDate.of(2024, 5, 20);

            Sighting av = Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .latitud(latitud)
                    .longitud(longitud)
                    .fotoBase64(foto)
                    .fecha(fecha)
                    .build();

            assertNotNull(av);
            assertEquals(mascota, av.getPet());
            assertEquals(reporter, av.getReporter());
            assertEquals(latitud, av.getCoordinates().getLatitude());
            assertEquals(longitud, av.getCoordinates().getLongitude());
            assertEquals(foto, av.getPhotoBase64());
            assertEquals(fecha, av.getDate());
            assertEquals("", av.getComment());
        }

        @Test
        @DisplayName("Falta mascota")
        public void testSinPet() {
            User reporter = getUser("Alejandra", "Garrido", "garrido@info.unlp.edu.ar");

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .reportador(reporter)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La mascota es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta reportador")
        public void testSinReportador() {
            User owner = getUser("Ivana", "Harari", "iharari@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .mascota(mascota)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("El reportador es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta latitud")
        public void testSinLatitud() {
            User owner = getUser("Alejandra Beatriz", "Lliteras", "lliteras@info.unlp.edu.ar");
            User reporter = getUser("Ricardo Marcelo", "Naiouf", "naiouf@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La latitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta longitud")
        public void testSinLongitud() {
            User owner = getUser("Cecilia Verónica", "Sanz", "csanz@info.unlp.edu.ar");
            User reporter = getUser("Pablo Javier", "Thomas", "pthomas@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .latitud(1.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La longitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta foto")
        public void testSinFoto() {
            User owner = getUser("Diego", "Torres", "torres@info.unlp.edu.ar");
            User reporter = getUser("Franco", "Chichizola", "chichizola@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La foto es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta fecha")
        public void testSinFecha() {
            User owner = getUser("Rodolfo Alfredo", "Bertone", "bertone@info.unlp.edu.ar");
            User reporter = getUser("Laura Cristina", "De Giusti", "ldgiusti@info.unlp.edu.ar");
            Pet mascota = getPet(owner);

            Exception ex = assertThrows(NullPointerException.class, () -> Sighting.builder()
                    .mascota(mascota)
                    .reportador(reporter)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .build());
            assertEquals("La fecha es obligatoria", ex.getMessage());
        }
    }
}