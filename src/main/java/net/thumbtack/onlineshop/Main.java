package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.AdministratorDao;
import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.dao.SessionDao;
import net.thumbtack.onlineshop.database.models.Administrator;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.Session;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;


@SpringBootApplication
public class Main {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Main.class);

        CategoryDao categoryDao = context.getBean(CategoryDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);

        Product product1 = new Product("vadim", 1);
        product1 = productDao.insert(product1);

        product1.setName("another");

        productDao.delete(product1);

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
