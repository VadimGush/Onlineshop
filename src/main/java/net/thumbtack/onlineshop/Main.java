package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.EventDao;
import net.thumbtack.onlineshop.database.models.Event;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Date;

@SpringBootApplication
public class Main {

    public static void main(String... args) {

        ApplicationContext context = SpringApplication.run(Main.class);

        EventDao dao = context.getBean(EventDao.class);
        Event event = dao.insertEvent(new Event("new event", new Date()));
        System.out.println(event.toString());



    }

}
