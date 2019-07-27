package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class PurchaseDao implements Dao {

    private EntityManager manager;

    @Autowired
    public PurchaseDao(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Добавить запись в историю покупок
     */
    public void insert(Purchase entry) {
        manager.persist(entry);
    }

    /**
     * Получает историю покупок отсортированных по товарам
     *
     * @param limit количество записей
     * @param offset с какой записи начать выдачу
     * @return история покупок
     */
    public List<Purchase> getPurchasesSortedByProducts(int limit, int offset) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        criteria.select(from);
        criteria.orderBy(builder.asc(from.get("product")));

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /**
     * Получает историю покупок отсортированных по клиентам
     *
     * @param limit количество записей
     * @param offset с какой записи начать выдачу
     * @return история покупок
     */
    public List<Purchase> getPurchasesSortedByClients(int limit, int offset) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        criteria.select(from);
        criteria.orderBy(builder.asc(from.get("account")));

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /**
     * Получает историю покупок для указанных товаров. Список группируется
     * по товарам
     *
     * @param products список id товаров
     * @param limit количество записей
     * @param offset с какой записи начать выборку
     * @return история покупок
     */
    public List<Purchase> getProductsPurchases(List<Long> products, int limit, int offset) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        // Делаем селект
        criteria.select(from);

        // Оператор IN (product1, product2, ... , productN)
        Expression<Long> expression = from.get("product");
        criteria.where(expression.in(products));

        // Сортируем
        criteria.orderBy(builder.asc(from.get("product")));

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /**
     * Получает историю покупок для указанных клиентов. Список группируется
     * по клиентам
     *
     * @param clients список id клентов
     * @param limit количество записей
     * @param offset с какой записи начать выборку
     * @return история покупок
     */
    public List<Purchase> getClientsPurchases(List<Long> clients, int limit, int offset) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        // Делаем селект
        criteria.select(from);

        // Оператор IN
        Expression<Long> expression = from.get("account");
        criteria.where(expression.in(clients));

        // Сортируем
        criteria.orderBy(builder.asc(from.get("account")));

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.setFirstResult(offset).setMaxResults(limit).getResultList();
    }

    /**
     * Удалить всю историю покупок
     */
    public void clear() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<Purchase> criteria = builder.createCriteriaDelete(Purchase.class);

        criteria.from(Purchase.class);

        manager.createQuery(criteria).executeUpdate();
    }
}
