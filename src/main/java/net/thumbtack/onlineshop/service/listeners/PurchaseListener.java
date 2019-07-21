package net.thumbtack.onlineshop.service.listeners;

import net.thumbtack.onlineshop.domain.dao.PurchaseDao;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.Purchase;
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
public class PurchaseListener {

    private PurchaseDao purchaseDao;
    private MailService mailService;

    @Autowired
    public PurchaseListener(PurchaseDao purchaseDao, MailService mailService) {
        this.purchaseDao = purchaseDao;
        this.mailService = mailService;
    }

    /**
     * Записывает информацию о покупке товара в историю покупок
     *
     * @param event событие покупки товара
     */
    @EventListener
    public void saveProductPurchaseInHistory(ProductPurchaseEvent event) {
        // Теперь делаем запись в историю покупок
        purchaseDao.insert(
                new Purchase(
                        event.getProduct(),
                        event.getClient(),
                        event.getDate(),
                        event.getCount(),
                        event.getPrice()
                )
        );
    }

    /**
     * Записываем информацию о покупке корзины в историю покупок
     *
     * @param event событие покупки корзины
     */
    @EventListener
    public void saveBasketPurchaseInHistory(BasketPurchaseEvent event) {
        // Делаем запись в истории покупок о каждой позиции в списке
        for (BasketPurchaseEvent.Entry entry : event.getList()) {
            purchaseDao.insert(
                    new Purchase(
                            entry.getProduct(),
                            event.getClient(),
                            event.getDate(),
                            entry.getCount(),
                            entry.getPrice()
                    )
            );
        }
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
        Product product = event.getProduct();

        // Отправляем одним письмом
        mailService.sendMessage(
                event.getClient().getFirstName(),
                event.getClient().getEmail(),
                "Уведомление о покупке товара",
                "<h4>Уведомление о покупке товара</h4>" +
                        createProductEntry(product.getName(), event.getCount(), event.getPrice()) +
                        "<hr>" +
                        "<b>Общая сумма:</b> " + (event.getCount() * event.getPrice()) + " руб." +
                        "<br><br>" +
                        "<b>Спасибо за покупку!</b>"
        );
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
        // Формируем список покупок
        StringBuilder purchaseList = new StringBuilder();

        // Посчитаем сумму всей покупки
        int sum = 0;

        for (BasketPurchaseEvent.Entry entry : event.getList()) {
            purchaseList.append(
                    createProductEntry(entry.getProduct().getName(), entry.getCount(), entry.getPrice())
            );
            purchaseList.append("<hr>");

            sum += entry.getCount() * entry.getPrice();
        }

        // Отправляем всё одним письмом
        mailService.sendMessage(
                event.getClient().getFirstName(),
                event.getClient().getEmail(),
                "Уведомление о покупке товаров",
                "<h4>Уведомление о покупке товаров</h4>" +
                        purchaseList.toString() +
                        "<b>Общая сумма:</b> " + sum + " руб." +
                        "<br><br>" +
                        "<b>Спасибо за покупку!</b>"
        );
    }

    private String createProductEntry(String name, int count, int price) {
        return
                "<b>Товар:</b>" + name + "<br>" +
                "<b>Количество:</b> " + count + "<br>" +
                "<b>Цена за единицу:</b> " + price + "руб. <br>";
    }

}
