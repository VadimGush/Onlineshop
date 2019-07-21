package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.PurchaseDao;
import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.Purchase;
import net.thumbtack.onlineshop.service.events.BasketPurchaseEvent;
import net.thumbtack.onlineshop.service.events.ProductPurchaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PurchasesService extends GeneralService {

    public enum Target { CLIENT, PRODUCT };

    private ApplicationEventPublisher eventPublisher;

    private PurchaseDao purchaseDao;

    @Autowired
    public PurchasesService(
            SessionDao sessionDao,
            PurchaseDao purchaseDao,
            ApplicationEventPublisher eventPublisher) {
        super(sessionDao);
        this.purchaseDao = purchaseDao;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Сохраняет информацию о покупке одного товара
     *
     * @param client клиент, который совершил покупку
     * @param product товар, который был куплен
     */
    public void saveProductPurchase(Account client, Product product, int count) {

        // Сохраняем запись о покупке товара
        purchaseDao.insert(
                new Purchase(product, client, new Date(), count, product.getPrice())
        );

        // Создаём событие о покупке товара
        eventPublisher.publishEvent(new ProductPurchaseEvent(
                this, product, client, count, product.getPrice()
        ));
    }

    /**
     * Сохраняет информацию о покупке корзины
     *
     * @param client клиент, который совершил покупку
     * @param products список товаров и количество того, сколько их было выкуплено
     */
    public void saveBasketPurchase(Account client, Map<Product, Integer> products)  {

        // Создаём событие о покупке товара
        BasketPurchaseEvent event = new BasketPurchaseEvent(this, client);

        // Сохраняем записи о покупке товаров
        products.forEach((product, count) -> {

            // Записываем в БД инфу о покупке
            purchaseDao.insert(
                    new Purchase(product, client, new Date(), count, product.getPrice())
            );

            // Записываем в событие инфу о покупке одного товара
            event.put(product, count, product.getPrice());
        });

        // Создаём событие о покупке корзины
        eventPublisher.publishEvent(event);
    }

    /**
     * Получает историю покупок для товара или клиента
     *
     * @param session сессия пользователя
     * @param target целевая группа для выборки (клиенты или товары)
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @return история покупок
     * @throws ServiceException если пользователь не является администратором
     */
    public String getPurchases(String session, Target target, int offset, int limit) throws ServiceException {

        getAdmin(session);

        if (target == Target.CLIENT) {
            return getClientsPurchases(offset, limit);
        } else {
            return getProductsPurchases(offset, limit);
        }
    }

    /**
     * Получает историю покупок для клиентов
     *
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @return историю покупок
     */
    private String getClientsPurchases(int offset, int limit) {
        return "{}";
    }

    /**
     * Получает историю покупок для товара
     *
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @return историю покупок
     */
    private String getProductsPurchases(int offset, int limit) {
        return "{}";
    }
}
