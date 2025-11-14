package io.github.grupo01.volve_a_casa.persistence.entities.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class Coordinates {
    private float latitude;
    private float longitude;
}
