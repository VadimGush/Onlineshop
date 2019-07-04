package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Client;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public class BasketDao {

    @PersistenceContext
    private EntityManager manager;



    public void insert(Basket basket) {
        manager.persist(basket);
    }

    public void update(Basket basket) {
        manager.merge(basket);
    }

    public void delete(Basket basket) {
        manager.remove(manager.merge(basket));
    }

    public List<Basket> get(Client client) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Basket> criteria = builder.createQuery(Basket.class);
        Root<Basket> from = criteria.from(Basket.class);

        criteria.select(from).where(
                builder.equal(from.get("client"), client.getId())
        );

        TypedQuery<Basket> typed = manager.createQuery(criteria);
        return typed.getResultList();

    }
}
