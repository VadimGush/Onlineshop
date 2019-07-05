package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.*;
import net.thumbtack.onlineshop.database.models.*;
import net.thumbtack.onlineshop.dto.BuyProductDto;
import net.thumbtack.onlineshop.dto.ClientDto;
import net.thumbtack.onlineshop.service.AdminService;
import net.thumbtack.onlineshop.service.ClientService;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.*;


@SpringBootApplication
public class Main {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(Main.class);

        CategoryDao categoryDao = context.getBean(CategoryDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);

        Category category = new Category("category");
        categoryDao.insert(category);
        categoryDao.update(category);

        Product product = new Product("iphone", 1, 1);
        productDao.insert(product);

        Set<Category> categories = new HashSet<Category>();
        categories.add(category);

        product.setCategories(categories);
        productDao.update(product);

        System.out.println("------------");
        for (Category entity : product.getCategories()) {
            System.out.println(entity.getName());
        }

        categoryDao.delete(category);
    }

}
