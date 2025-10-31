package io.github.vicen621.volveacasa.persistence.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UsuarioTest {

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
            Usuario.Rol rol = Usuario.Rol.ADMIN;
            boolean habilitado = false;

            Usuario usuario = Usuario.builder()
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
            assertEquals(nombre, usuario.getNombre());
            assertEquals(apellidos, usuario.getApellidos());
            assertEquals(email, usuario.getEmail());
            assertEquals(contrasena, usuario.getContrasena());
            assertEquals(telefono, usuario.getTelefono());
            assertEquals(ciudad, usuario.getCiudad());
            assertEquals(barrio, usuario.getBarrio());
            assertEquals(latitud, usuario.getCoordenadas().getLatitud());
            assertEquals(longitud, usuario.getCoordenadas().getLongitud());
            assertFalse(usuario.isHabilitado());
            assertEquals(rol, usuario.getRol());
            assertEquals(puntos, usuario.getPuntos());
            assertEquals(0, usuario.getMedallas().size());
            assertEquals(0, usuario.getAvistamientos().size());
            assertEquals(0, usuario.getMascotasCreadas().size());
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

            Usuario usuario = Usuario.builder()
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
            assertEquals(nombre, usuario.getNombre());
            assertEquals(apellidos, usuario.getApellidos());
            assertEquals(email, usuario.getEmail());
            assertEquals(contrasena, usuario.getContrasena());
            assertEquals(telefono, usuario.getTelefono());
            assertEquals(ciudad, usuario.getCiudad());
            assertEquals(barrio, usuario.getBarrio());
            assertEquals(latitud, usuario.getCoordenadas().getLatitud());
            assertEquals(longitud, usuario.getCoordenadas().getLongitud());
            assertTrue(usuario.isHabilitado());
            assertEquals(Usuario.Rol.USUARIO, usuario.getRol());
            assertEquals(0, usuario.getPuntos());
            assertEquals(0, usuario.getMedallas().size());
            assertEquals(0, usuario.getAvistamientos().size());
            assertEquals(0, usuario.getMascotasCreadas().size());
        }

        @Test
        @DisplayName("Test constructor sin nombre")
        public void testConstruccionSinNombre_throwsNullPointerException() {
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
            Usuario.Builder builder = Usuario.builder()
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
