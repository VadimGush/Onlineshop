package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Account;
import net.thumbtack.onlineshop.domain.models.Basket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class BasketDao implements Dao {

    private EntityManager manager;

    @Autowired
    public BasketDao(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Сохраняет в БД запись в корзине
     *
     * @param basket запись в корзине
     */
    public void insert(Basket basket) {
        manager.persist(basket);
    }

    /**
     * Обновляет запись в корзине
     *
     * @param basket запись в корзине
     */
    public void update(Basket basket) {
        manager.merge(basket);
    }

    /**
     * Удаляет запись в корзине
     *
     * @param basket запись в корзине
     */
    public void delete(Basket basket) {
        manager.remove(manager.merge(basket));
    }

    /**
     * Получает запись из корзины
     * <br>
     * <b>Внимание:</b> в записи корзины указывается ссылка на продукт в БД и рядом поле
     * с количеством этого продукта, который заказывал клиент. Получается что у нас
     * два поля с количеством. Один из таблицы product, который говорит сколько у нас
     * товара на складе, а другой в таблице basket, который говорит
     * сколько товара клиент положил в свою корзину.
     * <br>
     * Поэтому количество товара в корзине надо получать только через Basket.getCount()
     * (Никакого Basket.getProduct().getCount() - это количество товара на складе!)
     *
     * @param account   пользователь, которому принадлежит корзина
     * @param productId id товара
     * @return запись из корзины
     */
    public Basket get(Account account, long productId) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Basket> criteria = builder.createQuery(Basket.class);
        Root<Basket> from = criteria.from(Basket.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("account"), account.getId()),
                builder.and(),
                builder.equal(from.get("product"), productId)
        );

        TypedQuery<Basket> typed = manager.createQuery(criteria);
        try {
            return typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }


    /**
     * Получает весь список записей в корзине
     *
     * @param account пользователь, которому принадлежит корзина
     * @return содержимое корзины
     */
    public List<Basket> get(Account account) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Basket> criteria = builder.createQuery(Basket.class);
        Root<Basket> from = criteria.from(Basket.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("account"), account.getId())
        );

        TypedQuery<Basket> typed = manager.createQuery(criteria);

        return typed.getResultList();

    }

    /**
     * Удаляет всю таблицу записей в корзинах
     */
    public void clear() {
        manager.createNativeQuery("delete from basket")
                .executeUpdate();
    }
}
