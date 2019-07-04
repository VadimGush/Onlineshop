package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Client;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Transactional
@Repository
public class ClientDao {

    @PersistenceContext
    private EntityManager manager;

    public void insert(Client client) {
        manager.persist(client);
    }

    public void update(Client client) {
        manager.merge(client);
    }

    public Client get(String login, String password) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Client> criteria = builder.createQuery(Client.class);
        Root<Client> from = criteria.from(Client.class);
        criteria.select(from);
        criteria.where(
                builder.equal(from.get("login"), login),
                builder.and(),
                builder.equal(from.get("password"), password));
        TypedQuery<Client> typed = manager.createQuery(criteria);
        Client client;

        try {
            client = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return client;
    }

    public void delete(Client client) {
        manager.remove(manager.merge(client));
    }

    public List<Client> getAll() {
        CriteriaBuilder builder = manager.getCriteriaBuilder();

        CriteriaQuery<Client> criteria = builder.createQuery(Client.class);
        Root<Client> from = criteria.from(Client.class);

        criteria.select(from);

        TypedQuery<Client> typed = manager.createQuery(criteria);
        return typed.getResultList();
    }
}
