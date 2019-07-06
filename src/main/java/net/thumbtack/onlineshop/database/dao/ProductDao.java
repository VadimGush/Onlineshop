package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Category;
import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
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
public class ProductDao {

    private EntityManager manager;

    @Autowired
    public ProductDao(EntityManager manager) {
        this.manager = manager;
    }

    public void insert(Product product) {
        manager.persist(product);
    }

    public void update(Product product) {
        manager.merge(product);
    }

    public void delete(Product product) {
        manager.remove(manager.merge(product));
    }

    public void insertCategory(ProductCategory productCategory) {
        manager.persist(productCategory);
    }

    public void deleteCategory(ProductCategory productCategory) {
        manager.remove(manager.merge(productCategory));
    }

    public List<ProductCategory> getCategories(long productId) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ProductCategory> criteria = builder.createQuery(ProductCategory.class);
        Root<ProductCategory> from = criteria.from(ProductCategory.class);

        criteria.select(from);
        criteria.where(
                builder.equal(from.get("product"), productId)
        );

        TypedQuery<ProductCategory> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    public Product get(long id) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> from = criteria.from(Product.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("id"), id));

        TypedQuery<Product> typed = manager.createQuery(criteria);
        try {
            return typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<Product> getAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> from = criteria.from(Product.class);

        criteria.select(from);

        TypedQuery<Product> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

}
