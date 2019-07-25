package net.thumbtack.onlineshop.service.listeners;

import net.thumbtack.onlineshop.service.MailService;
import net.thumbtack.onlineshop.service.events.BasketPurchaseEvent;
import net.thumbtack.onlineshop.service.events.ProductPurchaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Обработчик событий о покупке товара и корзины
 */
@Component
public class PurchaseEventListener {

    private MailService mailService;

    @Autowired
    public PurchaseEventListener(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Уведомляет клиента по электронной почте о покупке товара.
     * <br>
     * Отправка письма производится асинхронно
     *
     * @param event событие покупки товара
     */
    @Async
    @EventListener
    public void mailClientAboutProductPurchase(ProductPurchaseEvent event) {
        mailService.sendBuyProductReport(event.getPurchase());
    }

    /**
     * Уведомляет клиента по электронной почте о покупке корзины
     * <br>
     * Отправка производится асинхронно
     *
     * @param event событие о покупки корзины
     */
    @Async
    @EventListener
    public void mailClientAboutBasketPurchase(BasketPurchaseEvent event) {
        mailService.sendBuyBasketReport(event.getClient(), event.getPurchases());
    }

}
