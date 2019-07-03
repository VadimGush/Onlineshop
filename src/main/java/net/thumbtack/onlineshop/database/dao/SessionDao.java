package net.thumbtack.onlineshop.database.dao;

import net.thumbtack.onlineshop.database.models.Session;
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
public class SessionDao {

    @PersistenceContext
    private EntityManager manager;

    public void insert(Session session) {
        manager.persist(session);
    }

    public void delete(Session session) {
        manager.remove(manager.merge(session));
    }

    public Session get(String UUID) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = builder.createQuery(Session.class);
        Root<Session> from = criteria.from(Session.class);
        criteria.select(from);
        criteria.where(
                builder.equal(from.get("UUID"), UUID));
        TypedQuery<Session> typed = manager.createQuery(criteria);
        Session session;

        try {
            session = typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

        return session;
    }
}
