package net.thumbtack.onlineshop.service.events;

import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * Событие покуки корзины
 */
public class BasketPurchaseEvent extends ApplicationEvent {

    // Клиент, который совершил покупку
    private Account client;
    // Список покупок
    private List<Purchase> purchases;

    public BasketPurchaseEvent(Object source, Account client, List<Purchase> purchases) {
        super(source);
        this.client = client;
        this.purchases = purchases;
    }

    public List<Purchase> getPurchases() {
        return purchases;
    }

    public Account getClient() {
        return client;
    }
}
