package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.models.Client;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;



@SpringBootApplication
public class Main {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Main.class);
    }

}
