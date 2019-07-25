package net.thumbtack.onlineshop.service.events;

import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.context.ApplicationEvent;

/**
 * Событие покупки товара
 */
public class ProductPurchaseEvent extends ApplicationEvent {

    private Purchase purchase;

    public ProductPurchaseEvent(Object source, Purchase purchase) {
        super(source);
        this.purchase = purchase;
    }

    public Purchase getPurchase() {
        return purchase;
    }
}
