package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.CategoryDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import net.thumbtack.onlineshop.service.ServiceException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class Main {

    public static void main(String... args) throws ServiceException {
        ApplicationContext context = SpringApplication.run(Main.class);

        ProductDao productDao = context.getBean(ProductDao.class);
        CategoryDao categoryDao = context.getBean(CategoryDao.class);

        List<Product> sourceProducts = Arrays.asList(
                new Product("warhouse", 1, 1),
                new Product("bayoneta", 1, 1),
                new Product("anoher", 1, 1),
                new Product("cia", 1, 1),
                new Product("xanother2", 1, 1),
                new Product("arye", 1, 1)
        );

        List<Category> categories = Arrays.asList(
                new Category("cat category"),
                new Category("ama category"),
                new Category("war category")
        );

        // Создаём список продуктов
        productDao.insert(sourceProducts.get(0));
        productDao.insert(sourceProducts.get(1));
        productDao.insert(sourceProducts.get(2));
        productDao.insert(sourceProducts.get(3));
        productDao.insert(sourceProducts.get(4));
        productDao.insert(sourceProducts.get(5));

        // Добавляем категории
        categoryDao.insert(categories.get(0));
        categoryDao.insert(categories.get(1));
        categoryDao.insert(categories.get(2));

        // Связываем продукты с их категориями
        productDao.insertCategory(new ProductCategory(sourceProducts.get(0), categories.get(0)));
        productDao.insertCategory(new ProductCategory(sourceProducts.get(1), categories.get(0)));
        productDao.insertCategory(new ProductCategory(sourceProducts.get(2), categories.get(1)));
        productDao.insertCategory(new ProductCategory(sourceProducts.get(3), categories.get(2)));

        // List<ProductCategory> products = productDao.getAllSorted();
        List<Product> products = productDao.getAllWithoutCategory();

        StringBuilder builder = new StringBuilder();

        /*
        for (ProductCategory product : products) {
            builder.append(product.getProduct().getName() + " : " + product.getCategory().getName());
            builder.append("\n");
        }
         */

        for (Product product : products) {
            builder.append(product.getName());
            builder.append("\n");
        }

        System.out.println("PRODUCTS");
        System.out.println(builder.toString());
    }



}
