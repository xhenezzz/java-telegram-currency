package javatestbot.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;


@Service
@PropertySource("application.properties")
//Сервис нашего бота предназначен для реализации основной бизнес логики
public class TelegramBotApiService {
    //URL первого API
    private static final String API_URL = "https://api.apilayer.com/exchangerates_data/convert";
    //первый API_KEY для работы с API
    @Value("${API_KEY}")
    private static String API_KEY;

    //второй API KEY
    @Value("${API.KEY}")
    private static String apikey;

    //Метод реализующих конвертацию валют
    public String getCurrencyRate(String message) throws IOException {
        //Получаем сообщение от пользователя и разбиваем его на 3 переменные, попутно удаляя пробелы!
        String[] msg = message.split(" ");
        double amount = Double.parseDouble(msg[0]);
        String fromCurrency = msg[1];
        String toCurrency = msg[2];
        //Создаем клиент для вызова HTTP, чтобы в дальнейшем отправлять запросы на API
        //Данный API порой ложится поэтому я выставил время ожидания ответа чуть побольше
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(300,TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(API_URL + "?to=" + toCurrency + "&from=" + fromCurrency + "&amount=" + amount)
                .addHeader("apikey", API_KEY)
                .method("GET", null)
                .build();
        //Создаем экземпляр класса Call, представляющий асинхронный вызов запроса. client - это экземпляр класса OkHttpClient, который используется для выполнения HTTP-запросов.
        Response response = client.newCall(request).execute();
        //Получаем текстовый ответ от API
        String responseBody = response.body().string();

        //Парсим нужные нам данные, а именно основную валюту - таргетную валюту - исходную сумму и полученный результат
        JSONObject jsonObject = new JSONObject(responseBody);
        JSONObject query = jsonObject.getJSONObject("query");
        BigDecimal myAmount = query.getBigDecimal("amount");
        String from = query.getString("from");
        String to = query.getString("to");
        BigDecimal result = jsonObject.getBigDecimal("result");
        //Преобразим все в опрятный вид при помощи String.format
        //%.2f - спецификатор для форматирования числа с плавающей точкой (f), с двумя знаками после запятой
        //%s - спецификатор для форматирования строки
        String answer = String.format("%.2f %s = %.2f %s", myAmount, from, result, to);
        //Возвращаем ответ
        return answer;
    }

    //Принцип второго метода идентичен, просто тут я решил взять API, который вроде-как работает стабильно
    public String getCurrencyV2(String message) throws IOException{
        String[] msg = message.split(" ");
        double amount = Double.parseDouble(msg[0]);
        String from = msg[1];
        String to = msg[2];
        // Setting URL
        String url_str = "https://v6.exchangerate-api.com/v6/" + apikey + "/pair/"+from+"/"+to+"/"+amount;

        // Making Request
        OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(300,TimeUnit.SECONDS).build();
        Request request = new Request.Builder()
                .url(url_str)
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();

        JSONObject jsonObject = new JSONObject(responseBody);
        String baseCode = jsonObject.getString("base_code");
        String targetCode = jsonObject.getString("target_code");
        BigDecimal result = jsonObject.getBigDecimal("conversion_result");
        String answer = String.format("%.2f %s = %.2f %s", amount, baseCode, result, targetCode);

        return answer;
    }
}

