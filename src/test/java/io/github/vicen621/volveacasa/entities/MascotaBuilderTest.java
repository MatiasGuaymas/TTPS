package io.github.vicen621.volveacasa.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class MascotaBuilderTest {

    private Usuario creadorDummy() {
        return new Usuario();
    }

    @Test
    @DisplayName("Construcción exitosa de Mascota")
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
        Mascota.Estado estado = Mascota.Estado.PERDIDO_PROPIO;
        Mascota.Tipo tipo = Mascota.Tipo.PERRO;
        String foto = "base64";
        Usuario creador = creadorDummy();

        Mascota m = Mascota.builder()
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
        assertEquals(nombre, m.getNombre());
        assertEquals(tamano, m.getTamano());
        assertEquals(descripcion, m.getDescripcion());
        assertEquals(color, m.getColor());
        assertEquals(raza, m.getRaza());
        assertEquals(peso, m.getPeso());
        assertEquals(latitud, m.getCoordenadas().getLatitud());
        assertEquals(longitud, m.getCoordenadas().getLongitud());
        assertEquals(fechaPerdida, m.getFechaPerdida());
        assertEquals(estado, m.getEstado());
        assertEquals(tipo, m.getTipo());
        assertEquals(1, m.getFotosBase64().size());
        assertTrue(m.getFotosBase64().contains(foto));
        assertEquals(creador, m.getCreador());
        assertEquals(0, m.getAvistamientos().size());
    }

    @Test
    @DisplayName("Falta nombre")
    public void testSinNombre() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .tamano("M")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El nombre es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta tamaño")
    public void testSinTamano() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El tamano es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta descripcion")
    public void testSinDescripcion() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("La descripcion es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("Falta color")
    public void testSinColor() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El color es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta raza")
    public void testSinRaza() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("La raza es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("Falta peso")
    public void testSinPeso() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El peso es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta latitud")
    public void testSinLatitud() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("La latitud es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("Falta longitud")
    public void testSinLongitud() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("La longitud es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("Falta fecha de perdida")
    public void testSinFechaPerdida() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("La fecha de perdida es obligatoria", ex.getMessage());
    }

    @Test
    @DisplayName("Falta estado")
    public void testSinEstado() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El estado es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta tipo")
    public void testSinTipo() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .agregarFoto("f")
                .creador(creador)
                .build());
        assertEquals("El tipo es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Falta creador")
    public void testSinCreador() {
        Exception ex = assertThrows(NullPointerException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .agregarFoto("f")
                .build());
        assertEquals("El creador es obligatorio", ex.getMessage());
    }

    @Test
    @DisplayName("Debe tener al menos una foto")
    public void testSinFotos() {
        Usuario creador = creadorDummy();
        Exception ex = assertThrows(IllegalArgumentException.class, () -> Mascota.builder()
                .nombre("n")
                .tamano("t")
                .descripcion("d")
                .color("c")
                .raza("r")
                .peso(1.0f)
                .latitud(1.0f)
                .longitud(1.0f)
                .fechaPerdida(LocalDate.now())
                .estado(Mascota.Estado.PERDIDO_PROPIO)
                .tipo(Mascota.Tipo.PERRO)
                .creador(creador)
                .build());
        assertEquals("Debe agregar al menos una foto", ex.getMessage());
    }
}
