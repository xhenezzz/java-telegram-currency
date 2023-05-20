package javatestbot.demo.model;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import jakarta.annotation.PostConstruct;
import javatestbot.demo.config.BotConfig;
import javatestbot.demo.repo.MessageRepository;
import javatestbot.demo.service.TelegramBotApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class MyBot {
    //Вызываем конфиг нашего бота, сервис, а также репозиторий и модель сущности для репозитория
    BotConfig botConfig;

    @Autowired
    private final TelegramBotApiService botApiService;

    @Autowired
    private MessageRepository repository;

    MyMsg model = new MyMsg();

    public MyBot(TelegramBotApiService botApiService, BotConfig botConfig, MessageRepository messageRepository) {
        this.botApiService = botApiService;
        this.botConfig = botConfig;
        this.repository = messageRepository;
    }

    //Инициализируем нашего бота
    //@PostConstruct - это аннотация, которая используется для указания метода, который должен быть выполнен сразу после создания бина и завершения его инициализации.
    @PostConstruct
    public void init() {
        TelegramBot bot = new TelegramBot(botConfig.getBotToken());

        bot.setUpdatesListener(updates -> {
            //Прогоняем каждый апдейт
            for (Update update : updates) {
                //Если сообщение имеется и оно не пустое
                if (update.message() != null && update.message().text() != null) {
                    //Получаем наше сообщение
                    Message incomingMessage = update.message();
                    String text = incomingMessage.text();
                    String answer = " ";
                    //Парсим команды
                    switch (text){
                        case "/start":
                            bot.execute(new SendMessage(update.message().chat().id(), "Введите мне валюту а я ее конвертирую!\n" +
                                    "Пример сообщения: 1 EUR KZT"));
                            break;
                        default:
                            try {
                                //Сохраняем ответ метода по конвертации в переменную answer для дольнейшего сохранения
                                answer = botApiService.getCurrencyV2(text);
                                bot.execute(new SendMessage(update.message().chat().id(), answer));
                            } catch (IOException e) {
                                //Дальше идет обработка исключений, чтобы бот не отрубился, и клиент хотя бы понял в чем проблема.
                                bot.execute(new SendMessage(update.message().chat().id(),"Бот сломался пожалуйста подождите!"));
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            } catch (NumberFormatException e){
                                e.printStackTrace();
                                bot.execute(new SendMessage(update.message().chat().id(),"Неправильно указано количество денег!"));
                            }
                    }
                    //Сохраняем информацию с чата в нашу модель, а дальше и в БД
                    model.setDate(new Date());
                    model.setUsername(update.message().chat().username());
                    model.setChatId(update.message().chat().id());
                    model.setAnswer(answer);
                    model.setUserMsg(update.message().text());
                    repository.save(model);
                }
            }
            //CONFIRMED_UPDATES_ALL гарантирует, что все обновления будут подтверждены и не будут повторно отправляться Telegram Bot API.
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}