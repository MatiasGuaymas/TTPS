package io.github.vicen621.volveacasa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class SightingTest {
/*
    @Nested
    class BuilderTest {
        private User getUser() {
            return User.builder()
                    .nombre("test")
                    .apellidos("test")
                    .email("test@gmail.com")
                    .contrasena("1234")
                    .telefono("123456789")
                    .ciudad("CiudadTest")
                    .barrio("BarrioTest")
                    .latitud(-34.6037f)
                    .longitud(-58.3816f)
                    .build();
        }

        @Test
        @DisplayName("Construcción exitosa de Avistamiento")
        public void testConstruccionExitosa() {
            Pet mascota = new Pet(); // constructor protegido pero accesible en el mismo paquete
            User reportador = new User();

            float latitud = 12.34f;
            float longitud = -56.78f;
            String foto = "base64string";
            LocalDate fecha = LocalDate.of(2024, 5, 20);
            String comentario = "Lo vi cerca del parque";

            Avistamiento av = Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(latitud)
                    .longitud(longitud)
                    .fotoBase64(foto)
                    .fecha(fecha)
                    .comentario(comentario)
                    .build();

            assertNotNull(av);
            assertEquals(mascota, av.getPet());
            // Testo que la relación sea bidireccional
            assertTrue(mascota.getAvistamientos().contains(av));
            assertEquals(reportador, av.getReportador());
            // Testeo que la relación sea bidireccional
            assertTrue(reportador.getAvistamientos().contains(av));
            assertEquals(latitud, av.getCoordenadas().getLatitud());
            assertEquals(longitud, av.getCoordenadas().getLongitud());
            assertEquals(foto, av.getFotoBase64());
            assertEquals(fecha, av.getFecha());
            assertEquals(comentario, av.getComentario());
        }


        @Test
        @DisplayName("Test constructor sin comentario")
        public void testConstruccionExitosaSinComentario() {
            Pet mascota = new Pet(); // constructor protegido pero accesible en el mismo paquete
            User reportador = new User();

            float latitud = 12.34f;
            float longitud = -56.78f;
            String foto = "base64string";
            LocalDate fecha = LocalDate.of(2024, 5, 20);

            Avistamiento av = Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(latitud)
                    .longitud(longitud)
                    .fotoBase64(foto)
                    .fecha(fecha)
                    .build();

            assertNotNull(av);
            assertEquals(mascota, av.getPet());
            assertEquals(reportador, av.getReportador());
            assertEquals(latitud, av.getCoordenadas().getLatitud());
            assertEquals(longitud, av.getCoordenadas().getLongitud());
            assertEquals(foto, av.getFotoBase64());
            assertEquals(fecha, av.getFecha());
            assertEquals("", av.getComentario());
        }

        @Test
        @DisplayName("Falta mascota")
        public void testSinPet() {
            User reportador = new User();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .reportador(reportador)
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
            Pet mascota = new Pet();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
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
            Pet mascota = new Pet();
            User reportador = new User();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La latitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta longitud")
        public void testSinLongitud() {
            Pet mascota = new Pet();
            User reportador = new User();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(1.0f)
                    .fotoBase64("f")
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La longitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta foto")
        public void testSinFoto() {
            Pet mascota = new Pet();
            User reportador = new User();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fecha(LocalDate.now())
                    .build());
            assertEquals("La foto es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta fecha")
        public void testSinFecha() {
            Pet mascota = new Pet();
            User reportador = new User();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .build());
            assertEquals("La fecha es obligatoria", ex.getMessage());
        }
    }*/
}
