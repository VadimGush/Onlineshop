package net.thumbtack.onlineshop.service;

import net.thumbtack.onlineshop.domain.dao.AccountDao;
import net.thumbtack.onlineshop.domain.dao.ProductDao;
import net.thumbtack.onlineshop.domain.dao.PurchaseDao;
import net.thumbtack.onlineshop.domain.dao.SessionDao;
import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.Purchase;
import net.thumbtack.onlineshop.dto.PurchasesDto;
import net.thumbtack.onlineshop.service.events.BasketPurchaseEvent;
import net.thumbtack.onlineshop.service.events.ProductPurchaseEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PurchasesService extends GeneralService {

    public enum Target { CLIENT, PRODUCT }

    private ApplicationEventPublisher eventPublisher;

    private PurchaseDao purchaseDao;
    private ProductDao productDao;
    private AccountDao accountDao;

    @Autowired
    public PurchasesService(
            SessionDao sessionDao,
            PurchaseDao purchaseDao,
            AccountDao accountDao,
            ProductDao productDao,
            ApplicationEventPublisher eventPublisher) {
        super(sessionDao);
        this.purchaseDao = purchaseDao;
        this.eventPublisher = eventPublisher;
        this.accountDao = accountDao;
        this.productDao = productDao;
    }

    /**
     * Сохраняет информацию о покупке одного товара
     *
     * @param client клиент, который совершил покупку
     * @param product товар, который был куплен
     */
    void saveProductPurchase(Account client, Product product, int count) {

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
    void saveBasketPurchase(Account client, Map<Product, Integer> products)  {

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
     * @param id id товара/клиента
     * @return история покупок
     * @throws ServiceException если пользователь не является администратором или
     * указанный товар или клиент не найден
     */
    public PurchasesDto getPurchases(
            String session,
            Target target,
            int offset,
            int limit,
            Long id,
            List<Long> categories) throws ServiceException {

        getAdmin(session);

        if (target == Target.CLIENT) {
            // Списки покупок для клиентов
            return getClientPurchases(offset, limit, id);

        } else {
            // Списки покупок для товаров
            return getProductPurchases(offset, limit, id, categories);
        }

    }

    /**
     * Получает историю покупок для товара или товаров. Для истории покупок товаров можно получить только
     * те записи в истории, товары которых принадлежат определённому списку категорий.
     *
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @param id id товара (null - если нужно получить историю покупок для всех товаров)
     * @param categories список категорий, к которым должны принадлежать товары (null/empty - все товары)
     * @return история покупок
     * @throws ServiceException если товар под данным id не был найден
     */
    private PurchasesDto getProductPurchases(int offset, int limit, Long id, List<Long> categories) throws ServiceException {

        PurchasesDto result = new PurchasesDto();
        List<Purchase> purchases;

        if (id == null) {

            // Выборка по категориям
            if (categories != null && !categories.isEmpty()) {

                // Получаем список товаров, которые относяться к данным категориям
                Set<Product> products = productDao.getAllWithCategories(categories);

                // После того, как получили список товаров, теперь можно
                // найти в истории все записи, принадлежащие данным товарам

                // Создаём список id
                List<Long> productsId = new ArrayList<>();
                products.forEach(p -> productsId.add(p.getId()));

                // Получаем историю покупок для данных товаров
                purchases = purchaseDao.getProductsPurchases(productsId, limit, offset);

            } else {

                // Если нет запроса на получение истории покупок одного товара,
                // то получаем для всех
                purchases = purchaseDao.getPurchasesSortedByProducts(limit, offset);

            }

        } else {
            // Проверяем что такой товар вообще есть
            if (productDao.get(id) == null) {
                throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);
            }

            // Если указан, то для одного товара
            purchases = purchaseDao.getProductPurchases(id, limit, offset);
        }

        purchases.forEach(purchase ->
                result.addPurchase(new PurchasesDto.PurchaseDto(purchase))
        );
        return result;
    }

    /**
     * Получает историю покупок для клиентов/клиента
     *
     * @param offset позиция, с которой необходимо начать выборку
     * @param limit количество записей
     * @param id id клиента (null - если нужно получить историю покупок всех клиентов)
     * @return история покупок
     * @throws ServiceException если клиент под указанным id не найден
     */
    private PurchasesDto getClientPurchases(int offset, int limit, Long id) throws ServiceException {

        PurchasesDto result = new PurchasesDto();
        List<Purchase> purchases;

        if (id == null) {
            // Если нет запроса на получении истории покупок конкретного клиента,
            // то получаем все записи
            purchases = purchaseDao.getPurchasesSortedByClients(limit, offset);

        } else {
            // Проверям что такой аккаунт вообще есть
            if (accountDao.get(id) == null) {
                throw new ServiceException(ServiceException.ErrorCode.USER_NOT_FOUND);
            }

            // Если id указан, значит получим для отдельного клиента
            purchases = purchaseDao.getClientPurchases(id, limit, offset);
        }

        purchases.forEach(purchase ->
                result.addPurchase(new PurchasesDto.PurchaseDto(purchase))
        );
        return result;
    }

}
