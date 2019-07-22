package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Purchase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
     * Общее количество записей в истории покупок
     *
     * @return количество записей
     */
    public Long getCount() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Purchase> from = criteria.from(Purchase.class);

        criteria.select(builder.count(from));

        TypedQuery<Long> typed = manager.createQuery(criteria);
        return typed.getSingleResult();
    }

    /**
     * Получет историю покупок отсортированных по товарам
     *
     * @return история покупок
     */
    public List<Purchase> getProductsPurchases(long limit, long offset) {
        Query query = manager.createNativeQuery(
                "select * from purchase order by product_id limit " + limit + " offset " + offset,
                Purchase.class);

        return query.getResultList();
    }

    /**
     * Получает историю покупок отсортированных по клиентам
     *
     * @return история покупок
     */
    public List<Purchase> getClientsPurchases(long limit, long offset) {
        Query query = manager.createNativeQuery(
                "select * from purchase order by account_id limit " + limit + " offset " + offset,
                Purchase.class);

        return query.getResultList();
    }

    /**
     * Удалить всю историю покупок
     */
    public void clear() {
        manager.createNativeQuery("delete from purchase")
                .executeUpdate();
    }
}
