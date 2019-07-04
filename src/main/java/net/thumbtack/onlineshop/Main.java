package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.AccountDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.AccountFactory;
import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.service.AdminService;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;


@SpringBootApplication
public class Main {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Main.class);

        AdminService adminService = context.getBean(AdminService.class);
        AccountDao clientDao = context.getBean(AccountDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);
        BasketDao basketDao = context.getBean(BasketDao.class);

        /*
        Category category1 = new Category("category1");
        Category category2 = new Category("category2");
        categoryDao.insert(category1);
        categoryDao.insert(category2);

        Product product2 = new Product("another", 1, Arrays.asList(category1, category2));
        productDao.insert(product2);
        System.out.println(product2.getCategories().size());

        productDao.delete(product1);
         */
    }

}
