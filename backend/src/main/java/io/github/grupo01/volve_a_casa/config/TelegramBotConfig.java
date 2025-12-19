package io.github.grupo01.volve_a_casa.config;

import io.github.grupo01.volve_a_casa.telegram.IATelegramBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(IATelegramBot iaTelegramBot) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(iaTelegramBot);
        System.out.println("✅ Telegram Bot registrado exitosamente - puedes probarlo en Telegram");
        return botsApi;
    }
}
