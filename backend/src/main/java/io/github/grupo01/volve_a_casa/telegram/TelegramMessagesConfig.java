package io.github.grupo01.volve_a_casa.telegram;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

/**
 * Configuración para cargar mensajes de Telegram desde archivo properties.
 */
@Configuration
public class TelegramMessagesConfig {

    @Bean(name = "telegramMessageSource")
    public MessageSource telegramMessageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("telegram-messages");
        messageSource.setDefaultEncoding(StandardCharsets.UTF_8.name());
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
}
