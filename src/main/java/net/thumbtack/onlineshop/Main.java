package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.*;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(Main.class);

        CategoryDao categoryDao = context.getBean(CategoryDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);
        BasketDao basketDao = context.getBean(BasketDao.class);
        AccountDao accountDao = context.getBean(AccountDao.class);

        Category category = new Category("category");
        categoryDao.insert(category);

        Product product = new Product("iphone", 1, 1);
        productDao.insert(product);

        Account client = AccountFactory.createClient(
                "ewrew", "werew", "werew", "werw", "werew","werwe", "w"
        );
        accountDao.insert(client);

        Basket basket = new Basket(client, product, 10);
        basketDao.insert(basket);
    }


}
