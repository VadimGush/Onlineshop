package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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
     * Получет историю покупок для товара
     * @param productId id товара
     * @return история покупок
     */
    public List<Purchase> getProductPurchases(long productId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("product"), productId)
        );

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    /**
     * Получает историю покупок для клиента
     * @param clientId клиента
     * @return история покупок
     */
    public List<Purchase> getClientPurchases(long clientId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Purchase> criteria = builder.createQuery(Purchase.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("account"), clientId)
        );

        TypedQuery<Purchase> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    /**
     * Удалить всю историю покупок
     */
    public void clear() {
        manager.createNativeQuery("delete from history")
                .executeUpdate();
    }
}
