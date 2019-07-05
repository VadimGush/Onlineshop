package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Basket;
import net.thumbtack.onlineshop.database.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    private EntityManager manager;

    @Autowired
    public BasketDao(EntityManager manager) {
        this.manager = manager;
    }


    public void insert(Basket basket) {
        manager.persist(basket);
    }

    public void update(Basket basket) {
        manager.merge(basket);
    }

    public void delete(Basket basket) {
        manager.remove(manager.merge(basket));
    }

    public Basket get(Account account, long productId) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Basket> criteria = builder.createQuery(Basket.class);
        Root<Basket> from = criteria.from(Basket.class);

        criteria.select(from).where(
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


    public List<Basket> get(Account account) {

        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Basket> criteria = builder.createQuery(Basket.class);
        Root<Basket> from = criteria.from(Basket.class);

        criteria.select(from).where(
                builder.equal(from.get("account"), account.getId())
        );

        TypedQuery<Basket> typed = manager.createQuery(criteria);

        return typed.getResultList();

    }
}
