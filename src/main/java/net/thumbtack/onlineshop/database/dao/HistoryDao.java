package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.HistoryEntry;
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
public class HistoryDao implements Dao {

    private EntityManager manager;

    @Autowired
    public HistoryDao(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Добавить запись в историю покупок
     */
    public void insert(HistoryEntry entry) {
        manager.persist(entry);
    }

    /**
     * Получет историю покупок для товара
     * @param productId id товара
     * @return история покупок
     */
    public List<HistoryEntry> getProductHistory(long productId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<HistoryEntry> criteria = builder.createQuery(HistoryEntry.class);
        Root<HistoryEntry> from = criteria.from(HistoryEntry.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("product"), productId)
        );

        TypedQuery<HistoryEntry> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    /**
     * Получает историю покупок для клиента
     * @param clientId клиента
     * @return история покупок
     */
    public List<HistoryEntry> getClientHistory(long clientId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<HistoryEntry> criteria = builder.createQuery(HistoryEntry.class);
        Root<HistoryEntry> from = criteria.from(HistoryEntry.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("account"), clientId)
        );

        TypedQuery<HistoryEntry> typed = manager.createQuery(criteria);
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
