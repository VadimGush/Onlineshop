package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Product;
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
public class ProductDao {

    @PersistenceContext
    private EntityManager manager;

    public Product insert(Product product) {
        manager.persist(product);
        return manager.merge(product);
    }

    public void update(Product product) {
        manager.merge(product);
    }

    public void delete(Product product) {
        manager.remove(manager.merge(product));
    }

    public List<Product> getAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> from = criteria.from(Product.class);
        CriteriaQuery<Product> all = criteria.select(from);

        TypedQuery<Product> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

}
