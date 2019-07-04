package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Category;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CategoryDao {

    private EntityManager manager;

    @Autowired
    public CategoryDao(EntityManager manager) {
        this.manager = manager;
    }

    public void insert(Category category) {
        manager.persist(category);
    }

    public void update(Category category) {
        manager.merge(category);
    }

    public void delete(Category category) {
        manager.remove(manager.merge(category));
    }

    public List<Category> getAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Category> criteria = builder.createQuery(Category.class);
        Root<Category> from = criteria.from(Category.class);
        CriteriaQuery<Category> all = criteria.select(from);

        TypedQuery<Category> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }
}
