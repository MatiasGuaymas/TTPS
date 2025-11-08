package io.github.grupo01.volve_a_casa.persistence.entities;

import io.github.grupo01.volve_a_casa.persistence.entities.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    @Nested
    class BuilderTest {

        @Test
        @DisplayName("Test constructor exitoso")
        public void testConstruccionExitosa() {
            String nombre = "Juan";
            String apellidos = "Perez";
            String email = "juan@test.com";
            String contrasena = "password";
            String telefono = "221 111-1111";
            String ciudad = "La Plata";
            String barrio = "Villa Elisa";
            float latitud = 54.3f;
            float longitud = -2.1f;
            int puntos = 800;
            User.Role rol = User.Role.ADMIN;
            boolean habilitado = false;

            User usuario = User.builder()
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .email(email)
                    .contrasena(contrasena)
                    .telefono(telefono)
                    .ciudad(ciudad)
                    .barrio(barrio)
                    .latitud(latitud)
                    .longitud(longitud)
                    .habilitado(habilitado)
                    .rol(rol)
                    .puntos(puntos)
                    .build();

            assertNotNull(usuario);
            assertEquals(nombre, usuario.getName());
            assertEquals(apellidos, usuario.getLastName());
            assertEquals(email, usuario.getEmail());
            assertEquals(contrasena, usuario.getPassword());
            assertEquals(telefono, usuario.getPhone());
            assertEquals(ciudad, usuario.getCity());
            assertEquals(barrio, usuario.getNeighborhood());
            assertEquals(latitud, usuario.getCoordinates().getLatitude());
            assertEquals(longitud, usuario.getCoordinates().getLongitude());
            assertFalse(usuario.isEnabled());
            assertEquals(rol, usuario.getRole());
            assertEquals(puntos, usuario.getPoints());
            assertEquals(0, usuario.getMedals().size());
            assertEquals(0, usuario.getSightings().size());
            assertEquals(0, usuario.getCreatedPets().size());
        }

        @Test
        @DisplayName("Construccion exitosa con parametros por defecto")
        public void testConstruccionExitosaConParametrosPorDefecto() {
            String nombre = "Juan";
            String apellidos = "Perez";
            String email = "juan@test.com";
            String contrasena = "password";
            String telefono = "221 111-1111";
            String ciudad = "La Plata";
            String barrio = "Villa Elisa";
            float latitud = 54.3f;
            float longitud = -2.1f;

            User usuario = User.builder()
                    .nombre(nombre)
                    .apellidos(apellidos)
                    .email(email)
                    .contrasena(contrasena)
                    .telefono(telefono)
                    .ciudad(ciudad)
                    .barrio(barrio)
                    .latitud(latitud)
                    .longitud(longitud)
                    .build();

            assertNotNull(usuario);
            assertEquals(nombre, usuario.getName());
            assertEquals(apellidos, usuario.getLastName());
            assertEquals(email, usuario.getEmail());
            assertEquals(contrasena, usuario.getPassword());
            assertEquals(telefono, usuario.getPhone());
            assertEquals(ciudad, usuario.getCity());
            assertEquals(barrio, usuario.getNeighborhood());
            assertEquals(latitud, usuario.getCoordinates().getLatitude());
            assertEquals(longitud, usuario.getCoordinates().getLongitude());
            assertTrue(usuario.isEnabled());
            assertEquals(User.Role.USER, usuario.getRole());
            assertEquals(0, usuario.getPoints());
            assertEquals(0, usuario.getMedals().size());
            assertEquals(0, usuario.getSightings().size());
            assertEquals(0, usuario.getCreatedPets().size());
        }

        @Test
        @DisplayName("Test constructor sin nombre")
        public void testConstruccionSinNombre_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("El nombre es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin apellidos")
        public void testConstructorSinApellidos_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("Los apellidos son obligatorios", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin email")
        public void testConstructorSinEmail_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("El email es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin contraseña")
        public void testConstructorSinContrasena_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("La contraseña es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin telefono")
        public void testConstructorSinTelefono_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("El teléfono es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin ciudad")
        public void testConstructorSinCiudad_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .barrio("Villa Elisa")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("La ciudad es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin barrio")
        public void testConstructorSinBarrio_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .latitud(54.3f)
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("El barrio es obligatorio", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin latitud")
        public void testConstructorSinLatitud_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .longitud(-2.1f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("La latitud es obligatoria", ex.getMessage());
        }

        @Test
        @DisplayName("Test constructor sin longitud")
        public void testConstructorSinLongitud_throwsNullPointerException() {
            User.Builder builder = User.builder()
                    .nombre("Juan")
                    .apellidos("Perez")
                    .email("juan@test.com")
                    .contrasena("password")
                    .telefono("221 111-1111")
                    .ciudad("La Plata")
                    .barrio("Villa Elisa")
                    .latitud(54.3f);

            Exception ex = assertThrows(NullPointerException.class, builder::build);
            assertEquals("La longitud es obligatoria", ex.getMessage());
        }
    }
}
