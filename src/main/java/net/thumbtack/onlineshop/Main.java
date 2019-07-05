package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.BuyProductDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.service.AdminService;
import net.thumbtack.onlineshop.service.ClientService;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;


@SpringBootApplication
public class Main {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(Main.class);

        AdminService adminService = context.getBean(AdminService.class);
        ClientService clientService = context.getBean(ClientService.class);
        AccountDao clientDao = context.getBean(AccountDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);
        BasketDao basketDao = context.getBean(BasketDao.class);
        SessionDao sessionDao = context.getBean(SessionDao.class);

        Account account = AccountFactory.createClient(
                "vadim", "wrew", "werew", "werewr", "werwe", "werew", "wrew"
        );
        Account client = clientService.register(new ClientDto(account));
        // Ну и надо вручную его залогинить
        sessionDao.insert(new Session("token", client));

        System.out.println("Registered user with id: " + client.getId().toString());

        Product product = new Product("iphone", 1, 1000);
        productDao.insert(product);

        long id = productDao.getAll().get(0).getId();
        System.out.println("Registered product with id: " + id);

        // Теперь добавим товар в корзину
        List<Basket> basket = clientService.addToBasket("token", new BuyProductDto(
                id, "iphone", 1000, 10
        ));

        System.out.println("=== BASKET ===");
        for (Basket entity : basket) {
            System.out.println("    " + entity.getProduct().getName() + ": " + entity.getCount());
        }

        // теперь ещё немного товара
        basket = clientService.addToBasket("token", new BuyProductDto(
                id, "iphone", 1000, 10
        ));

        System.out.println("=== BASKET ===");
        for (Basket entity : basket) {
            System.out.println("    " + entity.getProduct().getName() + ": " + entity.getCount());
        }


        productDao.delete(product);

        basket = clientService.getBasket("token");

        System.out.println("=== BASKET ===");
        for (Basket entity : basket) {
            System.out.println("    " + entity.getProduct().getName() + ": " + entity.getCount());
        }

    }

}
