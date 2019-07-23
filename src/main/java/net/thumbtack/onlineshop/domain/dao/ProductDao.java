package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Product;
import net.thumbtack.onlineshop.domain.models.ProductCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Repository
public class ProductDao implements Dao {

    private EntityManager manager;

    @Autowired
    public ProductDao(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Записывает товар в БД
     *
     * @param product товар
     */
    public void insert(Product product) {
        manager.persist(product);
    }

    /**
     * Обновляем товар в БД
     *
     * @param product товар
     */
    public void update(Product product) {
        manager.merge(product);
    }

    /**
     * Помечает товар как удалённый.
     * <br>
     * Удалённый товар далее исключается из любых запросов выборки, но остаётся в БД.
     *
     * @param product товар
     */
    public void delete(Product product) {
        product.setDeleted(true);
        manager.merge(product);
    }

    /**
     * Добавляет категорию товара
     *
     * @param productCategory категория товара
     */
    public void insertCategory(ProductCategory productCategory) {
        manager.persist(productCategory);
    }

    /**
     * Удаляет категорию товара
     *
     * @param productCategory категория товара
     */
    public void deleteCategory(ProductCategory productCategory) {
        manager.remove(manager.merge(productCategory));
    }

    /**
     * Получает список категорий, к которым принадлежит данный товар
     *
     * @param productId id товара
     * @return список категорий
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
     * Получает товар по id
     *
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
     * Получает весь список товаров (за исключением удалённых)
     *
     * @return список товаров
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
     * Список всех товаров (за исключением удалённых) без категорий
     *
     * @return список всех товаров, у которых нет не одной категории
     */
    public List<Product> getAllWithoutCategory() {

        // Использую чистый SQL, так как в EntityManager я не разобрался как сделать left join
        Query query = manager.createNativeQuery(
                "select a.* from product a left join productcategory b on" +
                        " a.id = b.product_id where b.category_id is NULL and deleted = 0",
                Product.class);

        return query.getResultList();
    }

    /**
     * Список всех товаров (за исключением удалённых) хотя бы с одной категорией
     *
     * @return список товаров, у которых есть хотя бы одна категория
     */
    public List<ProductCategory> getAllWithCategory() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<ProductCategory> criteria = builder.createQuery(ProductCategory.class);
        Root<ProductCategory> from = criteria.from(ProductCategory.class);

        criteria.select(from);

        TypedQuery<ProductCategory> typed = manager.createQuery(criteria);
        List<ProductCategory> result = new ArrayList<>();

        // Вместо создания сложного запроса, мы вручную удалим
        // товары, которые были удалены. Это очень плохая практика, но как временное
        // решение пока сойдёт
        typed.getResultList().stream()
                .filter(productCategory -> !productCategory.getProduct().getDeleted())
                .forEach(result::add);

        return result;
    }

    /**
     * Получает список всех товаров, которые принадлежат данным категориям
     * (без повторений)
     *
     * @param categories список категорий
     * @return список товаров (или пустую коллекцию, если categories == null)
     */
    public Set<Product> getAllWithCategories(List<Long> categories) {

        if (categories == null || categories.isEmpty())
            return new HashSet<>();

        List<ProductCategory> products = getAllWithCategory();
        Set<Product> result = new HashSet<>();

        for (long category : categories) {
            for (ProductCategory product : products) {
                if (product.getCategory().getId() == category) {
                    result.add(product.getProduct());
                }
            }
        }

        return result;
    }

    /**
     * Удаляет таблицы категорий и товаров из БД
     */
    public void clear() {
        // Категории товаров тоже будут удалены, так как на них стоит OnDelete.CASCADE
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaDelete<Product> criteria = builder.createCriteriaDelete(Product.class);

        criteria.from(Product.class);

        manager.createQuery(criteria).executeUpdate();
    }

}
