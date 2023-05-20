package javatestbot.demo.config;

import com.pengrad.telegrambot.TelegramBot;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


//Это наш конфигурационный файл, куда мы вписываем токен бота(сам токен хранится в конфигурационном файле)
@Configuration
@Data
@PropertySource("application.properties")
public class BotConfig {
    //Парсим токен из конфигурационного файла
    @Value("${BOT_TOKEN}")
    String botToken;


    //Создаем бин для настройки экземпляра TelegramBot
    @Bean
    public TelegramBot telegramBot() {
        return new TelegramBot(botToken);
    }
}
