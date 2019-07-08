package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Product;
import net.thumbtack.onlineshop.database.models.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
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
        // Товар не удаляется из БД, но помечается удалённым
        // по той причине, что ТЗ требует чтобы клиент мог хранить в корзине
        // уже удалённые товары
        // manager.remove(manager.merge(product));
        product.setDeleted(true);
        manager.merge(product);
    }

    public void insertCategory(ProductCategory productCategory) {
        manager.persist(productCategory);
    }

    public void deleteCategory(ProductCategory productCategory) {
        manager.remove(manager.merge(productCategory));
    }

    /**
     * @param productId id товара
     * @return список категорий для данного товара
     */
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

    /**
     * @param id id товара
     * @return товар по указанному id. null - если товар не найден
     */
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

    /**
     * @return возвращает весь список товаров
     */
    public List<Product> getAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Product> criteria = builder.createQuery(Product.class);
        Root<Product> from = criteria.from(Product.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("deleted"), false));

        TypedQuery<Product> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    /**
     * @return список всех товаров, у которых нет не одной категории
     */
    public List<Product> getAllWithoutCategory() {

        Query query = manager.createNativeQuery(
                "select a.* from product a left join productcategory b on" +
                        " a.id = b.product_id where b.category_id is NULL and deleted = 0"
        , Product.class);
        List<Product> result = query.getResultList();
        return result;
    }

    /**
     * @return список товаров, у которых есть хотя бы одна категория
     */
    public List<ProductCategory> getAllWithCategory() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ProductCategory> criteria = builder.createQuery(ProductCategory.class);
        Root<ProductCategory> from = criteria.from(ProductCategory.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("deleted"), true));

        TypedQuery<ProductCategory> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

}
