package io.github.vicen621.volveacasa.persistence.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class AvistamientoTest {

    // TODO: Ver como eliminar un avistamiento desde el usuario

    @Nested
    class BuilderTest {

        @Test
        @DisplayName("Construcción exitosa de Avistamiento")
        public void testConstruccionExitosa() {
            Mascota mascota = new Mascota(); // constructor protegido pero accesible en el mismo paquete
            Usuario reportador = new Usuario();

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
            assertEquals(mascota, av.getMascota());
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
            Mascota mascota = new Mascota(); // constructor protegido pero accesible en el mismo paquete
            Usuario reportador = new Usuario();

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
            assertEquals(mascota, av.getMascota());
            assertEquals(reportador, av.getReportador());
            assertEquals(latitud, av.getCoordenadas().getLatitud());
            assertEquals(longitud, av.getCoordenadas().getLongitud());
            assertEquals(foto, av.getFotoBase64());
            assertEquals(fecha, av.getFecha());
            assertEquals("", av.getComentario());
        }

        @Test
        @DisplayName("Falta mascota")
        public void testSinMascota() {
            Usuario reportador = new Usuario();
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
            Mascota mascota = new Mascota();
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
            Mascota mascota = new Mascota();
            Usuario reportador = new Usuario();
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
            Mascota mascota = new Mascota();
            Usuario reportador = new Usuario();
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
            Mascota mascota = new Mascota();
            Usuario reportador = new Usuario();
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
            Mascota mascota = new Mascota();
            Usuario reportador = new Usuario();
            Exception ex = assertThrows(NullPointerException.class, () -> Avistamiento.builder()
                    .mascota(mascota)
                    .reportador(reportador)
                    .latitud(1.0f)
                    .longitud(2.0f)
                    .fotoBase64("f")
                    .build());
            assertEquals("La fecha es obligatoria", ex.getMessage());
        }
    }
}
