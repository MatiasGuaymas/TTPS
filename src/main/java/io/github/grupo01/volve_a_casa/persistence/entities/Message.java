package io.github.grupo01.volve_a_casa.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Data
@Entity
@Table(name="messages")
@Component
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Message {
    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name="destinatario_id")
    private User receiver;

    @ManyToOne
    @JoinColumn(name="emisor_id")
    private User sender;

    public Message(String content, LocalDate date, User receiver, User sender) {
        this.content = content;
        this.date = date;
        this.receiver = receiver;
        this.sender = sender;
    }
}
