package io.github.vicen621.volveacasa.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Objects;

@Data
@Entity
@Table(name="mensajes")
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Mensaje {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;

    private LocalDate fecha;

    @ManyToOne
    @JoinColumn(name="destinatario_id")
    private Usuario destinatario;

    @ManyToOne
    @JoinColumn(name="emisor_id")
    private Usuario emisor;

    public Mensaje(String contenido, LocalDate fecha, Usuario destinatario, Usuario emisor) {
        this.contenido = contenido;
        this.fecha = fecha;
        this.destinatario = destinatario;
        this.emisor = emisor;
    }
}
