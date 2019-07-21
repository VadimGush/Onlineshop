package net.thumbtack.onlineshop.domain.dao;

import net.thumbtack.onlineshop.domain.models.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Transactional
@Repository
public class SessionDao implements Dao {

    private EntityManager manager;

    @Autowired
    public SessionDao(EntityManager manager) {
        this.manager = manager;
    }

    /**
     * Добавляет сессию в БД
     * <br>
     * Сессии могут совпадать по идентификаторам, так как пользователь может
     * зайти с нескольких устройств одновременно.
     *
     * @param session сессия пользователя
     */
    public void insert(Session session) {
        manager.persist(session);
    }

    /**
     * Удаляет сессию из БД
     *
     * @param session сессия пользователя
     */
    public void delete(Session session) {
        manager.remove(manager.merge(session));
    }

    /**
     * Получает сессию пользователя по её ID.
     * <br>
     * Каждая сессия содержит в себе уникальный ключ сформированный по UUID.
     * Сессия содержит в себе внутри UUID и ссылку на пользователя, которому принадлежит
     * данная сессия.
     *
     * @param UUID идентификатор сессии
     * @return экземпляр сессии
     */
    public Session get(String UUID) {
        CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Session> criteria = builder.createQuery(Session.class);
        Root<Session> from = criteria.from(Session.class);

        criteria.select(from);
        criteria.where(builder.equal(from.get("UUID"), UUID));

        TypedQuery<Session> typed = manager.createQuery(criteria);

        try {
            return typed.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Удаляет таблицу сессий из БД
     */
    public void clear() {
        manager.createNativeQuery("delete from session")
                .executeUpdate();
    }
}
