package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class OnlineShopServer {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(OnlineShopServer.class);
    }



}
