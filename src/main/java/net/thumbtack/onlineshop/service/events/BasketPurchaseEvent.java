package net.thumbtack.onlineshop.service.events;

import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Product;
import org.springframework.context.ApplicationEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Событие покуки корзины
 */
public class BasketPurchaseEvent extends ApplicationEvent {

    // Клиент, который совершил покупку
    private Account client;
    // Время покупки
    private Date date;
    // Список купленных товаров
    private List<Entry> list;

    public BasketPurchaseEvent(Object source, Account client) {
        super(source);
        this.client = client;
        this.date = new Date();
        this.list = new ArrayList<>();
    }

    public void put(Product product, int count, int price) {
        list.add(new Entry(product, count, price));
    }

    public Account getClient() {
        return client;
    }

    public Date getDate() {
        return date;
    }

    public List<Entry> getList() {
        return list;
    }

    public class Entry {

        // Какой товар был куплен
        private Product product;
        // Сколько товара
        private int count;
        // За какую цену
        private int price;

        public Entry(Product product, int count, int price) {
            this.product = product;
            this.count = count;
            this.price = price;
        }

        public Product getProduct() {
            return product;
        }

        public int getCount() {
            return count;
        }

        public int getPrice() {
            return price;
        }
    }

}
