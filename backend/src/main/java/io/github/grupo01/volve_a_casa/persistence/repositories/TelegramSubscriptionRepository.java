package io.github.grupo01.volve_a_casa.persistence.repositories;

import io.github.grupo01.volve_a_casa.persistence.entities.TelegramSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TelegramSubscriptionRepository extends JpaRepository<TelegramSubscription, Long> {
    
    Optional<TelegramSubscription> findByChatIdAndPetId(Long chatId, Long petId);
    
    List<TelegramSubscription> findByPetId(Long petId);
    
    List<TelegramSubscription> findByChatId(Long chatId);
    
    void deleteByChatIdAndPetId(Long chatId, Long petId);
}
