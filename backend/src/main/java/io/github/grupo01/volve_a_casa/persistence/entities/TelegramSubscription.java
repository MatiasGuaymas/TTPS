package io.github.grupo01.volve_a_casa.persistence.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "telegram_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"chatId", "pet_id"}))
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TelegramSubscription {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long chatId;

    @ManyToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    public TelegramSubscription(Long chatId, Pet pet) {
        this.chatId = chatId;
        this.pet = pet;
    }
}
