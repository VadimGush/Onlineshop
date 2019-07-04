package net.thumbtack.onlineshop;

import net.thumbtack.onlineshop.database.dao.BasketDao;
import net.thumbtack.onlineshop.database.dao.ClientDao;
import net.thumbtack.onlineshop.database.dao.ProductDao;
import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Client;
import net.thumbtack.onlineshop.database.models.Product;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.List;


@SpringBootApplication
public class Main {

    public static void main(String... args) {
        ApplicationContext context = SpringApplication.run(Main.class);

        ClientDao clientDao = context.getBean(ClientDao.class);
        ProductDao productDao = context.getBean(ProductDao.class);
        BasketDao basketDao = context.getBean(BasketDao.class);

        Client client = new Client("dfaer", "", "", "", "", "", "");
        clientDao.insert(client);

        Product product = new Product("product1", 1);
        productDao.insert(product);

        Basket basket = new Basket(client, product, 1);
        basketDao.insert(basket);

        List<Basket> list = basketDao.get(client);
        for (Basket element : list) {
            System.out.println(element.getClient().getId() + " - count: " + element.getCount() + " - " + element.getProduct().getId());
        }

        basketDao.delete(basket);

        list = basketDao.get(client);
        for (Basket element : list) {
            System.out.println(element.getClient().getId() + " - count: " + element.getCount() + " - " + element.getProduct().getId());
        }

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
