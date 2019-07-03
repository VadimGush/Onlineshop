package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Administrator;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Transactional
@Repository
public class AdministratorDao {

    @PersistenceContext
    private EntityManager manager;

    public void insert(Administrator admin) {
        manager.persist(admin);
    }

    public void update(Administrator admin) {
        manager.merge(admin);
    }

    public Administrator get(String login, String password) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Administrator> criteria = builder.createQuery(Administrator.class);
        Root<Administrator> from = criteria.from(Administrator.class);
        criteria.select(from);
        criteria.where(
                builder.equal(from.get("login"), login),
                builder.and(),
                builder.equal(from.get("password"), password));
        TypedQuery<Administrator> typed = manager.createQuery(criteria);
        Administrator admin;

        try {
            admin = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return admin;
    }

    public void delete(Administrator admin) {
        manager.remove(manager.merge(admin));
    }



}
