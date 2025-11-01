package io.github.vicen621.volveacasa.persistence.entities.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Coordenadas {
    private float latitud;
    private float longitud;
}
