package javatestbot.demo.repo;

import javatestbot.demo.model.MyMsg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Создаем репозиторий для работы с БД
@Repository
public interface MessageRepository extends JpaRepository<MyMsg, Long>{

}
