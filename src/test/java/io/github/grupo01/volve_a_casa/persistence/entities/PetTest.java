package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class PetTest {

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
        @DisplayName("Construcción exitosa de Pet")
        public void testConstruccionExitosa() {
            String nombre = "Firulais";
            String tamano = "Mediano";
            String descripcion = "Perro juguetón";
            String color = "Marrón";
            String raza = "Mestizo";
            float peso = 12.5f;
            float latitud = -34.9f;
            float longitud = -58.4f;
            LocalDate fechaPerdida = LocalDate.of(2024, 1, 15);
            Pet.State estado = Pet.State.PERDIDO_PROPIO;
            Pet.Type tipo = Pet.Type.PERRO;
            String foto = "base64";
            User creador = getUser();

            Pet m = Pet.builder()
                    .nombre(nombre)
                    .tamano(tamano)
                    .descripcion(descripcion)
                    .color(color)
                    .raza(raza)
                    .peso(peso)
                    .latitud(latitud)
                    .longitud(longitud)
                    .fechaPerdida(fechaPerdida)
                    .estado(estado)
                    .tipo(tipo)
                    .agregarFoto(foto)
                    .creador(creador)
                    .build();

            assertNotNull(m);
            assertEquals(nombre, m.getName());
            assertEquals(tamano, m.getSize());
            assertEquals(descripcion, m.getDescription());
            assertEquals(color, m.getColor());
            assertEquals(raza, m.getRace());
            assertEquals(peso, m.getWeight());
            assertEquals(latitud, m.getCoordinates().getLatitude());
            assertEquals(longitud, m.getCoordinates().getLongitude());
            assertEquals(fechaPerdida, m.getLostDate());
            assertEquals(estado, m.getState());
            assertEquals(tipo, m.getType());
            assertEquals(1, m.getPhotosBase64().size());
            assertTrue(m.getPhotosBase64().contains(foto));
            assertEquals(creador, m.getCreator());
            assertEquals(0, m.getSightings().size());
        }

        @Test
        @DisplayName("Falta nombre")
        public void testSinNombre() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .tamano("M")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El nombre es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta tamaño")
        public void testSinTamano() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El tamano es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta descripcion")
        public void testSinDescripcion() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("La descripcion es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta color")
        public void testSinColor() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El color es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta raza")
        public void testSinRaza() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("La raza es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta peso")
        public void testSinPeso() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El peso es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta latitud")
        public void testSinLatitud() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("La latitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta longitud")
        public void testSinLongitud() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("La longitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta fecha de perdida")
        public void testSinFechaPerdida() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("La fecha de perdida es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Falta estado")
        public void testSinState() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El estado es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta tipo")
        public void testSinType() {
            User creador = getUser();
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .agregarFoto("f")
                    .creador(creador)
                    .build());
            assertEquals("El tipo es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Falta creador")
        public void testSinCreador() {
            Exception ex = assertThrows(NullPointerException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .agregarFoto("f")
                    .build());
            assertEquals("El creador es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Debe tener al menos una foto")
        public void testSinFotos() {
            User creador = getUser();
            Exception ex = assertThrows(IllegalArgumentException.class, () -> Pet.builder()
                    .nombre("n")
                    .tamano("t")
                    .descripcion("d")
                    .color("c")
                    .raza("r")
                    .peso(1.0f)
                    .latitud(1.0f)
                    .longitud(1.0f)
                    .fechaPerdida(LocalDate.now())
                    .estado(Pet.State.PERDIDO_PROPIO)
                    .tipo(Pet.Type.PERRO)
                    .creador(creador)
                    .build());
            assertEquals("Debe agregar al menos una foto", ex.getMessage());
        }
    }
}
