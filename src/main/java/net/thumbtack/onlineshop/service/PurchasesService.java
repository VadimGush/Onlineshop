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

        Purchase purchase = new Purchase(product, client, new Date(), count, product.getPrice());

        // Сохраняем запись о покупке товара
        purchaseDao.insert(purchase);

        // Создаём событие о покупке товара
        eventPublisher.publishEvent(new ProductPurchaseEvent(this, purchase));
    }

    /**
     * Сохраняет информацию о покупке корзины
     *
     * @param client клиент, который совершил покупку
     * @param products список товаров и количество того, сколько их было выкуплено
     */
    void saveBasketPurchase(Account client, Map<Product, Integer> products)  {

        List<Purchase> purchases = new ArrayList<>();

        // Сохраняем записи о покупке товаров
        products.forEach((product, count) -> {

            Purchase purchase = new Purchase(product, client, new Date(), count, product.getPrice());

            // Записываем в БД инфу о покупке
            purchaseDao.insert(purchase);

            // Записываем в событие инфу о покупке одного товара
            purchases.add(purchase);
        });

        // Создаём событие о покупке корзины
        eventPublisher.publishEvent(new BasketPurchaseEvent(this, client, purchases));
    }

    /**
     * Получает историю покупок для товара или клиента
     *
     * @param session сессия пользователя
     * @param target целевая группа для выборки (клиенты или товары)
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @param ids id товаров/клиентов
     * @return история покупок
     * @throws ServiceException если пользователь не является администратором или
     * указанный товар или клиент не найден
     */
    public PurchasesDto getPurchases(
            String session,
            Target target,
            int offset,
            int limit,
            List<Long> ids,
            List<Long> categories) throws ServiceException {

        getAdmin(session);

        if (target == Target.CLIENT) {
            // Списки покупок для клиентов
            return getClientPurchases(offset, limit, ids);

        } else {
            // Списки покупок для товаров
            return getProductPurchases(offset, limit, ids, categories);
        }

    }

    /**
     * Получает историю покупок для товара или товаров. Для истории покупок товаров можно получить только
     * те записи в истории, товары которых принадлежат определённому списку категорий.
     *
     * @param offset позиция начала выборки
     * @param limit размер выборки
     * @param productsId список id товаров, для которых нужно получить историю покупок
     * @param categories список категорий, к которым должны принадлежать товары (null/empty - все товары)
     * @return история покупок
     * @throws ServiceException если товар под данным id не был найден
     */
    private PurchasesDto getProductPurchases(int offset, int limit, List<Long> productsId, List<Long> categories) throws ServiceException {

        PurchasesDto result = new PurchasesDto();
        List<Purchase> purchases;

        if (productsId == null || productsId.isEmpty()) {

            // Выборка по категориям
            if (categories != null && !categories.isEmpty()) {

                // Получаем список товаров, которые относяться к данным категориям
                Set<Product> products = productDao.getAllWithCategories(categories);

                // Формируем список их id-ов
                List<Long> resultProductsId = new ArrayList<>();
                products.forEach(p -> resultProductsId.add(p.getId()));

                // Получаем историю покупок для данных товаров
                purchases = purchaseDao.getProductsPurchases(resultProductsId, limit, offset);

            } else {

                // Если список категорий не указан, то получаем для всех
                purchases = purchaseDao.getPurchasesSortedByProducts(limit, offset);
            }

        } else {

            // Проверяем что каждый товар в списке существует
            for (Long id : productsId) {
                if (!productDao.exists(id)) {
                    throw new ServiceException(ServiceException.ErrorCode.PRODUCT_NOT_FOUND);
                }
            }

            // Получаем список покупок для товара/товаров
            purchases = purchaseDao.getProductsPurchases(productsId, limit, offset);
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
     * @param clientsId список id клиентов (клиента), для которых нужно получить историю покупок
     * @return история покупок
     * @throws ServiceException если клиент под указанным id не найден
     */
    private PurchasesDto getClientPurchases(int offset, int limit, List<Long> clientsId) throws ServiceException {

        PurchasesDto result = new PurchasesDto();
        List<Purchase> purchases;

        if (clientsId == null || clientsId.isEmpty()) {

            // Получаем историю покупок для всех клиентов
            purchases = purchaseDao.getPurchasesSortedByClients(limit, offset);

        } else {
            // Проверяем что каждый клиент в списке существует
            for (Long id : clientsId) {
                if (!accountDao.exists(id)) {
                    throw new ServiceException(ServiceException.ErrorCode.USER_NOT_FOUND);
                }
            }

            // Получаем список покупок для клиента/клиентов
            purchases = purchaseDao.getClientsPurchases(clientsId, limit, offset);
        }

        purchases.forEach(purchase ->
                result.addPurchase(new PurchasesDto.PurchaseDto(purchase))
        );
        return result;
    }

}
