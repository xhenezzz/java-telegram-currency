package javatestbot.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

//Создаем модель сущности для сохранения сообщений пользователя и ответа сервера
@Data
@AllArgsConstructor @NoArgsConstructor
@Entity
@Table(name = "message_list")
public class MyMsg {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;
    Date date;
    private String username;
    private long chatId;
    private String userMsg;
    private String answer;
}
