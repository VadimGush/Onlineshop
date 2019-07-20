package net.thumbtack.onlineshop.service.events;

import net.thumbtack.onlineshop.database.models.Account;
import net.thumbtack.onlineshop.database.models.Product;
import org.springframework.context.ApplicationEvent;

import java.util.Date;

/**
 * Событие покупки товара
 */
public class ProductPurchaseEvent extends ApplicationEvent {

    // Клиент, который совершил покупку
    private Account client;
    // Товар, который он купил
    private Product product;
    // Время создания события
    private Date date;
    // Сколько товара было выкуплено
    private int count;
    // По какой цене был куплен товар
    private int price;

    public ProductPurchaseEvent(Object source, Account client, Product product, int count, int price) {
        super(source);
        this.client = client;
        this.product = product;
        this.count = count;
        this.price = price;
        this.date = new Date();

    }

    public Account getClient() {
        return client;
    }

    public Product getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }

    public int getCount() {
        return count;
    }
}
