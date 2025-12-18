package io.github.grupo01.volve_a_casa.telegram;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Servicio para acceder a los mensajes del bot de Telegram.
 * Lee los mensajes desde telegram-messages.properties y permite formatearlos con parámetros.
 */
@Component
public class TelegramMessages {

    private final MessageSource messageSource;
    private static final Locale LOCALE = new Locale("es", "AR");

    @Autowired
    public TelegramMessages(@Qualifier("telegramMessageSource") MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Obtiene un mensaje sin parámetros.
     * 
     * @param key Clave del mensaje en telegram-messages.properties
     * @return El mensaje formateado
     */
    public String get(String key) {
        return messageSource.getMessage(key, null, LOCALE);
    }

    /**
     * Obtiene un mensaje con parámetros.
     * 
     * @param key Clave del mensaje en telegram-messages.properties
     * @param params Parámetros para reemplazar en el mensaje (ej: {0}, {1})
     * @return El mensaje formateado con los parámetros
     */
    public String get(String key, Object... params) {
        return messageSource.getMessage(key, params, LOCALE);
    }

    public String welcome() {
        return get("bot.welcome");
    }

    public String commandUnknown() {
        return get("bot.command.unknown");
    }

    public String help() {
        return String.join("\n\n",
                get("help.title"),
                get("help.mascotas"),
                get("help.mascota"),
                get("help.perdida"),
                get("help.preguntar"),
                get("help.suscribir"),
                get("help.desuscribir"),
                get("help.cancelar"),
                get("help.comandos")
        );
    }
}
