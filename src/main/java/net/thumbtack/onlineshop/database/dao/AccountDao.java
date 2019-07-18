package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional
@Repository
public class AccountDao implements Dao {

    private EntityManager manager;

    @Autowired
    public AccountDao(EntityManager manager) {
        this.manager = manager;
    }

    public void insert(Account account) {
        manager.persist(account);
    }

    public void update(Account account) {
        manager.merge(account);
    }

    public Account get(String login, String password) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> from = criteria.from(Account.class);
        criteria.select(from);
        criteria.where(
                builder.equal(from.get("login"), login),
                builder.and(),
                builder.equal(from.get("password"), password));
        TypedQuery<Account> typed = manager.createQuery(criteria);

        try {
            return typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean exists(String login) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(builder.count(from));
        criteria.where(builder.equal(from.get("login"), login));

        TypedQuery<Long> typed = manager.createQuery(criteria);

        return typed.getSingleResult() != 0;
    }

    public void delete(Account account) {
        manager.remove(manager.merge(account));
    }

    public List<Account> getClients() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();

        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> from = criteria.from(Account.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("admin"), false));

        TypedQuery<Account> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }

    public void clear() {
        manager.createNativeQuery("delete from account")
                .executeUpdate();
    }

}
