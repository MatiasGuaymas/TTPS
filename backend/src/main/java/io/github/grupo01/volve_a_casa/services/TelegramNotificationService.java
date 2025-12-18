package io.github.grupo01.volve_a_casa.services;

import io.github.grupo01.volve_a_casa.persistence.entities.Pet;
import io.github.grupo01.volve_a_casa.persistence.entities.Sighting;
import io.github.grupo01.volve_a_casa.persistence.entities.TelegramSubscription;
import io.github.grupo01.volve_a_casa.persistence.repositories.PetRepository;
import io.github.grupo01.volve_a_casa.persistence.repositories.TelegramSubscriptionRepository;
import io.github.grupo01.volve_a_casa.telegram.IATelegramBot;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TelegramNotificationService {

    @Autowired
    private TelegramSubscriptionRepository subscriptionRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    @Lazy
    private IATelegramBot telegramBot;

    @Transactional
    public String suscribir(Long chatId, Long petId) {
        Optional<Pet> petOpt = petRepository.findById(petId);
        
        if (petOpt.isEmpty()) {
            return "❌ No se encontró ninguna mascota con el ID " + petId + ".";
        }

        Pet pet = petOpt.get();
        
        // Verificar si ya está suscrito
        if (subscriptionRepository.findByChatIdAndPetId(chatId, petId).isPresent()) {
            return "⚠️ Ya estás suscrito a las notificaciones de " + pet.getName() + ".";
        }

        TelegramSubscription subscription = new TelegramSubscription(chatId, pet);
        subscriptionRepository.save(subscription);
        
        return "✅ Te has suscrito exitosamente a las notificaciones de avistamientos de " + pet.getName() + " 🐾";
    }

    @Transactional
    public String desuscribir(Long chatId, Long petId) {
        Optional<Pet> petOpt = petRepository.findById(petId);
        
        if (petOpt.isEmpty()) {
            return "❌ No se encontró ninguna mascota con el ID " + petId + ".";
        }

        Pet pet = petOpt.get();
        
        Optional<TelegramSubscription> subscription = subscriptionRepository.findByChatIdAndPetId(chatId, petId);
        
        if (subscription.isEmpty()) {
            return "⚠️ No estás suscrito a las notificaciones de " + pet.getName() + ".";
        }

        subscriptionRepository.deleteByChatIdAndPetId(chatId, petId);
        
        return "✅ Te has desuscrito de las notificaciones de " + pet.getName() + ".";
    }

    public void notificarAvistamiento(Sighting sighting) {
        List<TelegramSubscription> subscriptions = subscriptionRepository.findByPetId(sighting.getPet().getId());
        
        String message = "🔔 *Nueva notificación de avistamiento*\n\n" +
                "🐾 Mascota: *" + sighting.getPet().getName() + "*\n" +
                "📍 Ubicación reportada\n" +
                "📅 Fecha: " + sighting.getDate() + "\n\n" +
                "💬 Comentario: " + (sighting.getComment() != null ? sighting.getComment() : "Sin comentarios") + "\n\n" +
                "👉 Ver detalles en la app";
        
        for (TelegramSubscription subscription : subscriptions) {
            telegramBot.sendNotification(subscription.getChatId(), message);
        }
    }

    public List<TelegramSubscription> obtenerSuscripciones(Long chatId) {
        return subscriptionRepository.findByChatId(chatId);
    }
}
